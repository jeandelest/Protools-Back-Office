package com.protools.flowableDemo.services.Utils;

import com.protools.flowableDemo.controllers.BpmnExportController;
import com.protools.flowableDemo.services.FamillePOCService.GetSampleFamille;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RessourceUtils {
    private static Logger logger = LogManager.getLogger(RessourceUtils.class);
    public static String getResourceFileAsString(String fileName) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        String fileNameFinal = "processes/"+fileName+".bpmn20.xml";
        logger.info("\t >> Getting file : "+fileNameFinal);


        InputStream file = classloader.getResourceAsStream(fileNameFinal);
        logger.info("\t >> File found : "+ file.toString());
        String lines = new String();
        Document doc = null;

        try (InputStreamReader streamReader =
                     new InputStreamReader(file, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {

                lines += line;
            }
            DocumentBuilder builder = dbf.newDocumentBuilder();

            doc = builder.parse(new InputSource(new StringReader(lines)));


        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        // Convert to String (even tho we apparently want a xml file)
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
            transformer.transform(domSource, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();

    }
}
