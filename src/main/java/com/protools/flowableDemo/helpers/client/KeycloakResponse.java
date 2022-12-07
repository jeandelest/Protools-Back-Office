package com.protools.flowableDemo.helpers.client;

import lombok.Data;

@Data
class KeycloakResponse {
    private String access_token;

    private Integer expires_in;

    public KeycloakResponse() {
    }

    public KeycloakResponse(String access_token, Integer expires_in) {
        this.access_token = access_token;
        this.expires_in = expires_in;
    }
}
