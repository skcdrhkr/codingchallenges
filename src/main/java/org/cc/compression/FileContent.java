package org.cc.compression;

import java.util.ArrayList;

public class FileContent {
    String header;
    ArrayList<Byte> body;

    public FileContent(String header, ArrayList<Byte> body) {
        this.header = header;
        this.body = body;
    }
}
