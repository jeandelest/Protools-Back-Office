package fr.insee.protools.backend.dto.rem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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
