package com.ederfmatos.library.lib.bean;

import com.ederfmatos.library.exception.BusinessException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class ApiErrors {

    public List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(toList());
    }

    public ApiErrors(BusinessException exception) {
        this.errors = singletonList(exception.getMessage());
    }

    public ApiErrors(ResponseStatusException exception) {
        this.errors = singletonList(exception.getReason());
    }
}
