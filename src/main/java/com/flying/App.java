package com.flying;

import java.io.FileInputStream;
import java.util.Properties;

public class App {

    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            throw new IllegalArgumentException("No configuration file specified");
        }
        Properties properties = new Properties();
        properties.load(new FileInputStream(FilePathUtil.convertPath(args[0])));
        new FileProcessor(properties).process();
    }
}
