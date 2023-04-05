package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.ProtoolsTestUtils;
import fr.insee.protools.backend.service.context.ContextConstants;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.nomenclature.NomenclatureService;
import fr.insee.protools.backend.service.platine.questionnaire.PlatineQuestionnaireService;
import fr.insee.protools.backend.service.platine.questionnaire.dto.campaign.CampaignDto;
import fr.insee.protools.backend.service.platine.questionnaire.dto.campaign.MetadataValue;
import fr.insee.protools.backend.service.questionnaire_model.QuestionnaireModelService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.test.FlowableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.*;

import static fr.insee.protools.backend.service.context.ContextConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@FlowableTest
class PlatineQuestionnaireCreateContextTaskTest {

    final static String ressourceFolder = ClassUtils.convertClassNameToResourcePath(PlatineQuestionnaireCreateContextTaskTest.class.getPackageName());
    final static String platine_context_json = ressourceFolder+"/protools-contexte-platine.json";
    final static String platine_context_incorrect_json = ressourceFolder+"/protools-contexte-platine-incorrect.json";

    @Mock PlatineQuestionnaireService platineQuestionnaireService;
    @Mock QuestionnaireModelService questionnaireModelService;
    @Mock NomenclatureService nomenclatureService;
    @Mock ContextService protoolsContext;
    @Spy ObjectMapper objectMapper;

    @InjectMocks
    PlatineQuestionnaireCreateContextTask platineQuestTask;

    private String nomenclatureContent1="{\"content\": \"content1\"}";
    private String nomenclatureContent2="{\"content\": \"content2\"}";
    private String nomenclatureID1="ID1";
    private String nomenclatureID2="ID2";
    private String nomenclatureLabel1="label1";
    private String nomenclatureLabel2="label2";
    private String questionnaireContent1 ="{\"id\": \"TOTO\"}";

    private JsonNode initContexteMock(String contexteToLoad){
        JsonNode contextRootNode = ProtoolsTestUtils.asJsonNode(contexteToLoad);
        when(protoolsContext.getContextByProcessInstance(anyString())).thenReturn(contextRootNode);
        return contextRootNode;
    }

    private JsonNode initNomenclatureTest(){
        // preconditions
        List<Map<String,String>> nomenclatures = new ArrayList<>(2);
        Map<String,String> nomenclature1=Map.of("id",nomenclatureID1,"cheminRepertoire","path", "label",nomenclatureLabel1);
        Map<String,String> nomenclature2=Map.of("id",nomenclatureID2,"cheminRepertoire","path", "label",nomenclatureLabel2);
        nomenclatures.add(nomenclature1);
        nomenclatures.add(nomenclature2);
        //For some tests where the nomenclature already exists in platine, this mock will never be used
        //To avoid mockito exception warning about unused mock, we can mark it as lenient
        lenient().when(nomenclatureService.getNomenclatureContent(nomenclatureID2,"path")).thenReturn(nomenclatureContent2);
        lenient().when(nomenclatureService.getNomenclatureContent(nomenclatureID1,"path")).thenReturn(nomenclatureContent1);
        return objectMapper.valueToTree(nomenclatures);
    }

    @Test
    void initRequiredNomenclatures_should_work_when_no_nomenclature_not_exists_in_platine() throws IOException {
        // preconditions (2 nomenclatures - None already exists in platine)
        JsonNode nomenclaturesNode= initNomenclatureTest();
        when(platineQuestionnaireService.getNomenclaturesId()).thenReturn(new HashSet<>());
        var iter = nomenclaturesNode.elements();

        //Execute the unit under test
        platineQuestTask.initRequiredNomenclatures("1", iter);

        // postconditions : Both nomenclatures are created
        verify(platineQuestionnaireService).postNomenclature(
                nomenclatureID1,
                nomenclatureLabel1,
                objectMapper.readTree(nomenclatureContent1));
        verify(platineQuestionnaireService).postNomenclature(
                nomenclatureID2,
                nomenclatureLabel2,
                objectMapper.readTree(nomenclatureContent2));
    }

    @Test
    void initRequiredNomenclatures_should_work_when_one_nomenclature_exists_in_platine() throws IOException {
        // preconditions (2 nomenclatures - One already exists in platine)
        JsonNode nomenclaturesNode= initNomenclatureTest();
        when(platineQuestionnaireService.getNomenclaturesId()).thenReturn(Set.of("XXX",nomenclatureID1,"TOTO","TATA"));
        var iter = nomenclaturesNode.elements();

        //Execute the unit under test
        platineQuestTask.initRequiredNomenclatures("1", iter);

        // postconditions
        //Existing nomenclature is not created
        verify(platineQuestionnaireService,never()).postNomenclature(
                nomenclatureID1,
                nomenclatureLabel1,
                objectMapper.readTree(nomenclatureContent1));
        //New nomenclature is created
        verify(platineQuestionnaireService).postNomenclature(
                nomenclatureID2,
                nomenclatureLabel2,
                objectMapper.readTree(nomenclatureContent2));
    }

