package fr.insee.protools.backend.service.sabiane.pilotage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReferentDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;
}