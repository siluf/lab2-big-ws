package translator.web.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import translator.domain.Language;
import translator.domain.TranslatedText;
import translator.exception.LanguageDetectionException;
import translator.exception.TranslatorException;
import translator.service.TranslatorService;
import translator.web.ws.schema.GetTranslationRequest;
import translator.web.ws.schema.GetTranslationResponse;
import translator.web.ws.schema.GetTranslationWithLanguageDetectionRequest;
import translator.web.ws.schema.GetTranslationWithLanguageDetectionResponse;

import java.util.Arrays;

@Endpoint
public class TranslatorEndpoint {

	@Autowired
	TranslatorService translatorService;

	@PayloadRoot(namespace = "http://translator/web/ws/schema", localPart = "getTranslationRequest")
	@ResponsePayload
	public GetTranslationResponse translator(@RequestPayload GetTranslationRequest request) {
		GetTranslationResponse response = new GetTranslationResponse();
		try {
			TranslatedText translatedText = translatorService.translate(request.getLangFrom(), request.getLangTo(),
					request.getText());
			response.setResultCode("ok");
			response.setTranslation(translatedText.getTranslation());
		} catch (TranslatorException e) {
			response.setResultCode("error");
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@PayloadRoot(namespace = "http://translator/web/ws/schema", localPart = "getTranslationWithLanguageDetectionRequest")
	@ResponsePayload
	public GetTranslationWithLanguageDetectionResponse translator(@RequestPayload GetTranslationWithLanguageDetectionRequest request) {
		GetTranslationWithLanguageDetectionResponse response = new GetTranslationWithLanguageDetectionResponse();
		try {
			Language languageDetector = translatorService.detectLanguage(request.getText(), Arrays.asList(Language.values()));
			TranslatedText translatedText = translatorService.translate(languageDetector.asStr(), request.getLangTo(), request.getText());

			response.setResultCode("ok");
			response.setTranslation(translatedText.getTranslation());
		} catch (TranslatorException | LanguageDetectionException e) {
			response.setResultCode("error");
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

}
