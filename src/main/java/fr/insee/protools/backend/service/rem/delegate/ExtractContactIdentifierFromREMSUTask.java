package fr.insee.protools.backend.service.rem.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.exception.IncorrectSUBPMNError;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_DIRECTORYACCESS_ID_CONTACT;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_SURVEY_UNIT;

/**
 * This delegate will search for the identifiantCompte (internet identifier) in a REM SU
 * additional information.
 */
@Component
@Slf4j
public class ExtractContactIdentifierFromREMSUTask implements JavaDelegate {

    private static final String REM_ADDITIONALINFOS = "additionalInformations";
    private static final String REM_ADDITIONALINFOS_KEY = "key";
    private static final String REM_ADDITIONALINFOS_VALUE = "value";
    private static final String REM_ADDITIONALINFOS_KEY_IDENTIFIANTCOMPTE = "identifiantCompte";
    private static final String REM_REPOSITORY_ID = "repositoryId";
    private static final String REM_EXTERNAL_ID = "externalId";

    @Override
    public void execute(DelegateExecution execution) {
        //No need protools context ==> no checkContextOrThrow
        log.debug("ProcessInstanceId={} -  begin",execution.getProcessInstanceId());
        JsonNode remSUNode = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_REM_SURVEY_UNIT, JsonNode.class);
        JsonNode additionalInfoNode = remSUNode.path(REM_ADDITIONALINFOS);

        boolean found = false;
        if (additionalInfoNode!=null && additionalInfoNode.isArray()) {
            for (JsonNode jsonNode : additionalInfoNode) {
                String key = jsonNode.path(REM_ADDITIONALINFOS_KEY).asText();
                if(key.equalsIgnoreCase(REM_ADDITIONALINFOS_KEY_IDENTIFIANTCOMPTE)){
                    String idInternaute=jsonNode.path(REM_ADDITIONALINFOS_VALUE).asText();
                    execution.setVariableLocal(VARNAME_DIRECTORYACCESS_ID_CONTACT,idInternaute);
                    found=true;
                    break;
                }
            }
        }

        if(!found){
            String repositoryId = remSUNode.path(REM_REPOSITORY_ID).asText();
            String externalId = remSUNode.path(REM_EXTERNAL_ID).asText();
            String msg = String.format("No identifiantCompte found for REM repositoryId=%s - externalId=%s",repositoryId,externalId);
            log.error(msg);
            throw new IncorrectSUBPMNError(msg);
        }
        log.debug("ProcessInstanceId={} -  end", execution.getProcessInstanceId());
    }
}
