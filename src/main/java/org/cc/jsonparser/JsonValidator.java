package org.cc.jsonparser;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonValidator {

    private static final Character START_OBJECT = '{';
    private static final Character END_OBJECT = '}';
    private static final Character START_ARRAY = '[';
    private static final Character END_ARRAY = ']';
    private static final Character DELIM_STRING = '"';
    private static final Character COLON = ':';
    private static final Character COMMA = ',';
    private static int position = 0;
    private static int len;
    private static char[] input;

    /**
     * json := value
     * value := object | array | string | number | boolean | null
     * object := '{' (pair (',' pair)*)? '}'
     * pair := string ':' value
     * array := '[' (value (',' value)*)? ']'
     * string := '"' chars '"'
     * number := digit+ ('.' digit+)? (('e'|'E') ('+'|'-')? digit+)?
     * boolean := 'true' | 'false'
     * null := 'null'
     */

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

    public static boolean validateJSON(String content) {
        content = cleanUpWhiteSpaces(content);
        if (content.isEmpty())
            return false;

        input = content.toCharArray();
        position = 0;
        len = input.length;
        return parseJSON() && reachedEnd();
    }

    private static boolean parseJSON() {

        if (reachedEnd())
            return false;
        char start = input[position];

        if (start == START_OBJECT) {
            return parseJsonObject();
        } else if (start == START_ARRAY) {
            return parseJsonArray();
        } else if (start == DELIM_STRING) {
            return parseString();
        } else if (Character.isDigit(start) || start == '+' || start == '-') {
            return parseNumber();
        } else if (start == 'f' || start == 't') {
            return parseBoolean();
        } else if (start == 'n') {
            return parseNull();
        } else {
            return false;
        }
    }

    private static boolean parseJsonObject() {
        boolean result = true;
        position += 1;
        if (input[position] == END_OBJECT) {
            position += 1;
            return true;
        }

        while (position < len) {
            if (input[position] != DELIM_STRING) {
                return false;
            }
            result &= parseString();

            if (reachedEnd() || input[position] != COLON)
                return false;
            position += 1;

            result &= parseJSON();
            if (reachedEnd()) return false;
            if (input[position] != COMMA) {
                break;
            }
            position += 1;
        }

        if (reachedEnd() || input[position] != END_OBJECT) {
            return false;
        }
        position += 1;
        return result;
    }

    private static boolean parseJsonArray() {
        boolean result = true;
        position += 1;
        if (input[position] == END_ARRAY) {
            position += 1;
            return true;
        }

        while (position < len) {
            result &= parseJSON();
            if (reachedEnd()) return false;
            if (input[position] != COMMA) {
                break;
            }
            position += 1;
        }

        if (reachedEnd() || input[position] != END_ARRAY) {
            return false;
        }

        position += 1;
        return result;
    }

    private static boolean parseNull() {
        if (position + 4 <= len && "null".equals(String.valueOf(input, position, 4))) {
            position += 4;
            return true;
        }
        return false;
    }

    private static boolean parseBoolean() {
        // true, false
        if (position + 4 <= len && "true".equals(String.valueOf(input, position, 4))) {
            position += 4;
            return true;
        } else if (position + 5 <= len && "false".equals(String.valueOf(input, position, 5))) {
            position += 5;
            return true;
        }
        return false;
    }

    private static boolean parseNumber() {
        int endIndex = position;
        boolean containsPeriod = false;
        String number;

        if (input[endIndex] == '+' || input[endIndex] == '-') {
            endIndex++;
        }

        while (endIndex < len && (Character.isDigit(input[endIndex]) || input[endIndex] == '.'
                || Character.toLowerCase(input[endIndex]) == 'e')) {
            if (input[endIndex] == '.') {
                if (containsPeriod)
                    return false;
                containsPeriod = true;
            }
            endIndex++;
        }

        number = String.valueOf(input, position, endIndex - position);
        if (number.length() > 1 && number.charAt(0) == '0' && number.charAt(1) != '.') {
            return false;
        }

        try {
            if (containsPeriod) {
                Double.parseDouble(number);
            } else {
                Long.valueOf(number);
            }
        } catch (NumberFormatException e) {
            try {
                new BigInteger(number);
            } catch (NumberFormatException exception) {
                return false;
            }

        }
        position = endIndex;
        return true;
    }

    private static boolean parseString() {
        int endIndex = position + 1;
        while (endIndex < len && input[endIndex] != '"') {
            endIndex++;
        }
        String parsedString = new String(input, position + 1, endIndex - position);
        if (containsSingleBackSpace(parsedString)) {
            return false;
        }
        position = endIndex + 1;
        return true;
    }

    private static boolean containsSingleBackSpace(String parsedString) {
        int index = parsedString.indexOf("\\");
        if (index == -1) return false;
        return !(index < parsedString.length() - 1 && parsedString.charAt(index + 1) == '\\');
    }

    private static String cleanUpWhiteSpaces(String content) {
        content = content.trim();
        content = content.replaceAll("\\s*\\{\\s*", "{");
        content = content.replaceAll("\\s*}\\s*", "}");
        content = content.replaceAll("\\s*\\[\\s*", "[");
        content = content.replaceAll("\\s*]\\s*", "]");
        content = content.replaceAll("\\s*:\\s*", ":");
        content = content.replaceAll("\\s*,\\s*", ",");

        return content;
    }

    private static boolean reachedEnd() {
        return position >= len;
    }
}
