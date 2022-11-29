package com.protools.flowableDemo.services.mocks;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TempGetMockEraData implements JavaDelegate {

        @Override
        public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
            log.info("\t >> Temporary Service Task Upload Mock Era Data <<  ");

            String jsonStringList = "{\"id\":39127127,\"internaute\":\"B2WHA8R\",\"mail\":\"martinemainardis@gmail.com\",\"sexe\":\"2\"},{\"id\":40389229,\"internaute\":\"UJYVFS73\",\"mail\":\"teddymay@hotmail.fr\",\"sexe\":\"1\"},{\"id\":39649371,\"internaute\":\"GKPTER95\",\"mail\":\"sgreffon@yahoo.fr\",\"sexe\":\"2\"},{\"id\":40253024,\"internaute\":\"NJRTGF75\",\"mail\":\"vanessa.perez.bonne@gmail.com\",\"sexe\":\"2\"},";
            // Get data from ERA
            Gson gson = new Gson();
            List<String> responseList = new ArrayList<>(Arrays.asList(jsonStringList.split("},")));
            log.info("\t \t Got responseList : " + responseList);
            List<Map> unitList = new ArrayList<>();
            for (String s : responseList) {

                Map unitMap = gson.fromJson(s+"}", Map.class);
                unitList.add(unitMap);
            }
            log.info("\t \t Sample unitList : " + unitList);
            delegateExecution.setVariable("sample", unitList);
        }

}
