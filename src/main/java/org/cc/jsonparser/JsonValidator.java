package org.cc.jsonparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonValidator {

    private static final String START_OBJECT = "{";
    private static final String END_OBJECT = "}";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Enter a file to validate.");
            System.exit(1);
        }
        Path path = Path.of(args[0]);
        try {
            String content = Files.readString(path);
            boolean isValid = validateJSON(content);
            if (isValid) {
                System.out.println("Input file contains a Valid JSON.");
            } else {
                System.out.println("Input file doesn't contain a Valid JSON");
                System.exit(1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static boolean validateJSON(String content) {
        return false;
    }

}
