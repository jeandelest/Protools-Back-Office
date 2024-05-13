package fr.insee.protools.backend.service.common.platine_sabiane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.ProtoolsTestUtils;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.campaign.MetadataValue;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.campaign.MetadataValueItem;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.campaign.MetadataVariables;
import fr.insee.protools.backend.service.context.ContextConstants;
import fr.insee.protools.backend.service.nomenclature.NomenclatureService;
import fr.insee.protools.backend.service.platine.delegate.PlatineQuestionnaireCreateContextTaskTest;
import fr.insee.protools.backend.service.questionnaire_model.QuestionnaireModelService;
import fr.insee.protools.backend.service.sabiane.delegate.SabianeQuestionnaireCreateContextTaskTest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.insee.protools.backend.service.context.ContextConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireHelperTest {

    final static String platineRessourceFolder = ClassUtils.convertClassNameToResourcePath(PlatineQuestionnaireCreateContextTaskTest.class.getPackageName());
    final static String platine_context_json = platineRessourceFolder + "/protools-contexte-platine-individu.json";

    final static String sabianeRessourceFolder = ClassUtils.convertClassNameToResourcePath(SabianeQuestionnaireCreateContextTaskTest.class.getPackageName());
    final static String sabiane_context_json = sabianeRessourceFolder + "/protools-contexte-sabiane.json";


    @Mock QuestionnaireModelService questionnaireModelService;
    @Mock NomenclatureService nomenclatureService;
    @Mock QuestionnairePlatineSabianeService questionnairePlatineSabianeService;
    @Spy ObjectMapper objectMapper;

    private final String nomenclatureContent1 = "{\"content\":\"content1\"}";
    private final String nomenclatureContent2 = "{\"content\":\"content2\"}";
    private final String nomenclatureID1 = "ID1";
    private final String nomenclatureID2 = "ID2";
    private final String nomenclatureLabel1 = "label1";
    private final String nomenclatureLabel2 = "label2";
    private final String questionnaireContent1 = "{\"id\":\"TOTO\"}";

    private JsonNode initNomenclatureTest() {
        // preconditions
        List<Map<String, String>> nomenclatures = new ArrayList<>(2);
        Map<String, String> nomenclature1 = Map.of("id", nomenclatureID1, "cheminRepertoire", "path", "label", nomenclatureLabel1);
        Map<String, String> nomenclature2 = Map.of("id", nomenclatureID2, "cheminRepertoire", "path", "label", nomenclatureLabel2);
        nomenclatures.add(nomenclature1);
        nomenclatures.add(nomenclature2);
        //For some tests where the nomenclature already exists in platine, this mock will never be used
        //To avoid mockito exception warning about unused mock, we can mark it as lenient
        lenient().when(nomenclatureService.getNomenclatureContent(nomenclatureID2, "path")).thenReturn(nomenclatureContent2);
        lenient().when(nomenclatureService.getNomenclatureContent(nomenclatureID1, "path")).thenReturn(nomenclatureContent1);
        return objectMapper.valueToTree(nomenclatures);
    }

    @Test
    void initRequiredNomenclatures_should_work_when_no_nomenclature_not_exists_in_remote_plateform() throws IOException {
        // preconditions (2 nomenclatures - None already exists in platine/sabiane)
        JsonNode nomenclaturesNode = initNomenclatureTest();
        when(questionnairePlatineSabianeService.getNomenclaturesId()).thenReturn(new HashSet<>());
        var iter = nomenclaturesNode.elements();

        //Execute the unit under test
        QuestionnaireHelper.initRequiredNomenclatures(questionnairePlatineSabianeService, nomenclatureService, "1", iter);

        // postconditions : Both nomenclatures are created
        verify(questionnairePlatineSabianeService).postNomenclature(
                nomenclatureID1,
                nomenclatureLabel1,
                objectMapper.readTree(nomenclatureContent1));
        verify(questionnairePlatineSabianeService).postNomenclature(
                nomenclatureID2,
                nomenclatureLabel2,
                objectMapper.readTree(nomenclatureContent2));
    }

    @Test
    void initRequiredNomenclatures_should_work_when_one_nomenclature_exists_in_remote_plateform() throws IOException {
        // preconditions (2 nomenclatures - One already exists in platine/sabiane)
        JsonNode nomenclaturesNode = initNomenclatureTest();
        when(questionnairePlatineSabianeService.getNomenclaturesId()).thenReturn(Set.of("XXX", nomenclatureID1, "TOTO", "TATA"));
        var iter = nomenclaturesNode.elements();

        //Execute the unit under test
        QuestionnaireHelper.initRequiredNomenclatures(questionnairePlatineSabianeService, nomenclatureService, "1", iter);

        // postconditions
        //Existing nomenclature is not created
        verify(questionnairePlatineSabianeService, never()).postNomenclature(
                nomenclatureID1,
                nomenclatureLabel1,
                objectMapper.readTree(nomenclatureContent1));
        //New nomenclature is created
        verify(questionnairePlatineSabianeService).postNomenclature(
                nomenclatureID2,
                nomenclatureLabel2,
                objectMapper.readTree(nomenclatureContent2));
    }

    private Set<String> runInitQuestionnaireModels(JsonNode contextRootNode) {
        // preconditions
        when(questionnaireModelService.getQuestionnaireModel(anyString(), anyString())).thenReturn(questionnaireContent1);

        //Execute the unit under test
        return QuestionnaireHelper.initQuestionnaireModels(questionnairePlatineSabianeService, questionnaireModelService, "1", contextRootNode);
    }

    @Test
    void initQuestionnaireModels_should_return_list_of_modelsId_with_platine_context() {
        JsonNode contextRootNode = ProtoolsTestUtils.asJsonNode(platine_context_json);
        assertEquals(1, contextRootNode.path(ContextConstants.CTX_QUESTIONNAIRE_MODELS).size(), "Context error : expected exactly one questionnaire model");

        Set<String> questionnaireModels = runInitQuestionnaireModels(contextRootNode);

        // postconditions : we expect to find exactly one questionnaire Model
        String idQuestionnaireModel = contextRootNode.path(ContextConstants.CTX_QUESTIONNAIRE_MODELS).get(0).path(CTX_QUESTIONNAIRE_MODEL_ID).asText();
        assertEquals(Set.of(idQuestionnaireModel), questionnaireModels);
        verify(questionnaireModelService,times(1)).getQuestionnaireModel(
                contextRootNode.path(CTX_QUESTIONNAIRE_MODELS).get(0).path(CTX_QUESTIONNAIRE_MODEL_ID).asText()
                , contextRootNode.path(CTX_QUESTIONNAIRE_MODELS).get(0).path(CTX_QUESTIONNAIRE_MODEL_CHEMIN_REPERTOIRE).asText());

        final ArgumentCaptor<Set<String> > nomenclaturesCaptor
                = ArgumentCaptor.forClass((Class) Set.class);
        ArgumentCaptor<String> acModelID = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> acLabel = ArgumentCaptor.forClass(String.class);

        verify(questionnairePlatineSabianeService,times(1)).postQuestionnaireModel(
                acModelID.capture(),
                acLabel.capture(),
                any(), nomenclaturesCaptor.capture());
        assertEquals(contextRootNode.path(CTX_QUESTIONNAIRE_MODELS).get(0).path(CTX_QUESTIONNAIRE_MODEL_ID).asText(),acModelID.getValue());
        assertEquals(contextRootNode.path(CTX_QUESTIONNAIRE_MODELS).get(0).path(CTX_QUESTIONNAIRE_MODEL_LABEL).asText(),acLabel.getValue());

        List<Set<String>> captured = nomenclaturesCaptor.getAllValues();
        assertEquals(1,captured.size(),"We are supposed to call the method only one");
        Set<String> actualNomenclatures = captured.get(0);
        Set<String> expectedNomenclatures = Set.of(
                "L_DEPNAIS-1-1-0",
                "L_PAYSNAIS-1-1-0",
                "L_NATIONETR-1-1-0");
        assertTrue(expectedNomenclatures.size()==actualNomenclatures.size()
                &&expectedNomenclatures.containsAll(actualNomenclatures)
        && actualNomenclatures.containsAll(expectedNomenclatures)
        ,"The set of nomenclature is incorrect");
    }

    @Test
    void initQuestionnaireModels_should_return_list_of_modelsId_with_sabiane_context() {
        JsonNode contextRootNode = ProtoolsTestUtils.asJsonNode(sabiane_context_json);
        assertEquals(2, contextRootNode.path(ContextConstants.CTX_QUESTIONNAIRE_MODELS).size(), "Context error : expected exactly one questionnaire model");

        Set<String> questionnaireModels = runInitQuestionnaireModels(contextRootNode);

        // postconditions : we expect to find 2 Questionnaire Models
        assertEquals(Stream.of("FAM2022X01", "tic2023a00_webMBG").collect(Collectors.toCollection(HashSet::new)), questionnaireModels);
    }

    /**
     * BIG integration test
     */
    @Test
    void createQuestionnaire_should_work_with_sabiane_context() {
        JsonNode contextRootNode = ProtoolsTestUtils.asJsonNode(sabiane_context_json);
        when(questionnairePlatineSabianeService.getNomenclaturesId()).thenReturn(new HashSet<>());
        when(nomenclatureService.getNomenclatureContent(any(), any())).thenReturn(nomenclatureContent1);
        when(questionnaireModelService.getQuestionnaireModel(anyString(), anyString())).thenReturn(questionnaireContent1);

        MetadataValueItem medatadataItem = new MetadataValueItem("TOTO", "VALUE");
        MetadataValue metadataValue = MetadataValue
                .builder()
                .value(MetadataVariables.builder()
                        .variables(List.of(medatadataItem))
                        .inseeContext("INSEE_CONTEXT")
                        .build()
                )
                .build();
        //Execute the unit under test
        QuestionnaireHelper.createQuestionnaire(
                contextRootNode, questionnairePlatineSabianeService, nomenclatureService, questionnaireModelService, "1", metadataValue);


        //Post Conditions on nomenclature part
        ArgumentCaptor<String> acNomenclatureId = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> acNomenclatureLabel = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<JsonNode> acNomenclatureValue = ArgumentCaptor.forClass(JsonNode.class);
        //Context has 4 nomenclatures
        verify(questionnairePlatineSabianeService, times(4)).postNomenclature(acNomenclatureId.capture(), acNomenclatureLabel.capture(), acNomenclatureValue.capture());
        //Verify arguments
        Pair<String, String> nomenclature1 = new ImmutablePair<>("L_DEPNAIS-1-1-0", "Liste des départements");
        Pair<String, String> nomenclature2 = new ImmutablePair("L_PAYSNAIS-1-1-0", "liste des pays");
        Pair<String, String> nomenclature3 = new ImmutablePair("L_NATIONETR-1-1-0", "liste des nationalités");
        Pair<String, String> nomenclature4 = new ImmutablePair("L_COMMUNEPASSEE-1-1-0", "liste des communes");
        List<Pair<String, String>> listOfNomenclatures = List.of(nomenclature1, nomenclature2, nomenclature3, nomenclature4);
        //Creation of each nomenclature
        assertTrue(
                acNomenclatureId.getAllValues()
                        .containsAll(
                                List.of(nomenclature1.getKey(), nomenclature2.getKey(), nomenclature3.getKey(), nomenclature4.getKey())
                        ));
        //With correct parameters
        for (int i = 0; i < 4; i++) {
            String argId = acNomenclatureId.getAllValues().get(i);
            Pair<String, String> nomenclature = listOfNomenclatures.stream()
                    .filter(item -> argId.equals(item.getKey()))
                    .findAny()
                    .orElse(null);
            assertNotNull(nomenclature, "Internal error in test : nomenclature should not be null here");
            assertEquals( nomenclature.getValue(),acNomenclatureLabel.getAllValues().get(i));
            assertEquals(nomenclatureContent1,acNomenclatureValue.getAllValues().get(i).toString().trim());
        }


        //Post Conditions on questionnaire model part
        ArgumentCaptor<String> acQuestionnaireId = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> acQuestionnaireLabel = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<JsonNode> acQuestionnaireValue = ArgumentCaptor.forClass(JsonNode.class);
        ArgumentCaptor<Set<String>> acRequiredNomenclatures = ArgumentCaptor.forClass( Set.class);
        //Creation of each model
        verify(questionnairePlatineSabianeService, times(2))
                .postQuestionnaireModel(
                        acQuestionnaireId.capture(),acQuestionnaireLabel.capture(),acQuestionnaireValue.capture(),acRequiredNomenclatures.capture()
                        );
        //With correct parameters
        Tuple q1 = new Tuple("tic2023a00_webMBG","Super modèle 1",Set.of("L_DEPNAIS-1-1-0", "L_PAYSNAIS-1-1-0","L_NATIONETR-1-1-0"));
        Tuple q2 = new Tuple("FAM2022X01","Super modèle 2",Set.of("L_COMMUNEPASSEE-1-1-0"));
        List<Tuple> listOfModels = List.of(q1,q2);

        for (int i = 0; i < 2; i++) {
            String argId = acQuestionnaireId.getAllValues().get(i);
            Tuple modele = listOfModels.stream()
                    .filter(item -> argId.equals(item.toList().get(0)))
                    .findAny()
                    .orElse(null);
            assertNotNull(modele, "Internal error in test : modele should not be null here");
            assertEquals( modele.toList().get(1),acQuestionnaireLabel.getAllValues().get(i));
            assertEquals( modele.toList().get(2),acRequiredNomenclatures.getAllValues().get(i));
            assertEquals(questionnaireContent1,acQuestionnaireValue.getAllValues().get(i).toString());

        }

    }

}