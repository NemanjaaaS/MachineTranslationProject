package com.transperfect.machinetranslation.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TranslationRequest {
    private String sourceLanguage;
    private String targetLanguage;
    private String domain;
    private String content;
}
