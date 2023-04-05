package fr.insee.protools.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utilitary class to load ressource files in String, POJO (using jackson) or JsonNode
 */
public class ProtoolsTestUtils {

    /**
     * Load the file in <filePath> in a String
     */
    public static String asString(String filePath) {
        try {
           return IOUtils.toString(ProtoolsTestUtils.class.getClassLoader().getResource(filePath), UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Load the file in <filePath> in a POJO using jackson
     */
    public static <T> T asObject(String filePath, Class<T> targetClass) {
        try {
            String s = asString(filePath);
            return  new ObjectMapper().readValue(s,targetClass);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Load the file in <filePath> in a JsonNode using jackson
     */
    public static JsonNode asJsonNode(String jsonRessourcePath) {
        try (InputStream is = ProtoolsTestUtils.class.getClassLoader().getResourceAsStream(jsonRessourcePath)){

            return  new ObjectMapper().readTree(is);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}
