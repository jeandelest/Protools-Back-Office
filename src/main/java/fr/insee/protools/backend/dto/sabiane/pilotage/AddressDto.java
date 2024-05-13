package fr.insee.protools.backend.dto.sabiane.pilotage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDto {

	/**
	 * First line of the AdressDto
	 */
	private String l1;

	/**
	 * Second line of the AdressDto
	 */
	private String l2;

	/**
	 * Third line of the AdressDto
	 */
	private String l3;

	/**
	 * Fourth line of the AdressDto
	 */
	private String l4;

	/**
	 * Fifth line of the AdressDto
	 */
	private String l5;

	/**
	 * Sixtth line of the AdressDto
	 */
	private String l6;

	/**
	 * Seventh line of the AdressDto
	 */
	private String l7;

	private Boolean elevator;
	private String building;
	private String floor;
	private String door;
	private String staircase;
	private Boolean cityPriorityDistrict;
}