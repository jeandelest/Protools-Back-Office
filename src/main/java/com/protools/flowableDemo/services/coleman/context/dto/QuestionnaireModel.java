package com.protools.flowableDemo.services.coleman.context.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ToString
@JacksonXmlRootElement(localName = "QuestionnaireModel") public class QuestionnaireModel {
    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "label")
    private String label;

    @JacksonXmlElementWrapper(localName = "RequiredNomenclatures")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<Nomenclature> requiredNomenclatures;

    //Only used to generate JSON for colleman
    //TODO : à refactorer car c'est moche de chez moche
    Collection<String> requiredNomenclatureIds;
    Map<?, ?> value;

    public QuestionnaireModel(String id, String label, Collection<String> requiredNomenclatureIds, Map<?, ?> value) {
        this.id = id;
        this.label = label;
        this.requiredNomenclatureIds = requiredNomenclatureIds;
        this.value = value;
    }


    public List<String> getRequiredNomenclatureIds() {
        return
            ((requiredNomenclatures==null)? Collections.emptyList():
            requiredNomenclatures.stream().map(Nomenclature::getId).collect(Collectors.toList()));
    }

    @JsonGetter(value = "idQuestionnaireModel")
    public String getId() {
        return id;
    }

}
