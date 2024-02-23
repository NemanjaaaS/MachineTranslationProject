package com.transperfect.machinetranslation.controllers;
import com.transperfect.machinetranslation.exceptions.ValidationException;
import com.transperfect.machinetranslation.models.TranslationRequest;
import com.transperfect.machinetranslation.service.CompanyMachineTranslationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/transperfect-api")
public class CompanyMachineTranslationController {
    private final CompanyMachineTranslationService companyMachineTranslationService;

    public CompanyMachineTranslationController(CompanyMachineTranslationService companyMachineTranslationService) {
        this.companyMachineTranslationService = companyMachineTranslationService;
    }

    /**
     * Note: Ideally, SpringDoc (Springdoc OpenAPI) would be used for API documentation generation. However, due to the absence
     * of a direct URL for accessing the Machine Translation Service, the API documentation has been provided in a separate file.
     * Method: POST
     * /transperfect-api/validate
     *
     * Endpoint for validating and translating a TranslationRequest.
     * If validate fails it ValidationException will be caught and Bad Request response returned
     * If validate is success the content will be translated from source to target language.
     *
     * @param request The TranslateRequest which will be validated and then translated
     * @return ResponseEntity with translated content if successful, or error 400 BAD_REQUEST if validation fails
     */
    @PostMapping("/validate")
    public ResponseEntity<String> validateTranslate(@RequestBody TranslationRequest request){
        try{
            companyMachineTranslationService.validateRequest(request);
            String translatedContent = companyMachineTranslationService.translate(request);
            return ResponseEntity.ok(translatedContent);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}
