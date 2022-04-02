package com.mjkrt.rendr.entity.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.util.Pair;

public class TableHolder {
    
    private List<ColumnHeader> columnHeaders;
    
    private Set<List<String>> dataRows; // Could substitute with linked list or tree

    public TableHolder(List<ColumnHeader> columnHeaders, Set<List<String>> dataRows) {
        verifyStructure(columnHeaders, dataRows);
        
        this.columnHeaders = columnHeaders;
        this.dataRows = dataRows;
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
    
    private static void verifyStructure(List<ColumnHeader> columnHeaders, Set<List<String>> dataRows) {
        int headerSize = columnHeaders.size();
        for (List<String> row : dataRows) {
            if (row.size() == headerSize) {
                continue;
            }
            throw new IllegalArgumentException("Header size and row size are not equal in table holder");
        }
    }
    
    public boolean checkIfCanNaturalJoin(TableHolder otherTable) {
        List<Pair<Integer, Integer>> indexPairs = getSameHeaderIndexPairs(otherTable.columnHeaders);
        return !(indexPairs.isEmpty());
    }
    
    public TableHolder naturalJoin(TableHolder otherTable) {
        List<Pair<Integer, Integer>> linkedPairs = getSameHeaderIndexPairs(otherTable.columnHeaders);
        if (linkedPairs.isEmpty()) {
            throw new IllegalArgumentException("TableHolders are not able to natural join");
        }
        List<Integer> unrelatedOtherIndexes = getOtherExcessHeaderIndexes(otherTable.columnHeaders, linkedPairs);
        List<ColumnHeader> newHeaders = naturalJoinHeaders(otherTable.columnHeaders, unrelatedOtherIndexes);
        Set<List<String>> newDataRows = naturalJoinDataRows(otherTable.dataRows, linkedPairs, unrelatedOtherIndexes);
        return new TableHolder(newHeaders, newDataRows);
    }
    
    private List<Pair<Integer, Integer>> getSameHeaderIndexPairs(List<ColumnHeader> otherHeaders) {
        List<Pair<Integer, Integer>> linkedPairs = new ArrayList<>();
        for (int i = 0; i < this.columnHeaders.size(); i++) {
            for (int j = 0; j < otherHeaders.size(); j++) {
                if (this.columnHeaders.get(i) == otherHeaders.get(j)) {
                    linkedPairs.add( new Pair<>(i, j) );
                }
            }
        }
        return linkedPairs;
    }

    private List<Integer> getOtherExcessHeaderIndexes(List<ColumnHeader> otherHeaders,
            List<Pair<Integer, Integer>> linkedPairs) {
        
        Set<Integer> otherHeaderIndexes = IntStream.range(0, otherHeaders.size())
                .boxed()
                .collect(Collectors.toSet());
        Set<Integer> otherLinkedIndexes = linkedPairs.stream().map(Pair::getSecond).collect(Collectors.toSet());
        List<Integer> unlinkedHeaders = new ArrayList<>();
        for (int idx : otherHeaderIndexes) {
            if (otherLinkedIndexes.contains(idx)) {
                continue;
            }
            unlinkedHeaders.add(idx);
        }
        unlinkedHeaders.sort(Integer::compareTo);
        return unlinkedHeaders;
    }
    
    private List<ColumnHeader> naturalJoinHeaders(List<ColumnHeader> otherHeader,
            List<Integer> unrelatedOtherIndexes) {
        
        List<ColumnHeader> newColumnHeaders = new ArrayList<>(this.columnHeaders);
        for (int otherIdx : unrelatedOtherIndexes) {
            columnHeaders.add(otherHeader.get(otherIdx));
        }
        return newColumnHeaders;
    }

    private Set<List<String>> naturalJoinDataRows(Set<List<String>> otherDataRows,
            List<Pair<Integer, Integer>> linkedPairs,
            List<Integer> unrelatedOtherIndexes) {
        
        Set<List<String>> newDataRows = new HashSet<>();
        for (List<String> thisRow : this.dataRows) {
            for (List<String> otherRow : otherDataRows) {
                if (!doesRowsMatchByNaturalJoin(thisRow, otherRow, linkedPairs)) {
                    continue;
                }
                List<String> newRow = naturalJoinSingleRows(thisRow, otherRow, linkedPairs, unrelatedOtherIndexes);
                newDataRows.add(newRow);
            }
        }
        return newDataRows;
    }

    private static List<String> naturalJoinSingleRows(List<String> thisRow,
            List<String> otherRow,
            List<Pair<Integer, Integer>> linkedPairs,
            List<Integer> unrelatedOtherIndexes) {
        
        if (!doesRowsMatchByNaturalJoin(thisRow, otherRow, linkedPairs)) {
            throw new IllegalArgumentException("Rows are not able to natural join");
        }
        List<String> newRow = new ArrayList<>(thisRow);
        for (int otherIdx : unrelatedOtherIndexes) {
            newRow.add(otherRow.get(otherIdx));   
        }
        return newRow;
    }
    
    private static boolean doesRowsMatchByNaturalJoin(List<String> thisRow,
            List<String> otherRow,
            List<Pair<Integer, Integer>> linkedPairs) {
        return linkedPairs.stream()
                .allMatch(pair -> Objects.equals(thisRow.get(pair.getFirst()), otherRow.get(pair.getSecond())));
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
                && Objects.equals(dataRows, that.dataRows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnHeaders, dataRows);
    }

    @Override
    public String toString() {
        return "TableHolder{" +
                "columnHeaders=" + columnHeaders +
                ", dataRows=" + dataRows +
                '}';
    }
}
