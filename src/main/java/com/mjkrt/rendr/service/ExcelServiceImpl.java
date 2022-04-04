package com.mjkrt.rendr.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.service.file.FileService;
import com.mjkrt.rendr.service.mapper.DataMapperService;
import com.mjkrt.rendr.service.mapper.JsonService;
import com.mjkrt.rendr.service.mapper.TableHolderService;
import com.mjkrt.rendr.service.template.DataTemplateService;
import com.mjkrt.rendr.service.template.TemplateExtractorService;
import com.mjkrt.rendr.service.writter.DataWriterService;
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
    
    @Autowired
    private JsonService jsonService;
    
    @Autowired
    private TableHolderService tableHolderService;

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
    public ByteArrayInputStream generateExcel(JsonNode dataNode) throws IOException {
        long templateId = dataNode.get("templateId").longValue();
        LOG.info("Generating excel for template ID " + templateId);
        
        Workbook workbook = loadTemplateResourceFromId(templateId);
        Map<DataTable, TableHolder> tableHolders = getTableToHolderMap(templateId, dataNode.get("jsonObjects"));
        Map<DataCell, String> substitutionMap = getCellToDataMap(templateId, dataNode);
        
        dataWriterService.mapDataToWorkbook(tableHolders, substitutionMap, workbook);
        return dataWriterService.writeToStream(workbook);
    }
    
    private Workbook loadTemplateResourceFromId(long templateId) throws IOException {
        if (!dataTemplateService.isPresent(templateId)) {
            throw new IllegalArgumentException("Template with given ID is not present");
        }
        Resource templateResource = fileService.load(templateId + EXCEL_EXT);
        return new XSSFWorkbook(templateResource.getInputStream());
    }
    
    private Map<DataTable, TableHolder> getTableToHolderMap(long templateId, JsonNode node) throws IOException {
        List<TableHolder> baseHolders = jsonService.getTableHolders(node);
        List<TableHolder> compactHolders = tableHolderService.compact(baseHolders);
        List<DataTable> tables = dataTemplateService.findDataTablesWithTemplateId(templateId);
        return dataMapperService.generateTableToHolderMap(tables, compactHolders);
    }
    
    private Map<DataCell, String> getCellToDataMap(long templateId, JsonNode node) throws IOException {
        List<DataCell> cells = dataTemplateService.findDataCellsWithTemplateId(templateId);
        Map<DataCell, String> cellToDataMap = new HashMap<>();
        // todo add logic
        return cellToDataMap;
    }

    @Override
    public void copyByteStreamToResponse(HttpServletResponse response,
            ByteArrayInputStream stream,
            String fileName) throws IOException {

        String formattedFileName = fileName.replaceAll("\\s+", "-"); // replace whitespaces
        if (formattedFileName.contains(".")) {
            formattedFileName = formattedFileName.substring(0, formattedFileName.lastIndexOf('.')); // remove ext
        }
        LOG.info("Copying input stream to " + formattedFileName + EXCEL_EXT);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + formattedFileName + EXCEL_EXT);
        IOUtils.copy(stream, response.getOutputStream());
        LOG.info("Excel '" + formattedFileName + ".xlsx" + "' generated");
    }
}
