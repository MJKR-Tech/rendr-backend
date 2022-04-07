package com.mjkrt.rendr.service.writer;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.mjkrt.rendr.entity.helper.TableHolder;

public interface DataWriterService {
    void mapDataToWorkbook(Map<Long, TableHolder> dataMap,
            Map<Long, String> cellSubstitutions,
            Workbook workbook, long tableId);
    ByteArrayInputStream writeToStream(Workbook workbook);
}
