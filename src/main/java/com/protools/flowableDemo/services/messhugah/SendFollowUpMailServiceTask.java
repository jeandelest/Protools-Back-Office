package com.protools.flowableDemo.services.messhugah;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.protools.flowableDemo.services.utils.ContextConstants.*;

@Component
@Slf4j
public class SendFollowUpMailServiceTask implements JavaDelegate {


    @Autowired
    private SendMailService sendMailService;
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Send Follow Up Email ServiceTask");

        // Retrieve email content data
        Map unit = (Map) delegateExecution.getVariable(UNIT);
        int sexe = Integer.valueOf((String) unit.get(SEXE));
        List<LinkedHashMap<String,Object>> partition = (List<LinkedHashMap<String,Object>>) delegateExecution.getVariable(PARTITION);
        LinkedHashMap<String,Object> communications = (LinkedHashMap<String,Object>) partition.get(sexe - 1).get(COMMUNICATIONS);
        List<LinkedHashMap<String,Object>> communication = (List<LinkedHashMap<String, Object>>) communications.get(COMMUNICATION);

        //TODO : Faire moins sale
        LinkedHashMap<String,Object> communicationRelance = null;
        LinkedHashMap<String,Object> contenuCommunication = null;
        for (LinkedHashMap<String,Object> comm: communication ){
            //log.info("Comm: " + comm.toString());
            //log.info("Comm type: " + comm.get("MoyenCommunication")+" - "+ comm.get("TypeCommunication"));
            if (comm.get(MOYEN_COMMUNICATION).equals("mail") && comm.get(TYPE_COMMUNICATION).equals("relance")){

                communicationRelance = comm;
                contenuCommunication = (LinkedHashMap<String,Object>) comm.get(CONTENU_COMMUNICATION);

                // Retrieve Campaign data
                // This part is not mandatory, it only serves as a mark to not get lost in all those variables
                String Enq_ServiceCollecteurSignataireFonction = (String) delegateExecution.getVariable(SERVICE_COLLECTEUR_SIGNATAIRE_FONCTION);
                String Enq_ServiceCollecteurSignataireNom = (String) delegateExecution.getVariable(SERVICE_COLLECTEUR_SIGNATAIRE_NOM);
                String Enq_RespTraitement = (String) delegateExecution.getVariable(RESPONSABLE_TRAITEMENT);
                String Enq_RespOperationnel = (String) delegateExecution.getVariable(RESPONSABLE_OPERATIONNEL);
                String Enq_MailRespOperationnel = (String) delegateExecution.getVariable(MAIL_RESPONSABLE_OPERATIONNEL);
                String Enq_UrlEnquete = (String) delegateExecution.getVariable(URL_ENQUETE);
                String Enq_LogoPrestataire = (String) delegateExecution.getVariable(LOGO_PRESTATAIRE);
                String Enq_Prestataire = (String) delegateExecution.getVariable(PRESTATAIRE);

                // Create Email request body
                LinkedHashMap<String,Object> finalContenuCommunication = contenuCommunication;
                LinkedHashMap<String,Object> finalCommunicationRelance = communicationRelance;
                var data = new HashMap<String, Object>() {{
                    put("Ue_CalcIdentifiant", unit.get(INTERNAUTE));
                    put("Enq_ThemeMieuxConnaitreMail", finalContenuCommunication.get("ThemeMieuxConnaitreMail"));
                    put("Enq_ServiceCollecteurSignataireFonction", Enq_ServiceCollecteurSignataireFonction);
                    put("Enq_ServiceCollecteurSignataireNom", Enq_ServiceCollecteurSignataireNom);
                    put("Enq_RespTraitement", Enq_RespTraitement);
                    put("Enq_RespOperationnel",Enq_RespOperationnel);
                    put("Enq_MailRespOperationnel",Enq_MailRespOperationnel);
                    put("Enq_UrlEnquete",Enq_UrlEnquete);
                    put("Enq_LogoPrestataire",Enq_LogoPrestataire);
                    put("Enq_Prestataire",Enq_Prestataire);
                    put("Mail_Objet", finalCommunicationRelance.get(OBJET));
                    put("Mail_BoiteRetour", finalCommunicationRelance.get(MAIL_RETOUR));
                    put("Enq_RelanceLibreMailParagraphe1", finalContenuCommunication.get("RelanceLibreMailParagraphe1"));
                    put("Enq_RelanceLibreMailParagraphe2", finalContenuCommunication.get("RelanceLibreMailParagraphe2"));
                    put("Enq_RelanceLibreMailParagraphe3", finalContenuCommunication.get("RelanceLibreMailParagraphe3"));
                    put("Enq_RelanceLibreMailParagraphe4", finalContenuCommunication.get("RelanceLibreMailParagraphe4"));
                    put("Enq_ComplementConnexion", finalContenuCommunication.get("ComplementConnexion"));
                }};
                var values = new HashMap<String, Object>() {{
                    put("email", unit.get(MAIL));
                    put("data", data);

                }};
                log.info("\t \t FollowUp Mail data: " + values);
                var objectMapper = new ObjectMapper();
                String requestBody = null;
                try {
                    requestBody = objectMapper
                            .writeValueAsString(values);
                    sendMailService.sendMail(requestBody);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }
}
