package com.mjkrt.rendr.entity.helper;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TableHolder {

    private static Comparator<List<String>> generateComparator(int columnIdx, boolean isAsc) {
        int directionInt = ((isAsc) ? 1 : -1);
        return (list1, list2) -> {
            if (NumberUtils.isParsable(list1.get(columnIdx))) {
                double d1 = Double.parseDouble(list1.get(columnIdx));
                double d2 = Double.parseDouble(list2.get(columnIdx));
                return Double.compare(d1, d2) * directionInt;
            }
            return list1.get(columnIdx).compareTo(list2.get(columnIdx)) * directionInt;
        };
    } 

    private Comparator<List<String>> sortByComparator = generateComparator(0, true); // default

    private List<ColumnHeader> columnHeaders;

    private Set<List<String>> dataRows = new HashSet<>();

    public TableHolder(List<ColumnHeader> columnHeaders, Set<List<String>> dataRows) {
        verifyStructure(columnHeaders, dataRows);
        this.columnHeaders = columnHeaders;
        this.dataRows = dataRows;
    }

    public TableHolder(List<ColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    private static void verifyStructure(List<ColumnHeader> columnHeaders, Set<List<String>> dataRows) {
        int headerSize = columnHeaders.size();
        if (headerSize == 0) {
            throw new IllegalArgumentException("Headers cannot be empty");
        }
        for (List<String> row : dataRows) {
            if (row.size() == headerSize) {
                continue;
            }
            throw new IllegalArgumentException("Header size and row size are not equal in table holder");
        }
    }

    public List<ColumnHeader> getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(List<ColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public Set<List<String>> getDataRows() {
        return dataRows;
    }

    public void setDataRows(Set<List<String>> dataRows) {
        this.dataRows = dataRows;
    }

    public void setDataRow(List<String> dataRow) {
        this.dataRows.add(dataRow);
    }

    public void setSortColumnAndDirection(ColumnHeader header, boolean isAscending) {
        int idx = columnHeaders.indexOf(header);
        if (idx < 0) {
            return; // not found
        }
        sortByComparator = generateComparator(idx, isAscending);
    }

    public List<List<String>> generateOrderedTable() {
        return dataRows.stream()
                .sorted(sortByComparator)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableHolder that = (TableHolder) o;
        return Objects.equals(columnHeaders, that.columnHeaders)
                && Objects.equals(sortByComparator, that.sortByComparator)
                && Objects.equals(dataRows, that.dataRows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnHeaders, sortByComparator, dataRows);
    }

    @Override
    public String toString() {
        return "TableHolder{" +
                "columnHeaders=" + columnHeaders +
                ", sortByComparator=" + sortByComparator +
                ", dataRows=" + dataRows +
                '}';
    }
}
