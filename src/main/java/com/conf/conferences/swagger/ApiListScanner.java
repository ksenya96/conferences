package com.conf.conferences.swagger;

import io.swagger.models.RefModel;
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
import static com.conf.conferences.ApiConstants.UNAUTHORIZED_CODE;
import static com.conf.conferences.ApiConstants.UNAUTHORIZED_MESSAGE;

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
                getApiDescription("/login/google", "Google registration and authentication",
                        responseMessages(new ModelRef("JwtResponse")), "oauth2-authentication"),
                getApiDescription("/login/github", "Github registration and authentication",
                        responseMessages(new ModelRef("JwtResponse")), "oauth2-authentication"),
                getApiDescription("/login/facebook", "Facebook registration and authentication",
                        responseMessages(new ModelRef("JwtResponse")), "oauth2-authentication"),
                getApiDescription("/login/linkedin", "Linkedin registration and authentication",
                        responseMessages(new ModelRef("JwtResponse")), "oauth2-authentication"),
                getApiDescription("/logout", "Logout",
                        responseMessages(null), "authentication-controller")
        );
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }

    private ApiDescription getApiDescription(String path, String description,
                                             Set<ResponseMessage> responseMessages, String tag) {
        return new ApiDescriptionBuilder(new Defaults().operationOrdering())
                .description(description)
                .path(path)
                .operations(Collections.singletonList(new OperationBuilder(operationNames)
                        .method(HttpMethod.GET)
                        .summary(description)
                        .responseMessages(responseMessages)
                        .tags(Collections.singleton(tag))
                        .build()))
                .build();
    }

    private Set<ResponseMessage> responseMessages(ModelRef okResponseModel) { //<8>
        Set<ResponseMessage> responseMessages = new HashSet<>();
        responseMessages.add(new ResponseMessageBuilder()
                .code(OK_CODE)
                .message(OK_MESSAGE)
                .responseModel(okResponseModel)
                .build());
        responseMessages.add(new ResponseMessageBuilder()
                .code(BAD_REQUEST_CODE)
                .message(BAD_REQUEST_MESSAGE)
                .responseModel(new ModelRef("ErrorModel"))
                .build());
        responseMessages.add(new ResponseMessageBuilder()
                .code(UNAUTHORIZED_CODE)
                .message(UNAUTHORIZED_MESSAGE)
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
