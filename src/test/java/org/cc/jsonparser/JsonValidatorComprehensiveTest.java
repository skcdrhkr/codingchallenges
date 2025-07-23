package org.cc.jsonparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class JsonValidatorComprehensiveTest {
    public static final String[] VALID_JSON_FILES = {
            "./testjson/valid_01_empty_object.json",
            "./testjson/valid_02_basic_object.json",
            "./testjson/valid_03_mixed_types.json",
            "./testjson/valid_04_empty_array.json",
            "./testjson/valid_05_number_array.json",
            "./testjson/valid_06_string_array.json",
            "./testjson/valid_07_object_array.json",
            "./testjson/valid_08_nested_object.json",
            "./testjson/valid_09_all_types.json",
            "./testjson/valid_10_edge_numbers.json",
            "./testjson/valid_11_unicode.json",
            "./testjson/valid_12_empty_values.json",
            "./testjson/valid_13_control_chars.json",
            "./testjson/valid_14_max_values.json"
    };

    public static final String[] INVALID_JSON_FILES = {
            "./testjson/invalid_01_unquoted_keys.json",
            "./testjson/invalid_02_single_quotes.json",
            "./testjson/invalid_03_trailing_comma_object.json",
            "./testjson/invalid_04_trailing_comma_array.json",
            "./testjson/invalid_05_missing_comma_object.json",
            "./testjson/invalid_06_missing_comma_array.json",
            "./testjson/invalid_07_unmatched_brace.json",
            "./testjson/invalid_08_extra_brace.json",
            "./testjson/invalid_09_unmatched_bracket.json",
            "./testjson/invalid_10_extra_bracket.json",
            "./testjson/invalid_11_undefined_value.json",
            "./testjson/invalid_12_infinity.json",
            "./testjson/invalid_13_unescaped_quotes.json",
            "./testjson/invalid_14_comments.json",
            "./testjson/invalid_15_missing_value.json",
            "./testjson/invalid_16_missing_key.json",
            "./testjson/invalid_17_duplicate_keys.json",
            "./testjson/invalid_18_invalid_escape.json",
            "./testjson/invalid_19_leading_zero.json",
            "./testjson/invalid_20_incomplete_object.json",
            "./testjson/valid_21_bare_string.json",
            "./testjson/valid_22_bare_number.json",
            "./testjson/valid_23_bare_boolean.json",
            "./testjson/invalid_24_semicolon_separator.json",
            "./testjson/invalid_25_hex_number.json"
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
    public void testValid_01_EmptyObject() {
        validateJsonFile("./testjson/valid_01_empty_object.json", true);
    }

    @Test
    public void testValid_02_BasicObject() {
        validateJsonFile("./testjson/valid_02_basic_object.json", true);
    }

    @Test
    public void testValid_03_MixedTypes() {
        validateJsonFile("./testjson/valid_03_mixed_types.json", true);
    }

    @Test
    public void testValid_04_EmptyArray() {
        validateJsonFile("./testjson/valid_04_empty_array.json", true);
    }

    @Test
    public void testValid_05_NumberArray() {
        validateJsonFile("./testjson/valid_05_number_array.json", true);
    }

    @Test
    public void testValid_06_StringArray() {
        validateJsonFile("./testjson/valid_06_string_array.json", true);
    }

    @Test
    public void testValid_07_ObjectArray() {
        validateJsonFile("./testjson/valid_07_object_array.json", true);
    }

    @Test
    public void testValid_08_NestedObject() {
        validateJsonFile("./testjson/valid_08_nested_object.json", true);
    }

    @Test
    public void testValid_09_AllTypes() {
        validateJsonFile("./testjson/valid_09_all_types.json", true);
    }

    @Test
    public void testValid_10_EdgeNumbers() {
        validateJsonFile("./testjson/valid_10_edge_numbers.json", true);
    }

    @Test
    public void testValid_11_Unicode() {
        validateJsonFile("./testjson/valid_11_unicode.json", true);
    }

    @Test
    public void testValid_12_EmptyValues() {
        validateJsonFile("./testjson/valid_12_empty_values.json", true);
    }

    @Test
    public void testValid_13_ControlChars() {
        validateJsonFile("./testjson/valid_13_control_chars.json", true);
    }

    @Test
    public void testValid_14_MaxValues() {
        validateJsonFile("./testjson/valid_14_max_values.json", true);
    }

    // Individual test cases for INVALID JSON files

    @Test
    public void testInvalid_01_UnquotedKeys() {
        validateJsonFile("./testjson/invalid_01_unquoted_keys.json", false);
    }

    @Test
    public void testInvalid_02_SingleQuotes() {
        validateJsonFile("./testjson/invalid_02_single_quotes.json", false);
    }

    @Test
    public void testInvalid_03_TrailingCommaObject() {
        validateJsonFile("./testjson/invalid_03_trailing_comma_object.json", false);
    }

    @Test
    public void testInvalid_04_TrailingCommaArray() {
        validateJsonFile("./testjson/invalid_04_trailing_comma_array.json", false);
    }

    @Test
    public void testInvalid_05_MissingCommaObject() {
        validateJsonFile("./testjson/invalid_05_missing_comma_object.json", false);
    }

    @Test
    public void testInvalid_06_MissingCommaArray() {
        validateJsonFile("./testjson/invalid_06_missing_comma_array.json", false);
    }

    @Test
    public void testInvalid_07_UnmatchedBrace() {
        validateJsonFile("./testjson/invalid_07_unmatched_brace.json", false);
    }

    @Test
    public void testInvalid_08_ExtraBrace() {
        validateJsonFile("./testjson/invalid_08_extra_brace.json", false);
    }

    @Test
    public void testInvalid_09_UnmatchedBracket() {
        validateJsonFile("./testjson/invalid_09_unmatched_bracket.json", false);
    }

    @Test
    public void testInvalid_10_ExtraBracket() {
        validateJsonFile("./testjson/invalid_10_extra_bracket.json", false);
    }

    @Test
    public void testInvalid_11_UndefinedValue() {
        validateJsonFile("./testjson/invalid_11_undefined_value.json", false);
    }

    @Test
    public void testInvalid_12_Infinity() {
        validateJsonFile("./testjson/invalid_12_infinity.json", false);
    }

    @Test
    public void testInvalid_13_UnescapedQuotes() {
        validateJsonFile("./testjson/invalid_13_unescaped_quotes.json", false);
    }

    @Test
    public void testInvalid_14_Comments() {
        validateJsonFile("./testjson/invalid_14_comments.json", false);
    }

    @Test
    public void testInvalid_15_MissingValue() {
        validateJsonFile("./testjson/invalid_15_missing_value.json", false);
    }

    @Test
    public void testInvalid_16_MissingKey() {
        validateJsonFile("./testjson/invalid_16_missing_key.json", false);
    }

    @Test
    public void testInvalid_17_DuplicateKeys() {
        validateJsonFile("./testjson/invalid_17_duplicate_keys.json", false);
    }

    @Test
    public void testInvalid_18_InvalidEscape() {
        validateJsonFile("./testjson/invalid_18_invalid_escape.json", false);
    }

    @Test
    public void testInvalid_19_LeadingZero() {
        validateJsonFile("./testjson/invalid_19_leading_zero.json", false);
    }

    @Test
    public void testInvalid_20_IncompleteObject() {
        validateJsonFile("./testjson/invalid_20_incomplete_object.json", false);
    }

    @Test
    public void testValid_21_BareString() {
        validateJsonFile("./testjson/valid_21_bare_string.json",true );
    }

    @Test
    public void testValid_22_BareNumber() {
        validateJsonFile("./testjson/valid_22_bare_number.json", true);
    }

    @Test
    public void testValid_23_BareBoolean() {
        validateJsonFile("./testjson/valid_23_bare_boolean.json", true);
    }

    @Test
    public void testInvalid_24_SemicolonSeparator() {
        validateJsonFile("./testjson/invalid_24_semicolon_separator.json", false);
    }

    @Test
    public void testInvalid_25_HexNumber() {
        validateJsonFile("./testjson/invalid_25_hex_number.json", false);
    }

    // Optional: Keep the original loop-based tests as integration tests
/*
    @Test
    public void testAllValidJsons_Integration() {
        for (String jsonFile : VALID_JSON_FILES) {
            validateJsonFile(jsonFile, true);
        }
    }

    @Test
    public void testAllInvalidJsons_Integration() {
        for (String jsonFile : INVALID_JSON_FILES) {
            validateJsonFile(jsonFile, false);
        }
    } */
}
