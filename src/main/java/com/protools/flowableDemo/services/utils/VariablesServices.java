package com.protools.flowableDemo.services.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@Service
@Slf4j
public class VariablesServices {
    private VariablesServices() {
    }
    public static Collection<String> stringToCollectionString(String string){
        log.info("\t >> Converting String to Collection ... <<  ");
        Collection<String > response = new HashSet<String>() {
        };
        Gson gson = new Gson();
        try {
            String[] map = gson.fromJson(string,String[].class);
            response.addAll(Arrays.asList(map));
        } catch (Exception e) {
            log.error("\t >> ERROR : String to Collection conversion failed");
        }
        return response;
    }

}
