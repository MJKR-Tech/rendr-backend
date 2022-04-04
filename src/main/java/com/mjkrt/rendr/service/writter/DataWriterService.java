package com.mjkrt.rendr.service.writter;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.helper.TableHolder;

public interface DataWriterService {
    void mapDataToWorkbook(Map<DataTable, TableHolder> dataMap,
            Map<DataCell, String> cellSubstitutions,
            Workbook workbook);
    ByteArrayInputStream writeToStream(Workbook workbook);
}
