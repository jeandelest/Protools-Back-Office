package fr.insee.protools.backend.service.sabiane.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.MetadataConstants;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.campaign.MetadataValue;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.campaign.MetadataValueItem;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.campaign.MetadataVariables;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.common.platine_sabiane.QuestionnaireHelper;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.nomenclature.NomenclatureService;
import fr.insee.protools.backend.service.questionnaire_model.QuestionnaireModelService;
import fr.insee.protools.backend.service.sabiane.questionnaire.SabianeQuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static fr.insee.protools.backend.service.context.ContextConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SabianeQuestionnaireCreateContextTask implements JavaDelegate, DelegateContextVerifier {

    private final ContextService protoolsContext;
    private final ObjectMapper objectMapper;
    private final NomenclatureService nomenclatureService;
    private final QuestionnaireModelService questionnaireModelService;
    private final SabianeQuestionnaireService sabianeQuestionnaireService;

    @Override
    public void execute(DelegateExecution execution) {
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        //check context
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);
        log.info("ProcessInstanceId={} - campagne={}"
                ,execution.getProcessInstanceId(),contextRootNode.path(CTX_CAMPAGNE_CONTEXTE).asText());

        MetadataValue metadataDto = createMetadataDto(contextRootNode);
        QuestionnaireHelper.createQuestionnaire(contextRootNode,sabianeQuestionnaireService,nomenclatureService,
                questionnaireModelService,execution.getProcessInstanceId(),metadataDto);

        log.debug("ProcessInstanceId={}  end",execution.getProcessInstanceId());
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        if(contextRootNode==null){
            return Set.of(String.format("Class=%s : Context is missing ", this.getClass().getSimpleName()));
        }

        //Standard verifications (common between platine and sabiane)
        Set<String> missingNodes =
                QuestionnaireHelper.getCreateCtxContextErrors(contextRootNode);

        //Metadata used by sabiane
        Set<String> requiredMetadonnes =
                Set.of(CTX_META_LABEL_LONG_OPERATION);

        if (contextRootNode.get(CTX_METADONNEES) != null) {
            missingNodes.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredMetadonnes,contextRootNode.path(CTX_METADONNEES),getClass()));
        }

        return missingNodes;
    }

    MetadataValue createMetadataDto(JsonNode contextRootNode){
        JsonNode metadataNode = contextRootNode.path(CTX_METADONNEES);
        return MetadataValue.builder()
                .value(MetadataVariables.builder()
                        .variables(
                                List.of(
                                        new MetadataValueItem(MetadataConstants.Enq_LibelleEnquete,metadataNode.path(CTX_META_LABEL_LONG_OPERATION).asText())
                                        )
                        )
                        .inseeContext(contextRootNode.path(CTX_CAMPAGNE_CONTEXTE).asText())
                        .build())
                .build();
    }
}
