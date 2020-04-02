package com.conf.conferences.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiDescriptionBuilder;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.conf.conferences.ApiConstants.BAD_REQUEST_CODE;
import static com.conf.conferences.ApiConstants.BAD_REQUEST_MESSAGE;
import static com.conf.conferences.ApiConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.conf.conferences.ApiConstants.INTERNAL_SERVER_ERROR_MESSAGE;
import static com.conf.conferences.ApiConstants.OK_CODE;
import static com.conf.conferences.ApiConstants.OK_MESSAGE;

@Component
public class ApiListScanner implements ApiListingScannerPlugin {

    private final CachingOperationNameGenerator operationNames;

    @Autowired
    public ApiListScanner(CachingOperationNameGenerator operationNames) {
        this.operationNames = operationNames;
    }

    @Override
    public List<ApiDescription> apply(DocumentationContext documentationContext) {
        return Arrays.asList(
                getApiDescription("/login/google", "Google authentication"),
                getApiDescription("/login/github", "Github authentication"),
                getApiDescription("/login/facebook", "Facebook authentication"),
                getApiDescription("/login/linkedin", "Linkedin authentication")
        );
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }

    private ApiDescription getApiDescription(String path, String description) {
        return new ApiDescriptionBuilder(new Defaults().operationOrdering())
                .description(description)
                .path(path)
                .operations(Collections.singletonList(new OperationBuilder(operationNames)
                        .method(HttpMethod.GET)
                        .summary(description)
                        .responseMessages(responseMessages())
                        .tags(Collections.singleton("oauth2-authentication"))
                        .build()))
                .build();
    }

    private Set<ResponseMessage> responseMessages() { //<8>
        Set<ResponseMessage> responseMessages = new HashSet<>();
        responseMessages.add(new ResponseMessageBuilder()
                .code(OK_CODE)
                .message(OK_MESSAGE)
                .responseModel(new ModelRef("JwtResponse"))
                .build());
        responseMessages.add(new ResponseMessageBuilder()
                .code(BAD_REQUEST_CODE)
                .message(BAD_REQUEST_MESSAGE)
                .responseModel(new ModelRef("ErrorModel"))
                .build());
        responseMessages.add(new ResponseMessageBuilder()
                .code(INTERNAL_SERVER_ERROR_CODE)
                .message(INTERNAL_SERVER_ERROR_MESSAGE)
                .responseModel(new ModelRef("ErrorModel"))
                .build());
        return responseMessages;
    }
}
