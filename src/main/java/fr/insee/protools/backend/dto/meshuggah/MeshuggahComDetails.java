package fr.insee.protools.backend.dto.meshuggah;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class MeshuggahComDetails {
        String campaignId;
        String partitioningId;
        String medium;
        String phase;
        String operation;
        String mode;
        String protocol;
        boolean avecQuestionnaire;
}