package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.platine.pilotage.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static fr.insee.protools.backend.service.context.ContextConstants.*;

@Slf4j
@Component
public class PlatinePilotageCreateContextTask implements JavaDelegate, DelegateContextVerifier {

    @Autowired ContextService protoolsContext;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin",execution.getProcessInstanceId());
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);
        var dtos = computeMetadataDtoForEachPartition(contextRootNode);
        log.info("ProcessInstanceId={}  end",execution.getProcessInstanceId());

    }
    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        Set<String> results=new HashSet<>();
        Set<String> requiredNodes =
                Set.of(
                        //Global & Campaign
                        CTX_METADONNEE,ID,SERIE_ID,ANNEE,PERIODE,PARTITIONS,LABEL,PERIODICITE
                );
        Set<String> requiredMetadonnees =
                //For source/Serie
                Set.of(LABEL_LONG_SERIE,LABEL_COURT_SERIE,PORTAIL_MES_ENQUETE_OPERATION,
                        //For support
                        ID_ASSITANCE,LABEL_ASSITANCE,TEL_ASSITANCE,MAIL_ASSITANCE,PAYS_ASSITANCE,
                        NUMERO_VOIE_ASSITANCE,NOM_VOIE_ASSITANCE,COMMUNE_ASSITANCE,CODE_POSTAL_ASSITANCE,
                        //For Owner
                        ID_PROPRIETAIRE,LABEL_PROPRIETAIRE,MINISTERE_TUTELLE,LOGO_PROPRIETAIRE,
                        //For Source/serie
                        //For Survey
                        LABEL_LONG_OPERATION,LABEL_COURT_OPERATION,OBJECTIFS_LONGS,OBJECTIFS_COURTS,
                        NUMERO_VISA,CNIS_URL,DIFFUSION_URL,NOTICE_URL,SPECIMENT_URL
                );
        Set<String> requiredPartition =
                Set.of(LABEL_PARTITION,DATE_DEBUT_COLLECTE,DATE_FIN_COLLECTE,DATE_RETOUR);
        results.addAll(computeMissingChildrenMessages(requiredNodes,contextRootNode,getClass()));
        results.addAll(computeMissingChildrenMessages(requiredMetadonnees,contextRootNode.path(CTX_METADONNEE),getClass()));

        var partitionIterator =contextRootNode.get(PARTITIONS).elements();
        //Partitions
        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            results.addAll(computeMissingChildrenMessages(requiredPartition,partitionNode,getClass()));
        }


        //Check value of PERIODE Enum
        if(! EnumUtils.isValidEnum(PeriodEnum.class, contextRootNode.path(PERIODE).asText())){
            results.add(computeIncorrectMessage(PERIODE,"Incorrect enum value. Expected one of "+ Arrays.toString(PeriodEnum.values()),getClass()));
        }
        //Check value of PERIODICITE Enum
        if(! EnumUtils.isValidEnum(PeriodicityEnum.class, contextRootNode.path(PERIODICITE).asText())){
            results.add(computeIncorrectMessage(PERIODICITE,"Incorrect enum value. Expected one of "+ Arrays.toString(PeriodEnum.values()),getClass()));
        }
        return results;
    }

    private static Set<MetadataDto> computeMetadataDtoForEachPartition(JsonNode contextRootNode) {

        Set<MetadataDto> result = new HashSet<>();
        String campainId = contextRootNode.path(ID).asText();
        //TODO : ici on gère différentes partitions
        //Get the list of partitions
        var partitionIterator =contextRootNode.get(PARTITIONS).elements();

        //These parts are always the same
        CampaignDto campaignDto = computeCampaignDto(contextRootNode);
        SurveyDto surveyDto = computeSurveyDto(contextRootNode);
        SourceDto sourceDto = computeSourceDto(contextRootNode);
        OwnerDto ownerDto = computeOwnerDto(contextRootNode);
        SupportDto supportDto = computeSupportDto(contextRootNode);


        //Partition part
        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            PartitioningDto partitioningDto = computePartitioningDto(partitionNode, campainId);
            MetadataDto metadataDto = new MetadataDto(partitioningDto, campaignDto, surveyDto, sourceDto, ownerDto, supportDto);
            result.add(metadataDto);
        }
        return result;
    }

    private static SupportDto computeSupportDto(JsonNode contextRootNode) {
        return SupportDto.builder()
                .id(ID_ASSITANCE)
                .label(LABEL_ASSITANCE)
                .phoneNumber(TEL_ASSITANCE)
                .mail(MAIL_ASSITANCE)
                .countryName(PAYS_ASSITANCE)
                .streetNumber(NUMERO_VOIE_ASSITANCE)
                .streetName(NOM_VOIE_ASSITANCE)
                .city(COMMUNE_ASSITANCE)
                .zipCode(CODE_POSTAL_ASSITANCE)
                .build();
    }

    private static OwnerDto computeOwnerDto(JsonNode contextRootNode) {
        String idProprietaire = contextRootNode.path(ID_PROPRIETAIRE).asText();
        String labelProprietaire = contextRootNode.path(LABEL_PROPRIETAIRE).asText();
        String ministereTutelle = contextRootNode.path(MINISTERE_TUTELLE).asText();
        String logoProprietaire = contextRootNode.path(LOGO_PROPRIETAIRE).asText();
        return OwnerDto.builder()
                .id(idProprietaire)
                .label(labelProprietaire)
                .ministry(ministereTutelle)
                .logo(logoProprietaire)
                .build();
    }

    private static SourceDto computeSourceDto(JsonNode contextRootNode) {
        String serieId = contextRootNode.path(SERIE_ID).asText();
        String labelLongSerie = contextRootNode.path(CTX_METADONNEE).path(LABEL_LONG_SERIE).asText();
        String labelCourtSerie = contextRootNode.path(CTX_METADONNEE).path(LABEL_COURT_SERIE).asText();
        PeriodicityEnum periodicite = PeriodicityEnum.valueOf(contextRootNode.path(PERIODICITE).asText());
        boolean mandatoryMySurveys = contextRootNode.path(CTX_METADONNEE).path(PORTAIL_MES_ENQUETE_OPERATION).asBoolean();
        
        return SourceDto.builder()
                .id(serieId)
                .longWording(labelLongSerie)
                .shortWording(labelCourtSerie)
                .periodicity(periodicite)
                .mandatoryMySurveys(mandatoryMySurveys)
                .build();
    }

    private static SurveyDto computeSurveyDto(JsonNode contextRootNode) {
        String campaignId = contextRootNode.path(ID).asText();
        String serieId = contextRootNode.path(SERIE_ID).asText();
        int year = contextRootNode.path(ANNEE).asInt();
        String campaignLabel = contextRootNode.path(LABEL).asText();
        String labelLongOperation = contextRootNode.path(LABEL_LONG_OPERATION).asText();
        String labelCourtOperation = contextRootNode.path(LABEL_COURT_OPERATION).asText();
        String labelLongObjectifs = contextRootNode.path(OBJECTIFS_LONGS).asText();
        String labelCourtObjectifs = contextRootNode.path(OBJECTIFS_COURTS).asText();
        String numeroVisa = contextRootNode.path(NUMERO_VISA).asText();
        String cnisUrl = contextRootNode.path(CNIS_URL).asText();
        String diffusionUrl = contextRootNode.path(DIFFUSION_URL).asText();
        String noticeUrl = contextRootNode.path(NOTICE_URL).asText();
        String specimenUrl = contextRootNode.path(SPECIMENT_URL).asText();
        //boolean mandatory = contextRootNode.path(CARACTERE_OBLIGATOIRE).asBoolean();
        return SurveyDto.builder()
                .id(campaignId)
                .sourceId(serieId)
                .year(year)
                .sampleSize(0)
                .longWording(labelLongOperation)
                .shortWording(labelCourtOperation)
                .shortObjectives(labelCourtObjectifs)
                .longObjectives(labelLongObjectifs)
                .visaNumber(numeroVisa)
                .cnisUrl(cnisUrl)
                .diffusionUrl(diffusionUrl)
                .noticeUrl(noticeUrl)
                .specimenUrl(specimenUrl)
                .communication("")
 //               .mandatory(mandatory)
                .build();
    }


    private static PartitioningDto computePartitioningDto(JsonNode partitionNode, String campaignId) {
        return PartitioningDto.builder()
                .id(partitionNode.path(ID).asText())
                .campaignId(campaignId)
                .label(partitionNode.path(LABEL_PARTITION).asText())
                .openingDate(partitionNode.path(DATE_DEBUT_COLLECTE).asText())
                .closingDate(partitionNode.path(DATE_FIN_COLLECTE).asText())
                .returnDate(partitionNode.path(DATE_RETOUR).asText())
                .build();
    }

    private static CampaignDto computeCampaignDto(JsonNode contextRootNode) {
        String campaignId = contextRootNode.path(ID).asText();
        String serieId = contextRootNode.path(SERIE_ID).asText();
        int year = contextRootNode.path(ANNEE).asInt();
        String campaignLabel = contextRootNode.path(LABEL).asText();
        PeriodEnum period = PeriodEnum.valueOf(contextRootNode.path(PERIODE).asText());
        return CampaignDto.builder()
                .id(campaignId)
                .surveyId(serieId)
                .year(year)
                .campaignWording(campaignLabel)
                .period(period)
                .build();
    }
}