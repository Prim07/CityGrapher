package com.agh.bsct.algorithm.algorithms.writer;

public interface ValuesWriter {

    String OUTPUT_FILES_BASE_DIRECTORY = "written-values/";

    void writeLine(String key, String... values);

    void closeResources();

}
