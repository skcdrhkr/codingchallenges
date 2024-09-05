package org.cc;

import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        Function<Integer, String> f = _ -> "foo";
        try {
            f.apply(4);
        } catch (Exception _) {
            System.out.println("thrown");
        }
    }
}