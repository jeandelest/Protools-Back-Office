package com.protools.flowableDemo.services.Utils;

import com.protools.flowableDemo.controllers.BpmnExportController;
import com.protools.flowableDemo.services.FamillePOCService.GetSampleFamille;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RessourceUtils {
    private static Logger logger = LogManager.getLogger(RessourceUtils.class);
    public static String getResourceFileAsString(String fileName) throws IOException {
        String fileNameFinal = "processes/"+fileName+".bpmn20.xml";
        logger.info("\t >> Getting file : "+fileNameFinal);

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream file = classloader.getResourceAsStream(fileNameFinal);
        logger.info("\t >> File found : "+ file.toString());
        String lines = new String();

        try (InputStreamReader streamReader =
                     new InputStreamReader(file, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {

                lines += line;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;

    }
}
