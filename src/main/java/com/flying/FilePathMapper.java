package com.flying;

public class FilePathMapper {
    private String inputDir;
    private String outputDir;
    private String zipType;

    FilePathMapper(String inputDir, String outputDir, String zipType) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.zipType = zipType;
    }

    public String getOutputFilePath(String inputFilePath) {
        return outputDir + inputFilePath.substring(inputDir.length()) + "." + zipType;
    }

}
