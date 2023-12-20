package fr.insee.protools.backend.service.rem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuIdMappingJson {
    private long partitionId;
    private List<SuIdMappingRecord> data;
    private int count;
}
