package com.protools.flowableDemo.services.coleman.context.providers;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NomenclatureValueProviderImpl implements NomenclatureValueProvider {

    @Value("${fr.insee.nomenclature.value.provider.uri:#{null}}")
    private String nomenclatureValueProviderUri;

    //TODO : définir realm
    String realm = " to be defined";
    @Autowired
    private WebClientHelper webClientHelper;

    @Override
    public Collection<?> getNomenclatureValue(String nomenclatureId) {
        Collection response =
            webClientHelper.getWebClientForRealm(realm, nomenclatureValueProviderUri)
            .get()
            .uri("/"+getPath(nomenclatureId))
            .retrieve()
            .bodyToMono(Collection.class)
            .block();
        return response;
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
