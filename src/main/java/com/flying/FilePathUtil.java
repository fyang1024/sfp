package com.flying;

public class FilePathUtil {
    private static boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    static String convertPath(String path) {
        if (isWindows) return path.replace("\\", "\\\\");
        return path;
    }
}
