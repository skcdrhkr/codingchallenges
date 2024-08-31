package org.cc.compression;

import static org.cc.compression.Decoder.extractFile;
import static org.cc.compression.Encoder.compressFile;

public class HuffmanEncoder {
    public static void main(String[] args) {

        if (args.length >= 1) {
            String command = args[0];

            if (command.equalsIgnoreCase("compress")) {
                compressFile(args[1], args[2]);
            } else if (command.equalsIgnoreCase("extract")) {
                extractFile(args[1], args[2]);
            } else if (command.equals("--help") || command.equals("-h")) {
                System.out.println("java HuffmanEncoder.java [compres/extract] [input file] [output file]");
            }
        } else {
            System.out.println("Invalid options. For help run: java HuffmanEncoder --help / -h");
        }

    }
}
