package com.flying.handlers;

import com.flying.FilePathMapper;

import java.io.File;

public class File7Zipper implements FileHandler {

    private String executablePath;
    private String zipType;
    private String zipPassword;
    private FilePathMapper filePathMapper;

    public File7Zipper(FilePathMapper filePathMapper, String executablePath, String zipType, String zipPassword) {
        this.filePathMapper = filePathMapper;
        this.executablePath = executablePath;
        this.zipType = zipType;
        this.zipPassword = zipPassword;
    }

    public void handle(File file) throws Exception {
        String inputFilePath = file.getAbsolutePath();
        String outputFilePath = filePathMapper.getOutputFilePath(inputFilePath);
        File outputFile = new File(outputFilePath);
        new File(outputFile.getParent()).mkdirs();
        String command = buildCommand(inputFilePath, outputFilePath);
        Process process = Runtime.getRuntime().exec(command);
        int exitValue = process.waitFor();
        if (exitValue != 0) {
            throw new Exception("Zip failed (exit code: " + exitValue + ", command: " + command + ")");
        }
    }

    private String buildCommand(String inputFilePath, String outputFilePath) {
        return executablePath +
                " a" +
                " -t" + zipType +
                " " + addQuoteIfNeeded(outputFilePath) +
                " " + addQuoteIfNeeded(inputFilePath) +
                " -p" + zipPassword;
    }

    private String addQuoteIfNeeded(String path) {
        if(path.contains(" ")) {
            return "\"" + path + "\"";
        }
        return path;
    }
}
