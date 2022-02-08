package com.mjkrt.rendr.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.mjkrt.rendr.entity.SimpleRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOG = LogsCenter.getLogger(ExcelServiceImpl.class);
    
    @Override
    public ByteArrayInputStream generateWorkBook(List<SimpleRow> data) {
        
        LOG.info("Generating workbook");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sample Data");

        int rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        CellStyle headerCellStyle = generateHeaderStyle(workbook);

        List<String> headers = SimpleRow.getFields();
        generateHeaders(headerRow, headerCellStyle, headers);

        LOG.info("Generating " + data.size() +" dataRows");
        for (SimpleRow datum : data) {
            Row dataRow = sheet.createRow(rowCount++);
            addDataToRow(dataRow, datum);
        }
        
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        
        return writeToStream(workbook);
    }
    
    private void generateHeaders(Row headerRow, CellStyle headerCellStyle, List<String> headers) {
        LOG.info("Generating headers " + headers);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerCellStyle);
        }
    }
    
    private CellStyle generateHeaderStyle(Workbook workbook) {
        LOG.info("Generating header style");
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerCellStyle;
    }
    
    private void addDataToRow(Row dataRow, SimpleRow datum) {
        dataRow.createCell(0).setCellValue(datum.getInstrumentType());
        dataRow.createCell(1).setCellValue(datum.getTicker());
        dataRow.createCell(2).setCellValue(datum.getContractCode());
        dataRow.createCell(3).setCellValue(datum.getCoupon());
        dataRow.createCell(4).setCellValue(datum.getMaturityDate());
        dataRow.createCell(5).setCellValue(datum.getCurrency());
        dataRow.createCell(6).setCellValue(datum.getIsin());
        dataRow.createCell(7).setCellValue(datum.getCurrentFace());
        dataRow.createCell(8).setCellValue(datum.getOriginalFace());
        dataRow.createCell(9).setCellValue(datum.getPrice());
        dataRow.createCell(10).setCellValue(datum.getMarketValue());
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
}
