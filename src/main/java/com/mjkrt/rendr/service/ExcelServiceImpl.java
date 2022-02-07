package com.mjkrt.rendr.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.mjkrt.rendr.entity.FinancialData;
import com.mjkrt.rendr.utils.LogsCenter;
import com.mjkrt.rendr.utils.SampleData;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOG = LogsCenter.getLogger(ExcelServiceImpl.class);
    
    @Override
    public Workbook generateWorkBook() throws IOException {
        List<FinancialData> financialDataList = SampleData.getSampleFinancialData();
        String excelFilePath = "SampleFinancialData.xls";

        Workbook workbook = new XSSFWorkbook();
        writeExcel(workbook, financialDataList, excelFilePath);
        return workbook;
    }

    private void writeExcel(Workbook workbook, List<FinancialData> listBook, String excelFilePath) throws IOException {
        Sheet sheet = workbook.createSheet();
        int rowCount = 0;

        for (FinancialData data : listBook) {
            rowCount++;
            Row row = sheet.createRow(rowCount);
            writeBook(data, row);
        }

        try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            workbook.write(outputStream);
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    private void writeBook(FinancialData data, Row row) {
        Cell cell = row.createCell(1);
        cell.setCellValue(data.getTitle());

        cell = row.createCell(2);
        cell.setCellValue(data.getAuthor());

        cell = row.createCell(3);
        cell.setCellValue(data.getPrice());
    }
}
