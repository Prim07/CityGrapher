package com.agh.bsct.algorithm.algorithms.outputwriter;

import com.agh.bsct.algorithm.algorithms.properties.AlgorithmProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

@Component
public class GnuplotOutputWriter implements AlgorithmOutputWriter {

    private static final String FILE_NAME_PREFIX = "values_for_";
    private static final String FILE_EXTENSION = ".txt";
    private static final Character GNUPLOT_COLUMN_SEPARATOR = ' ';

    private BufferedWriter bufferedWriter;
    private FileWriter fileWriter;
    private final boolean isWritingEnabled;

    private AlgorithmProperties algorithmProperties;

    @Autowired
    public GnuplotOutputWriter(AlgorithmProperties algorithmProperties) {
        this.algorithmProperties = algorithmProperties;

        this.isWritingEnabled = this.algorithmProperties.getIsWritingValuesToFileEnabled();
    }

    @Override
    public void initializeResources(String algorithmTaskId) {
        if (isWritingEnabled) {
            try {
                new File(OUTPUT_FILES_BASE_DIRECTORY).mkdirs();
                String fileName = OUTPUT_FILES_BASE_DIRECTORY + FILE_NAME_PREFIX + algorithmTaskId + FILE_EXTENSION;
                this.fileWriter = new FileWriter(fileName, false);
                this.bufferedWriter = new BufferedWriter(this.fileWriter);
            } catch (IOException e) {
                System.out.println("Error while creating FileWriter: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeLineIfEnabled(String key, String... values) {
        if (isWritingEnabled) {
            var lineToWrite = getGnuplotFormattedLine(key, values);
            try {
                bufferedWriter.write(lineToWrite);
                bufferedWriter.newLine();
            } catch (IOException e) {
                System.out.println("Error while writing to BufferedWriter line: " + lineToWrite + ". "
                        + "Error message" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closeResources() {
        if (isWritingEnabled) {
            try {
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while closing BufferedWriter: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @SafeVarargs
    public final <T extends Number> void writeLineIfEnabled(T key, T... values) {
        if (isWritingEnabled) {
            String[] valuesToString = Arrays.stream(values)
                    .map(T::toString)
                    .toArray(String[]::new);

            writeLineIfEnabled(key.toString(), valuesToString);
        }
    }

    private String getGnuplotFormattedLine(String key, String[] values) {
        StringBuilder gnuplotStyleFormattedLine = new StringBuilder(key);

        for (var value : values) {
            gnuplotStyleFormattedLine.append(GNUPLOT_COLUMN_SEPARATOR).append(value);
        }

        return gnuplotStyleFormattedLine.toString();
    }
}
