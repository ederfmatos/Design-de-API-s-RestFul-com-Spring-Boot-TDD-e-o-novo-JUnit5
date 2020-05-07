package com.ederfmatos.library.lib;

import org.modelmapper.ModelMapper;

public class LibraryMapper {

    private LibraryMapper() {}

    private static final ModelMapper mapper = new ModelMapper();

    public static ModelMapper getMapper() {
        return mapper;
    }

}
