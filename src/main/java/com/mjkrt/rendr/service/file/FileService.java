package com.mjkrt.rendr.service.file;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void save(MultipartFile file, String fileName);
    Resource load(String filename);
    Resource loadSample();
    void delete(String filename);
    List<String> listAll();
}
