package fr.insee.protools.backend.service.era.dto;

import lombok.Getter;

@Getter
public enum GenderType {
    MALE("1", "hommes"), FEMALE("2", "femmes");

    private final String value;
    private final String label;

    GenderType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static GenderType fromValue(int value) {
        return switch (value) {
            case 1 -> MALE;
            case 2 -> FEMALE;
            default -> throw new IllegalStateException("Unexpected value for ERA gender: " + value);
        };
    }

    public static GenderType fromLabel(String label) {
        return switch (label) {
            case "hommes" -> MALE;
            case "femmes" -> FEMALE;
            default -> throw new IllegalStateException("Unexpected value for ERA gender label: " + label);
        };
    }

    public static String getAllValidLabels() {
        return MALE.label + "," + FEMALE.label;
    }

}
