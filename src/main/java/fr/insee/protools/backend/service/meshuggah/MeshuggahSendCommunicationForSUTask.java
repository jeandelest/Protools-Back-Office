package fr.insee.protools.backend.service.meshuggah;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.exception.IncorrectPlatineContactError;
import fr.insee.protools.backend.service.meshuggah.dto.MeshuggahComDetails;
import fr.insee.protools.backend.service.platine.pilotage.dto.contact.PlatineContactDto;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.context.ContextConstants.*;
import static fr.insee.protools.backend.service.utils.ContextUtils.getCurrentPartitionNode;

@Component
@Slf4j
public class MeshuggahSendCommunicationForSUTask implements JavaDelegate, DelegateContextVerifier {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired ContextService protoolsContext;
    @Autowired MeshuggahService meshuggahService;

    private static JsonNode initBody(PlatineContactDto platineContact) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("email", platineContact.getEmail());
        body.put("Ue_CalcIdentifiant", platineContact.getIdentifier());
        return body;
    }

    @Override
    public void execute(DelegateExecution execution) {
        //Contexte
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log, execution.getProcessInstanceId(), contextRootNode);
        String campainId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();

        //Variables
        Long currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, Long.class);
        JsonNode contactNode = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_PLATINE_CONTACT, JsonNode.class);

        String mediumStr = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_COMMUNICATION_MEDIUM, String.class);
        String phaseStr = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_COMMUNICATION_PHASE, String.class);
        MeshuggahUtils.MediumEnum medium = MeshuggahUtils.MediumEnum.fromCtxValue(mediumStr);
        MeshuggahUtils.PhaseEnum phase = MeshuggahUtils.PhaseEnum.fromCtxValue(phaseStr);

        PlatineContactDto platineContactDto;
        try {
            platineContactDto = objectMapper.treeToValue(contactNode, PlatineContactDto.class);
        } catch (JsonProcessingException e) {
            throw new IncorrectPlatineContactError("Error while parsing the json retrieved for platine contact : " + contactNode, contactNode, e);
        }
        //TODO :  hack avec le mail de marc
        platineContactDto.setEmail("marc.berger@insee.fr");


        //Get current partition from contexte
        JsonNode currentPartitionNode = getCurrentPartitionNode(contextRootNode, currentPartitionId);
        log.info("ProcessInstanceId={} - campainId={}  begin", execution.getProcessInstanceId(), campainId);

        JsonNode communicationNode = MeshuggahUtils.getCommunication(currentPartitionNode, medium, phase);
        JsonNode body = initBody(platineContactDto);

        MeshuggahComDetails meshuggahComDetails = MeshuggahUtils.computeMeshuggahComDetails(campainId, currentPartitionId, communicationNode);
        meshuggahService.postSendCommunication(meshuggahComDetails, body);

        log.info("ProcessInstanceId={} - campainId={}  end", execution.getProcessInstanceId(), campainId);
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        if (contextRootNode == null) {
            return Set.of("Context is missing");
        }
        Set<String> results = new HashSet<>();
        Set<String> requiredNodes = Set.of(
                //Global & Campaign
                CTX_CAMPAGNE_ID, CTX_PARTITIONS);
        Set<String> requiredPartition = Set.of(CTX_PARTITION_ID, CTX_PARTITION_COMMUNICATIONS);

        Set<String> requiredCommunication = MeshuggahUtils.getCommunicationRequiredFields();
        results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredNodes, contextRootNode, getClass()));

        //Partitions
        var partitionIterator = contextRootNode.path(CTX_PARTITIONS).elements();
        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            var missingChildren = DelegateContextVerifier.computeMissingChildrenMessages(requiredPartition, partitionNode, getClass());
            if (!missingChildren.isEmpty()) {
                results.addAll(missingChildren);
                continue;
            }

            //Communications of the partition
            var communicationIterator = partitionNode.path(CTX_PARTITION_COMMUNICATIONS).elements();
            while (communicationIterator.hasNext()) {
                var communicationNode = communicationIterator.next();
                var missingChildrenCom = DelegateContextVerifier.computeMissingChildrenMessages(requiredCommunication, communicationNode, getClass());
                if (!missingChildrenCom.isEmpty()) {
                    results.addAll(missingChildrenCom);
                }
            }
        }
        return results;
    }


}

