package fr.insee.protools.backend.service.meshuggah;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.ProtoolsTestUtils;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectBPMNError;
import fr.insee.protools.backend.service.era.dto.CensusJsonDto;
import fr.insee.protools.backend.service.era.dto.GenderType;
import fr.insee.protools.backend.service.meshuggah.dto.MeshuggahComDetails;
import fr.insee.protools.backend.service.platine.pilotage.metadata.MetadataDto;
import fr.insee.protools.backend.service.platine.utils.PlatineHelper;
import fr.insee.protools.backend.service.utils.TestWithContext;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.test.FlowableTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_ERA_QUERY_END_DATE;
import static fr.insee.protools.backend.service.context.ContextConstants.*;
import static fr.insee.protools.backend.service.context.ContextConstants.CTX_PARTITION_COMMUNICATION_AVEC_QUESTIONNAIRE_PAPIER;
import static fr.insee.protools.backend.service.utils.FlowableVariableUtils.getMissingVariableMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@FlowableTest
class MeshuggahSendOpeningMailCommunicationForSUTaskTest extends TestWithContext {
    static ObjectMapper objectMapper = new ObjectMapper();
    final static String meshuggahContext_1part_Ok =
            """
            {
              "id": "AAC2023A00",
              "partitions": [{
                "id": 99,
                "communications" :[
                  {
                    "moyenCommunication" : "mail",
                    "phaseCommunication" : "ouverture",
                    "avecQuestionnairePapier" : false,
                    "protocole" : null,
                    "mode" : "web",
                    "typeModele" : "relance_libre"
                  }
                ]
              }]
            }
            """;
    private final String platineContactContent = """
            {
                        "email": "toto@insee.fr",
                        "identifier": "id"
            }
            """;
    @Mock MeshuggahService meshuggahService;
    @InjectMocks MeshuggahSendOpeningMailCommunicationForSUTask meshuggahTask;

    @Test
    @DisplayName("Test execute method - should throw if VARNAME_CURRENT_PARTITION_ID or VARNAME_PLATINE_CONTACT not initialized")
    void execute_should_throw_FlowableIllegalArgumentException_when_variables_notDefined() throws JsonProcessingException {

        //Precondition
        DelegateExecution execution = mock(DelegateExecution.class);
        lenient().when(execution.getProcessInstanceId()).thenReturn(dumyId);
        initContexteMockWithString(meshuggahContext_1part_Ok);

        //Execute the unit under test
        FlowableIllegalArgumentException exception = assertThrows(FlowableIllegalArgumentException.class, () -> meshuggahTask.execute(execution));
        //Post conditions
        assertThat(exception.getMessage()).isEqualTo(getMissingVariableMessage(VARNAME_CURRENT_PARTITION_ID));

        //Create First variable
        when(execution.getVariable(VARNAME_CURRENT_PARTITION_ID, Long.class)).thenReturn(99L);
        //Execute again
        exception = assertThrows(FlowableIllegalArgumentException.class, () -> meshuggahTask.execute(execution));
        //Check the error
        assertThat(exception.getMessage()).isEqualTo(getMissingVariableMessage(VARNAME_PLATINE_CONTACT));

        //Create 2nd variable
        when(execution.getVariable(VARNAME_PLATINE_CONTACT, JsonNode.class)).thenReturn(objectMapper.readTree(platineContactContent));
        //Execute again
        assertDoesNotThrow(() -> meshuggahTask.execute(execution));
    }

