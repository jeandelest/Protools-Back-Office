package fr.insee.protools.backend.dto.sabiane.pilotage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
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