package fr.insee.protools.backend.service.sabiane.pilotage.dto;

public enum IdentificationConfiguration {
    //"Identification - Access - Situation - Category - Occupant"
    IASCO("faf_logement"),
    NOIDENT("aucun");

    /**
     * label of the IdentificationConfiguration
     */
    private String label;

    /**
     * Defaut constructor for a IdentificationConfiguration
     * 
     * @param label
     */
    IdentificationConfiguration(String label) {
        this.label = label;
    }

    /**
     * Get the label for a IdentificationConfiguration
     * 
     * @return label
     */
    public String getLabel() {
        return label;
    }

    public static IdentificationConfiguration valueOfLabel(String label) {
        for (IdentificationConfiguration e : values()) {
            if (e.label.equalsIgnoreCase(label)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant with value="+label+ " for class " + IdentificationConfiguration.class.getCanonicalName());
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