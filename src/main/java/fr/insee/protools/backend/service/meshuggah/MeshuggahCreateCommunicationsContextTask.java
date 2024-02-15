package fr.insee.protools.backend.service.meshuggah;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.protools.backend.dto.meshuggah.MeshuggahComDetails;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.insee.protools.backend.service.context.ContextConstants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class MeshuggahCreateCommunicationsContextTask implements JavaDelegate, DelegateContextVerifier {

    private final ContextService protoolsContext;
    private final MeshuggahService meshuggahService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void execute(DelegateExecution execution) {
        //Contexte
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);
        String campainId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();

        log.info("ProcessInstanceId={} - campainId={}  begin",execution.getProcessInstanceId(), campainId);
        treatPartitions(campainId,contextRootNode);
        log.info("ProcessInstanceId={} - campainId={}  end",execution.getProcessInstanceId(), campainId);
    }


    private void treatPartitions(String campainId, JsonNode contextRootNode){
        //Partitions
        var partitionIterator =contextRootNode.path(CTX_PARTITIONS).elements();
        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            Long partitionId = partitionNode.path(CTX_PARTITION_ID).asLong();
            treatPartition(campainId,partitionId,contextRootNode,partitionNode);
        }
    }

    private void treatPartition(String campainId, Long partitionId,JsonNode contextRootNode,JsonNode partitionNode){
        log.trace("treatPartition partitionId={}",partitionId);
        //Treat every communication of this partition
        var communicationsIterator =partitionNode.path(CTX_PARTITION_COMMUNICATIONS).elements();
        while (communicationsIterator.hasNext()) {
            var communicationNode = communicationsIterator.next();
            JsonNode body = initBody(contextRootNode,communicationNode);
            MeshuggahComDetails meshuggahComDetails = MeshuggahUtils.computeMeshuggahComDetails(campainId,partitionId,communicationNode);
            meshuggahService.postCreateCommunication(meshuggahComDetails, body);
        }
    }



    private static JsonNode initBody(JsonNode contextRootNode, JsonNode communicationNode){

        //Meta données
        String logoPrestataire =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_LOGO_PRESTATAIRE).asText();
        String mailRespOperationnel =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_MAIL_RESP_OPERATIONNEL).asText();
        boolean isPrestataire =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_PRESTATAIRE).asBoolean();
        String responsableOperationnel =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_RESPONSABLE_OPERATIONNEL).asText();
        String responsableTraitement =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_RESPONSABLE_TRAITEMENT).asText();
        String srvcCollSignFunc =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_SRVC_COL_SIGN_FONCTION).asText();
        String srvcCollSignNom =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_SRVC_COL_SIGN_NOM).asText();
        String themeMieuxConnaitreMail =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_THEME_MIEUX_CONNAITRE_MAIL).asText();
        String urlEnq =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_URL_ENQUETE).asText();
        String boiteMailRetour =   contextRootNode.path(CTX_METADONNEES).path(CTX_META_MAIL_BOITE_RETOUR).asText();

        ObjectNode body = objectMapper.createObjectNode();
        body.put("Enq_ComplementConnexion",communicationNode.path(CTX_PARTITION_COMMUNICATION_COMPLEMENT_CONNEXION).asText());
        body.put("Enq_LogoPrestataire",logoPrestataire);
        body.put("Enq_MailRespOperationnel",mailRespOperationnel);
        body.put("Enq_Prestataire",isPrestataire?"oui":"non");
        body.put("Enq_RelanceLibreMailParagraphe1",communicationNode.path(CTX_PARTITION_COMMUNICATION_RELANCE_LIBRE_PARAGRAPHE1).asText());
        body.put("Enq_RelanceLibreMailParagraphe2",communicationNode.path(CTX_PARTITION_COMMUNICATION_RELANCE_LIBRE_PARAGRAPHE2).asText());
        body.put("Enq_RelanceLibreMailParagraphe3",communicationNode.path(CTX_PARTITION_COMMUNICATION_RELANCE_LIBRE_PARAGRAPHE3).asText());
        body.put("Enq_RelanceLibreMailParagraphe4",communicationNode.path(CTX_PARTITION_COMMUNICATION_RELANCE_LIBRE_PARAGRAPHE4).asText());
        body.put("Enq_RespOperationnel",responsableOperationnel);
        body.put("Enq_RespTraitement",responsableTraitement);
        body.put("Enq_ServiceCollecteurSignataireFonction",srvcCollSignFunc);
        body.put("Enq_ServiceCollecteurSignataireNom",srvcCollSignNom);
        body.put("Enq_ThemeMieuxConnaitreMail",themeMieuxConnaitreMail);
        body.put("Enq_UrlEnquete",urlEnq);
        body.put("Mail_BoiteRetour",boiteMailRetour);
        body.put("Mail_Objet",communicationNode.path(CTX_PARTITION_COMMUNICATION_OBJET_MAIL).asText());

        return body;
    }



    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        if (contextRootNode == null) {
            return Set.of("Context is missing");
        }
        Set<String> results = new HashSet<>();
        Set<String> requiredNodes =
                Set.of(
                        //Global & Campaign
                        CTX_CAMPAGNE_ID,CTX_METADONNEES,CTX_PARTITIONS
                );
        Set<String> requiredPartition =
                Set.of(CTX_PARTITION_ID,CTX_PARTITION_COMMUNICATIONS);

        Set<String> requiredMetadonnees =
                Set.of(CTX_META_LOGO_PRESTATAIRE,CTX_META_MAIL_RESP_OPERATIONNEL,CTX_META_PRESTATAIRE,CTX_META_RESPONSABLE_OPERATIONNEL,CTX_META_RESPONSABLE_TRAITEMENT,
                        CTX_META_SRVC_COL_SIGN_FONCTION,CTX_META_SRVC_COL_SIGN_NOM,CTX_META_THEME_MIEUX_CONNAITRE_MAIL,CTX_META_URL_ENQUETE,CTX_META_MAIL_BOITE_RETOUR
                );
        Set<String> requiredCommunication =
                Stream.of(CTX_PARTITION_COMMUNICATION_COMPLEMENT_CONNEXION,
                        CTX_PARTITION_COMMUNICATION_RELANCE_LIBRE_PARAGRAPHE1,
                        CTX_PARTITION_COMMUNICATION_RELANCE_LIBRE_PARAGRAPHE2,
                        CTX_PARTITION_COMMUNICATION_RELANCE_LIBRE_PARAGRAPHE3,
                        CTX_PARTITION_COMMUNICATION_RELANCE_LIBRE_PARAGRAPHE4,
                        CTX_PARTITION_COMMUNICATION_OBJET_MAIL
                        ).collect(Collectors.toSet());
        requiredCommunication.addAll(MeshuggahUtils.getCommunicationRequiredFields());

        results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredNodes, contextRootNode, getClass()));
        results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredMetadonnees,contextRootNode.path(CTX_METADONNEES),getClass()));

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
                //Verify medium enum
                String medium = communicationNode.path(CTX_PARTITION_COMMUNICATION_MOYEN).asText();
                if(! EnumUtils.isValidEnumIgnoreCase(MeshuggahUtils.MediumEnum.class, medium)){
                    results.add(DelegateContextVerifier.computeIncorrectEnumMessage(CTX_PARTITION_COMMUNICATION_MOYEN,medium, Arrays.toString(MeshuggahUtils.MediumEnum.values()),getClass()));
                }

                //Verify medium enum
                String phase = communicationNode.path(CTX_PARTITION_COMMUNICATION_PHASE).asText();
                if(! EnumUtils.isValidEnumIgnoreCase(MeshuggahUtils.PhaseEnum.class, phase)){
                    results.add(DelegateContextVerifier.computeIncorrectEnumMessage(CTX_PARTITION_COMMUNICATION_PHASE,medium, Arrays.toString(MeshuggahUtils.PhaseEnum.values()),getClass()));
                }
            }
        }
        return results;
    }

}