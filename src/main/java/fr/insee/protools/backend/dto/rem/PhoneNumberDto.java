package fr.insee.protools.backend.dto.rem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumberDto {
    private REMPhoneSource source;
    private Boolean favorite;
    private String number;
}