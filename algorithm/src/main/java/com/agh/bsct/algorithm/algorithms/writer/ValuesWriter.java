package com.agh.bsct.algorithm.algorithms.writer;

public interface ValuesWriter {

    String OUTPUT_FILES_BASE_DIRECTORY = "written-values/";

    void initializeResources(String algorithmTaskId);

    void writeLineIfEnabled(String key, String... values);

    void closeResources();

}
