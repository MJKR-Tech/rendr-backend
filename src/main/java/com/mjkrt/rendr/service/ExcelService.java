package com.mjkrt.rendr.service;

import com.mjkrt.rendr.entity.SimpleRow;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ExcelService {
    ByteArrayInputStream generateWorkBook(List<SimpleRow> simpleRows);
}
