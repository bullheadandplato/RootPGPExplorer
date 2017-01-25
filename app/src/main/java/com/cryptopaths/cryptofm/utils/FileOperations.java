package com.cryptopaths.cryptofm.utils;

/**
 * Created by tripleheader on 1/25/17.
 * Interface for the FileUtils
 */

public interface FileOperations {
    long getFileSize(String filename);
    String getFileExtension(String filename);
    String getReadableSize(String filename);
    boolean isEncryptedFile(String filename);
    int getNumberofFiles(String filename);
    boolean isFile(String filename);
    String getLastModifiedData(String filename);
    boolean createFolder(String foldername);
    void deleteDecryptedFolder();
    boolean getFile(String filename);
    boolean checkReadStatus(String filename);

}
