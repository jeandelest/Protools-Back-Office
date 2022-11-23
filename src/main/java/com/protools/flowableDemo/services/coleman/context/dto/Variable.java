package com.protools.flowableDemo.services.coleman.context.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
public class Variable {
    @JacksonXmlProperty(localName = "nom")
    private String name;

    @JacksonXmlText
    private String value;
}
