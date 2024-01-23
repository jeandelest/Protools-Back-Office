package fr.insee.protools.backend.service.meshuggah;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectBPMNError;
import fr.insee.protools.backend.dto.meshuggah.MeshuggahComDetails;

import java.util.Set;

import static fr.insee.protools.backend.service.context.ContextConstants.*;

public class MeshuggahUtils {

    static MeshuggahComDetails computeMeshuggahComDetails(String campagneId, Long partitionId, JsonNode communicationNode) {
        String ctxMoyenCommunication = communicationNode.path(CTX_PARTITION_COMMUNICATION_MOYEN).asText();
        String ctxPhase = communicationNode.path(CTX_PARTITION_COMMUNICATION_PHASE).asText();
        String ctxTypeModele = communicationNode.path(CTX_PARTITION_COMMUNICATION_TYPE_MODELE).asText();
        String ctxMode = communicationNode.path(CTX_PARTITION_COMMUNICATION_MODE).asText();
        String ctxProtocol = communicationNode.path(CTX_PARTITION_COMMUNICATION_PROTOCOLE).asText();
        boolean meshAvecQuestionnaire = communicationNode.path(CTX_PARTITION_COMMUNICATION_AVEC_QUESTIONNAIRE_PAPIER).asBoolean();
        return MeshuggahComDetails.builder().campaignId(campagneId).partitioningId(String.valueOf(partitionId)).medium(MeshuggahUtils.MediumEnum.fromCtxValue(ctxMoyenCommunication).meshuggahValue).phase(MeshuggahUtils.PhaseEnum.fromCtxValue(ctxPhase).meshuggahValue).operation(MeshuggahUtils.TypeModeleEnum.fromCtxValue(ctxTypeModele).meshuggahValue).mode(MeshuggahUtils.ModeEnum.fromCtxValue(ctxMode).meshuggahValue).protocol(MeshuggahUtils.ProtocolEnum.fromCtxValue(ctxProtocol).meshuggahValue).avecQuestionnaire(meshAvecQuestionnaire).build();
    }


    public static Set<String> getCommunicationRequiredFields() {
        return Set.of(CTX_PARTITION_COMMUNICATION_MOYEN, CTX_PARTITION_COMMUNICATION_PHASE, CTX_PARTITION_COMMUNICATION_TYPE_MODELE, CTX_PARTITION_COMMUNICATION_MODE, CTX_PARTITION_COMMUNICATION_PROTOCOLE, CTX_PARTITION_COMMUNICATION_AVEC_QUESTIONNAIRE_PAPIER);
    }

    //Search for a communication in the contexte.
    public static JsonNode getCommunication(JsonNode partitionNode, MeshuggahUtils.MediumEnum medium, MeshuggahUtils.PhaseEnum phase) {
        JsonNode communicationNode = null;
        for (JsonNode subNode : partitionNode.path(CTX_PARTITION_COMMUNICATIONS)) {
            if (subNode.has(CTX_PARTITION_COMMUNICATION_MOYEN) && subNode.has(CTX_PARTITION_COMMUNICATION_PHASE) && subNode.get(CTX_PARTITION_COMMUNICATION_MOYEN).asText().equalsIgnoreCase(medium.ctxValue) && subNode.get(CTX_PARTITION_COMMUNICATION_PHASE).asText().equalsIgnoreCase(phase.ctxValue)) {
                communicationNode = subNode;
                break;
            }
        }
        if (communicationNode == null) {
            throw new BadContextIncorrectBPMNError(String.format("Communication medium=[%s] - phase=[%s] not found ", medium, phase));
        }
        return communicationNode;
    }

    enum MediumEnum {

        COURRIER("courrier", "COURRIER"), MAIL("mail", "EMAIL");

        final String meshuggahValue;
        final String ctxValue;

        MediumEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        public static MediumEnum fromCtxValue(String ctxValue) {
            for (var c : values()) {
                if (c.ctxValue.equalsIgnoreCase(ctxValue)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(ctxValue);
        }
    }

    enum PhaseEnum {

        OUVERTURE("ouverture", "OUVERTURE"), RELANCE("relance", "RELANCE"), PONCTUEL("ponctuel", "PONCTUEL");

        final String meshuggahValue;
        final String ctxValue;

        PhaseEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        public static PhaseEnum fromCtxValue(String ctxValue) {
            for (var c : values()) {
                if (c.ctxValue.equals(ctxValue)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(ctxValue);
        }
    }

    enum TypeModeleEnum {

        OUVERTURE("ouverture", "OUVERTURE"), RELANCE("relance", "RELANCE"), RELANCE_LIBRE("relance_libre", "RELANCE_LIBRE");

        final String meshuggahValue;
        final String ctxValue;

        TypeModeleEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        public static TypeModeleEnum fromCtxValue(String ctxValue) {
            for (var c : values()) {
                if (c.ctxValue.equals(ctxValue)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(ctxValue);
        }
    }

    enum ModeEnum {

        FAF("faf", "FAF"), TEL("tel", "TEL"), WEB("web", "WEB");

        final String meshuggahValue;
        final String ctxValue;

        ModeEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        public static ModeEnum fromCtxValue(String ctxValue) {
            for (var c : values()) {
                if (c.ctxValue.equals(ctxValue)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(ctxValue);
        }
    }

    enum ProtocolEnum {

        SEQ_WT("sequentiel web tel faf", "SEQ_WT"), SEQ_TW("sequentiel tel web", "SEQ_TW"), PANEL("panel", "PANEL"), DEFAULT("null", "DEFAULT");

        final String meshuggahValue;
        final String ctxValue;

        ProtocolEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        public static ProtocolEnum fromCtxValue(String ctxValue) {
            for (var c : values()) {
                if (c.ctxValue.equals(ctxValue)) {
                    return c;
                }
            }
            return DEFAULT;
        }
    }


}
