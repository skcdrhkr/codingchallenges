package org.cc.jsonparser;

import java.util.List;
import java.util.Stack;

public class WhitespaceParser implements Parser{
    @Override
    public void parse(String input, Integer index, Stack<String> stack, List<Parser> nextParsers) {
        while(index < input.length()) {
            if(!isWhiteSpace(input, index)) {
               break;
            }
            index++;
        }
    }

    public boolean isWhiteSpace(String input, int index) {
        return Character.isWhitespace(input.charAt(index));
    }
}
