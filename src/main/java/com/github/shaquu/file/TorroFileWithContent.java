package com.github.shaquu.file;

public class TorroFileWithContent extends TorroFile {

    private final Byte[] content;

    public TorroFileWithContent(String fileName, long fileSize, String md5, Byte[] content) {
        super(fileName, fileSize, md5);
        this.content = content;
    }

    public Byte[] getContent() {
        return content;
    }
}
