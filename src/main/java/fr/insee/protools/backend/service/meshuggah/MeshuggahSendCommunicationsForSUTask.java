package fr.insee.protools.backend.service.meshuggah;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MeshuggahSendCommunicationsForSUTask
//        implements JavaDelegate, DelegateContextVerifier
{
//
//    @Autowired ContextService protoolsContext;
//    @Autowired MeshuggahService meshuggahService;
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    static final Pattern echenancePattern = Pattern.compile("^\s*(?:J|j)\\+([0-9]+)\s*$");
//
//
//    @Override
//    public void execute(DelegateExecution execution) {
//        //Contexte
//        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
//        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);
//        String campainId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();
//
//        //Variables
//        JsonNode contactNode = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_PLATINE_CONTACT, JsonNode.class);
//
//        //each row is Tuple<Instant: creation instant,String : partitionId , String : remSUId>
//        Triple<Instant, Long, Long> suItem = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_SU_CREATION_ITEM, Triple.class);
//        Instant creationInstant = suItem.getLeft();
//        Long partitionId = suItem.getMiddle();
//        Long remSUId = suItem.getRight();
//
//        log.info("ProcessInstanceId={} - campainId={}  begin",execution.getProcessInstanceId(), campainId);
//
//
//        //Get partition from contexte
//        JsonNode partitionNode = getCurrentPartitionNode(contextRootNode, partitionId);
//
//        treatCommunications(partitionNode,creationInstant,remSUId);
//
//
//
//
//        PlatineContactDto platineContactDto;
//        try {
//            platineContactDto=objectMapper.treeToValue(contactNode,PlatineContactDto.class);
//        } catch (JsonProcessingException e) {
//            throw new IncorrectPlatineContactError("Error while parsing the json retrieved for platine contact : " + contactNode,contactNode, e);
//        }
//
//
//        //TODO :  Currently only send OPENING
//        JsonNode communicationNode = MeshuggahUtils.getCommunication(partitionNode,"mail","ouverture");
//        //TODO :  hack avec le mail de marc
//        platineContactDto.setEmail("marc.berger@insee.fr");
//        JsonNode body = initBody(platineContactDto);
//        MeshuggahComDetails meshuggahComDetails = MeshuggahUtils.computeMeshuggahComDetails(campainId,currentPartitionId,communicationNode);
//        meshuggahService.postSendCommunication(meshuggahComDetails, body);
//
//        log.info("ProcessInstanceId={} - campainId={}  end",execution.getProcessInstanceId(), campainId);
//    }
//
//    private void treatCommunications(JsonNode partitionNode, Instant creationInstant, Long remSUId) {
//
//        LocalDate createDate = LocalDate.ofInstant(creationInstant, ZoneId.systemDefault());
//        LocalDate currentDay = LocalDate.now();
//
//        //Partitions
//        //Treat every communication of this partition
//        var communicationsIterator =partitionNode.path(CTX_PARTITION_COMMUNICATIONS).elements();
//        while (communicationsIterator.hasNext()) {
//            var communicationNode = communicationsIterator.next();
//            var echeancesIterator = communicationNode.path(CTX_PARTITION_COMMUNICATION_ECHEANCES).elements();
//            while (communicationsIterator.hasNext()) {
//                String echeanceStr = echeancesIterator.next().asText();
//                // create matcher for pattern p and given string
//                Matcher matcher = echenancePattern.matcher(echeanceStr);
//                // if an occurrence if a pattern was found in a given string...
//                if (matcher.find()) {
//                    Integer nbOfDays = Integer.parseInt(matcher.group(1));
//                    //Compute echeance by adding days to creation date
//                    LocalDate echeanceDate = createDate.plusDays(nbOfDays);
//
//                    //If echeanceDate is before or equals to today
//                    if(echeanceDate.compareTo(currentDay)<=0) {
//                        JsonNode body = initBody(contextRootNode,communicationNode);
//                        MeshuggahComDetails meshuggahComDetails = MeshuggahUtils.computeMeshuggahComDetails(campainId,partitionId,communicationNode);
//                        meshuggahService.postCreateCommunication(meshuggahComDetails, body);
//                    }
//                }
//
//
//        }
//
//    }
//
//    private static JsonNode initBody(PlatineContactDto platine_contact){
//        ObjectNode body = objectMapper.createObjectNode();
//        body.put("email",platine_contact.getEmail());
//        body.put("Ue_CalcIdentifiant",platine_contact.getIdentifier());
//        return body;
//    }
//
//
//
//
//    @Override
//    public Set<String> getContextErrors(JsonNode contextRootNode) {
//        if (contextRootNode == null) {
//            return Set.of("Context is missing");
//        }
//        Set<String> results = new HashSet<>();
//        Set<String> requiredNodes =
//                Set.of(
//                        //Global & Campaign
//                        CTX_CAMPAGNE_ID,CTX_PARTITIONS
//                );
//        Set<String> requiredPartition =
//                Set.of(CTX_PARTITION_ID,CTX_PARTITION_COMMUNICATIONS);
//
//        Set<String> requiredCommunication = MeshuggahUtils.getCommunicationRequiredFields();
//        results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredNodes, contextRootNode, getClass()));
//
//        //Partitions
//        var partitionIterator = contextRootNode.path(CTX_PARTITIONS).elements();
//        while (partitionIterator.hasNext()) {
//            var partitionNode = partitionIterator.next();
//            var missingChildren = DelegateContextVerifier.computeMissingChildrenMessages(requiredPartition, partitionNode, getClass());
//            if (!missingChildren.isEmpty()) {
//                results.addAll(missingChildren);
//                continue;
//            }
//
//            //Communications of the partition
//            var communicationIterator = partitionNode.path(CTX_PARTITION_COMMUNICATIONS).elements();
//            while (communicationIterator.hasNext()) {
//                var communicationNode = partitionIterator.next();
//                var missingChildrenCom = DelegateContextVerifier.computeMissingChildrenMessages(requiredCommunication, communicationNode, getClass());
//                if (!missingChildrenCom.isEmpty()) {
//                    results.addAll(missingChildrenCom);
//                }
//            }
//        }
//        return results;
//    }
}

