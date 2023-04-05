package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.ProtoolsTestUtils;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.platine.pilotage.PlatinePilotageService;
import fr.insee.protools.backend.service.platine.pilotage.dto.MetadataDto;
import fr.insee.protools.backend.service.platine.questionnaire.dto.campaign.CampaignDto;
import fr.insee.protools.backend.service.platine.questionnaire.dto.campaign.MetadataValue;
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
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FlowableTest
public class PlatinePilotageCreateContextTaskTest {
    final static String ressourceFolder = ClassUtils.convertClassNameToResourcePath(PlatineQuestionnaireCreateContextTaskTest.class.getPackageName());
    final static String platine_context_json = ressourceFolder+"/protools-contexte-platine.json";
    final static String platine_context_incorrect_json = ressourceFolder+"/protools-contexte-platine-incorrect.json";


    @Mock PlatinePilotageService platinePilotageService;
    @Spy ContextService protoolsContext;
    @Spy ObjectMapper objectMapper;

    @InjectMocks
    PlatinePilotageCreateContextTask platinePilotageTask;

    String dumyId="ID1";

    private JsonNode initContexteMock(String contexteToLoad){
        JsonNode contextRootNode = ProtoolsTestUtils.asJsonNode(contexteToLoad);
        when(protoolsContext.getContextByProcessInstance(anyString())).thenReturn(contextRootNode);
        return contextRootNode;
    }

    @Test
    void execute_should_throw_BadContextIncorrectException_when_noContext() {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getProcessInstanceId()).thenReturn(dumyId);

        //Execute the unit under test
        assertThrows(BadContextIncorrectException.class,() -> platinePilotageTask.execute(execution));
    }

    @Test
    void execute_should_work_when_contextOK() {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getProcessInstanceId()).thenReturn(dumyId);
        initContexteMock(platine_context_json);
        String partitionId="1";

        //Execute the unit under test
        platinePilotageTask.execute(execution);

        //Post conditions : Exactly one call to post platinePilotageService.putMetadata
        //                  it is one call per partition defined in context
//        verify(platinePilotageService,times(1)).putMetadata(eq(partitionId), notNull());


        //Verify postCampaign
        ArgumentCaptor<MetadataDto> acMetadataDto = ArgumentCaptor.forClass(MetadataDto.class);
        verify(platinePilotageService,times(1)).putMetadata(eq(partitionId),acMetadataDto.capture());
        List<MetadataDto> allValues = acMetadataDto.getAllValues();
        assertEquals(1, allValues.size(),"We should have exactly one partition");
//TODO
    //    MetadataDto expectedMetadataNode = ProtoolsTestUtils.asObject(ressourceFolder + "/expected_post_questionnaire_metadata.json", MetadataDto.class);
        assertEquals("DEM2022X00",allValues.get(0).getCampaignDto().getId(),"Erreur with generated Metadata campaign id");

    }

}
