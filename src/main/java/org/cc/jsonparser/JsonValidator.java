package org.cc.jsonparser;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;

public class JsonValidator {

    private static final Character START_OBJECT = '{';
    private static final Character END_OBJECT = '}';
    private static final Character START_ARRAY = '[';
    private static final Character END_ARRAY = ']';
    private static final Character DELIM_STRING = '"';
    private static final Character COLON = ':';
    private static final Character COMMA = ',';
    private static Stack<String> stack = new Stack<>();
    private static int position = 0;

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
        content = content.trim();
        if (content.isEmpty())
            return false;

        char[] input = content.toCharArray();
        position = 0;
        return parseJSON(input);
    }

    private static boolean parseJSON(char[] input) {
        char start = input[position];

        if (start == START_OBJECT) {
            return parseJsonObject(input);
        } else if (start == START_ARRAY) {
            return parseJsonArray(input);
        } else if (start == DELIM_STRING) {
            return parseString(input);
        } else if (Character.isDigit(start) || start == '+' || start == '-') {
            return parseNumber(input);
        } else if (start == 'f' || start == 't') {
            return parseBoolean(input);
        } else if (start == 'n') {
            return parseNull(input);
        } else {
            return false;
        }
    }

    private static boolean parseJsonObject(char[] input) {
        int len = input.length;
        boolean result = true;
        position += 1;

        while (position < len && input[position] != END_OBJECT) {
            parseWhiteSpaces(input);
            if (input[position] != DELIM_STRING) {
                return false;
            }
            result &= parseString(input);
            parseWhiteSpaces(input);
            if (input[position] != COLON)
                return false;
            position += 1;
            parseWhiteSpaces(input);
            result &= parseJSON(input);
            parseWhiteSpaces(input);
            if (input[position] != COMMA) {
                break;
            } else if (position + 1 < len && input[position + 1] == '}') {
                return false;
            }
            position += 1;
        }

        if (input[position] != END_OBJECT) {
            return false;
        }
        position += 1;
        return result;
    }

    private static boolean parseJsonArray(char[] input) {
        int len = input.length;
        boolean result = true;
        // parseWhiteSpaces(input);
        position += 1;

        while (position < len && input[position] != END_ARRAY) {
            parseWhiteSpaces(input);
            result &= parseJSON(input);
            parseWhiteSpaces(input);
            if (input[position] != COMMA) {
                if (input[position] != END_ARRAY) {
                    return false;
                }
                break;
            } else if (position + 1 < len && input[position + 1] == ']') {
                return false;
            }
            position += 1;
        }

        position += 1;
        return result;
    }

    private static boolean parseNull(char[] input) {
        int len = input.length;
        if (position + 4 <= len && "null".equals(String.valueOf(input, position, 4))) {
            position += 4;
            return true;
        }
        return false;
    }

    private static boolean parseBoolean(char[] input) {
        int len = input.length;
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

    private static boolean parseNumber(char[] input) {
        int endIndex = position;
        int len = input.length;
        boolean containsPeriod = false;

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
        try {
            if (containsPeriod) {
                Double.parseDouble(String.valueOf(input, position, endIndex - position));
            } else {
                Long.valueOf(String.valueOf(input, position, endIndex - position));
            }
        } catch (NumberFormatException e) {
            try {
                BigInteger bigInteger = new BigInteger(String.valueOf(input, position, endIndex - position));
            } catch (NumberFormatException exception) {
                return false;
            }

        }
        position = endIndex;
        return true;
    }

    private static boolean parseString(char[] input) {
        int len = input.length;
        int endIndex = position + 1;
        while (endIndex < len && input[endIndex] != '"') {
            endIndex++;
        }
        position = endIndex + 1;
        return true;
    }

    private static void parseWhiteSpaces(char[] input) {
        int len = input.length;
        while (position < len && Character.isWhitespace(input[position])) {
            position++;
        }
    }
}
