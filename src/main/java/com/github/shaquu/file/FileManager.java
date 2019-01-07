package com.github.shaquu.file;

import com.github.shaquu.networking.NetworkNode;
import com.github.shaquu.utils.PrimitiveObject;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileManager {

    private final NetworkNode networkNode;

    private HashMap<String, Byte[]> md5toContent = new HashMap<>();
    private List<TorroFile> fileList = new ArrayList<>();

    public FileManager(NetworkNode networkNode, String folderPath) {
        this.networkNode = networkNode;

        this.loadFiles(folderPath);
    }

    private static String calculateMd5(byte[] fileContent) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        final byte[] resultByte = messageDigest.digest(fileContent);
        return String.format("%032x", new BigInteger(1, resultByte));
    }

    public List<TorroFile> getFileList() {
        return fileList;
    }

    public Byte[] getFileContent(String md5) {
        return md5toContent.getOrDefault(md5, null);
    }

    public void add(File file) throws IOException, NoSuchAlgorithmException {
        byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        String md5 = calculateMd5(fileContent);

        TorroFile torroFile = new TorroFile(file.getName(), file.length(), md5);
        fileList.add(torroFile);

        md5toContent.put(md5, PrimitiveObject.toByteArrObject(fileContent));
    }

    private void loadFiles(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists())
            return;

        File[] listFiles = folder.listFiles();

        assert listFiles != null;
        for (File file : listFiles) {
            try {
                add(file);
            } catch (IOException | NoSuchAlgorithmException e) {
                networkNode.getLogger().debug("Couldn't add file: " + file.getAbsolutePath());
            }
        }
    }
}
