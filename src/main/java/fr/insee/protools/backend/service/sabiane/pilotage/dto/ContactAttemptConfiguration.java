package fr.insee.protools.backend.service.sabiane.pilotage.dto;

public enum ContactAttemptConfiguration {
    F2F("faf"), TEL("tel");

    /**
     * label of the ContactAttemptConfiguration
     */
    private final String label;

    /**
     * Defaut constructor for a ContactAttemptConfiguration
     * 
     * @param label
     */
    ContactAttemptConfiguration(String label) {
        this.label = label;
    }

    /**
     * Get the label for a ContactAttemptConfiguration
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    public static ContactAttemptConfiguration valueOfLabel(String label) {
        for (ContactAttemptConfiguration e : values()) {
            if (e.label.equalsIgnoreCase(label)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant with value="+label+ " for class " + ContactAttemptConfiguration.class.getCanonicalName());
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