package com.mjkrt.rendr.service;

import com.mjkrt.rendr.entity.SimpleRow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface ExcelService {
//    ByteArrayInputStream generateWorkBook();

    ByteArrayInputStream generateWorkBook(List<SimpleRow> simpleRows);
}
