package com.mjkrt.rendr.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.entity.helper.TemplateIdHolder;
import com.mjkrt.rendr.service.ExcelService;
import com.mjkrt.rendr.tools.MockDataTemplate;

@SpringBootTest
@AutoConfigureMockMvc
public class ExcelControllerTest {
    
    private static final String PREFIX = "/api/v1";
    
    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    private ExcelService excelService;
    
    private static List<DataTemplate> generateTemplates() {
        return new ArrayList<>(Arrays.asList(
                MockDataTemplate.create().withTemplateId(1).withTemplateName("First").generate(),
                MockDataTemplate.create().withTemplateId(2).withTemplateName("Second").generate(),
                MockDataTemplate.create().withTemplateId(3).withTemplateName("Third").generate()));
    }
    
    private static MockMultipartFile generateMockExcel() {
        return new MockMultipartFile("file",
                "hello.xlsx",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());
    }

    private static byte[] loadMockSampleExcel() throws IOException {
        String resourcePath = "src/test/resources";
        String filename = "sample_template.xlsx";
        Path file = Paths.get(resourcePath).resolve(filename);
        Resource sampleResource = new UrlResource(file.toUri());
        return IOUtils.toByteArray(sampleResource.getInputStream());
    }

    // test GET data response
    @Test
    public void getTemplates_providesData() throws Exception {
        List<DataTemplate> records = generateTemplates();

        Mockito.when(excelService.getTemplates())
                .thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(PREFIX + "/getTemplates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // test POST success response
    @Test
    public void uploadTemplate_indicatesSuccess() throws Exception {
        MockMultipartFile file = generateMockExcel();

        Mockito.when(excelService.uploadTemplateFromFile(file))
                .thenReturn(new TemplateIdHolder(1L));

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(PREFIX + "/uploadTemplate")
                        .file(file))
                .andExpect(status().isOk());
    }
}
