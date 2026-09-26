package com.bbdu.placement.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility to print pretty ASCII tables in the console.
 */
public class TablePrinter {

    public static void printTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) return;

        int numCols = headers.length;
        int[] colWidths = new int[numCols];

        for (int i = 0; i < numCols; i++) {
            colWidths[i] = headers[i].length();
        }

        for (String[] row : rows) {
            for (int i = 0; i < numCols && i < row.length; i++) {
                if (row[i] != null && row[i].length() > colWidths[i]) {
                    colWidths[i] = row[i].length();
                }
            }
        }

        // Add padding
        for (int i = 0; i < numCols; i++) {
            colWidths[i] += 2;
        }

        // Separator line
        StringBuilder sep = new StringBuilder("+");
        for (int w : colWidths) {
            for (int j = 0; j < w; j++) sep.append("-");
            sep.append("+");
        }

        System.out.println(sep);

        // Header
        StringBuilder headerLine = new StringBuilder("|");
        for (int i = 0; i < numCols; i++) {
            headerLine.append(centerString(headers[i], colWidths[i])).append("|");
        }
        System.out.println(headerLine);
        System.out.println(sep);

        // Rows
        for (String[] row : rows) {
            StringBuilder rowLine = new StringBuilder("|");
            for (int i = 0; i < numCols; i++) {
                String val = (i < row.length && row[i] != null) ? row[i] : "";
                rowLine.append(" ").append(padRight(val, colWidths[i] - 1)).append("|");
            }
            System.out.println(rowLine);
        }
        System.out.println(sep);
    }

    private static String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() >= n) return s.substring(0, n);
        return String.format("%-" + n + "s", s);
    }

    private static String centerString(String s, int width) {
        if (s == null) s = "";
        int padSize = width - s.length();
        int padStart = padSize / 2;
        int padEnd = padSize - padStart;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padStart; i++) sb.append(" ");
        sb.append(s);
        for (int i = 0; i < padEnd; i++) sb.append(" ");
        return sb.toString();
    }
}
