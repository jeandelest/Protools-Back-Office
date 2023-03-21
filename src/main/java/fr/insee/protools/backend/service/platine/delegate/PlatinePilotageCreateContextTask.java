package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.platine.pilotage.PlatinePilotageService;
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
    @Autowired PlatinePilotageService platinePilotageService;
    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin",execution.getProcessInstanceId());
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);
        var dtos = computeMetadataDtoForEachPartition(contextRootNode);

        for (MetadataDto dto:  dtos) {
            platinePilotageService.putMetadata(dto.getPartitioningDto().getId(),dto);
        }

        log.info("ProcessInstanceId={}  end",execution.getProcessInstanceId());

    }
    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        Set<String> results=new HashSet<>();
        Set<String> requiredNodes =
                Set.of(
                        //Global & Campaign
                        CTX_METADONNEE, CTX_CAMPAGNE_ID, CTX_CAMPAGNE_LABEL, CTX_SERIE_ID, CTX_ANNEE, CTX_PERIODE, CTX_PARTITIONS, CTX_PERIODICITE
                );
        Set<String> requiredMetadonnees =
                //For source/Serie
                Set.of(CTX_META_SERIE_LABEL_LONG, CTX_META_SERIE_LABEL_COURT, CTX_META_PORTAIL_MES_ENQUETE_OPERATION,
                        //For support
                        CTX_META_ASSITANCE_ID, CTX_META_ASSISTANCE_LABEL, CTX_META_ASSITANCE_TEL, CTX_META_ASSISTANCE_MAIL, CTX_META_ASSISTANCE_PAYS,
                        CTX_META_ASSISTANCE_NUMERO_VOIE, CTX_META_ASSISTANCE_NOM_VOIE, CTX_META_ASSISTANCE_COMMUNE, CTX_META_ASSISTANCE_CODE_POSTAL,
                        //For Owner
                        CTX_META_PROPRIETAIRE_ID, CTX_META_PROPRIETAIRE_LABEL, CTX_META_MINISTERE_TUTELLE, CTX_META_PROPRIETAIRE_LOGO,
                        //For Source/serie
                        //For Survey
                        CTX_META_LABEL_LONG_OPERATION, CTX_META_LABEL_COURT_OPERATION, CTX_META_OBJECTIFS_LONGS, CTX_META_OBJECTIFS_COURTS,
                        CTX_META_NUMERO_VISA, CTX_META_CNIS_URL, CTX_META_DIFFUSION_URL, CTX_META_NOTICE_URL, CTX_META_SPECIMENT_URL
                );
        Set<String> requiredPartition =
                Set.of(CTX_PARTITION_LABEL, CTX_PARTITION_DATE_DEBUT_COLLECTE, CTX_PARTITION_DATE_FIN_COLLECTE, CTX_PARTITION_DATE_RETOUR);
        results.addAll(computeMissingChildrenMessages(requiredNodes,contextRootNode,getClass()));
        results.addAll(computeMissingChildrenMessages(requiredMetadonnees,contextRootNode.path(CTX_METADONNEE),getClass()));

        var partitionIterator =contextRootNode.get(CTX_PARTITIONS).elements();
        //Partitions
        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            results.addAll(computeMissingChildrenMessages(requiredPartition,partitionNode,getClass()));
        }


        //Check value of PERIODE Enum
        if(! EnumUtils.isValidEnum(PeriodEnum.class, contextRootNode.path(CTX_PERIODE).asText())){
            results.add(computeIncorrectMessage(CTX_PERIODE,"Incorrect enum value. Expected one of "+ Arrays.toString(PeriodEnum.values()),getClass()));
        }
        //Check value of PERIODICITE Enum
        if(! EnumUtils.isValidEnum(PeriodicityEnum.class, contextRootNode.path(CTX_PERIODICITE).asText())){
            results.add(computeIncorrectMessage(CTX_PERIODICITE,"Incorrect enum value. Expected one of "+ Arrays.toString(PeriodEnum.values()),getClass()));
        }
        return results;
    }

    private static Set<MetadataDto> computeMetadataDtoForEachPartition(JsonNode contextRootNode) {

        Set<MetadataDto> result = new HashSet<>();
        String campainId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();
        //TODO : ici on gère différentes partitions
        //Get the list of partitions
        var partitionIterator =contextRootNode.get(CTX_PARTITIONS).elements();

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
                .id(CTX_META_ASSITANCE_ID)
                .label(CTX_META_ASSISTANCE_LABEL)
                .phoneNumber(CTX_META_ASSITANCE_TEL)
                .mail(CTX_META_ASSISTANCE_MAIL)
                .countryName(CTX_META_ASSISTANCE_PAYS)
                .streetNumber(CTX_META_ASSISTANCE_NUMERO_VOIE)
                .streetName(CTX_META_ASSISTANCE_NOM_VOIE)
                .city(CTX_META_ASSISTANCE_COMMUNE)
                .zipCode(CTX_META_ASSISTANCE_CODE_POSTAL)
                .build();
    }

    private static OwnerDto computeOwnerDto(JsonNode contextRootNode) {
        String idProprietaire = contextRootNode.path(CTX_META_PROPRIETAIRE_ID).asText();
        String labelProprietaire = contextRootNode.path(CTX_META_PROPRIETAIRE_LABEL).asText();
        String ministereTutelle = contextRootNode.path(CTX_META_MINISTERE_TUTELLE).asText();
        String logoProprietaire = contextRootNode.path(CTX_META_PROPRIETAIRE_LOGO).asText();
        return OwnerDto.builder()
                .id(idProprietaire)
                .label(labelProprietaire)
                .ministry(ministereTutelle)
                .logo(logoProprietaire)
                .build();
    }

    private static SourceDto computeSourceDto(JsonNode contextRootNode) {
        String serieId = contextRootNode.path(CTX_SERIE_ID).asText();
        String labelLongSerie = contextRootNode.path(CTX_METADONNEE).path(CTX_META_SERIE_LABEL_LONG).asText();
        String labelCourtSerie = contextRootNode.path(CTX_METADONNEE).path(CTX_META_SERIE_LABEL_COURT).asText();
        PeriodicityEnum periodicite = PeriodicityEnum.valueOf(contextRootNode.path(CTX_PERIODICITE).asText());
        boolean mandatoryMySurveys = contextRootNode.path(CTX_METADONNEE).path(CTX_META_PORTAIL_MES_ENQUETE_OPERATION).asBoolean();
        
        return SourceDto.builder()
                .id(serieId)
                .longWording(labelLongSerie)
                .shortWording(labelCourtSerie)
                .periodicity(periodicite)
                .mandatoryMySurveys(mandatoryMySurveys)
                .build();
    }

    private static SurveyDto computeSurveyDto(JsonNode contextRootNode) {
        String id = contextRootNode.path(CTX_OPERATION_ID).asText();
        String serieId = contextRootNode.path(CTX_SERIE_ID).asText();
        int year = contextRootNode.path(CTX_ANNEE).asInt();
        String labelLongOperation = contextRootNode.path(CTX_META_LABEL_LONG_OPERATION).asText();
        String labelCourtOperation = contextRootNode.path(CTX_META_LABEL_COURT_OPERATION).asText();
        String labelLongObjectifs = contextRootNode.path(CTX_META_OBJECTIFS_LONGS).asText();
        String labelCourtObjectifs = contextRootNode.path(CTX_META_OBJECTIFS_COURTS).asText();
        String numeroVisa = contextRootNode.path(CTX_META_NUMERO_VISA).asText();
        String cnisUrl = contextRootNode.path(CTX_META_CNIS_URL).asText();
        String diffusionUrl = contextRootNode.path(CTX_META_DIFFUSION_URL).asText();
        String noticeUrl = contextRootNode.path(CTX_META_NOTICE_URL).asText();
        String specimenUrl = contextRootNode.path(CTX_META_SPECIMENT_URL).asText();
        //boolean mandatory = contextRootNode.path(CARACTERE_OBLIGATOIRE).asBoolean();
        return SurveyDto.builder()
                .id(id)
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
                .id(partitionNode.path(CTX_PARTITION_ID).asText())
                .campaignId(campaignId)
                .label(partitionNode.path(CTX_PARTITION_LABEL).asText())
                .openingDate(partitionNode.path(CTX_PARTITION_DATE_DEBUT_COLLECTE).asText())
                .closingDate(partitionNode.path(CTX_PARTITION_DATE_FIN_COLLECTE).asText())
                .returnDate(partitionNode.path(CTX_PARTITION_DATE_RETOUR).asText())
                .build();
    }

    private static CampaignDto computeCampaignDto(JsonNode contextRootNode) {
        String campaignId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();
        String serieId = contextRootNode.path(CTX_SERIE_ID).asText();
        int year = contextRootNode.path(CTX_ANNEE).asInt();
        String campaignLabel = contextRootNode.path(CTX_CAMPAGNE_LABEL).asText();
        PeriodEnum period = PeriodEnum.valueOf(contextRootNode.path(CTX_PERIODE).asText());
        return CampaignDto.builder()
                .id(campaignId)
                .surveyId(serieId)
                .year(year)
                .campaignWording(campaignLabel)
                .period(period)
                .build();
    }
}