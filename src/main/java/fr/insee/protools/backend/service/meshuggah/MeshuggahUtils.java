package fr.insee.protools.backend.service.meshuggah;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.context.ContextConstants;
import fr.insee.protools.backend.service.meshuggah.dto.MeshuggahComDetails;

import static fr.insee.protools.backend.service.context.ContextConstants.*;
import static fr.insee.protools.backend.service.context.ContextConstants.CTX_PARTITION_COMMUNICATION_AVEC_QUESTIONNAIRE_PAPIER;

public class MeshuggahUtils {

    static MeshuggahComDetails computeMeshuggahComDetails(Long partitionId, JsonNode contextRootNode, JsonNode communicationNode){
        String ctxMoyenCommunication = communicationNode.path(CTX_PARTITION_COMMUNICATION_MOYEN).asText();
        String ctxPhase = communicationNode.path(CTX_PARTITION_COMMUNICATION_PHASE).asText();
        String ctxTypeModele = communicationNode.path(CTX_PARTITION_COMMUNICATION_TYPE_MODELE).asText();
        String ctxMode = communicationNode.path(CTX_PARTITION_COMMUNICATION_MODE).asText();
        String ctxProtocol = communicationNode.path(CTX_PARTITION_COMMUNICATION_PROTOCOLE).asText();
        boolean meshAvecQuestionnaire = communicationNode.path(CTX_PARTITION_COMMUNICATION_AVEC_QUESTIONNAIRE_PAPIER).asBoolean();
        String campagneId=contextRootNode.path(ContextConstants.CTX_CAMPAGNE_ID).asText();

        return MeshuggahComDetails.builder()
                .campaignId(campagneId)
                .partitioningId(String.valueOf(partitionId))
                .medium(MeshuggahUtils.MediumEnum.fromCtxValue(ctxMoyenCommunication).meshuggahValue)
                .phase(MeshuggahUtils.PhaseEnum.fromCtxValue(ctxPhase).meshuggahValue)
                .operation(MeshuggahUtils.TypeModeleEnum.fromCtxValue(ctxTypeModele).meshuggahValue)
                .mode(MeshuggahUtils.ModeEnum.fromCtxValue(ctxMode).meshuggahValue)
                .protocol(MeshuggahUtils.ProtocolEnum.fromCtxValue(ctxProtocol).meshuggahValue)
                .avecQuestionnaire(meshAvecQuestionnaire)
                .build();
    }

    enum MediumEnum {

        COURRIER("courrier","COURRIER"), MAIL("mail","EMAIL");

        MediumEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        final String meshuggahValue;
        final String ctxValue;

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

        OUVERTURE("ouverture","OUVERTURE"),
        RELANCE("relance","RELANCE"),
        PONCTUEL("ponctuel","PONCTUEL");

        PhaseEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        final String meshuggahValue;
        final String ctxValue;

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

        OUVERTURE("ouverture","OUVERTURE"), RELANCE("relance","RELANCE"), RELANCE_LIBRE("relance_libre", "RELANCE_LIBRE");

        TypeModeleEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        final String meshuggahValue;
        final String ctxValue;

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

        FAF("faf","FAF"), TEL("tel","TEL"), WEB("web", "WEB");

        ModeEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        final String meshuggahValue;
        final String ctxValue;

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

        SEQ_WT("sequentiel web tel faf","SEQ_WT"), SEQ_TW("sequentiel tel web","SEQ_TW"), PANEL("panel", "PANEL")
        , DEFAULT("null","DEFAULT");

        ProtocolEnum(String ctxValue, String meshuggahValue) {
            this.meshuggahValue = meshuggahValue;
            this.ctxValue = ctxValue;
        }

        final String meshuggahValue;
        final String ctxValue;

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
