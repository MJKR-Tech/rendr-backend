package com.mjkrt.rendr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.service.file.FileService;
import com.mjkrt.rendr.service.mapper.DataMapperService;
import com.mjkrt.rendr.service.template.DataTemplateService;
import com.mjkrt.rendr.service.template.TemplateExtractorService;
import com.mjkrt.rendr.service.writer.DataWriterService;
import com.mjkrt.rendr.tools.MockDataTemplate;

@ExtendWith(MockitoExtension.class)
public class ExcelServiceTest {

    @InjectMocks
    private ExcelServiceImpl excelService;
    
    @Mock
    private DataTemplateService dataTemplateService;

    @Mock
    private TemplateExtractorService templateExtractorService;

    @Mock
    private DataMapperService dataMapperService;

    @Mock
    private DataWriterService dataWriterService;

    @Mock
    private FileService fileService;

    private static List<DataTemplate> generateTemplates() {
        return new ArrayList<>(Arrays.asList(
                MockDataTemplate.create().withTemplateId(1).withTemplateName("First").generate(),
                MockDataTemplate.create().withTemplateId(2).withTemplateName("Second").generate(),
                MockDataTemplate.create().withTemplateId(3).withTemplateName("Third").generate()));
    }
    
    @Test
    public void getTemplates_returnsTemplates() {
        when(dataTemplateService.listAll()).thenReturn(generateTemplates());
        
        List<DataTemplate> output = excelService.getTemplates();
        assertEquals(output, generateTemplates());
    }
}
