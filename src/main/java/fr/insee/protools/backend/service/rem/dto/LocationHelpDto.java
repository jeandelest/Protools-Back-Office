package fr.insee.protools.backend.service.rem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationHelpDto {

    private String cityCode;
    private String building;
    private String floor;
    private String staircase;
    private String door;
    private String iris;
    private String sector;
    //private GPSLocation gpsCoordinates;
    private Boolean elevator;
    private Boolean cityPriorityDistrict;
}