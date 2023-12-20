package fr.insee.protools.backend.service.platine.pilotage.metadata;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerDto {

    private String id;
    private String label;
    private String ministry;
    private String logo;

}