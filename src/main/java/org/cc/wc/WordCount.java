package org.cc.wc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class WordCount {

    private static File file;

    private static boolean byteCount, lineCount, wordCount, charCount;

    public static void main(String[] args) throws IOException {
        getCommandLineProperties();
        file = new File(args[0]);
        byte[] fileContents = Files.readAllBytes(file.toPath());
        List<String> lines = Files.readAllLines(file.toPath());
        printFileMetrics(fileContents, lines);
    }

    private static void printFileMetrics(byte[] fileContents, List<String> lines) {
        if (lineCount)
            System.out.print(String.format("%d ", lines.size()));
        if (wordCount) {
            int count = 0;
            for (String line : lines) {
                if (!line.isBlank()) {
                    //System.out.println(line.trim().split("\s+").length);
                    count += (line.trim().split("\\s+").length);
                }
            }
            System.out.print(String.format("%d ", count));
        }
        if (byteCount)
            System.out.print(String.format("%d ", fileContents.length));
        if (charCount) {
            System.out.print(String.format("%d ", new String(fileContents).length()));
        }
        System.out.println(String.format("%s", file.toString()));
    }

    private static void getCommandLineProperties() {
        byteCount = Boolean.valueOf(System.getProperty("byteCount"));
        lineCount = Boolean.valueOf(System.getProperty("lineCount"));
        wordCount = Boolean.valueOf(System.getProperty("wordCount"));
        charCount = Boolean.valueOf(System.getProperty("charCount"));
    }
}
