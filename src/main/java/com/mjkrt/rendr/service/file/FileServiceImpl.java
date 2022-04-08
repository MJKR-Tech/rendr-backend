package com.mjkrt.rendr.service.file;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    @Value("${upload.sample-file}")
    private String sampleFile;
    
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * @inheritDoc
     */
    @PostConstruct
    public void init() {
        LOG.info("Initialising directory '" + uploadPath + '\'');
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void save(MultipartFile file, String fileName) {
        LOG.info("Saving file " + uploadPath + '/' + fileName);
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

    /**
     * @inheritDoc
     */
    @Override
    public Resource load(String filename) {
        LOG.info("Loading file " + uploadPath + '/' + filename);
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

    /**
     * @inheritDoc
     */
    @Override
    public Resource loadSample() {
        LOG.info("Loading sample resource");
        return load(sampleFile);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void delete(String filename) {
        LOG.info("Deleting file " + uploadPath + '/' + filename);
        Path file = Paths.get(uploadPath).resolve(filename);
        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists()) {
                return;
            }
            FileSystemUtils.deleteRecursively(file.toFile());
            LOG.info("File " + uploadPath + '/' + filename + " deleted");
        } catch (MalformedURLException e) {
            LOG.warning(e.getLocalizedMessage());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<String> listAll() {
        try {
            return Files.list(Paths.get(uploadPath))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException io) {
            LOG.warning(io.getLocalizedMessage());
            return new ArrayList<>();
        }
    }
}
