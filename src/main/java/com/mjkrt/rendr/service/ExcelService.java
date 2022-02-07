package com.mjkrt.rendr.service;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelService {
    Workbook generateWorkBook() throws IOException;
}
