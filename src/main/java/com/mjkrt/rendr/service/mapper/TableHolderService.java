package com.mjkrt.rendr.service.mapper;

import java.util.List;

import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;

public interface TableHolderService {
    boolean checkIfCanNaturalJoin(TableHolder t1, TableHolder t2);
    TableHolder naturalJoin(TableHolder t1, TableHolder t2);
    TableHolder generateSubset(TableHolder t, List<ColumnHeader> desiredColumns);
}
