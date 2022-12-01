package com.protools.flowableDemo.services.utils;

import com.protools.flowableDemo.beans.XmlErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

@Service
@Slf4j
public class XmlValidator {

    public boolean isValid(String xsdPath, String xmlFile) throws IOException, SAXException {
        Source source = new StreamSource(new StringReader(xmlFile));
        Validator validator = initValidator(xsdPath);
        try {
            validator.validate(source);
            return true;
        } catch (SAXException e) {
            return false;
        }
    }

    public List<SAXParseException> listParsingExceptions(String xsdPath, String xmlFile) throws IOException, SAXException {
        Source source = new StreamSource(new StringReader(xmlFile));
        XmlErrorHandler xsdErrorHandler = new XmlErrorHandler();
        Validator validator = initValidator(xsdPath);
        validator.setErrorHandler(xsdErrorHandler);
        try {
            validator.validate(source);
        } catch (SAXParseException e) {}
        xsdErrorHandler.getExceptions().forEach(e -> log.info(e.getMessage()));
        return xsdErrorHandler.getExceptions();
    }

    private Validator initValidator(String xsdPath) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(getFile(xsdPath));
        Schema schema = factory.newSchema(schemaFile);
        return schema.newValidator();
    }

    private File getFile(String location) {
        return new File(getClass().getClassLoader().getResource(location).getFile());
    }

}