    @Test
    void initQuestionnaireModels_should_return_list_of_modelsId() {
        // preconditions
        JsonNode contextRootNode = ProtoolsTestUtils.asJsonNode(platine_context_json);
        assertEquals(contextRootNode.path(ContextConstants.CTX_QUESTIONNAIRE_MODELS).size(),1, "Context error : expected exactly one questionnaire model");
        when(questionnaireModelService.getQuestionnaireModel(anyString(), anyString())).thenReturn(questionnaireContent1);

        //Execute the unit under test
        Set<String> questionnaireModels =  platineQuestTask.initQuestionnaireModels("1", contextRootNode);

        // postconditions
        String idQuestionnaireModel = contextRootNode.path(ContextConstants.CTX_QUESTIONNAIRE_MODELS).get(0).path(CTX_QUESTIONNAIRE_MODEL_ID).asText();
        assertEquals(Set.of(idQuestionnaireModel),questionnaireModels);
    }


    @Test
    void execute_should_throwBadContextIncorrect_when_contextIsKO() {
        // preconditions
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getProcessInstanceId()).thenReturn("1");
        initContexteMock(platine_context_incorrect_json);

        //Execute the unit under test
        assertThrows(BadContextIncorrectException.class, () -> platineQuestTask.execute(execution));

        // postconditions
        //assertThat(execution.getVariable("myVariable")).isEqualTo("myValue");

    }

    /*@Test
    void initQuestionnaireModels_should_produce_correct_json(){
        platineQuestTask.²
    }*/

    @Test
    void execute_should_work_and_make_correct_calls() throws IOException {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getProcessInstanceId()).thenReturn("1");
        JsonNode contextRootNode = initContexteMock(platine_context_json);
        assertEquals(contextRootNode.path(ContextConstants.CTX_QUESTIONNAIRE_MODELS).size(),1, "Context error : expected exactly one questionnaire model");
        String idQuestionnaireModel = contextRootNode.path(ContextConstants.CTX_QUESTIONNAIRE_MODELS).get(0).path(CTX_QUESTIONNAIRE_MODEL_ID).asText();
        String labelQuestionnaireModel = contextRootNode.path(ContextConstants.CTX_QUESTIONNAIRE_MODELS).get(0).path(CTX_QUESTIONNAIRE_MODEL_LABEL).asText();
        String repertoireQuestionnaireModel = contextRootNode.path(ContextConstants.CTX_QUESTIONNAIRE_MODELS).get(0).path(CTX_QUESTIONNAIRE_MODEL_CHEMIN_REPERTOIRE).asText();

        //Prepare the list of existing nomenclatures ("L_NATIONETR-1-1-0" does not exists yet)
        when(platineQuestionnaireService.getNomenclaturesId()).thenReturn(Set.of("L_DEPNAIS-1-1-0","L_PAYSNAIS-1-1-0"));
        //Mock the read nomenclature
        String nomenclatureContent="{\"id\": \"nomenclatureContent\"}";
        when(nomenclatureService.getNomenclatureContent("L_NATIONETR-1-1-0","NATIONETR")).thenReturn(nomenclatureContent);
        //Mock questionnaire
        when(platineQuestionnaireService.questionnaireModelExists(anyString())).thenReturn(false);
        when(questionnaireModelService.getQuestionnaireModel(anyString(), anyString())).thenReturn(questionnaireContent1);


        //Execute the unit under test
        assertThatCode(() -> platineQuestTask.execute(execution)).doesNotThrowAnyException();

        //Verifications on nomenclatures
        verify(platineQuestionnaireService,atLeastOnce()).getNomenclaturesId();
        verify(platineQuestionnaireService).postNomenclature(
                "L_NATIONETR-1-1-0",
                "liste des nationalités",
                objectMapper.readTree(nomenclatureContent));

        //Verifications on questionnaires
        verify(platineQuestionnaireService,times(1)).questionnaireModelExists(idQuestionnaireModel);
        verify(questionnaireModelService,times(1)).getQuestionnaireModel(idQuestionnaireModel,repertoireQuestionnaireModel);
        verify(platineQuestionnaireService).postQuestionnaireModel(idQuestionnaireModel,labelQuestionnaireModel,
                objectMapper.readTree(questionnaireContent1),
                Set.of("L_DEPNAIS-1-1-0","L_PAYSNAIS-1-1-0","L_NATIONETR-1-1-0"));

        //Verify postCampaign
        ArgumentCaptor<CampaignDto> acCampaignDto = ArgumentCaptor.forClass(CampaignDto.class);
        verify(platineQuestionnaireService,times(1)).postCampaign(acCampaignDto.capture());
        List<CampaignDto> allValues = acCampaignDto.getAllValues();
        assertEquals(1, allValues.size(),"We should have exactly one campaign");
        MetadataValue expectedMetadataNode = ProtoolsTestUtils.asObject(ressourceFolder + "/expected_post_questionnaire_metadata.json", MetadataValue.class);
        assertEquals(expectedMetadataNode,allValues.get(0).getMetadata(),"Erreur with generated Metadata");
    }
}
