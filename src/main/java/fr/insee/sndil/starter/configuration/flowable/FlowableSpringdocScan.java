package fr.insee.sndil.starter.configuration.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.rest.service.api.RestResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    protected ObjectMapper objectMapper;

    //We need to define this bean which is required by a bean initialized by the @ComponentScan
    @Bean()
    public RestResponseFactory restResponseFactory() {
        RestResponseFactory restResponseFactory = new RestResponseFactory(objectMapper);
        return restResponseFactory;
    }

}