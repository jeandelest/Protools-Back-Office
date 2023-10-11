package fr.insee.protools.backend.service.platine.delegate;

import fr.insee.protools.backend.service.context.exception.BadContextIncorrectBPMNError;
import fr.insee.protools.backend.service.platine.pilotage.PlatinePilotageService;
import fr.insee.protools.backend.service.platine.pilotage.metadata.MetadataDto;
import fr.insee.protools.backend.service.utils.TestWithContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.test.FlowableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ClassUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FlowableTest
class PlatinePilotageCreateContextTaskTest extends TestWithContext {
    final static String ressourceFolder = ClassUtils.convertClassNameToResourcePath(PlatineQuestionnaireCreateContextTaskTest.class.getPackageName());
    final static String platine_context_json = ressourceFolder+"/protools-contexte-platine-individu.json";
    final static String platine_context_incorrect_json = ressourceFolder+"/protools-contexte-platine-incorrect.json";


    @Mock PlatinePilotageService platinePilotageService;
    @InjectMocks PlatinePilotageCreateContextTask platinePilotageTask;

    @Test
    void execute_should_throw_BadContextIncorrectException_when_noContext() {
        DelegateExecution execution = createMockedExecution();

        //Execute the unit under test
        assertThrows(BadContextIncorrectBPMNError.class,() -> platinePilotageTask.execute(execution));
    }

    @Test
    void execute_should_work_when_contextOK() {
        DelegateExecution execution = createMockedExecution();
        initContexteMockWithFile(platine_context_json);
        String partitionId="1";
        String campaignId="DEM2022X00";

        //Execute the unit under test
        platinePilotageTask.execute(execution);

        //Post conditions : Exactly one call to post platinePilotageService.putMetadata
        //                  it is one call per partition defined in context
//        verify(platinePilotageService,times(1)).putMetadata(eq(partitionId), notNull());


        //Verify postCampaign
        ArgumentCaptor<MetadataDto> acMetadataDto = ArgumentCaptor.forClass(MetadataDto.class);
        verify(platinePilotageService,times(1)).putMetadata(eq(campaignId+partitionId),acMetadataDto.capture());
        List<MetadataDto> allValues = acMetadataDto.getAllValues();
        assertEquals(1, allValues.size(),"We should have exactly one partition");
//TODO
    //    MetadataDto expectedMetadataNode = ProtoolsTestUtils.asObject(ressourceFolder + "/expected_post_questionnaire_metadata.json", MetadataDto.class);
        assertEquals("DEM2022X00",allValues.get(0).getCampaignDto().getId(),"Erreur with generated Metadata campaign id");

    }

}
