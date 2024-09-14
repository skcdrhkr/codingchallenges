package org.cc.cut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.cc.cut.Parameters.DELIM_PARAM;
import static org.cc.cut.Parameters.FIELD_PARAM;

public class CutText {


    private static int field;
    private static char delimeter = '\t';

    public static void main(String[] args) {
        parseInputArgs(args);
        String fileName = args[args.length - 1];
        try {
            List<String> lines = Files.readAllLines(Path.of(fileName));
            for (String line : lines) {
                System.out.println(line.split(String.valueOf(delimeter))[field - 1]);
            }
        } catch (IOException e) {
            System.out.println(fileName + " File doesn't exist");
            System.exit(1);
        }
    }

    private static void parseInputArgs(String[] args) {
        for (int index = 0; index < args.length - 1; index++) {
            String arg = args[index];
            boolean validParam = false;
            for (Parameters parameters : Parameters.values()) {
                if (arg.startsWith(parameters.getParam())) {
                    validParam = true;
                    if (parameters.equals(FIELD_PARAM)) {
                        String fieldString = arg.substring(parameters.getParam().length());
                        field = Integer.parseInt(fieldString);
                        break;
                    } else if (parameters.equals(DELIM_PARAM)) {
                        String delimString = arg.substring(parameters.getParam().length());
                        delimeter = delimString.replace("'", "").charAt(0);
                        break;
                    }
                }
            }
            if (!validParam) {
                System.out.println("Not a valid parameter.");
                System.exit(1);
            }
        }
    }
}
