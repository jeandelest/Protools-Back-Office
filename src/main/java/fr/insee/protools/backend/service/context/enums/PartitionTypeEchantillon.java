package fr.insee.protools.backend.service.context.enums;

public enum PartitionTypeEchantillon {
    LOGEMENT("logement"),
    INDIVIDU("individu");

    final String val;

    PartitionTypeEchantillon(String val) {
        this.val = val;
    }

    public String getAsString() {
        return val;
    }
}
