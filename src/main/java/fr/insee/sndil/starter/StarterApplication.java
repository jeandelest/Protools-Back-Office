package fr.insee.sndil.starter;

import fr.insee.sndil.starter.configuration.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication public class StarterApplication {

        public static void main(String[] args) {
                configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);        }

        public static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder){
                return springApplicationBuilder.sources(StarterApplication.class)
                    .listeners(new PropertiesLogger());
        }

}
