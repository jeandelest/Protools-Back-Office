package fr.insee.protools.backend.service.meshuggah;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.exception.IncorrectPlatineContactError;
import fr.insee.protools.backend.service.exception.IncorrectSUBPMNError;
import fr.insee.protools.backend.service.meshuggah.dto.MeshuggahComDetails;
import fr.insee.protools.backend.service.platine.pilotage.dto.contact.PlatineContactDto;
import fr.insee.protools.backend.service.rem.dto.REMSurveyUnitDto;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.context.ContextConstants.*;
import static fr.insee.protools.backend.service.utils.ContextUtils.getCurrentPartitionNode;

@Component
@Slf4j
public class MeshuggahSendCommunicationTask implements JavaDelegate, DelegateContextVerifier {

    @Autowired ContextService protoolsContext;
    @Autowired MeshuggahService meshuggahService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void execute(DelegateExecution execution) {
        //Contexte
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);
        String campainId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();

        //Variables
        Long currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, Long.class);
        JsonNode contactNode = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_PLATINE_CONTACT, JsonNode.class);
        PlatineContactDto platineContactDto;
        try {
            platineContactDto=objectMapper.treeToValue(contactNode,PlatineContactDto.class);
        } catch (JsonProcessingException e) {
            throw new IncorrectPlatineContactError("Error while parsing the json retrieved for platine contact : " + contactNode,contactNode, e);
        }

        //Get current partition from contexte
        JsonNode currentPartitionNode = getCurrentPartitionNode(contextRootNode, currentPartitionId);
        log.info("ProcessInstanceId={} - campainId={}  begin",execution.getProcessInstanceId(), campainId);

        //TODO :  Currently only send OPENING
        JsonNode communicationNode = getCommunication(currentPartitionNode,"mail","ouverture");
        //TODO :  hack avec le mail de marc
        platineContactDto.setEmail("marc.berger@insee.fr");
        JsonNode body = initBody(platineContactDto);
        MeshuggahComDetails meshuggahComDetails = MeshuggahUtils.computeMeshuggahComDetails(currentPartitionId,contextRootNode,communicationNode);
        meshuggahService.postSendCommunication(meshuggahComDetails, body);

        log.info("ProcessInstanceId={} - campainId={}  end",execution.getProcessInstanceId(), campainId);
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        return DelegateContextVerifier.super.getContextErrors(contextRootNode);
    }

    private static JsonNode initBody(PlatineContactDto platine_contact){
        ObjectNode body = objectMapper.createObjectNode();
        body.put("email",platine_contact.getEmail());
        body.put("Ue_CalcIdentifiant",platine_contact.getIdentifier());
        return body;
    }


    //Search for a communication in the contexte.
    public static JsonNode getCommunication(JsonNode partitionNode, String moyen, String phase) {
        JsonNode communicationNode=null;
        for (JsonNode subNode : partitionNode.path(CTX_PARTITION_COMMUNICATIONS)) {
            if (
                    subNode.has(CTX_PARTITION_COMMUNICATION_MOYEN) &&
                    subNode.has(CTX_PARTITION_COMMUNICATION_PHASE) &&
                    subNode.get(CTX_PARTITION_COMMUNICATION_MOYEN).asText().equalsIgnoreCase(moyen) &&
                    subNode.get(CTX_PARTITION_COMMUNICATION_PHASE).asText().equalsIgnoreCase(phase)
            ) {
                communicationNode = subNode;
                break;
            }
        }
        if (communicationNode == null) {
            throw new FlowableIllegalArgumentException(String.format("Commuinication moyen=[%s] - phase=[%s] not found ",
                    moyen,phase));
        }
        return communicationNode;
    }
}

