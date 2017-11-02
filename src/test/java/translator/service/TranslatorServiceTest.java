package translator.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import translator.Application;
import translator.domain.Language;
import translator.domain.TranslatedText;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TranslatorServiceTest {

    @Autowired
    TranslatorService translatorService;

    @Test
    public void translateTest() throws Exception {
        TranslatedText translatedText = translatorService.translate("en", "es", "This is a test of translation service");
        assertEquals("Esto es una prueba de servicio de traducción",translatedText.getTranslation());
    }

    @Test
    public void getLanguageTest() throws Exception {
        Language languageDetector = translatorService.detectLanguage("Hello world", Arrays.asList(Language.values()));
        assertEquals(Language.ENGLISH, languageDetector);
        assertNotEquals(Language.SPANISH, languageDetector);
    }

    @Test
    public void getLanguageAndTranslate() throws Exception {
        String langFrom = Language.ENGLISH.asStr();
        String langTo = Language.SPANISH.asStr();
        String textFrom = "Hi, this is a test";
        String textTo = "Hola, esto es una prueba";

        Language languageDetector = translatorService.detectLanguage(textFrom, Arrays.asList(Language.values()));
        TranslatedText translatedText = translatorService.translate(languageDetector.asStr(), langTo, textFrom);

        // Check that the detected language is correct
        assertEquals(langFrom, languageDetector.asStr());
        // Check that the translated message is correct
        assertEquals(translatedText.getTranslation(), textTo);

    }

}
