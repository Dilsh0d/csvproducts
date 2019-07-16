package com.csv.products;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Dilsh0d Tadjiev on 16.07.2019 11:15.
 * @project: Electron-Ticket
 * @version: 2.0
 */
public class CreateCsvFile {
    private static final char DEFAULT_SEPARATOR = ';';

    public static void main(String[] args) throws IOException {
        float min = 100f,max = 100_000f;
        for (int file=0;file<100;file++) {
            FileWriter writer = new FileWriter(args[0] + File.separator + String.valueOf(file) + ".csv");// D:\csv-files
            for(int row=0;row<1_000_000;row++){
                double price = min + Math.random() * (max - min);
                writeLine(writer, Arrays.asList(String.valueOf(row), "Product Name"+row, "Condation"+row, "State"+row,String.valueOf(Math.floor(price))));// Each file contains 5 columns: product ID (integer), Name (string), Condition (string), State (string), Price (float).
            }
            writer.flush();
            writer.close();
        }
    }

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());


    }

    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }
}
