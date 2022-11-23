package com.protools.flowableDemo.services.coleman.context.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NomenclatureValueProviderImpl implements NomenclatureValueProvider {

    @Value("${fr.insee.nomenclature.value.provider.uri:#{null}}")
    private String nomenclatureValueProviderUri;

    @Autowired
    private ProviderRestTemplate restTemplate;

    @Override
    public Collection<?> getNomenclatureValue(String nomenclatureId) {
        String uri = nomenclatureValueProviderUri + "/" + getPath(nomenclatureId);

        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Collection> response = restTemplate.exchange(uri, HttpMethod.GET, request, Collection.class);

        return response.getBody();
    }

    private String getPath(String nomenclatureId)  {
        Matcher matcher = Pattern.compile("^._(.*)-\\d+-\\d+-\\d+$").matcher(nomenclatureId);

        if (!matcher.find()) {
            //TODO : avoir des types d'exceptions prévus pour ca
            throw new RuntimeException(String.format("nomenclature id %s doesn't match", nomenclatureId));
        }

        return matcher.group(1) +  "/" + nomenclatureId + ".json";
    }

}
