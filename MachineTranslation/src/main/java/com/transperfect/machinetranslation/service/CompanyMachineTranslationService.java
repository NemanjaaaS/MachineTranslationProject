package com.transperfect.machinetranslation.service;

import com.transperfect.machinetranslation.exceptions.ValidationException;
import com.transperfect.machinetranslation.models.Domains;
import com.transperfect.machinetranslation.models.Languages;
import com.transperfect.machinetranslation.models.TranslationRequest;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Slf4j
@Service
public class CompanyMachineTranslationService implements CompanyMTService {


    private final RestTemplate restTemplate;
    /**
     * Base URL of the Machine Translation API
     * Assumptions:
     * Assuming that we have this URL which is stored in application.properties file
     * Using key 'transperfect.mt.api' which represent endpoint where application communicate with MT service
     *
     * Now in application.properties it is configured like this: "transperfect.mt.api = http://localhost:8080" but we should change to for example:
     * transperfect.mt.api = http://transpation-machine.com
     */

    @Value("${transperfect.mt.api}")
    private String apiUrl;
    private List<Languages> supportedLanguages = new ArrayList<>();
    private List<Domains> supportedDomains = new ArrayList<>();

    public CompanyMachineTranslationService(RestTemplateBuilder restTemplate) {
        this.restTemplate = restTemplate.build();
    }

    /**
     * Fetches the supported languages from Machine Translation service, it should be updated once a day using cron.
     * Function will be called every day at midnight. (We can change that depending on what user need)
     * Assumptions:
     * We assume that the endpoint for supported languages at apiUrl + "/languages" are available.
     * -In case that fetch is successful, and we got new data - we update existing supportedLanguages.
     * -In case that fetch failed the existing list of supported languages will return.

     * @return The list of supported languages.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Override
    public List<Languages> getSupportedLanguages() {
        try {
            String suppLanguagesUrl = apiUrl + "/languages";
            ResponseEntity<Languages[]> response = restTemplate.getForEntity(suppLanguagesUrl, Languages[].class);
            List<Languages> fetchedLanguages = Arrays.asList(Objects.requireNonNull(response.getBody()));
            if (!fetchedLanguages.isEmpty()) {
                supportedLanguages = fetchedLanguages;
                log.info("supportedLanguages updated");
            }
            log.info("fetchedLanguages list was empty, supportedLanguages list isn't updated");
            return supportedLanguages;

        } catch (Exception e) {
            log.error("Error updating supported languages. ", e.getMessage());
        }
        return supportedLanguages;
    }

    /**
     * Fetches the supported languages from Machine Translation service, it should be updated once a day using cron.
      * Function will be called every day at midnight.
     * Assumptions:
     * We assume that the endpoint for supported languages at apiUrl + "/domains" are available.
     * -In case that fetch is successful, and we got new data - we update existing supported domains.
     * -In case that fetch failed the existing list of supported domains will return.
     * @return The list of the supported domains.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Override
    public List<Domains> getSupportedDomains() {
        try {
            String suppDomainsUrl = apiUrl + "/domains";
            ResponseEntity<Domains[]> response = restTemplate.getForEntity(suppDomainsUrl, Domains[].class);
            List<Domains> fetchedDomains = Arrays.asList(response.getBody());
            if (!fetchedDomains.isEmpty()) {
                supportedDomains = Arrays.asList(Objects.requireNonNull(response.getBody()));
                log.info("Supported domains updated.");
            }
            log.info("fetchedDomains list is empty. Supported domains list isn't updated");
            return supportedDomains;
        } catch (Exception e) {
            log.error("Error updating supported domains.", e.getMessage());
            return supportedDomains;

        }
    }

    /**
     *Initializes the application with initial data for supported languages and domain.
     * In case that application is started for the first time, we need to insert data for supported languages and domains,
     * not to wait for the scheduled to be triggered.
     *
     * This method is calling getSupportedLanguages and getSupportedDomains to fetch the initial data.
     *
     */
    @PostConstruct
    public void getInitialData() {
        getSupportedLanguages();
        getSupportedDomains();
        log.info("Initial data for supported languages and domains");
    }

    /**
     * Translates the content from source language to target language.
     * Assumptions:
     * We assume that endpoint api + "/translate" is available
     * Translating content by sending POST request to translate endpoint
     * Request body is constructed using TranslationRequest (@param)
     * Content type is set to JSON
     *
     * @param request contain information about sourceLanguage, targetLanguage, domain, content
     * @return The translated content as String
     */
    @Override
    public String translate(TranslationRequest request) {
        String translateUrl = apiUrl + "/translate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TranslationRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(translateUrl, requestEntity, String.class);
        log.info("Content is translated.");
        return response.getBody();
    }

    /**
     * Count number of words in content by splitting it by spaces.
     * @param content The String value that should be translated, for which words need to be counted
     * @return The number of words in String.
     */
    private int countWords(String content) {
        log.info("Count number of words in content");
        return content.split("\\s+").length;
    }

    /**
     * Validate request (sourceLanguage, targetLanguage, domain, content).
     * The purpose for this validation method is to cut the costs before sending request for translation.
     *
     * This method validate if source language and target language are supported.
     * Validate if domain is supported.
     * Check if number of words in content is less than 30.
     *
     * @param request contain information about sourceLanguage, targetLanguage, domain, content
     * @throws ValidationException
     */
    @Override
    public void validateRequest(TranslationRequest request) throws ValidationException {
        if (supportedLanguages.stream()
                .noneMatch(languages -> languages.getLanguage().equals(request.getSourceLanguage())) ||
                supportedLanguages.stream()
                        .noneMatch(languages -> languages.getLanguage().equals(request.getTargetLanguage()))) {
            throw new ValidationException("Unsupported language!");
        }
        if (supportedDomains.stream()
                .noneMatch(domains -> domains.getDomains().equals(request.getDomain()))) {
            throw new ValidationException("Unsupported domain!");
        }
        if (countWords(request.getContent()) > 30) {
            throw new ValidationException("The length of the content is greater than 30!");
        }
        log.info("All required arguments are valid.");

    }


}
