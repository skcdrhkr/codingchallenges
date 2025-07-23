package org.cc.jsonparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class JsonValidatorSampleTest {

    private static final String[] validJsons = {
            "./step1/valid.json",
            "./step2/valid.json",
            "./step2/valid2.json",
            "./step3/valid.json",
            "./step4/valid.json",
            "./step4/valid2.json"
    };

    private static final String[] invalidJsons = {
            "./step1/invalid.json",
            "./step2/invalid.json",
            "./step2/invalid2.json",
            "./step3/invalid.json",
            "./step4/invalid.json",
    };

    // Helper method to validate a single JSON file
    private void validateJsonFile(String jsonFile, boolean expectedValid) {
        try (InputStream input = getClass().getResourceAsStream(jsonFile)) {
            Assertions.assertNotNull(input, "File not found: " + jsonFile);
            String inputString = new String(input.readAllBytes());
            if (expectedValid) {
                Assertions.assertTrue(JsonValidator.validateJSON(inputString),
                        "Expected valid JSON but validation failed for: " + jsonFile);
            } else {
                Assertions.assertFalse(JsonValidator.validateJSON(inputString),
                        "Expected invalid JSON but validation passed for: " + jsonFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + jsonFile, e);
        }
    }

    // Individual test cases for VALID JSON files

    @Test
    public void testValidJson_Step1() {
        validateJsonFile("./step1/valid.json", true);
    }

    @Test
    public void testValidJson_Step2_File1() {
        validateJsonFile("./step2/valid.json", true);
    }

    @Test
    public void testValidJson_Step2_File2() {
        validateJsonFile("./step2/valid2.json", true);
    }

    @Test
    public void testValidJson_Step3() {
        validateJsonFile("./step3/valid.json", true);
    }

    @Test
    public void testValidJson_Step4_File1() {
        validateJsonFile("./step4/valid.json", true);
    }

    @Test
    public void testValidJson_Step4_File2() {
        validateJsonFile("./step4/valid2.json", true);
    }

    // Individual test cases for INVALID JSON files

    @Test
    public void testInvalidJson_Step1() {
        validateJsonFile("./step1/invalid.json", false);
    }

    @Test
    public void testInvalidJson_Step2_File1() {
        validateJsonFile("./step2/invalid.json", false);
    }

    @Test
    public void testInvalidJson_Step2_File2() {
        validateJsonFile("./step2/invalid2.json", false);
    }

    @Test
    public void testInvalidJson_Step3() {
        validateJsonFile("./step3/invalid.json", false);
    }

    @Test
    public void testInvalidJson_Step4() {
        validateJsonFile("./step4/invalid.json", false);
    }

    // Optional: Keep the original loop-based tests as integration tests
/*
    @Test
    public void testAllValidJsons_Integration() {
        for (String jsonFile : validJsons) {
            validateJsonFile(jsonFile, true);
        }
    }

    @Test
    public void testAllInvalidJsons_Integration() {
        for (String jsonFile : invalidJsons) {
            validateJsonFile(jsonFile, false);
        }
    } */
}
