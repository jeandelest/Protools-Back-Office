package fr.insee.protools.backend.service.sabiane.pilotage.dto;

public enum ContactOutcomeConfiguration {
    F2F("faf"), TEL("tel");

    /**
     * label of the ContactOutcomeConfiguration
     */
    private String label;

    /**
     * Defaut constructor for a ContactOutcomeConfiguration
     *
     * @param label
     */
    ContactOutcomeConfiguration(String label) {
        this.label = label;
    }

    /**
     * Get the label for a ContactOutcomeConfiguration
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    public static ContactOutcomeConfiguration valueOfLabel(String label) {
        for (ContactOutcomeConfiguration e : values()) {
            if (e.label.equalsIgnoreCase(label)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant with value="+label+ " for class " + ContactOutcomeConfiguration.class.getCanonicalName());
    }

    public static String[] labels(){
        String[] results = new String[values().length];
        int i=0;
        for (var e : values()) {
            results[i]=e.label;
            i++;
        }
        return results;
    }
}