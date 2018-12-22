package com.agh.bsct.algorithm.algorithms.writer;

public interface ValuesWriter {

    ValuesWriter initialize(String algorithmTaskId);

    void writeLine(String key, String... values);

    void closeResources();

}
