package com.mjkrt.rendr.service.writter;

import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.mjkrt.rendr.entity.helper.TableHolder;

public interface DataWriterService {
    void mapDataToWorkbook(long templateId, Map<Long, TableHolder> dataMap, Workbook workbook);
}
