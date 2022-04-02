package com.mjkrt.rendr.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.service.ExcelService;
import com.mjkrt.rendr.service.JsonService;
import com.mjkrt.rendr.tools.MockDataTemplate;

@WebMvcTest(ExcelControllerTest.class)
public class ExcelControllerTest {
    
    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    private ExcelService excelService;
    
    @MockBean
    private JsonService jsonService;
    
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
                        .get("/getTemplates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // test POST success response
    @Test
    public void uploadTemplate_indicatesSuccess() throws Exception {
        MockMultipartFile file = generateMockExcel();

        Mockito.when(excelService.uploadTemplateFromFile(file))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/uploadTemplate")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    // test POST failure response
    @Test
    public void uploadTemplate_indicatesFailure() throws Exception {
        MockMultipartFile file = generateMockExcel();
        
        Mockito.when(excelService.uploadTemplateFromFile(file))
                .thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/uploadTemplate")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // test POST internal server error response
    @Test
    public void uploadTemplate_indicatesError() throws Exception {
        MockMultipartFile file = generateMockExcel();

        Mockito.doThrow(new RuntimeException())
                .when(excelService)
                .uploadTemplateFromFile(file);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/uploadTemplate")
                        .file(file))
                .andExpect(status().isInternalServerError());
    }

    // test excel download response
    @Test
    public void downloadSampleTemplate_providesCorrectExcel() throws Exception {
        ByteArrayInputStream resourceByteArray = new ByteArrayInputStream(loadMockSampleExcel());
        
        Mockito.when(excelService.getSampleTemplate())
                .thenReturn(resourceByteArray);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/downloadSampleTemplate"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(loadMockSampleExcel()));
    }
}
