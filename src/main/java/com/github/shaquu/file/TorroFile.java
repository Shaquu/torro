package com.github.shaquu.file;

import java.io.Serializable;

public class TorroFile implements Serializable {

    private String fileName;
    private long fileSize;
    private String md5;

    TorroFile(String fileName, long fileSize, String md5) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.md5 = md5;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getMd5() {
        return md5;
    }

    @Override
    public String toString() {
        return "TorroFile{" +
                "fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
