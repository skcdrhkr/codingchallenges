package org.cc.jsonparser;

public class Parser {
    public int parseWhiteSpace(char[] data, int index) {
        int length = data.length;
        while (index < length) {
            if (data[index] != ' ' && data[index] != '\n' && data[index] != '\r' && data[index] != '\t')
                break;
            index++;
        }
        return index;
    }

    public int parseNumerical(char[] data, int index) {
        return -1;
    }
}
