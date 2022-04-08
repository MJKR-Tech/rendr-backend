package com.mjkrt.rendr.service.file;

import static com.mjkrt.rendr.service.ExcelService.EXCEL_EXT;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mjkrt.rendr.service.template.DataTemplateService;
import com.mjkrt.rendr.utils.LogsCenter;

/**
 * FileScheduleServiceImpl.
 * 
 * This class helps to implement the services required in FileScheduleService.
 */
@Service
public class FileScheduleServiceImpl implements FileScheduleService {

    private static final Logger LOG = LogsCenter.getLogger(FileScheduleServiceImpl.class);

    @Value("${upload.sample-file}")
    private String sampleFile;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private DataTemplateService dataTemplateService;

    /**
     * @inheritDoc
     */
    @Scheduled(fixedDelayString = "${upload.frequency.milliseconds.clean-up}")
    @Override
    public void removeRedundantFiles() {
        LOG.info("Cleaning up files.");
        Set<String> dataBaseIdsToFileNames = dataTemplateService.listAllIds().stream()
                .map(id -> id + EXCEL_EXT)
                .collect(Collectors.toSet());
        List<String> redundantFileNames = fileService.listAll().stream()
                .filter(file -> !dataBaseIdsToFileNames.contains(file))
                .filter(file -> !file.equals(sampleFile))
                .collect(Collectors.toList());
        redundantFileNames.forEach(file -> fileService.delete(file));
        LOG.info("Cleared " + redundantFileNames.size() + " redundant files.");
    }
}
