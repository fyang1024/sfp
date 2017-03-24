package com.flying.handlers;

import com.flying.FilePathMapper;

import java.io.File;

public class FunctionApplier implements FileHandler {

    private FilePathMapper filePathMapper;

    public FunctionApplier(FilePathMapper filePathMapper) {
        this.filePathMapper = filePathMapper;
    }

    public void handle(File file) throws Exception {
        int result = applyFunction(filePathMapper.getOutputFilePath(file.getAbsolutePath()));
        if (result != 0) {
            throw new Exception("Function returned " + result);
        }
    }

    private int applyFunction(String outputFilePath) {
        //TODO implement me
        return 0;
    }

}
