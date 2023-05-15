package fr.insee.protools.backend.service.sabiane.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.context.ContextServiceImpl;
import fr.insee.protools.backend.service.context.exception.BadContextDateTimeParseException;
import fr.insee.protools.backend.service.sabiane.pilotage.SabianePilotageService;
import fr.insee.protools.backend.service.sabiane.pilotage.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

import static fr.insee.protools.backend.service.context.ContextConstants.*;

@Slf4j
@Component
public class SabianePilotageCreateContextTask implements JavaDelegate, DelegateContextVerifier {

    private static final String REFERENT_PRIMARY = "PRIMARY";
    private static final String REFERENT_SECONDARY = "SECONDARY";
    @Autowired ContextService protoolsContext;
    @Autowired SabianePilotageService sabianePilotageService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin",execution.getProcessInstanceId());
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);

        CampaignContextDto dto = computeCampaignContextDto(contextRootNode);
        sabianePilotageService.postCampaign(dto);

        log.info("ProcessInstanceId={}  end",execution.getProcessInstanceId());
    }

    private CampaignContextDto computeCampaignContextDto(JsonNode contextRootNode) {

        String campaignId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();
        String campaignLabel = contextRootNode.path(CTX_CAMPAGNE_LABEL).asText();
        String campaignAssistanceMail = contextRootNode.path(CTX_METADONNEES).path(CTX_META_ASSISTANCE_NIVO2_MAIL).asText();

        //TODO : vérifier les valeurs dans le check de la conf
        String essaisContact = contextRootNode.path(CTX_METADONNEES).path(CTX_META_ESSAIS_CONTACT).asText();
        ContactAttemptConfiguration contactAttemptConfiguration = ContactAttemptConfiguration.valueOfLabel(essaisContact);

        String bilanContact = contextRootNode.path(CTX_METADONNEES).path(CTX_META_BILAN_CONTACT).asText();
        ContactOutcomeConfiguration contactOutcomeConfiguration = ContactOutcomeConfiguration.valueOfLabel(bilanContact);

        String reperage = contextRootNode.path(CTX_METADONNEES).path(CTX_META_REPERAGE).asText();
        IdentificationConfiguration identificationConfiguration = IdentificationConfiguration.valueOfLabel(reperage);

        List<VisibilityContextDto> visibilityContextDtos = computeVisibilities(contextRootNode);
        List<ReferentDto> referents = computeReferents(contextRootNode);

        return CampaignContextDto.builder()
                .campaign(campaignId)
                .campaignLabel(campaignLabel)
                .visibilities(visibilityContextDtos)
                .referents(referents)
                .email(campaignAssistanceMail)
                .identificationConfiguration(identificationConfiguration)
                .contactOutcomeConfiguration(contactOutcomeConfiguration)
                .contactAttemptConfiguration(contactAttemptConfiguration)
                .build();
    }

    private List<ReferentDto> computeReferents(JsonNode contextRootNode) {
        JsonNode referentsPrincipauxNode  = contextRootNode.path(CTX_METADONNEES).path(CTX_META_REFERENTS_PRINCIPAUX);
        JsonNode referentsSecondairesNode = contextRootNode.path(CTX_METADONNEES).path(CTX_META_REFERENTS_SECONDAIRES);

        List<ReferentDto> result = new ArrayList<>(getReferents(referentsPrincipauxNode.elements(), true));
        result.addAll(getReferents(referentsSecondairesNode.elements(),false));
        return result;
    }

    private List<ReferentDto> getReferents(Iterator<JsonNode> refIterator, boolean isPrincipal){
        List<ReferentDto> result = new ArrayList<>();
        while(refIterator.hasNext()){
            var referentNode = refIterator.next();
            String prenom = referentNode.path(CTX_META_REFERENT_PRENOM).asText();
            String nom = referentNode.path(CTX_META_REFERENT_NOM).asText();
            String telephone = referentNode.path(CTX_META_REFERENT_TELEPHONE).asText();
            String role = (isPrincipal)? REFERENT_PRIMARY : REFERENT_SECONDARY;
            result.add(ReferentDto.builder()
                    .firstName(prenom)
                    .lastName(nom)
                    .phoneNumber(telephone)
                    .role(role)
                    .build());
        }
        return result;
    }

    private List<VisibilityContextDto> computeVisibilities(JsonNode contextRootNode)
    {
        List<VisibilityContextDto> result = new ArrayList<>();
        //need to compute mins/maxs of partitions
        var partitionIterator =contextRootNode.path(CTX_PARTITIONS).elements();

        Instant maxDateFinCollecte=null;
        Instant minDateDebutCollecte=null;
        Instant maxDateFinTraitement=null;
        Instant minDateDebutReperage=null;
        Instant minDateDebutVisibiliteEnqueteur=null;
        Instant maxDateDebutVisibiliteGestionnaire=null;

        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            Instant dateFinCollecte = ContextServiceImpl.getInstantFromPartition(partitionNode,CTX_PARTITION_DATE_FIN_COLLECTE);
            Instant dateDebutCollecte = ContextServiceImpl.getInstantFromPartition(partitionNode,CTX_PARTITION_DATE_DEBUT_COLLECTE);
            Instant dateFinTraitement = ContextServiceImpl.getInstantFromPartition(partitionNode,CTX_PARTITION_SABIANE_DATE_FIN_TRAITEMENT);
            Instant dateDebutReperage = ContextServiceImpl.getInstantFromPartition(partitionNode,CTX_PARTITION_SABIANE_DATE_DEBUT_REPERAGE);
            Instant dateDebutVisibiliteEnqueteur = ContextServiceImpl.getInstantFromPartition(partitionNode,CTX_PARTITION_SABIANE_DATE_DEBUT_VISIBILITE_ENQUETEUR);
            Instant dateDebutVisibiliteGestionnaire = ContextServiceImpl.getInstantFromPartition(partitionNode,CTX_PARTITION_SABIANE_DATE_DEBUT_VISIBILITE_GESTIONNAIRE);

            if(maxDateFinCollecte==null || maxDateFinCollecte.isBefore(dateFinCollecte)){
                maxDateFinCollecte=dateFinCollecte;
            }

            if(minDateDebutCollecte==null || minDateDebutCollecte.isAfter(dateDebutCollecte)){
                minDateDebutCollecte=dateDebutCollecte;
            }

            if(maxDateFinTraitement==null || maxDateFinTraitement.isBefore(dateFinTraitement)){
                maxDateFinTraitement=dateFinTraitement;
            }

            if(minDateDebutReperage==null || minDateDebutReperage.isAfter(dateDebutReperage)){
                minDateDebutReperage=dateDebutReperage;
            }

            if(minDateDebutVisibiliteEnqueteur==null || minDateDebutVisibiliteEnqueteur.isAfter(dateDebutVisibiliteEnqueteur)){
                minDateDebutVisibiliteEnqueteur=dateDebutVisibiliteEnqueteur;
            }

            if(maxDateDebutVisibiliteGestionnaire==null || maxDateDebutVisibiliteGestionnaire.isBefore(dateDebutVisibiliteGestionnaire)){
                maxDateDebutVisibiliteGestionnaire=dateDebutVisibiliteGestionnaire;
            }
        }

        var siteGestionIterator = contextRootNode.path(CTX_METADONNEES).path(CTX_META_SITES_GESTION).elements();
        while(siteGestionIterator.hasNext()){
            JsonNode siteGestion = siteGestionIterator.next();
            VisibilityContextDto visibilityContextDto = VisibilityContextDto.builder()
                    .collectionEndDate(maxDateFinCollecte.toEpochMilli())
                    .collectionStartDate(minDateDebutCollecte.toEpochMilli())
                    .endDate(maxDateFinTraitement.toEpochMilli())
                    .identificationPhaseStartDate(minDateDebutReperage.toEpochMilli())
                    .interviewerStartDate(minDateDebutVisibiliteEnqueteur.toEpochMilli())
                    .managementStartDate(maxDateDebutVisibiliteGestionnaire.toEpochMilli())
                    .organizationalUnit(siteGestion.asText())
                    .build();
            result.add(visibilityContextDto);
        }
        return result;
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        if(contextRootNode==null){
            return Set.of("Context is missing");
        }
        Set<String> results=new HashSet<>();
        Set<String> requiredNodes =
                Set.of(
                        //Global & Campaign
                        CTX_METADONNEES, CTX_CAMPAGNE_ID, CTX_CAMPAGNE_LABEL, CTX_CAMPAGNE_CONTEXTE
                );
        Set<String> requiredMetadonnees =
                Set.of(
                        CTX_META_LABEL_COURT_OPERATION,CTX_META_LABEL_LONG_OPERATION,
                        CTX_META_ASSISTANCE_NIVO2_MAIL,
                        CTX_META_REPERAGE,CTX_META_ESSAIS_CONTACT,CTX_META_BILAN_CONTACT,
                        CTX_META_REFERENTS_PRINCIPAUX, CTX_META_REFERENTS_SECONDAIRES,
                        //arraya with at least one element
                        CTX_META_SITES_GESTION
                );
        Set<String> requiredReferent =
                Set.of(CTX_META_REFERENT_NOM,CTX_META_REFERENT_PRENOM,CTX_META_REFERENT_TELEPHONE);
        Set<String> requiredPartitionDates=
                Set.of(CTX_PARTITION_DATE_DEBUT_COLLECTE, CTX_PARTITION_DATE_FIN_COLLECTE,
                        //specific sabiane
                        CTX_PARTITION_SABIANE_DATE_DEBUT_VISIBILITE_GESTIONNAIRE,
                        CTX_PARTITION_SABIANE_DATE_DEBUT_VISIBILITE_ENQUETEUR,
                        CTX_PARTITION_SABIANE_DATE_DEBUT_REPERAGE,
                        CTX_PARTITION_SABIANE_DATE_FIN_TRAITEMENT);
        //For a partition, we need all the dates + the ID + label
        Set<String> requiredPartition =
                new HashSet<>(requiredPartitionDates);
                requiredPartition.add(CTX_PARTITION_ID);
                requiredPartition.add(CTX_PARTITION_LABEL);


        results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredNodes,contextRootNode,getClass()));
        results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredMetadonnees,contextRootNode.path(CTX_METADONNEES),getClass()));

        var referentIterator=contextRootNode.path(CTX_META_REFERENTS_PRINCIPAUX).elements();
        while (referentIterator.hasNext()) {
            var referentNode = referentIterator.next();
            results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredReferent,referentNode,getClass()));
        }

        referentIterator=contextRootNode.path(CTX_META_REFERENTS_SECONDAIRES).elements();
        while (referentIterator.hasNext()) {
            var referentNode = referentIterator.next();
            results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredReferent,referentNode,getClass()));
        }

        var partitionIterator =contextRootNode.path(CTX_PARTITIONS).elements();
        //Partitions
        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredPartition,partitionNode,getClass()));

            for(var dateNode : requiredPartitionDates) {
                //Check the date format
                try {
                    ContextServiceImpl.getInstantFromPartition(partitionNode, dateNode);
                } catch (BadContextDateTimeParseException e) {
                    results.add(e.getMessage());
                }
            }
        }

        //Check sites de gestion (an array with at least one element)
        JsonNode sitesGestionNode= contextRootNode.path(CTX_METADONNEES).path(CTX_META_SITES_GESTION);
        if(!sitesGestionNode.isArray()){
            results.add(DelegateContextVerifier.computeIncorrectMessage(CTX_META_SITES_GESTION," should be an array", getClass()));
        }
        else if(sitesGestionNode.isEmpty()){
            results.add(DelegateContextVerifier.computeIncorrectMessage(CTX_META_SITES_GESTION," should not be an empty an array", getClass()));
        }
        else{
            var iter = sitesGestionNode.elements();
            while (iter.hasNext()){
                var value = iter.next();
                if(!value.isTextual()){
                    results.add(DelegateContextVerifier.computeIncorrectMessage(CTX_META_SITES_GESTION," contains non textual values", getClass()));
                    break;
                }
            }
        }

        //Check value of enums
        try {
            String essaisContact = contextRootNode.path(CTX_METADONNEES).path(CTX_META_ESSAIS_CONTACT).asText();
            ContactAttemptConfiguration.valueOfLabel(essaisContact);
        }catch (IllegalArgumentException e){
            results.add(DelegateContextVerifier.computeIncorrectEnumMessage(CTX_META_ESSAIS_CONTACT,contextRootNode.path(CTX_METADONNEES).path(CTX_META_ESSAIS_CONTACT).asText(),Arrays.toString(ContactAttemptConfiguration.labels()),getClass()));
        }

        try {
            String bilanContact = contextRootNode.path(CTX_METADONNEES).path(CTX_META_BILAN_CONTACT).asText();
            ContactOutcomeConfiguration.valueOfLabel(bilanContact);
        }catch (IllegalArgumentException e){
            results.add(DelegateContextVerifier.computeIncorrectEnumMessage(CTX_META_BILAN_CONTACT,contextRootNode.path(CTX_METADONNEES).path(CTX_META_BILAN_CONTACT).asText(),Arrays.toString(ContactOutcomeConfiguration.labels()),getClass()));
        }

        try {
            String reperage = contextRootNode.path(CTX_METADONNEES).path(CTX_META_REPERAGE).asText();
            IdentificationConfiguration.valueOfLabel(reperage);
        }catch (IllegalArgumentException e){
            results.add(DelegateContextVerifier.computeIncorrectEnumMessage(CTX_META_REPERAGE,contextRootNode.path(CTX_METADONNEES).path(CTX_META_REPERAGE).asText(),Arrays.toString(IdentificationConfiguration.labels()),getClass()));
        }

        return results;

    }
}
