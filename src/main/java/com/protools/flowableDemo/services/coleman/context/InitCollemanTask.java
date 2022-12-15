package com.protools.flowableDemo.services.coleman.context;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.protools.flowableDemo.helpers.client.WebClientHelper;
import com.protools.flowableDemo.services.coleman.context.dto.Campaign;
import com.protools.flowableDemo.services.coleman.context.dto.Nomenclature;
import com.protools.flowableDemo.services.coleman.context.dto.QuestionnaireModel;
import com.protools.flowableDemo.services.coleman.context.enums.CollectionPlatform;
import com.protools.flowableDemo.services.coleman.context.providers.NomenclatureValueProvider;
import com.protools.flowableDemo.services.coleman.context.providers.QuestionnaireModelValueProvider;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.protools.flowableDemo.helpers.client.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_COLEMAN_QUESTIONNAIRE;

@Service
@Slf4j
public class InitCollemanTask implements JavaDelegate {
    @Autowired
    private NomenclatureValueProvider nomenclatureValueProvider;


    @Autowired
    private QuestionnaireModelValueProvider questionnaireModelValueProvider;

    @Autowired
    WebClientHelper webClientHelper;


    public void lireXML(String xmlContextFilecontent){

        XmlMapper xmlMapper = XmlMapper.builder()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
        try {
            Campaign campagne = xmlMapper.readValue(xmlContextFilecontent, Campaign.class);
            treatNomenclatureAndModels(campagne.getNomenclatures(), campagne.getQuestionnaireModels());
        }
        catch(UnrecognizedPropertyException ex){
            log.error("Unknown element found in xml :  {}",ex.getMessage() );
            throw new RuntimeException(ex);
        }
        catch (IOException ex) {
            //TODO : on fait quoi en cas d'erreur à part la re-throw en runtime?
            log.error("IOException:  {}",ex.getMessage() );
            throw new RuntimeException(ex);
        }
    }

    public void treatNomenclatureAndModels(Collection<Nomenclature> nomenclatures, Collection<QuestionnaireModel> models) {

        Map<String, String> nomenclatureLabelsMappedById = getNomenclatureLabelsMappedById(nomenclatures);

        for (String id : getAllRequiredNomenclaturesIds(models)) {
            String label = nomenclatureLabelsMappedById.get(id);
            Collection<?> value = nomenclatureValueProvider.getNomenclatureValue(id);
            postNomenclature(new Nomenclature(id, label, value));
        }

        for (QuestionnaireModel model : models) {
            String id = model.getId();
            String label = model.getLabel();
            Collection<String> requiredNomenclatureIds = model.getRequiredNomenclatureIds();
            //TODO : déplacer le code ici?
            Map<?, ?> value = questionnaireModelValueProvider.getQuestionnaireModelValue(
                    CollectionPlatform.coleman, model.getId());

            postQuestionnaireModel(new QuestionnaireModel(id, label, requiredNomenclatureIds, value));
        }
    }

    private Collection<String> getAllRequiredNomenclaturesIds(Collection<QuestionnaireModel> models) {
        Set<String> allRequiredNomenclaturesIds = new HashSet<>();

        for (QuestionnaireModel model : models) {
            allRequiredNomenclaturesIds.addAll(model.getRequiredNomenclatureIds());
        }

        return allRequiredNomenclaturesIds;
    }

    private Map<String, String> getNomenclatureLabelsMappedById(Collection<Nomenclature> nomenclatures) {
        return nomenclatures.stream().collect(Collectors.toMap(Nomenclature::getId, Nomenclature::getLabel));
    }

    private void postNomenclature(Nomenclature nomenclature) {

        Nomenclature nomenclatureRécupérée =
            webClientHelper.getWebClient(KNOWN_API_COLEMAN_QUESTIONNAIRE).post()
            .uri("/nomenclature")
            .bodyValue(nomenclature)
            .retrieve()
            .bodyToMono(Nomenclature.class)
            .block();

        log.info("postNomenclature: response= "+nomenclatureRécupérée);
        //TODO:  gestion des erreurs (ex: 403...)
    }

    private void postQuestionnaireModel(QuestionnaireModel questionnaireModel) {
        String uri =  "/questionnaire-models";
        log.info("postQuestionnaireModel: {}",questionnaireModel);
        //TODO: reuse the same client in this class? warning, in this case, can it be a singleton?
        QuestionnaireModel response = webClientHelper.getWebClient(KNOWN_API_COLEMAN_QUESTIONNAIRE)
            .post()
            .uri(uri)
            .body(Mono.just(questionnaireModel),QuestionnaireModel.class)
            .retrieve()
            .bodyToMono(QuestionnaireModel.class)
            .block();
        log.info("postQuestionnaireModel: response={} ",response);

        //TODO: gestion des erreurs??
    }

    @Override public void execute(DelegateExecution execution) {
        String xmlContent = (String) execution.getVariable("contextRawFile");
        lireXML(xmlContent);
    }



    //TODO : a externaliser
    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Request: \n");
                //append clientRequest method and url
                clientRequest
                    .headers()
                    .forEach((name, values) -> values.forEach(value -> sb.append("["+name+"="+value+"]")));
                log.debug(sb.toString());
            }
            return Mono.just(clientRequest);
        });
    }

    ExchangeFilterFunction logResponse () {
        return ExchangeFilterFunction.ofResponseProcessor (clientResponse -> {
            if (log.isDebugEnabled()) {
                String message= String.format("Response : [header=%s] [body=%s]",clientResponse.headers(), clientResponse.bodyToMono(String.class)

                    );
                log.debug(message);
            }
            return Mono.just(clientResponse);
        });
    }

}
