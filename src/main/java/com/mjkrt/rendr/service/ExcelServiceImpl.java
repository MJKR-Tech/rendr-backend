package com.mjkrt.rendr.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.entity.helper.TemplateIdHolder;
import com.mjkrt.rendr.service.file.FileService;
import com.mjkrt.rendr.service.mapper.DataMapperService;
import com.mjkrt.rendr.service.mapper.JsonService;
import com.mjkrt.rendr.service.mapper.TableHolderService;
import com.mjkrt.rendr.service.template.DataTemplateService;
import com.mjkrt.rendr.service.template.TemplateExtractorService;
import com.mjkrt.rendr.service.writer.DataWriterService;
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

    /**
     * @inheritDoc
     */
    @Override
    public List<DataTemplate> getTemplates() {
        LOG.info("Getting all templates");
        
        List<DataTemplate> templates = dataTemplateService.listAll();
        LOG.info(templates.size() + " templates found: " + templates);
        return templates;
    }

    /**
     * @inheritDoc
     */
    @Override
    public TemplateIdHolder uploadTemplateFromFile(MultipartFile file) {
        LOG.info("Uploading file " + file.getOriginalFilename() + " as dataTemplate");
        Optional<DataTemplate> optionalTemplate = Optional.ofNullable(readAsWorkBook(file))
                .map(workbook -> templateExtractorService.extract(workbook, file.getOriginalFilename()))
                .map(this::saveTemplate);

        long templateId = optionalTemplate.map(DataTemplate::getTemplateId).orElseThrow();
        fileService.save(file, templateId + EXCEL_EXT);
        return new TemplateIdHolder(templateId);

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

    /**
     * @inheritDoc
     */
    @Override
    public boolean deleteTemplate(List<Long> templateIds) {
        LOG.info("Deleting DataTemplates " + templateIds);
        templateIds.forEach(templateId -> {
                    LOG.info("Delete template with ID "+ templateId);
                    dataTemplateService.deleteById(templateId);
                    fileService.delete(templateId + EXCEL_EXT);
                });
        return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteAllTemplates() {
        LOG.info("Deleting all DataTemplates");
        List<Long> templateIds = dataTemplateService.listAllIds();
        dataTemplateService.deleteAll();
        templateIds.forEach(id -> fileService.delete(id + EXCEL_EXT));
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getFileNameForTemplate(long templateId) {
        LOG.info("Deleting all DataTemplates");
        DataTemplate template = dataTemplateService.findById(templateId);
        return template.getTemplateName();
    }

    /**
     * @inheritDoc
     */
    @Override
    public ByteArrayInputStream getSampleTemplate() throws IOException {
        LOG.info("Obtaining sample template");
        Resource sampleResource = fileService.loadSample();
        byte[] byteArray = IOUtils.toByteArray(sampleResource.getInputStream());
        return new ByteArrayInputStream(byteArray);
    }

    /**
     * @inheritDoc
     */
    @Override
    public ByteArrayInputStream getTemplate(long templateId) throws IOException {
        LOG.info("Obtaining template with ID "+ templateId);
        Resource sampleResource = fileService.load(templateId + EXCEL_EXT);
        byte[] byteArray = IOUtils.toByteArray(sampleResource.getInputStream());
        return new ByteArrayInputStream(byteArray);
    }

    /**
     * @inheritDoc
     */
    @Override
    public ByteArrayInputStream generateExcel(JsonNode dataNode) throws IOException {
        long templateId = dataNode.get("templateId").longValue();
        LOG.info("Generating excel for template ID " + templateId);
        
        Workbook workbook = loadTemplateResourceFromId(templateId);
        
        List<ColumnHeader> columnHeaders = jsonService.getHeaders(dataNode.get("jsonObjects"));
        List<JsonNode> rows = jsonService.getRows(dataNode.get("jsonObjects"));
        List<DataCell> dataCells = dataTemplateService.findDataCellsWithTemplateId(templateId);
        List<TableHolder> linkedTables = dataMapperService.generateLinkedTableHolders(templateId,
                columnHeaders,
                rows);
        
        Map<Long, TableHolder> tableHolders = dataMapperService.generateTableMapping(templateId,
                columnHeaders,
                linkedTables);
        Map<Long, String> substitutionMap = dataMapperService.generateCellMapping(dataCells,
                linkedTables);
        
        dataWriterService.mapDataToWorkbook(tableHolders, substitutionMap, workbook, templateId);
        return dataWriterService.writeToStream(workbook);
    }
    
    private Workbook loadTemplateResourceFromId(long templateId) throws IOException {
        LOG.info("Loading template resource from id " + templateId);
        if (!dataTemplateService.isPresent(templateId)) {
            throw new IllegalArgumentException("Template with given ID is not present");
        }
        Resource templateResource = fileService.load(templateId + EXCEL_EXT);
        return new XSSFWorkbook(templateResource.getInputStream());
    }

    /**
     * @inheritDoc
     */
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
