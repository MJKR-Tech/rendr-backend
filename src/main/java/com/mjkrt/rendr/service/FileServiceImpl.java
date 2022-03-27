package com.mjkrt.rendr.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LogsCenter.getLogger(FileServiceImpl.class);

    @Value("${upload.sample.file}")
    private String sampleFile;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    @Override
    public void save(MultipartFile file, String fileName) {
        try {
            Path root = Paths.get(uploadPath);
            if (!Files.exists(root)) {
                init();
            }
            Files.copy(file.getInputStream(), root.resolve(fileName));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = Paths.get(uploadPath).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public Resource loadSample() {
        return load(sampleFile);
    }

    @Override
    public void delete(String filename) {
        Path file = Paths.get(uploadPath).resolve(filename);

        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists()) {
                return;
            }
            FileSystemUtils.deleteRecursively(file.toFile());
        } catch (MalformedURLException e) {
            LOG.warning(e.getLocalizedMessage());
        }
    }
}