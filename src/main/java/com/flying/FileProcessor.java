package com.flying;

import com.flying.handlers.File7Zipper;
import com.flying.handlers.FunctionApplier;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

class FileProcessor {

    private static final Logger LOGGER = Logger.getLogger(FileProcessor.class.getName());

    private Properties configProperties;

    FileProcessor(final Properties configProperties) throws IOException {
        this.configProperties = configProperties;
    }

    void process() throws Exception {
        File inputDir = new File(configProperties.getProperty("input.dir"));
        FileFilter fileFilter = createFileFilter(configProperties);
        FilePathMapper filePathMapper = createFilePathMapper(configProperties);
        File7Zipper file7Zipper = createFile7Zipper(configProperties, filePathMapper);
        FunctionApplier functionApplier = new FunctionApplier(filePathMapper);
        FileHandlerChain chain = new FileHandlerChain();
        chain.addHandlers(file7Zipper, functionApplier);
        ProcessLog processLog = new ProcessLog(configProperties.getProperty("process.log"), configProperties.getProperty("error.log"));
        doProcess(inputDir, fileFilter, chain, processLog, filePathMapper);
        processLog.deleteBackupIfAny();
    }

    private void doProcess(final File file, final FileFilter fileFilter, final FileHandlerChain chain, final ProcessLog processLog, FilePathMapper filePathMapper) throws Exception {
        if (file.isDirectory()) {
            File[] children = file.listFiles(fileFilter);
            if (children != null && children.length > 0) {
                for (File child : children) {
                    doProcess(child, fileFilter, chain, processLog, filePathMapper);
                }
            }
        } else {
            List<String> completedHandlers = processLog.getCompletedHandlers(file);
            if (completedHandlers.containsAll(chain.getHandlerNames())) {
                processLog.logCompletedHandlers(file, completedHandlers);
                LOGGER.log(Level.INFO, "{0} is already processed.", file.getAbsolutePath());
            } else {
                file.getParentFile().mkdirs();
                try {
                    List<String> justCompletedHandlers = chain.handle(file, completedHandlers);
                    completedHandlers.addAll(justCompletedHandlers);
                    processLog.logCompletedHandlers(file, completedHandlers);
                    LOGGER.log(Level.INFO, "processed {0}", file.getAbsoluteFile());
                } catch (ProcessException e) {
                    completedHandlers.addAll(e.getCompletedHandlers());
                    if(!completedHandlers.isEmpty()) {
                        processLog.logCompletedHandlers(file, completedHandlers);
                    }
                    processLog.logError(file, e);
                    LOGGER.log(Level.SEVERE, "failed to process " + file.getAbsolutePath(), e);
                }
            }
        }
    }

    private FilePathMapper createFilePathMapper(final Properties properties) {
        return new FilePathMapper(
                properties.getProperty("input.dir"),
                properties.getProperty("output.dir"),
                properties.getProperty("zip.type"));
    }

    private File7Zipper createFile7Zipper(final Properties properties, final FilePathMapper filePathMapper) {
        return new File7Zipper(
                filePathMapper,
                properties.getProperty("7z.path"),
                properties.getProperty("zip.type"),
                properties.getProperty("zip.password"));
    }

    private FileFilter createFileFilter(final Properties properties) {
        return new FileFilter() {
            private String[] patterns = properties.getProperty("file.name.pattern").split(",");
            private Pattern[] regexPatterns;

            public boolean accept(File file) {
                initRegexPatterns();
                if (file.isDirectory()) {
                    return true;
                } else {
                    for (Pattern pattern : regexPatterns) {
                        if (pattern.matcher(file.getName()).find()) {
                            return true;
                        }
                    }
                    return false;
                }
            }

            private void initRegexPatterns() {
                if (regexPatterns == null) {
                    regexPatterns = new Pattern[patterns.length];
                    for (int i = 0; i < patterns.length; i++) {
                        String regex = "^" + patterns[i].replace(".", "\\.").replace("*", ".*") + "$";
                        regexPatterns[i] = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                    }
                }
            }
        };
    }
}
