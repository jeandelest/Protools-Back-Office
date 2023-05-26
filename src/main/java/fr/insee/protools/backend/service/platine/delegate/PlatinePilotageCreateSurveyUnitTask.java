package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.context.enums.CampaignContextEnum;
import fr.insee.protools.backend.service.context.enums.PartitionTypeEchantillon;
import fr.insee.protools.backend.service.exception.IncorrectSUException;
import fr.insee.protools.backend.service.platine.pilotage.PlatinePilotageService;
import fr.insee.protools.backend.service.platine.pilotage.dto.PlatineAddressDto;
import fr.insee.protools.backend.service.platine.pilotage.dto.query.ContactAccreditationDto;
import fr.insee.protools.backend.service.platine.pilotage.dto.query.QuestioningWebclientDto;
import fr.insee.protools.backend.service.platine.pilotage.dto.questioning.PlatineQuestioningSurveyUnitDto;
import fr.insee.protools.backend.service.platine.utils.PlatineHelper;
import fr.insee.protools.backend.service.rem.dto.*;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.context.ContextConstants.*;
import static fr.insee.protools.backend.service.utils.ContextUtils.getCurrentPartitionNode;

@Slf4j
@Component
public class PlatinePilotageCreateSurveyUnitTask implements JavaDelegate, DelegateContextVerifier {


    @Autowired ContextService protoolsContext;
    @Autowired PlatinePilotageService platinePilotageService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CIVILITY_MONSIEUR = "Monsieur";
    private static final String CIVILITY_MADAME = "Madame";

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin", execution.getProcessInstanceId());
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        String campainId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();

