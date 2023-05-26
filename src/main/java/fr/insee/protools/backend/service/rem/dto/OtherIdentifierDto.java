package fr.insee.protools.backend.service.rem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtherIdentifierDto {

    private String numfa;
    private String rges;
    private String ssech;
    private String cle;
    private String le;
    private String ec;
    private String bs;
    private String nograp;
    private String nolog;
    private String noi;
    private String nole;
    private String autre;
}