package com.protools.flowableDemo.services.coleman.context.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class Partition {
    private String id;

    private String label;

    private Dates dates;

    @JacksonXmlProperty(localName = "QuestionnaireModel")
    private String questionnaireModelId;

    @JacksonXmlProperty(localName = "QuiRepond1")
    private String firstResponder;

    @JacksonXmlProperty(localName = "QuiRepond2")
    private String secondResponder;

    @JacksonXmlProperty(localName = "QuiRepond3")
    private String thirdResponder;

    private Collection<Communication> communications;
}
