package fr.insee.protools.backend.service.platine.pilotage.metadata;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SourceDto {

    private String id;
    private String longWording;
    private String shortWording;
    private PeriodicityEnum periodicity;
    private boolean mandatoryMySurveys;

}