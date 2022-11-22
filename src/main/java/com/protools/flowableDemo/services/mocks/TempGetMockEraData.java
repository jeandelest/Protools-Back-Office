package com.protools.flowableDemo.services.mocks;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TempGetMockEraData implements JavaDelegate {

        @Override
        public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
            log.info("\t >> Temporary Service Task Upload Mock Era Data <<  ");

            String jsonStringList = "[{\"id\":39127127,\"internaute\":\"XJQVGN52\",\"mail\":\"martinemainardis@gmail.com\",\"sexe\":\"2\"},{\"id\":40389229,\"internaute\":\"UJYVFS73\",\"mail\":\"teddymay@hotmail.fr\",\"sexe\":\"1\"},{\"id\":39649371,\"internaute\":\"GKPTER95\",\"mail\":\"sgreffon@yahoo.fr\",\"sexe\":\"2\"},{\"id\":40253024,\"internaute\":\"NJRTGF75\",\"mail\":\"vanessa.perez.bonne@gmail.com\",\"sexe\":\"2\"},]";
            // Get data from ERA
            Gson gson = new Gson();
            List<String> responseList = (List<String>) gson.fromJson(gson.toJson(jsonStringList),List.class);
            log.info("\t \t Got responseList : " + responseList);
            List<Map> unitList = new ArrayList<>();
            for (String s : responseList) {
                log.info("\t \t >> Sample ID : {} << ", s);
                Map unitMap = gson.fromJson(gson.toJson(s), Map.class);
                unitList.add(unitMap);
            }

            delegateExecution.setVariableLocal("sample", unitList);
        }

}
