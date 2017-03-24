package com.flying;

import com.flying.handlers.FileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class FileHandlerChain {

    private List<FileHandler> handlers = new ArrayList<>();
    private List<String> handerNames = null;

    List<String> handle(File file, List<String> completedHandlers) throws Exception {
        List<String> justCompletedHandlers = new ArrayList<>();
        for (FileHandler handler : handlers) {
            try {
                if(!completedHandlers.contains(handler.getClass().getSimpleName())) {
                    handler.handle(file);
                    justCompletedHandlers.add(handler.getClass().getSimpleName());
                }
            } catch (Exception e) {
                throw new ProcessException(justCompletedHandlers, e);
            }
        }
        return justCompletedHandlers;
    }

    void addHandlers(FileHandler... fileHandlers) {
        Collections.addAll(handlers, fileHandlers);
    }

    List<String> getHandlerNames() {
        if(handerNames == null) {
            handerNames = handlers.stream().map(s -> s.getClass().getSimpleName()).collect(Collectors.toList());
        }
        return handerNames;
    }
}
