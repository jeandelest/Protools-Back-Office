package fr.insee.protools.backend.service.platine.pilotage.metadata;

import lombok.Getter;

@Getter
public enum PeriodicityEnum {

    X("pluriannual"), A("annual"), S("semi-annual"), T("trimestrial"), B("bimonthly"),
    M("monthly");

    PeriodicityEnum(String value) {
        this.value = value;
    }

    final String value;

    public static PeriodicityEnum fromValue(String v) {
        for (PeriodicityEnum c : PeriodicityEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}