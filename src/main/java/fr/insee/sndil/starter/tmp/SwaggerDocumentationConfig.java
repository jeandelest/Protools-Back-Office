package fr.insee.sndil.starter.tmp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.rest.service.api.RestResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = "org.flowable.rest.service.api")

public class SwaggerDocumentationConfig {

    @Autowired
    protected ObjectMapper objectMapper;
    
    @Bean()
    public RestResponseFactory restResponseFactory() {
        RestResponseFactory restResponseFactory = new RestResponseFactory(objectMapper);
        return restResponseFactory;
    }

}