        String currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_CURRENT_PARTITION_ID, String.class);
        JsonNode remSUNode = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_REM_SURVEY_UNIT, JsonNode.class);
        String idInternaute = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_SUGOI_CREATED_ID_INTERNAUTE, String.class);

        JsonNode currentPartitionNode = getCurrentPartitionNode(contextRootNode, currentPartitionId);

        //Create the platine DTO object
        QuestioningWebclientDto dto = computeQuestioningWebclientDto(remSUNode,currentPartitionNode,idInternaute,campainId);
        //Call service
        platinePilotageService.putQuestionings(dto);

        log.info("ProcessInstanceId={}  end",execution.getProcessInstanceId());
    }

    private static QuestioningWebclientDto computeQuestioningWebclientDto(JsonNode remSUNode, JsonNode currentPartitionNode,String idInternaute, String idCampagne) {
        REMSurveyUnitDto remSurveyUnitDto;
        try {
            remSurveyUnitDto = objectMapper.treeToValue(remSUNode, REMSurveyUnitDto.class);
        } catch (JsonProcessingException e) {
            throw new IncorrectSUException("Error while parsing the json retrieved from REM : " + VARNAME_REM_SURVEY_UNIT,remSUNode, e);
        }

        //Search for right contact
        PersonDto contact;
        //SU Logement
        if (currentPartitionNode.path(CTX_PARTITION_TYPE_ECHANTILLON).textValue().equalsIgnoreCase(PartitionTypeEchantillon.LOGEMENT.getAsString())) {
            //We use the "main" person (actually it is the Declarant)
            contact = remSurveyUnitDto.getPersons().stream()
                    .filter(personDto -> (Boolean.TRUE.equals(personDto.getMain())))
                    .findFirst().orElseThrow(() -> new IncorrectSUException("No main person found in SU [id="+remSurveyUnitDto.getRepositoryId()+"]", remSUNode));
        }
        //SU INDIVIDU
        else {
            contact = remSurveyUnitDto.getPersons().stream()
                    .filter(personDto -> (Boolean.TRUE.equals(personDto.getSurveyed())))
                    .findFirst().orElseThrow(() -> new IncorrectSUException("No surveyed person found in SU [id="+remSurveyUnitDto.getRepositoryId()+"]", remSUNode));
        }

        PlatineAddressDto platineAdress = computePlatineAdress(remSurveyUnitDto.getAddress());
        ContactAccreditationDto platineContact = convertREMPersonToPlatineContact(idInternaute, platineAdress, contact);

        return
                QuestioningWebclientDto.builder()
                        .idPartitioning(PlatineHelper.computePilotagePartitionID(idCampagne,currentPartitionNode.path(CTX_PARTITION_ID).toString()))
                        .modelName(currentPartitionNode.path(CTX_PARTITION_QUESTIONNAIRE_MODEL).asText())
                        .surveyUnit(computeSurveyUnitDto(remSurveyUnitDto))
                        .contacts(List.of(platineContact))
                        .build();
    }

    private static PlatineQuestioningSurveyUnitDto computeSurveyUnitDto(REMSurveyUnitDto remSurveyUnitDto) {

        return PlatineQuestioningSurveyUnitDto.builder()
                .idSu(remSurveyUnitDto.getRepositoryId().toString())
                .identificationCode("")
                .identificationName("")
                .address(computePlatineAdress(remSurveyUnitDto.getAddress()))
                .build();
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
                        CTX_CAMPAGNE_ID,CTX_CAMPAGNE_CONTEXTE, CTX_PARTITIONS
                );
        Set<String> requiredPartition =
                Set.of(CTX_PARTITION_ID,CTX_PARTITION_QUESTIONNAIRE_MODEL, CTX_PARTITION_TYPE_ECHANTILLON);


        results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredNodes,contextRootNode,getClass()));
        if (!contextRootNode.path(CTX_CAMPAGNE_CONTEXTE).asText().equalsIgnoreCase(CampaignContextEnum.HOUSEHOLD.getAsString())) {
            results.add(DelegateContextVerifier.computeIncorrectMessage(CTX_CAMPAGNE_CONTEXTE,"contexte for platine can only be "+CampaignContextEnum.HOUSEHOLD,getClass()));
        }

        //Maybe one day we will have partitions for platine and partitions for sabiane and we will only validate the platine ones
        //Partitions
        var partitionIterator =contextRootNode.path(CTX_PARTITIONS).elements();
        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredPartition,partitionNode,getClass()));

            //Check value of Enum
            String enumVal = partitionNode.path(CTX_PARTITION_TYPE_ECHANTILLON).asText();
            if(! EnumUtils.isValidEnum(PartitionTypeEchantillon.class, enumVal)){
                results.add(DelegateContextVerifier.computeIncorrectEnumMessage(CTX_PARTITION_TYPE_ECHANTILLON,enumVal,Arrays.toString(PartitionTypeEchantillon.values()),getClass()));
            }
        }
        return results;
    }

    private static ContactAccreditationDto convertREMPersonToPlatineContact(String idInternaute, PlatineAddressDto platineAdress, PersonDto remPersonDto) {
        return ContactAccreditationDto.builder()
                .identifier(idInternaute)
                .externalId(remPersonDto.getExternalId())
                .function("")
                .lastName(remPersonDto.getLastName())
                .firstName(remPersonDto.getFirstName())
                .isMain(true)
                .civility(convertREMGenderToPlatineCivility(remPersonDto.getGender()))
                //Get favorite phone/mail ; if no favorite get the first of the list ; else empty
                .email(remPersonDto.getEmails().stream()
                        .filter(EmailDto::getFavorite)
                        .findFirst()
                        .orElse(remPersonDto.getEmails().stream().findFirst().orElse(new EmailDto()))
                        .getMailAddress())

                .phone(
                        remPersonDto.getPhoneNumbers().stream()
                                .filter(PhoneNumberDto::getFavorite)
                                .findFirst()
                                .orElse(remPersonDto.getPhoneNumbers().stream().findFirst().orElse(new PhoneNumberDto()))
                                .getNumber())
                .address(platineAdress)
                .build();
    }

    private static String convertREMGenderToPlatineCivility(String remGender) {
        //"civility": "Madame" IF REM.person.gender=2, ELSE "Monsieur"
        return switch (remGender) {
            case "2":
                yield CIVILITY_MADAME;
            default:
                yield CIVILITY_MONSIEUR;
        };
    }

    private static PlatineAddressDto computePlatineAdress(REMAddressDto remAddressDto) {
        //The REM  REMAddressDto and Platine PlatineQuestioningSurveyUnitDto are identical
        //I'll use the copyProperty to recopy identical members
        var platineAdresse = BeanUtils.instantiateClass(PlatineAddressDto.class);
        BeanUtils.copyProperties(remAddressDto, platineAdresse);
        return platineAdresse;
    }

}
