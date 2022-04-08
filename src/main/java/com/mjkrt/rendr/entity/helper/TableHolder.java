package com.mjkrt.rendr.entity.helper;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TableHolder.
 *
 * This class represents a table, and holds the information that a table would have.
 */
public class TableHolder {

    private static Comparator<List<String>> generateComparator(int columnIdx, boolean isAsc, ColumnDataType type) {
        int directionInt = ((isAsc) ? 1 : -1);
        return (list1, list2) -> {
            String string1 = list1.get(columnIdx);
            String string2 = list2.get(columnIdx);
            if (string1.isEmpty() && string2.isEmpty()) {
                return 0;
            } else if (!string1.isEmpty() && string2.isEmpty()) {
                return -1;
            } else if (string1.isEmpty() && !string2.isEmpty()) {
                return 1;
            }
            if (type == ColumnDataType.DECIMAL || type == ColumnDataType.DOUBLE) {
                BigDecimal d1 = new BigDecimal(string1);
                BigDecimal d2 = new BigDecimal(string2);
                return d1.compareTo(d2) * directionInt;
            }
            return string1.compareTo(string2) * directionInt;
        };
    }

    private Comparator<List<String>> sortByComparator;

    private List<ColumnHeader> columnHeaders;

    private Set<List<String>> dataRows = new HashSet<>();

    public TableHolder(List<ColumnHeader> columnHeaders, Set<List<String>> dataRows) {
        verifyStructure(columnHeaders, dataRows);
        this.columnHeaders = columnHeaders;
        this.dataRows = dataRows;
        this.sortByComparator = generateComparator(0, true, columnHeaders.get(0).getType());
    }

    public TableHolder(List<ColumnHeader> columnHeaders) {
        if (columnHeaders.isEmpty()) {
            throw new IllegalArgumentException("Headers cannot be empty");
        }
        this.columnHeaders = columnHeaders;
        this.sortByComparator = generateComparator(0, true, columnHeaders.get(0).getType());
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

    /**
     * Sets the order in which to sort the columns of the table.
     */
    public void setSortColumnAndDirection(ColumnHeader header, SortedOrdering ordering) {
        int columnIdx = getColumnHeaders().indexOf(header);
        if (columnIdx < 0 || ordering == SortedOrdering.NOT_USED) {
            return; //not found
        }
        boolean isAscending = (ordering == SortedOrdering.ASC);
        sortByComparator = generateComparator(columnIdx, isAscending, columnHeaders.get(columnIdx).getType());
    }

    /**
     * Returns a list, where each element is a list containing the data in each row of the table.
     * The ordering of the rows in the table is also ensured.
     *
     * @return A List of Lists containing the data of each row, in String format.
     */
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
