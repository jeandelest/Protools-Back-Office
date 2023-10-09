package fr.insee.protools.backend.service.platine.pilotage.dto;

import lombok.Getter;

@Getter
@SuppressWarnings("java:S115") //allow constants not in capital letters
public enum PlatinePilotageGenderType {
    Female("1", "Female"), Male("2", "Male"),Undefined("3","Undefined")  ;

    private final String value;
    private final String label;

    PlatinePilotageGenderType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static PlatinePilotageGenderType fromValue(int value) {
        return switch (value) {
            case 1 -> Female;
            case 2 -> Male;
            default -> Undefined;
        };
    }

    public static PlatinePilotageGenderType fromLabel(String label) {
        return switch (label) {
            case "Male" -> Male;
            case "Female" -> Female;
            default -> Undefined;
        };
    }

    public static String getAllValidLabels() {
        return Male.label + "," + Female.label + "," + Undefined.label  ;
    }

}
