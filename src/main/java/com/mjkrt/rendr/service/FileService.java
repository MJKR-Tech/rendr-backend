package com.mjkrt.rendr.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void save(MultipartFile file, String fileName);
    
    Resource load(String filename);
    
    Resource loadSample();
    
    void delete(String filename);
}
