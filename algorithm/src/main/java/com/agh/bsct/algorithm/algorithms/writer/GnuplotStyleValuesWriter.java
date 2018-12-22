package com.agh.bsct.algorithm.algorithms.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GnuplotStyleValuesWriter implements ValuesWriter {

    private static final String FILENAME_PREFIX = "values_for_";
    private static final String FILE_EXTENSION = ".txt";
    private static final Character GNUPLOT_COLUMN_SEPARATOR = ' ';

    private BufferedWriter writer;

    public GnuplotStyleValuesWriter(String algorithmTaskId) {
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
        } catch (IOException e) {
            System.out.println("Error while writing FileWriter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getGnuplotStyleFormattedLine(String key, String[] values) {
        StringBuilder gnuplotStyleFormattedLine = new StringBuilder(key);

        for (var value : values) {
            gnuplotStyleFormattedLine.append(GNUPLOT_COLUMN_SEPARATOR).append(value);
        }

        return gnuplotStyleFormattedLine.toString();
    }
}
