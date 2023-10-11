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
}
