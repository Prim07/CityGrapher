package com.agh.bsct.algorithm.algorithms.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class GnuplotStyleValuesWriter implements ValuesWriter {

    private static final String FILENAME_PREFIX = "values_for_";
    private static final String FILE_EXTENSION = ".txt";
    private static final Character GNUPLOT_COLUMN_SEPARATOR = ' ';

    private BufferedWriter writer;

    public  GnuplotStyleValuesWriter(String algorithmTaskId) {
        try {
            var fileWriter = new FileWriter(FILENAME_PREFIX + algorithmTaskId + FILE_EXTENSION, false);
            this.writer = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            System.out.println("Error while creating FileWriter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void writeLine(String key, String... values) {
        var lineToWrite = getGnuplotStyleFormattedLine(key, values);
        try {
            writer.write(lineToWrite);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error while writing to BufferedWriter line: " + lineToWrite + ". "
                    + "Error message" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void closeResources() {
        try {
            writer.close();
        } catch (IOException e) {
            System.out.println("Error while closing BufferedWriter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SafeVarargs
    public final <T extends Number> void writeLine(T key, T... values) {
        String[] valuesToString = Arrays.stream(values)
                .map(T::toString)
                .toArray(String[]::new);

        writeLine(key.toString(), valuesToString);
    }

    private String getGnuplotStyleFormattedLine(String key, String[] values) {
        StringBuilder gnuplotStyleFormattedLine = new StringBuilder(key);

        for (var value : values) {
            gnuplotStyleFormattedLine.append(GNUPLOT_COLUMN_SEPARATOR).append(value);
        }

        return gnuplotStyleFormattedLine.toString();
    }
}
