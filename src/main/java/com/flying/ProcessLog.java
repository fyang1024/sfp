package com.flying;

import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


class ProcessLog {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    // this FILE_HANDLER_SEPARATOR should not contain special characters in regex
    private static final String FILE_HANDLER_SEPARATOR = " handled by ";
    // this HANDLER_SEPARATOR should not contain special characters in regex
    private static final String HANDLER_SEPARATOR = ", ";

    private String processLogFile, errorLogFile, processLogBackupFile;
    private Map<String, List<String>> fileHandlers;
    private Format timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSSZ ");

    ProcessLog(String processLogFile, String errorLogFile) throws IOException {
        this.processLogFile = processLogFile;
        this.errorLogFile = errorLogFile;
        this.processLogBackupFile = processLogFile + ".bak";
        fileHandlers = new HashMap<>();
        File file = new File(processLogFile);
        if (file.exists()) {
            load(file, fileHandlers);
            if (!file.renameTo(new File(processLogBackupFile))) {
                throw new IOException("Failed to rename " + processLogFile);
            }
        }
        File parent = file.getParentFile();
        if(parent != null) {
            parent.mkdirs();
        }
        if (!file.createNewFile()) {
            throw new IOException("Failed to create new " + processLogFile);
        }
    }

    List<String> getCompletedHandlers(File file) {
        List<String> completedHandlers = fileHandlers.get(file.getAbsolutePath());
        return completedHandlers == null ? new ArrayList<>() : completedHandlers;
    }

    private void load(File file, Map<String, List<String>> fileHandlers) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        while (line != null) {
            String[] split = line.split(FILE_HANDLER_SEPARATOR);
            List<String> handlers = new ArrayList<>();
            Collections.addAll(handlers, split[1].split(HANDLER_SEPARATOR));
            fileHandlers.put(split[0], handlers);
            line = br.readLine();
        }
        br.close();
    }

    void logCompletedHandlers(File file, List<String> handlers) throws IOException {
        fileHandlers.put(file.getAbsolutePath(), handlers);
        Writer writer = new BufferedWriter(new FileWriter(processLogFile, true));
        writer.append(file.getAbsolutePath()).
                append(FILE_HANDLER_SEPARATOR).
                append(handlers.stream().map(s -> s).collect(Collectors.joining(HANDLER_SEPARATOR))).
                append(LINE_SEPARATOR);
        writer.flush();
        writer.close();
    }

    void logError(File file, Exception e) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(errorLogFile, true));
        writer.append(timeFormat.format(Calendar.getInstance().getTime())).
                append(file.getAbsolutePath()).
                append("Error - ").append(e.getMessage()).
                append(LINE_SEPARATOR);
        writer.flush();
        writer.close();
    }

    void deleteBackupIfAny() {
        File backup = new File(processLogBackupFile);
        if (backup.exists()) {
            backup.deleteOnExit();
        }
    }
}
