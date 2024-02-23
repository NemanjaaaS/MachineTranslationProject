package com.transperfect.machinetranslation.service;

import com.transperfect.machinetranslation.exceptions.ValidationException;
import com.transperfect.machinetranslation.models.Domains;
import com.transperfect.machinetranslation.models.Languages;
import com.transperfect.machinetranslation.models.TranslationRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CompanyMTService {
    List<Languages> getSupportedLanguages();

    List<Domains> getSupportedDomains();

    String translate(TranslationRequest request);

    void validateRequest(TranslationRequest request) throws ValidationException;

}
