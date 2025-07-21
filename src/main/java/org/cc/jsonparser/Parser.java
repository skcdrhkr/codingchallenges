package org.cc.jsonparser;

import java.util.List;
import java.util.Stack;

public interface Parser {
    void parse(String input, Integer index, Stack<String> stack, List<Parser> nextParsers);
}
