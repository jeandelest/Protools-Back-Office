package fr.insee.protools.backend.dto.sabiane.pilotage;

import lombok.Getter;

@Getter
public enum SabianeTitle {
	MISTER("M","Mister"), MISS("MME","MISS");

	private final String frenchCivility;
	private final String sabianeTitle;
	SabianeTitle(String frenchCivility, String sabianeTitle) {
		this.frenchCivility = frenchCivility;
		this.sabianeTitle = sabianeTitle;
	}
}