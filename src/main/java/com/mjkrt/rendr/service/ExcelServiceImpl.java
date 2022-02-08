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
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sample Data");

            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Creating header
            Cell cell = row.createCell(0); 
            cell.setCellValue("Instrument Type");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Ticker");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Contract Code");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Coupon");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Maturity");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Currency");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(6);
            cell.setCellValue("Current Face");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(7);
            cell.setCellValue("Original Face");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(8);
            cell.setCellValue("Price");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(9);
            cell.setCellValue("Market Value");
            cell.setCellStyle(headerCellStyle);

            // Creating data rows for each customer
//            List<FinancialData> data = SampleData.getSampleFinancialData();
            for(int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(data.get(i).getInstrumentType());
                dataRow.createCell(1).setCellValue(data.get(i).getTicker());
                dataRow.createCell(2).setCellValue(data.get(i).getContractCode());
                dataRow.createCell(3).setCellValue(data.get(i).getCoupon());
                dataRow.createCell(4).setCellValue(data.get(i).getMaturityDate());
                dataRow.createCell(5).setCellValue(data.get(i).getCurrency());
                dataRow.createCell(6).setCellValue(data.get(i).getCurrentFace());
                dataRow.createCell(7).setCellValue(data.get(i).getOriginalFace());
                dataRow.createCell(8).setCellValue(data.get(i).getPrice());
                dataRow.createCell(9).setCellValue(data.get(i).getMarketValue());
            }

            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