    @Test
    @DisplayName("Test execute method - should throw if Context is not correct")
    void execute_should_throw_BadContext_when_contextIncorrect() throws JsonProcessingException {
        //Precondition
        DelegateExecution execution = mock(DelegateExecution.class);
        lenient().when(execution.getProcessInstanceId()).thenReturn(dumyId);

        when(execution.getVariable(VARNAME_CURRENT_PARTITION_ID, Long.class)).thenReturn(99L);
        when(execution.getVariable(VARNAME_PLATINE_CONTACT, JsonNode.class)).thenReturn(objectMapper.readTree(platineContactContent));


        final String context1_no_part =
        """
            {
              "id": "AAC2023A00"
            }
        """;
        final String context2_no_com =
        """
            {
              "id": "AAC2023A00",
              "partitions": [{
                "id": 99
              }]
            }
        """;
        final String context3_no_moyen =
        """
            {
              "id": "AAC2023A00",
              "partitions": [{
                "id": 99,
                "communications" :[
                  {
                    "phaseCommunication" : "ouverture",
                    "avecQuestionnairePapier" : false,
                    "protocole" : null,
                    "mode" : "web",
                    "typeModele" : "relance_libre"
                  }
                ]
              }]
            }
        """;
        final String context4_error_in_phase=
        """
            {
              "id": "AAC2023A00",
              "partitions": [{
                "id": 99,
                "communications" :[
                  {
                    "moyenCommunication" : "mail",
                    "phaseCommunication" : "ouvertureeee",
                    "avecQuestionnairePapier" : false,
                    "protocole" : null,
                    "mode" : "web",
                    "typeModele" : "relance_libre"
                  }
                ]
              }]
            }
        """;

        final String context5_error_in_moyen=
        """
            {
              "id": "AAC2023A00",
              "partitions": [{
                "id": 99,
                "communications" :[
                  {
                    "moyenCommunication" : "email",
                    "phaseCommunication" : "ouverture",
                    "avecQuestionnairePapier" : false,
                    "protocole" : null,
                    "mode" : "web",
                    "typeModele" : "relance_libre"
                  }
                ]
              }]
            }
        """;

        final String context6_wrong_moyen=
        """
            {
              "id": "AAC2023A00",
              "partitions": [{
                "id": 99,
                "communications" :[
                  {
                    "moyenCommunication" : "courrier",
                    "phaseCommunication" : "ouverture",
                    "avecQuestionnairePapier" : false,
                    "protocole" : null,
                    "mode" : "web",
                    "typeModele" : "relance_libre"
                  }
                ]
              }]
            }
        """;
        final String context7_wrong_phase=
        """
            {
              "id": "AAC2023A00",
              "partitions": [{
                "id": 99,
                "communications" :[
                  {
                    "moyenCommunication" : "mail",
                    "phaseCommunication" : "relance",
                    "avecQuestionnairePapier" : false,
                    "protocole" : null,
                    "mode" : "web",
                    "typeModele" : "relance_libre"
                  }
                ]
              }]
            }
        """;
        List<String> contextErrorTestList=List.of(context1_no_part,context2_no_com,context3_no_moyen,context5_error_in_moyen,
                context6_wrong_moyen,context7_wrong_phase);
        for(String context : contextErrorTestList){
            //Precondition
            ProtoolsTestUtils.initContexteMockFromString(protoolsContext, context);
            //Run test
            assertThrows(BadContextIncorrectBPMNError.class, () -> meshuggahTask.execute(execution));
            Mockito.reset(protoolsContext);
        }
    }

    @Test
    @DisplayName("Test execute method - should work and make correct call to service")
    void execute_should_work_when_variables_OK() throws JsonProcessingException {
        //Precondition
        DelegateExecution execution = mock(DelegateExecution.class);
        lenient().when(execution.getProcessInstanceId()).thenReturn(dumyId);
        initContexteMockWithString(meshuggahContext_1part_Ok);

        Long partitionId=99L;
        when(execution.getVariable(VARNAME_CURRENT_PARTITION_ID, Long.class)).thenReturn(partitionId);
        when(execution.getVariable(VARNAME_PLATINE_CONTACT, JsonNode.class)).thenReturn(objectMapper.readTree(platineContactContent));
        //Run method under test
        assertDoesNotThrow(() -> meshuggahTask.execute(execution));

        //Post conditions
        MeshuggahComDetails expectedDetails = MeshuggahComDetails.builder()
                .campaignId("AAC2023A00")
                .phase("OUVERTURE")
                .medium("EMAIL")
                .mode("WEB")
                .partitioningId(partitionId.toString())
                .avecQuestionnaire(false)
                .protocol("DEFAULT")
                .operation("RELANCE_LIBRE")
                .build();
        ArgumentCaptor<MeshuggahComDetails> acDetails = ArgumentCaptor.forClass(MeshuggahComDetails.class);
        ArgumentCaptor<JsonNode> acBody = ArgumentCaptor.forClass(JsonNode.class);
        verify(meshuggahService,times(1)).postSendCommunication(acDetails.capture(),acBody.capture());
        MeshuggahComDetails details = acDetails.getValue();
        assertEquals(expectedDetails,details);
    }
}