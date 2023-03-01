package fr.insee.protools.backend.webclient;

import lombok.Data;

@Data
class KeycloakResponse {
    private String accesToken;

    private Integer expiresIn;

    public KeycloakResponse() {
    }

    public KeycloakResponse(String accesToken, Integer expiresIn) {
        this.accesToken = accesToken;
        this.expiresIn = expiresIn;
    }
}
