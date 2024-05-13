package fr.insee.protools.backend.dto.sabiane.pilotage;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleIdentifiersDto {
	private Integer bs;
	private String ec;
	private Integer le;
	private Integer noi;
	private Integer numfa;
	private Integer rges;
	private Long ssech;
	private Integer nolog;
	private Integer nole;
	private String autre;
	private String nograp;

}