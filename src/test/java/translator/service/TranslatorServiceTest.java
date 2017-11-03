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
        TranslatedText translatedText = translatorService.translate("es", "bg", "Nada se consigue sin esfuerzo y paciencia");
        assertEquals("Нищо не се постига без усилия и търпение",translatedText.getTranslation());
    }

}
