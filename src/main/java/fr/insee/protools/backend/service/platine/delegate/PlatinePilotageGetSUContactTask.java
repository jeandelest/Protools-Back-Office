package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.dto.platine.pilotage.contact.PlatineContactDto;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.platine.pilotage.PlatinePilotageService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.context.ContextConstants.CTX_CAMPAGNE_ID;
import static fr.insee.protools.backend.service.platine.utils.PlatineHelper.computePilotagePartitionID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlatinePilotageGetSUContactTask implements JavaDelegate, DelegateContextVerifier {

    private static final ObjectMapper objectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES,false);
    private final ContextService protoolsContext;
    private final PlatinePilotageService platinePilotageService;

    @Override
    public void execute(DelegateExecution execution) {
        //Contexte
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);
        String campainId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();
        //Process variables
        Long suId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_REM_SURVEY_UNIT_IDENTIFIER, Long.class);
        Long currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, Long.class);
        log.info("ProcessInstanceId={} - currentPartitionId={}  suId={} ",execution.getProcessInstanceId(), currentPartitionId,suId);

        //Get the Contact details from platine pilotage
        String platinePartitionId = computePilotagePartitionID(campainId,currentPartitionId);
        PlatineContactDto platineContactDto = platinePilotageService.getSUMainContact(suId, platinePartitionId);

        //STORE this info
        execution.getParent().setVariableLocal(VARNAME_PLATINE_CONTACT, objectMapper.valueToTree(platineContactDto));
        log.info("Got contact info :  platineContactDto.Identifier={}", platineContactDto.getIdentifier());
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        if(contextRootNode==null){
            return Set.of("Context is missing");
        }
        return DelegateContextVerifier.computeMissingChildrenMessages(Set.of(CTX_CAMPAGNE_ID),contextRootNode,getClass());
    }
}