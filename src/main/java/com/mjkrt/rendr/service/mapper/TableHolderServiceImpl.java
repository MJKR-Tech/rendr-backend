package com.mjkrt.rendr.service.mapper;

import java.util.ArrayList;
import java.util.HashSet;
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

/**
 * TableHolderServiceImpl
 *
 * The implementation of the TableHolderService Interface. Class does the combining of data from TableHolder, and
 * joining of data into bigger subsets.
 */
@Service
public class TableHolderServiceImpl implements TableHolderService {

    private static final Logger LOG = LogsCenter.getLogger(TableHolderServiceImpl.class);

    /**
     * @inheritDoc
     */
    @Override
    public boolean checkIfCanNaturalJoin(TableHolder t1, TableHolder t2) {
        LOG.info("Calling checkIfCanNaturalJoin");
        List<Pair<Integer, Integer>> linkedPairs = getSameHeaderIndexPairs(
                t1.getColumnHeaders(),
                t2.getColumnHeaders());
        return !linkedPairs.isEmpty();
    }

    private List<Pair<Integer, Integer>> getSameHeaderIndexPairs(List<ColumnHeader> headers1,
            List<ColumnHeader> headers2) {

        LOG.info("Calling getSameHeaderIndexPairs");
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

    /**
     * @inheritDoc
     */
    @Override
    public TableHolder naturalJoin(TableHolder t1, TableHolder t2) {
        
        LOG.info("Calling naturalJoin");
        List<ColumnHeader> headers1 = t1.getColumnHeaders();
        List<ColumnHeader> headers2 = t2.getColumnHeaders();
        Set<List<String>> rows1 = t1.getDataRows();
        Set<List<String>> rows2 = t2.getDataRows();
        
        List<Pair<Integer, Integer>> linkedPairs = getSameHeaderIndexPairs(headers1, headers2);
        if (linkedPairs.isEmpty()) {
            return null;
        }
        List<Integer> unrelatedOtherIndexes = getOtherExcessHeaderIndexes(headers2, linkedPairs);
        List<ColumnHeader> newHeaders = naturalJoinHeaders(headers1, headers2, unrelatedOtherIndexes);
        Set<List<String>> newDataRows = naturalJoinDataRows(rows1, rows2, linkedPairs, unrelatedOtherIndexes);
        return new TableHolder(newHeaders, newDataRows);
    }

    /**
     * Returns list of integers of unrelated ColumnHeaders.
     *
     * @param otherHeaders List of ColumnHeaders.
     * @param linkedPairs The Pairs of linked ColumnHeader indexes.
     * @return The list of integers of unrelated ColumnHeaders.
     */
    private List<Integer> getOtherExcessHeaderIndexes(List<ColumnHeader> otherHeaders,
            List<Pair<Integer, Integer>> linkedPairs) {

        LOG.info("Calling getOtherExcessHeaderIndexes");
        Set<Integer> otherLinkedIndexes = linkedPairs.stream()
                .map(Pair::getSecond)
                .collect(Collectors.toSet());

        return IntStream.range(0, otherHeaders.size())
                .filter(idx -> !otherLinkedIndexes.contains(idx))
                .boxed()
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * Returns a big subset list of ColumnHeaders from headers1 and headers2.
     *
     * @param headers1 The first list of ColumnHeaders.
     * @param headers2 The second list of ColumnHeaders.
     * @param unrelatedOtherIndexes The indexes of unrelated ColumnHeaders.
     * @return The big subset list of ColumnHeaders.
     */
    private List<ColumnHeader> naturalJoinHeaders(List<ColumnHeader> headers1,
            List<ColumnHeader> headers2,
            List<Integer> unrelatedOtherIndexes) {

        LOG.info("Calling naturalJoinHeaders");
        List<ColumnHeader> newColumnHeaders = new ArrayList<>(headers1);
        for (int otherIdx : unrelatedOtherIndexes) {
            newColumnHeaders.add(headers2.get(otherIdx));
        }
        return newColumnHeaders;
    }

    /**
     * Returns new set of list of String naturally joined from two sets.
     *
     * @param rows1 First set of list of String.
     * @param rows2 Second set of list of String.
     * @param linkedPairs The related indexes of lists of String.
     * @param unrelatedOtherIndexes The unrelated indexes of lists of String.
     * @return The new set of list of String.
     */
    private Set<List<String>> naturalJoinDataRows(Set<List<String>> rows1,
            Set<List<String>> rows2,
            List<Pair<Integer, Integer>> linkedPairs,
            List<Integer> unrelatedOtherIndexes) {

        LOG.info("Calling naturalJoinDataRows");
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

    /**
     * Returns new list of String naturally joined from two lists.
     *
     * @param rows1 The first list of String.
     * @param rows2 The second list of String.
     * @param linkedPairs The related indexes of String.
     * @param unrelatedOtherIndexes The unrelated indexes of String.
     * @return The new list of String.
     */
    private List<String> naturalJoinSingleRows(List<String> thisRow,
            List<String> otherRow,
            List<Pair<Integer, Integer>> linkedPairs,
            List<Integer> unrelatedOtherIndexes) {

        LOG.info("Calling naturalJoinSingleRows");
        if (!doesRowsMatchByNaturalJoin(thisRow, otherRow, linkedPairs)) {
            throw new IllegalArgumentException("Rows are not able to natural join");
        }
        List<String> newRow = new ArrayList<>(thisRow);
        for (int otherIdx : unrelatedOtherIndexes) {
            newRow.add(otherRow.get(otherIdx));
        }
        return newRow;
    }

    /**
     * Checks if two rows of data (list of String) match by Natural Join.
     *
     * @param thisRow The first list of String.
     * @param otherRow The second list of String.
     * @param linkedPairs The related indexes of String.
     * @return True if joined, false otherwise.
     */
    private boolean doesRowsMatchByNaturalJoin(List<String> thisRow,
            List<String> otherRow,
            List<Pair<Integer, Integer>> linkedPairs) {

        LOG.info("Calling doesRowsMatchByNaturalJoin");
        Predicate<Pair<Integer, Integer>> matchByStringInRow = pair ->
                Objects.equals(thisRow.get(pair.getFirst()), otherRow.get(pair.getSecond()));
        return linkedPairs.stream().allMatch(matchByStringInRow);
    }

    /**
     * @inheritDoc
     */
    @Override
    public TableHolder generateSubset(TableHolder t, List<ColumnHeader> desiredColumns) {
        if (t == null) {
            return null;
        }
        
        LOG.info("Calling generateSubset");
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
        Set<List<String>> newRows = getNewRows(currentRows, indexMappings);
        return new TableHolder(newHeaders, newRows);
    }

    /**
     * Returns the list of indexes where desired ColumnHeader in desiredColumns exists in currentHeaders.
     *
     * @param currentHeaders The list of ColumnHeaders to be mapped to.
     * @param desiredColumns The list of ColumnHeaders to be mapped from.
     * @return The list of indexes, where indexes -1 represents an empty ColumnHeader.
     */
    private List<Integer> getMappingOfHeaders(List<ColumnHeader> currentHeaders,
            List<ColumnHeader> desiredColumns) {

        LOG.info("Calling getMappingOfHeaders");
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

    /**
     * Return copy of List of ColumnHeaders with "MOCK" ColumnHeaders to fill empty Headers.
     *
     * @param currentHeaders The list of ColumnHeaders to copy.
     * @param indexMappings The indexes of where each ColumnHeader to append to, where -1 indicates a "MOCK"
     *                      ColumnHeader.
     * @return The copy of List of ColumnHeaders.
     */
    private List<ColumnHeader> getNewHeaders(List<ColumnHeader> currentHeaders, List<Integer> indexMappings) {
        LOG.info("Calling getNewHeaders");
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

    /**
     * Fills the list of String from set currentRows with empty String at places with "MOCK" ColumnHeaders.
     *
     * @param currentRows The set of list of String.
     * @param indexMappings The indexes where -1 indicates a "MOCK" ColumnHeader.
     * @return
     */
    private Set<List<String>> getNewRows(Set<List<String>> currentRows, List<Integer> indexMappings) {
        LOG.info("Calling getNewRows");
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
}
