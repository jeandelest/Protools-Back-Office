package fr.insee.protools.backend.service.sabiane.pilotage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class VisibilityContextDto {


    /**
     * Organizational unit of the visibility
     */
    private String organizationalUnit;

    /**
     * Collection start date of the visibility
     */
    private Long collectionStartDate;

    /**
     * Collection end date of the visibility
     */
    private Long collectionEndDate;

    /**
     * Identification phase start date of the visibility
     */
    private Long identificationPhaseStartDate;

    /**
     * interviewer start date of the visibility
     */
    private Long interviewerStartDate;

    /**
     * Manager start date of the visibility
     */
    private Long managementStartDate;

    /**
     * End date of the visibility
     */
    private Long endDate;


}