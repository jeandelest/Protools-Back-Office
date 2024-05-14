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

    public static CampaignContextEnum fromLabel(String label) {
        return switch (label.toLowerCase()) {
            case "household" -> HOUSEHOLD;
            case "business" -> BUSINESS;
            default -> throw new IllegalStateException("Unexpected value for CampaignContextEnum: " + label);
        };
    }
}
