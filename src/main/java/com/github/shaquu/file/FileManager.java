package com.github.shaquu.file;

import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private List<TorroFileContent> fileContent = new ArrayList<>();
    private List<TorroFile> fileList = new ArrayList<>();

    public FileManager() {

    }


    public List<TorroFile> getFileList() {
        return fileList;
    }

    public void add(TorroFile torroFile) {
        fileList.add(torroFile);
    }
}
