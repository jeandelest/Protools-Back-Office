package fr.insee.protools.backend.dto.sabiane.pilotage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateDto {

	private Long date;
	private StateType type;
}