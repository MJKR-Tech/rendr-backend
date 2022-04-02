package com.mjkrt.rendr.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import com.mjkrt.rendr.entity.helper.ColumnHeader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.utils.LogsCenter;

@Transactional
@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOG = LogsCenter.getLogger(ExcelServiceImpl.class);
    
    @Autowired
    private DataTemplateService dataTemplateService;

    @Autowired
    private TemplateExtractorService templateExtractorService;

    @Autowired
    private DataMapperService dataMapperService;

    @Autowired
    private DataWriterService dataWriterService;
    
    @Autowired
    private FileService fileService;

    @Override
    public List<DataTemplate> getTemplates() {
        LOG.info("Getting all templates");
        
        List<DataTemplate> templates = dataTemplateService.listAll();
        LOG.info(templates.size() + " templates found: " + templates);
        return templates;
    }
    
    @Override
    public boolean uploadTemplateFromFile(MultipartFile file) {
        LOG.info("Uploading file " + file.getOriginalFilename() + " as dataTemplate");
        Optional<DataTemplate> optionalTemplate = Optional.ofNullable(readAsWorkBook(file))
                .map(workbook -> templateExtractorService.extract(workbook, file.getOriginalFilename()))
                .map(this::saveTemplate);

        try {
            long templateId = optionalTemplate.map(DataTemplate::getTemplateId).orElseThrow();
            fileService.save(file, templateId + EXCEL_EXT);
            return true;
            
        } catch (Exception e) {
            LOG.warning(e.getLocalizedMessage());
            return false;
        }
    }

    private Workbook readAsWorkBook(MultipartFile file) {
        LOG.info("Reading file " + file.getOriginalFilename() + " as " + file.getOriginalFilename());
        LOG.info("File content type: " + file.getContentType());
        List<String> excelTypes = List.of(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // xlsx
                "application/vnd.ms-excel" // xls
        );
        
        if (file.getContentType() == null || !excelTypes.contains(file.getContentType())) {
            LOG.warning("Invalid file type fed");
            return null;
        }
        
        try {
            return (excelTypes.get(0).equals(file.getContentType()))
                    ? new XSSFWorkbook(file.getInputStream())
                    : new HSSFWorkbook(file.getInputStream());
        } catch (IOException io) {
            LOG.warning("Unable to read excel");
            return null;
        }
    }

    private DataTemplate saveTemplate(DataTemplate template) {
        LOG.info("Saving template " + template);
        return dataTemplateService.save(template);
    }

    @Override
    public boolean deleteTemplate(long templateId) {
        LOG.info("Delete template with ID "+ templateId);
        dataTemplateService.deleteById(templateId);
        fileService.delete(templateId + EXCEL_EXT);
        return true;
    }

    @Override
    public String getFileNameForTemplate(long templateId) {
        DataTemplate template = dataTemplateService.findById(templateId);
        return template.getTemplateName();
    }

    @Override
    public ByteArrayInputStream getSampleTemplate() throws IOException {
        LOG.info("Obtaining sample template");
        Resource sampleResource = fileService.loadSample();
        byte[] byteArray = IOUtils.toByteArray(sampleResource.getInputStream());
        return new ByteArrayInputStream(byteArray);
    }

    @Override
    public ByteArrayInputStream getTemplate(long templateId) throws IOException {
        LOG.info("Obtaining template with ID "+ templateId);
        Resource sampleResource = fileService.load(templateId + EXCEL_EXT);
        byte[] byteArray = IOUtils.toByteArray(sampleResource.getInputStream());
        return new ByteArrayInputStream(byteArray);
    }

    @Override
    public ByteArrayInputStream generateExcel(long templateId, 
            List<ColumnHeader> headers,
            List<JsonNode> rows) throws IOException {
        
        LOG.info("Generating excel for template ID " + templateId);
        
        Resource templateResource = fileService.load(templateId + EXCEL_EXT);
        Workbook workbook = new XSSFWorkbook(templateResource.getInputStream());
        Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> dataMap = dataMapperService
                .generateJsonMapping(templateId, headers, rows);
        dataWriterService.mapDataToWorkbook(templateId, dataMap, workbook);
        
        return writeToStream(workbook);
    }

    private ByteArrayInputStream writeToStream(Workbook workbook) {
        try {
            LOG.info("Writing to output stream");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException ex) {
            LOG.warning("IOException faced.");
            ex.printStackTrace();
            return null;
        }
    }

    // todo delete after full system testing
    @Override
    public void deleteAllTemplates() {
        dataTemplateService.listAll().stream()
                .map(DataTemplate::getTemplateId)
                .forEach(id -> fileService.delete(id + EXCEL_EXT));
        dataTemplateService.deleteAll();
    }
}
