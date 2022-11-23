package com.protools.flowableDemo.services.coleman.context.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class Nomenclature {
    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "label")
    private String label;

    private Collection<?> value;

    public Nomenclature() {
    }

    public Nomenclature(String id, String label, Collection<?> value) {
        this.id = id;
        this.label = label;
        this.value = value;
    }
}
