package fr.insee.protools.backend.service.sabiane.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.context.enums.PartitionTypeEchantillon;
import fr.insee.protools.backend.service.exception.IncorrectSUBPMNError;
import fr.insee.protools.backend.service.platine.utils.PlatineHelper;
import fr.insee.protools.backend.service.rem.RemDtoUtils;
import fr.insee.protools.backend.service.rem.dto.*;
import fr.insee.protools.backend.service.sabiane.SabianeIdHelper;
import fr.insee.protools.backend.service.sabiane.pilotage.SabianePilotageService;
import fr.insee.protools.backend.service.sabiane.pilotage.dto.*;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CURRENT_PARTITION_ID;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_SURVEY_UNIT;
import static fr.insee.protools.backend.service.context.ContextConstants.*;
import static fr.insee.protools.backend.service.utils.ContextUtils.getCurrentPartitionNode;

@Slf4j
@Component
public class SabianePilotageCreateSUTask implements JavaDelegate, DelegateContextVerifier {

    private final static String EMPTY = "";
    private final static DateTimeFormatter birthdateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final static ZoneId parisTimezone = ZoneId.of("Europe/Paris");
    private static final ObjectMapper objectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false).configure(FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
    @Autowired ContextService protoolsContext;
    @Autowired SabianePilotageService sabianePilotageService;


    @Override
    public void execute(DelegateExecution execution) {
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log, execution.getProcessInstanceId(), contextRootNode);

        Long currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, Long.class);
        JsonNode remSUNode = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_REM_SURVEY_UNIT, JsonNode.class);
        JsonNode currentPartitionNode = getCurrentPartitionNode(contextRootNode, currentPartitionId);

        Boolean priority = currentPartitionNode.path(CTX_PARTITION_PRIORITAIRE).asBoolean();
        boolean isLogement = currentPartitionNode.path(CTX_PARTITION_TYPE_ECHANTILLON).textValue().equalsIgnoreCase(PartitionTypeEchantillon.LOGEMENT.getAsString());

        //Create the DTO object
        SurveyUnitContextDto dto = createSabianeSUContextDto(contextRootNode, currentPartitionId, remSUNode, isLogement, priority);

        log.info("ProcessInstanceId={} - currentPartitionId={} - remSU.id={}", execution.getProcessInstanceId(), currentPartitionId, dto.getId());

        //Call service
        //sabianePilotageService.xxxxxxxxxxx(dto, contextRootNode.path(CTX_CAMPAGNE_ID).asText());

        log.debug("ProcessInstanceId={}  end", execution.getProcessInstanceId());
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        if(true)
            throw new RuntimeException("TOTO");
        else
            return null;

    }


    private static long computeSabianeBirthDateFromRem(String remBirthdate) {
        String adjudstedBirthDate = remBirthdate.trim();
        int len = adjudstedBirthDate.length();
        switch (len) {
            case 4:
                adjudstedBirthDate += "0101";//If 4 char (ex: 2009) consider 1st junary
                break;
            case 6:
                adjudstedBirthDate += "01";//If 6 char (ex: 200912) : consider 1st day of month
                break;
            default:
        }
        try {
            LocalDate localDate = LocalDate.parse(adjudstedBirthDate, birthdateFormatter);
            ZonedDateTime zonedDateTime = localDate.atStartOfDay(parisTimezone);
            return zonedDateTime.toInstant().getEpochSecond();
        } catch (DateTimeParseException e) {
            throw new IncorrectSUBPMNError("Error while parsing the json retrieved from REM : the birthdate" + remBirthdate + " is incorrect.");
        }
    }

    private static SabianePhoneNumberDto createSabianePhoneNumberFromREM(PhoneNumberDto remPhone, Source sabianeSource) {
        return new SabianePhoneNumberDto(sabianeSource, remPhone.getFavorite(), remPhone.getNumber());
    }

    protected static List<SabianePhoneNumberDto> createSabianePhoneList(List<PhoneNumberDto> remPhones) {
        List<SabianePhoneNumberDto> res = new ArrayList<>(2);
        //Get the favorites numbers (excluding interviewers ones)
        List<PhoneNumberDto> favoritesNumeros =
                remPhones.stream()
                        .filter(e -> !REMPhoneSource.INTERVIEWER.equals(e.getSource()))
                        .filter(e->Boolean.TRUE.equals(e.getFavorite()))
                        .toList();

        //INTERVIEWER : Keep all phones numbers
        remPhones.stream()
                .filter(e -> REMPhoneSource.INTERVIEWER.equals(e.getSource()))
                .forEach(phoneNumberDto ->  res.add(createSabianePhoneNumberFromREM(phoneNumberDto, Source.INTERVIEWER)));


        List<PhoneNumberDto> nonInterviewerNonFavoriteByPriority =
                remPhones.stream()
                        .filter(e->Boolean.FALSE.equals(e.getFavorite()))
                        //Discard interviewer as they are handled just before
                        .filter(phoneNumberDto -> !(phoneNumberDto.getSource().equals(REMPhoneSource.INTERVIEWER)))
                        .sorted(Comparator.comparingInt(obj -> {
                            //Selection priority order INITIAL>DIRECTORY>OTHER
                            switch (obj.getSource()) {
                                case INITIAL:
                                    return 0;
                                case DIRECTORY:
                                    return 1;
                                case OTHER:
                                    return 2;
                                default:
                                    return 3; // Handle other cases if necessary
                            }
                        }))
                        .collect(Collectors.toCollection(ArrayList::new));//Must be mutable

        switch (favoritesNumeros.size()) {
            case 0://no favorite number : FILL FISCAL and DIRECTORY using priority (initial, directory, other)
                if (!nonInterviewerNonFavoriteByPriority.isEmpty()) {
                    PhoneNumberDto remInitialNonFavorite = nonInterviewerNonFavoriteByPriority.remove(0);
                    res.add(createSabianePhoneNumberFromREM(remInitialNonFavorite, Source.FISCAL));
                }
                if (!nonInterviewerNonFavoriteByPriority.isEmpty()) {
                    PhoneNumberDto remInitialNonFavorite = nonInterviewerNonFavoriteByPriority.remove(0);
                    res.add(createSabianePhoneNumberFromREM(remInitialNonFavorite, Source.DIRECTORY));
                }
                break;
            case 1:
                res.add(createSabianePhoneNumberFromREM(favoritesNumeros.get(0), Source.FISCAL));
                if (!nonInterviewerNonFavoriteByPriority.isEmpty()) {
                    PhoneNumberDto remInitialNonFavorite = nonInterviewerNonFavoriteByPriority.remove(0);
                    res.add(createSabianePhoneNumberFromREM(remInitialNonFavorite, Source.DIRECTORY));
                }
                break;
            case 2:
                res.add(createSabianePhoneNumberFromREM(favoritesNumeros.get(0), Source.FISCAL));
                res.add(createSabianePhoneNumberFromREM(favoritesNumeros.get(1), Source.DIRECTORY));
                break;
            default: //More than 2 favorite numbers
                throw new IncorrectSUBPMNError("Error while parsing the json retrieved from REM : more than 2 favorite phone number");
        }
        return res;
    }

    private static SabianePersonDto createSabianePersonFromRemPerson(PersonDto remPerson, Boolean isPrivileged) {
        return SabianePersonDto.builder().title(convertREMGenderToSabianeCivilityTitle(remPerson.getGender())).firstName(remPerson.getFirstName()).lastName(remPerson.getLastName()).email(remPerson.getEmails().stream().findFirst().map(EmailDto::toString).orElse("")).birthdate(computeSabianeBirthDateFromRem(remPerson.getDateOfBirth())).favoriteEmail(Boolean.FALSE).privileged(isPrivileged).phoneNumbers(createSabianePhoneList(remPerson.getPhoneNumbers())).build();
    }

    private static SurveyUnitContextDto createSabianeSUContextDto(JsonNode contextRootNode, Long currentPartitionId, JsonNode remSUNode, Boolean isLogement, Boolean priority) {
        REMSurveyUnitDto remSurveyUnitDto = PlatineHelper.parseRemSUNode(objectMapper, VARNAME_REM_SURVEY_UNIT, remSUNode);
        Pair<PersonDto, Optional<PersonDto>> mainAndSecondaryPerson = RemDtoUtils.findContactAndSecondary(remSUNode, remSurveyUnitDto, isLogement);
        String id = SabianeIdHelper.computeSabianeID(currentPartitionId.toString(), remSurveyUnitDto.getRepositoryId().toString());

        List<SabianePersonDto> sabianePersons = new ArrayList<>(2);
        sabianePersons.add(createSabianePersonFromRemPerson(mainAndSecondaryPerson.getLeft(), true)); //first is privileged
        mainAndSecondaryPerson.getRight().ifPresent(personDto -> sabianePersons.add(createSabianePersonFromRemPerson(personDto, false)));


        return SurveyUnitContextDto.builder()
                .id(id)
                .persons(sabianePersons)
                .address(computeSabianeAdress(remSurveyUnitDto, mainAndSecondaryPerson.getLeft()))
                .organizationUnitId("TOBEDONE")
                .priority(priority)
                .campaign(contextRootNode.path(CTX_CAMPAGNE_ID).textValue())
                .sampleIdentifiers(
                        SampleIdentifiersDto.builder()
                                .autre("0")
                                .bs(0)
                                .ec("0")
                                .le(0)
                                .nograp(remSurveyUnitDto.getOtherIdentifier().getNograp()).noi(0).nole(0).nolog(0).numfa(0).rges(0).ssech(currentPartitionId).build())
                //.states(StateDto.builder().date(Instant.now().toEpochMilli()).type(StateType.NVM))
                .build();
    }

    /**
     * @param str the string to parse
     * @return l2 and l3 part of an adress by splitting str at 38 char. If str is null or empty return a pair of empty strings
     */
    private static Pair<String, String> computeL2L3(String str) {
        if (str == null || str.isEmpty() || str.isBlank()) {
            return Pair.of(EMPTY, EMPTY);
        }
        if (str.length() <= 38) {
            return Pair.of(str, EMPTY);
        }
        return Pair.of(str.substring(0, 38), str.substring(39, str.length() - 1));
    }

    static SabianeTitle convertREMGenderToSabianeCivilityTitle(String remGender) {
        return switch (remGender) {
            case "2":
                yield SabianeTitle.MISS;
            case "1":
                yield SabianeTitle.MISTER;
                //TODO : BPMN Error
            default:
                throw new IllegalStateException("Unexpected value: " + remGender);
        };
    }

    private static AddressDto computeSabianeAdress(REMSurveyUnitDto remSuDto, PersonDto contact) {

        REMAddressDto remAddressDto = remSuDto.getAddress();
        //l1 : “M” si REM.person.gender = "1" ; “MME” sinon   +    “firstName” +  “lastName” (pour la person mis en privileged)
        String l1 = String.format("%s %s %s", convertREMGenderToSabianeCivilityTitle(contact.getGender()).getFrenchCivility(), contact.getFirstName(), contact.getLastName());
        //l2 : partie de “REM.address.addressSupplement” <=38 caractères
        //l3 : partie de “REM.address.addressSupplement” > 38 caractères
        Pair<String, String> l2_l3 = computeL2L3(remAddressDto.getAddressSupplement());
        String l2 = l2_l3.getLeft();
        String l3 = l2_l3.getRight();

        //l4: “REM.address.streetNumber” + “REM.address.repetitionIndex” + “REM.address.streetType” + “REM.address.streetName”
        String l4 = String.format("%s %s %s %s", remAddressDto.getStreetNumber(), remAddressDto.getRepetitionIndex(), remAddressDto.getStreetType(), remAddressDto.getStreetName());
        //l5: “REM.address.specialDistribution”
        String l5 = remAddressDto.getSpecialDistribution();
        //l6: “REM.address.zipCode” + “REM.address.cityName”
        String l6 = String.format("%s %s", remAddressDto.getZipCode(), remAddressDto.getCityName());
        //l7: REM.countryName  ou “FRANCE” si null
        String l7 = (StringUtils.isBlank(remAddressDto.getCountryName()) ? "FRANCE" : remAddressDto.getCountryName());

        var sabianeAdressDto = AddressDto.builder().elevator(remAddressDto.getLocationHelp().getElevator()).building(remAddressDto.getLocationHelp().getBuilding()).floor(remAddressDto.getLocationHelp().getFloor()).door(remAddressDto.getLocationHelp().getDoor()).staircase(remAddressDto.getLocationHelp().getStaircase()).cityPriorityDistrict(remAddressDto.getLocationHelp().getCityPriorityDistrict()).l1(l1).l2(l2).l3(l3).l4(l4).l5(l5).l6(l6).l7(l7).build();
        return sabianeAdressDto;
    }


}
