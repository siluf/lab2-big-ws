package translator.infrastructure;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;

import translator.domain.Language;
import translator.domain.LanguageSourceTarget;
import translator.domain.Translator;
import translator.exception.TranslatorException;

abstract class TranslatorImpl implements Translator {

    private static final String ENCODING_UTF_8 = "UTF-8";

    @Override
	@Async
    public AsyncResult<String> translate(LanguageSourceTarget languageSourceTarget, String text) {
        try {
            String encodedText = URLEncoder.encode(text, ENCODING_UTF_8);
            String from = languageSourceTarget.getSource().asStr();
            String to = languageSourceTarget.getTarget().asStr();
            return new AsyncResult<>(translateInternal(from, to, text, encodedText));
        } catch (IOException e) {
            log().error("Problems translating:" + e.getMessage(), e);
            throw new TranslatorException("Problems translating:" + e.getMessage(), e);
        }
    }

    private String translateInternal(String from, String to, String text, String encodedText) throws IOException {
        HttpRequestBase requestBase = getHttpRequest(from, to, text, encodedText);
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = httpclient.execute(requestBase);
        HttpEntity responseEntity = response.getEntity();
        String responseAsStr = transformToString(responseEntity);
        if (StringUtils.hasText(responseAsStr)) {
            return getTranslationFrom(responseAsStr);
        }
        return "";
    }

    /**
     * Convert elements to strings and concatenate them, separated by commas
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html">oracle.com Collectors</a>
     */
    @Override
    @Async
    public AsyncResult<Language> detectLanguage(String text, Collection<Language> hints) {
        try {
            String encodedText = URLEncoder.encode(text, ENCODING_UTF_8);
            String hintsAsStr = hints.stream().map(Language::asStr).collect(Collectors.joining(","));
            return new AsyncResult<>(detectLanguageInternal(encodedText, hintsAsStr));
        } catch (IOException e) {
            log().error("Problems detecting language:" + e.getMessage(), e);
            throw new TranslatorException("Problems detecting language:" + e.getMessage(), e);
        }
    }

    private Language detectLanguageInternal(String encodedText, String hints) throws IOException {
        HttpRequestBase requestBase = getHttpRequestLanguageDetector(encodedText, hints);
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = httpclient.execute(requestBase);
        HttpEntity responseEntity = response.getEntity();
        String responseAsStr = transformToString(responseEntity);
        if (StringUtils.hasText(responseAsStr)) {
            return getDetectedLanguage(responseAsStr);
        }
        return null;
    }

    protected abstract HttpRequestBase getHttpRequest(String from, String to, String text, String encodedText);

    protected abstract HttpRequestBase getHttpRequestLanguageDetector(String encodedText, String hints);

    /**
     * Get the detected language from the given message
     * @param responseAsStr: Response Language that contains the detected language
     * @return response
     */
    protected abstract Language getDetectedLanguage(String responseAsStr);

    /**
     * Get the translation text from the response
     *
     * @param responseAsStr: Response string that contains the translation
     * @return response
     */
    protected abstract String getTranslationFrom(String responseAsStr);


    private static String transformToString(HttpEntity entity) throws IOException {
        if (entity == null) {
            return "";
        }
        try (
        	Scanner scanner = new Scanner(entity.getContent(), ENCODING_UTF_8)
        ) {
        	return scanner.useDelimiter("\\A").next();
        }
    }

}
