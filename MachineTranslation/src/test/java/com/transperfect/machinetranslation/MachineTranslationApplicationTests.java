package com.transperfect.machinetranslation;

import com.transperfect.machinetranslation.exceptions.ValidationException;
import com.transperfect.machinetranslation.models.Domains;
import com.transperfect.machinetranslation.models.Languages;
import com.transperfect.machinetranslation.models.TranslationRequest;
import com.transperfect.machinetranslation.service.CompanyMachineTranslationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class MachineTranslationApplicationTests {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    @InjectMocks
    private CompanyMachineTranslationService translationService;

    MachineTranslationApplicationTests() {
    }

    static List<Languages> supportedLanguages = new ArrayList<>();
    static List<Domains> supportedDomains = new ArrayList<>();

    @BeforeAll
    static void init() {

        supportedLanguages.add(new Languages("en-US"));
        supportedLanguages.add(new Languages("fr-FR"));
        supportedLanguages.add(new Languages("de-DE"));

        supportedDomains.add(new Domains("general"));
        supportedDomains.add(new Domains("business"));
        supportedDomains.add(new Domains("academic"));
    }

    /**
     * Testing if exception will be thrown if source language is unsupported.
     * I expect that ValidationException will be caught with "Unsupported language!" message.
     */
    @Test
    void validateRequestSourceLangError() {


        TranslationRequest request = new TranslationRequest();
        request.setSourceLanguage("en-UK");
        request.setTargetLanguage("en-US");
        request.setDomain("general");
        request.setContent("Hello TransPerfect");


        translationService.setSupportedLanguages(supportedLanguages);
        translationService.setSupportedDomains(supportedDomains);
        when(restTemplateBuilder.build()).thenReturn(new RestTemplate());
        ValidationException exception = assertThrows(ValidationException.class, () -> translationService.validateRequest(request));
        assertEquals("Unsupported language!", exception.getMessage(), "Unsupported language! Should catch ValidationException");

    }

    /**
     * Testing if exception will be thrown if target language is unsupported.
     * I expect that ValidationException will be caught with "Unsupported language!" message.
     */
    @Test
    void validateRequestTargetLangError() {


        TranslationRequest request = new TranslationRequest();
        request.setSourceLanguage("en-US");
        request.setTargetLanguage("en-UK");
        request.setDomain("general");
        request.setContent("Hello TransPerfect");


        translationService.setSupportedLanguages(supportedLanguages);
        translationService.setSupportedDomains(supportedDomains);
        when(restTemplateBuilder.build()).thenReturn(new RestTemplate());
        ValidationException exception = assertThrows(ValidationException.class, () -> translationService.validateRequest(request));

        assertEquals("Unsupported language!", exception.getMessage(), "Unsupported language! Should catch ValidationException");
    }

    /**
     * Testing if exception will be thrown if domain is unsupported.
     * I expect that ValidationException will be caught with "Unsupported domain!" message.
     */
    @Test
    void validateRequestDomainError() {


        TranslationRequest request = new TranslationRequest();
        request.setSourceLanguage("en-US");
        request.setTargetLanguage("fr-FR");
        request.setDomain("technology");
        request.setContent("Hello TransPerfect");

        translationService.setSupportedLanguages(supportedLanguages);
        translationService.setSupportedDomains(supportedDomains);
        when(restTemplateBuilder.build()).thenReturn(new RestTemplate());
        ValidationException exception = assertThrows(ValidationException.class, () -> translationService.validateRequest(request));
        assertEquals("Unsupported domain!", exception.getMessage(), "Unsupported domain! Should catch ValidationException");
    }

    /**
     * Testing if exception will be thrown if number of words in content is greater than 30.
     * I expect that ValidationException will be caught with "The length of the content is greater than 30!" message.
     */
    @Test
    void validateRequestContentError() {


        TranslationRequest request = new TranslationRequest();
        request.setSourceLanguage("en-US");
        request.setTargetLanguage("fr-FR");
        request.setDomain("general");
        request.setContent("Hello TransPerfect there is more than 30 words in this content. Hello TransPerfect there is more than 30 words in this content. Hello TransPerfect there is more than 30 words in this content. Hello TransPerfect there is more than 30 words in this content.");

        translationService.setSupportedLanguages(supportedLanguages);
        translationService.setSupportedDomains(supportedDomains);
        when(restTemplateBuilder.build()).thenReturn(new RestTemplate());
        ValidationException exception = assertThrows(ValidationException.class, () -> translationService.validateRequest(request));
        assertEquals("The length of the content is greater than 30!", exception.getMessage(), "Content error! Should catch ValidationException");
    }

    @Test
    void validateRequestNoExceotionThrown() {
        TranslationRequest request = new TranslationRequest();
        request.setSourceLanguage("en-US");
        request.setTargetLanguage("fr-FR");
        request.setDomain("general");
        request.setContent("Hello TransPerfect.");

        translationService.setSupportedLanguages(supportedLanguages);
        translationService.setSupportedDomains(supportedDomains);
        when(restTemplateBuilder.build()).thenReturn(new RestTemplate());
        assertDoesNotThrow(() -> translationService.validateRequest(request));

    }

}
