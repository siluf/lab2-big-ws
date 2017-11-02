package translator.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import translator.domain.Language;
import translator.exception.LanguageDetectionException;
import translator.exception.TranslatorException;

@Component("yandexTranslator")
public class YandexTranslator extends TranslatorImpl {

	private ObjectMapper om = new ObjectMapper(); 
	
	@Value("${yandex.api_key}")
	private String API_KEY;
	
    @Override
    protected HttpRequestBase getHttpRequest(String from, String to, String text, String encodedText) {
		return new HttpGet("https://translate.yandex.net/api/v1.5/tr.json/translate?key="+API_KEY+"&lang="+from+"-"+to+"&text="+encodedText);
    }

    @Override
	protected HttpRequestBase getHttpRequestLanguageDetector(String encodedText, String hints) {
    	String hintHttpUrl = !hints.isEmpty() ? "&hint"+hints : "";
		return new HttpGet("https://translate.yandex.net/api/v1.5/tr.json/detect?key="+API_KEY+"&text="+encodedText+hintHttpUrl);
	}


    @Override
	protected String getTranslationFrom(String responseAsStr) {
    	try {
			return (String) om.readValue(responseAsStr, YandexResponse.class).text[0];
		} catch (Exception e) {
			throw new TranslatorException("Failed processing "+responseAsStr, e);
		}
    }

    @Override
	protected Language getDetectedLanguage(String responseAsStr) {
		try {
			return Language.fromString(om.readValue(responseAsStr, YandexLanguageDetectorResponse.class).lang);
		} catch (Exception e) {
			throw new LanguageDetectionException("Failed processing "+responseAsStr, e);
		}
	}

}

class YandexResponse {
    public String code;
    public String lang;
    public Object[] text;
}

class YandexLanguageDetectorResponse {
	public String code;
	public String lang;
}
