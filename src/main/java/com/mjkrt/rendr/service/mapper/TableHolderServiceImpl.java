package com.mjkrt.rendr.service.mapper;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;

import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class TableHolderServiceImpl implements TableHolderService {

    private static final Logger LOG = LogsCenter.getLogger(TableHolderServiceImpl.class);

    @Override
    public boolean checkIfCanNaturalJoin(TableHolder t1, TableHolder t2) {
        List<Pair<Integer, Integer>> linkedPairs = getSameHeaderIndexPairs(t1.getColumnHeaders(),
                t2.getColumnHeaders());
        return !linkedPairs.isEmpty();
    }

    private List<Pair<Integer, Integer>> getSameHeaderIndexPairs(List<ColumnHeader> headers1,
            List<ColumnHeader> headers2) {
        
        List<Pair<Integer, Integer>> linkedPairs = new ArrayList<>();
        for (int i = 0; i < headers1.size(); i++) {
            for (int j = 0; j < headers2.size(); j++) {
                if (headers1.get(i).equals(headers2.get(j))) {
                    linkedPairs.add( new Pair<>(i, j) );
                }
            }
        }
        return linkedPairs;
    }

    @Override
    public TableHolder naturalJoin(TableHolder t1, TableHolder t2) {
        List<ColumnHeader> headers1 = t1.getColumnHeaders();
        List<ColumnHeader> headers2 = t2.getColumnHeaders();
        Set<List<String>> rows1 = t1.getDataRows();
        Set<List<String>> rows2 = t2.getDataRows();
        
        List<Pair<Integer, Integer>> linkedPairs = getSameHeaderIndexPairs(headers1, headers2);
        if (linkedPairs.isEmpty()) {
//            throw new IllegalArgumentException("TableHolders are not able to natural join");
            return null;
        }
        List<Integer> unrelatedOtherIndexes = getOtherExcessHeaderIndexes(headers2, linkedPairs);
        List<ColumnHeader> newHeaders = naturalJoinHeaders(headers1, headers2, unrelatedOtherIndexes);
        Set<List<String>> newDataRows = naturalJoinDataRows(rows1, rows2, linkedPairs, unrelatedOtherIndexes);
        return new TableHolder(newHeaders, newDataRows);
    }

    private List<Integer> getOtherExcessHeaderIndexes(List<ColumnHeader> otherHeaders,
            List<Pair<Integer, Integer>> linkedPairs) {
        
        Set<Integer> otherLinkedIndexes = linkedPairs.stream()
                .map(Pair::getSecond)
                .collect(Collectors.toSet());

        return IntStream.range(0, otherHeaders.size())
                .filter(idx -> !otherLinkedIndexes.contains(idx))
                .boxed()
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
    }

    private List<ColumnHeader> naturalJoinHeaders(List<ColumnHeader> headers1,
            List<ColumnHeader> headers2,
            List<Integer> unrelatedOtherIndexes) {

        List<ColumnHeader> newColumnHeaders = new ArrayList<>(headers1);
        for (int otherIdx : unrelatedOtherIndexes) {
            newColumnHeaders.add(headers2.get(otherIdx));
        }
        return newColumnHeaders;
    }

    private Set<List<String>> naturalJoinDataRows(Set<List<String>> rows1,
            Set<List<String>> rows2,
            List<Pair<Integer, Integer>> linkedPairs,
            List<Integer> unrelatedOtherIndexes) {

        Set<List<String>> newDataRows = new HashSet<>();
        for (List<String> thisRow : rows1) {
            for (List<String> otherRow : rows2) {
                if (!doesRowsMatchByNaturalJoin(thisRow, otherRow, linkedPairs)) {
                    continue;
                }
                List<String> newRow = naturalJoinSingleRows(thisRow, otherRow, linkedPairs, unrelatedOtherIndexes);
                newDataRows.add(newRow);
            }
        }
        return newDataRows;
    }

    private List<String> naturalJoinSingleRows(List<String> thisRow,
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

    private boolean doesRowsMatchByNaturalJoin(List<String> thisRow,
            List<String> otherRow,
            List<Pair<Integer, Integer>> linkedPairs) {
        
        Predicate<Pair<Integer, Integer>> matchByStringInRow = pair ->
                Objects.equals(thisRow.get(pair.getFirst()), otherRow.get(pair.getSecond()));
        return linkedPairs.stream().allMatch(matchByStringInRow);
    }

    @Override
    public TableHolder generateSubset(TableHolder t, List<ColumnHeader> desiredColumns) {
        if (t == null) {
            return null;
        }

        List<ColumnHeader> currentHeaders = t.getColumnHeaders();
        List<Integer> indexMappings = getMappingOfHeaders(currentHeaders, desiredColumns);
        int count = 0;
        for (int i : indexMappings) {
            if (i >= 0) {
                count++;
                break;
            }
        }
        if (indexMappings.isEmpty() || count == 0) {
            throw new IllegalArgumentException("Subset to generate cannot be empty");
        }
        
        List<ColumnHeader> newHeaders = getNewHeaders(currentHeaders, indexMappings);
        Set<List<String>> currentRows = t.getDataRows();
        Set<List<String>> newRows = getNewRows(currentRows, indexMappings, newHeaders);
        return new TableHolder(newHeaders, newRows);
    }

    private List<Integer> getMappingOfHeaders(List<ColumnHeader> currentHeaders, List<ColumnHeader> desiredColumns) {
        List<Integer> mappings = new ArrayList<>();
        for (ColumnHeader desiredHeader : desiredColumns) {
            for (int i = 0; i < currentHeaders.size(); i++) {
                ColumnHeader currHeader = currentHeaders.get(i);
                if (currHeader.equals(desiredHeader)) {
                    mappings.add(i);
                }
            }
            if (desiredHeader.getName().isEmpty()) {
                mappings.add(-1);
            }
        }
        return mappings;
    }

    private List<ColumnHeader> getNewHeaders(List<ColumnHeader> currentHeaders, List<Integer> indexMappings) {
        List<ColumnHeader> newHeaders = new ArrayList<>();
        for (int idx : indexMappings) {
            if (idx == -1) {
                newHeaders.add(ColumnHeader.getMockColumnHeader());
                continue;
            }
            newHeaders.add(currentHeaders.get(idx));
        }
        return newHeaders;
    }

    private Set<List<String>> getNewRows(Set<List<String>> currentRows, List<Integer> indexMappings, List<ColumnHeader> desiredCh) {
        Set<List<String>> newRows = new HashSet<>();
        for (List<String> row : currentRows) {
            List<String> newRow = new ArrayList<>();
            for (int idx : indexMappings) {
                if (idx == -1) {
                    newRow.add("");
                    continue;
                }
                newRow.add(row.get(idx));
            }
            newRows.add(newRow);
        }
        return newRows;
    }

    @Override
    public List<TableHolder> compact(List<TableHolder> tableHolders) {
        if (tableHolders.size() < 2) {
            return tableHolders;
        }

        Deque<TableHolder> holders = new LinkedList<>(tableHolders);
        boolean hasChanged = true;
        
        while (hasChanged) {
            hasChanged = false;
            TableHolder top = holders.pop(); // pop from top
            for (int i = 0; i < holders.size(); i++) {
                TableHolder next = holders.pop(); // pop from top
                if (checkIfCanNaturalJoin(top, next)) {
                    hasChanged = true;
                    top = naturalJoin(top, next);
                } else {
                    holders.add(next); // add to bottom
                }
            }
            holders.add(top); // add to bottom
        }
        return new ArrayList<>(holders);
    }
}
