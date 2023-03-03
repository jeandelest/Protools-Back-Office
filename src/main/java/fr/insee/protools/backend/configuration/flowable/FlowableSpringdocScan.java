package fr.insee.protools.backend.configuration.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.rest.service.api.RestResponseFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * This class allows springdoc to expose the flowable REST API :
 */
@Configuration
//The component scan so the beans (@RestController) will be availables
@ComponentScan(basePackages = "org.flowable.rest.service.api")
public class FlowableSpringdocScan {

    //We need to define this bean which is required by a bean initialized by the @ComponentScan
    @Bean()
    @ConditionalOnMissingBean
    public RestResponseFactory restResponseFactory(ObjectMapper objectMapper) {
        return  new RestResponseFactory(objectMapper);
    }

}