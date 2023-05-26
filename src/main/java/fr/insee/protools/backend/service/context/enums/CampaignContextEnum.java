package fr.insee.protools.backend.service.context.enums;

public enum CampaignContextEnum {
    HOUSEHOLD("household"),
    BUSINESS("business");

    final String contexte;

    CampaignContextEnum(String contexte) {
        this.contexte = contexte;
    }

    public String getAsString() {
        return contexte;
    }
}
