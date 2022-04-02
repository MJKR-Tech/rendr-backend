package com.mjkrt.rendr.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Workbook;

import com.mjkrt.rendr.entity.helper.ColumnHeader;

public interface DataWriterService {
    void mapDataToWorkbook(long templateId,
            Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> dataMap,
            Workbook workbook);
}
