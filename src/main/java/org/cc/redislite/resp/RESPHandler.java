package org.cc.redislite.resp;

import java.util.ArrayList;

public class RESPHandler {

    public String[] deserialize(String request) {
        char[] requestChars = request.toCharArray();
        String[] parsedCommand;
        if (requestChars[0] == '*') {
            parsedCommand = deserializeBulkStringArrayRequest(requestChars);
        } else {
            throw new RuntimeException("Not a Valid request.");
        }
        return parsedCommand;
    }

    public String[] deserializeBulkStringArrayRequest(char[] requestChars) {
        int arrayLen = 0, index = 1;
        while (requestChars[index] != '\r') {
            arrayLen *= 10;
            arrayLen += (requestChars[index++] - '0');
        }
        index += 2; // Skipping '\r\n' terminator
        ArrayList<String> inputCommand = new ArrayList<>();
        for (int ind = 0; ind < arrayLen; ind++) {
            if (requestChars[index] == '$') {
                int bulkStringLen = 0;
                index += 1;
                while (requestChars[index] != '\r') {
                    bulkStringLen *= 10;
                    bulkStringLen += (requestChars[index++] - '0');
                }
                index += 2; //Skipping '\r\n' terminator
                inputCommand.add(new String(requestChars, index, bulkStringLen));
                index += bulkStringLen;
                index += 2; // Skipping next terminator '\r\n'
            }
        }

        return inputCommand.toArray(new String[0]);
    }

    public String serializedBulkStringArray(String[] responseArray) {
        StringBuilder serializedResponse = new StringBuilder();
        serializedResponse.append("*").append(responseArray.length).append("\r\n");
        for (String cur : responseArray) {
            serializedResponse.append("$").append(cur.length()).append("\r\n");
            serializedResponse.append(cur).append("\r\n");
        }
        return serializedResponse.toString();
    }

    public String serliaizedSimpleString(String response) {
        return "+" +
                response +
                "\r\n";
    }

    public String serializedBulkString(String response) {
        return "$" +
                response.length() +
                "\r\n" +
                response +
                "\r\n";
    }

    public String serializedErrorMessage(String error) {
        return "-" +
                error +
                "\r\n";
    }

    public String serializedInteger(Long num) {
        return ":" + num.toString() + "\r\n";
    }

}
