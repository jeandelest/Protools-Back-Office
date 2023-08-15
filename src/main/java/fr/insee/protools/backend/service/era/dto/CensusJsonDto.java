package fr.insee.protools.backend.service.era.dto;

import com.fasterxml.jackson.databind.node.BaseJsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CensusJsonDto implements Serializable{
    private Long id;
    private String numvoiloc;
    private String bisterloc;
    private String typevoiloc;
    private String nomvoiloc;
    private String resloc;
    private String car;
    private String cpostloc;
    private Long idinternaute;
    private String mail;
    private String identifiantCompte;
    private BaseJsonNode externals;
}
