package com.mjkrt.rendr.service.file;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileService.
 * 
 * This service provides file level interactions for other services to tap onto.
 */
public interface FileService {

    /**
     * Saves a file with the given file name.
     * 
     * @param file file to save
     * @param fileName file name to save as
     */
    void save(MultipartFile file, String fileName);

    /**
     * Loads a Resource based on the file name specified
     * 
     * @param filename file name to find and load
     * @return Resource instance
     */
    Resource load(String filename);

    /**
     * Loads the standard sample resource.
     * 
     * @return sample Resource instance
     */
    Resource loadSample();

    /**
     * Deletes a file based on the file name specified.
     * 
     * @param filename file name to find and delete
     */
    void delete(String filename);

    /**
     * Lists all files present in storage.
     * 
     * @return list of file names present in storage
     */
    List<String> listAll();
}
