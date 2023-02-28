package fr.insee.sndil.starter;

import fr.insee.sndil.starter.properties.RestAppProperties;
import fr.insee.sndil.starter.conf.BootstrapConfiguration;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;

import fr.insee.sndil.starter.configuration.PropertiesLogger;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.rest.service.api.runtime.process.ProcessInstanceCollectionResource;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import java.util.List;

@EnableConfigurationProperties({
    RestAppProperties.class
})
@Import({
    BootstrapConfiguration.class
})
@SpringBootApplication
public class StarterApplication {
        org.flowable.rest.service.api.RestResponseFactory v;
        public static void main(String[] args) {
                configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);        }

        public static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder){
                return springApplicationBuilder.sources(StarterApplication.class)
                    .listeners(new PropertiesLogger());
        }
        /*@EventListener(ApplicationReadyEvent.class)
        public void startApp() {

                var webClient = WebClient.create("http://time.jsontest.com/");

                Mono<TimeResponse> result = webClient.get()
                    .retrieve()
                    .bodyToMono(TimeResponse.class);

                result.subscribe(res -> logger.info("{}", res));
        }*/

}
