package com.protools.flowableDemo.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Survey {
    private Long id;
    private String name;
    private String dateDeb;
    private String dateEnd;
    private String state_survey;
    private String sampleSize;


    public Survey(){}

    public Survey(Long id, String name, String dateDeb, String dateEnd, String state_survey) {
        this.id = id;
        this.name = name;
        this.dateDeb = dateDeb;
        this.dateEnd = dateEnd;
        this.state_survey = state_survey;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Survey survey = (Survey) o;
        return Objects.equals(name, survey.name) && Objects.equals(dateDeb, survey.dateDeb) && Objects.equals(dateEnd, survey.dateEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dateDeb, dateEnd);
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateDeb='" + dateDeb + '\'' +
                ", dateEnd='" + dateEnd + '\'' +
                ", state='" + state_survey + '\'' +
                '}';
    }
}
