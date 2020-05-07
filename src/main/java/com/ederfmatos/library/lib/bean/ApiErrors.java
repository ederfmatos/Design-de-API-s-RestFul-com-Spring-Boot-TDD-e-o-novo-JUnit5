package com.ederfmatos.library.lib.bean;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ApiErrors {

    public List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(toList());
    }
}
