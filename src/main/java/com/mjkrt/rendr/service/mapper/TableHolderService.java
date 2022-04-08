package com.mjkrt.rendr.service.mapper;

import java.util.List;

import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;

/**
 * TableHolderService.
 *
 * This service helps to provide an interface for TableHolderServices to combine data from TableHolders and
 * ColumnHeaders.
 */
public interface TableHolderService {

    /**
     * Checks if two TableHolders are able to be naturally joined. Condition passes if there are common ColumnHeaders
     * within each TableHolder.
     *
     * @param t1 The first TableHolder to be compared.
     * @param t2 The second TableHolder to be compared.
     * @return True if condition passes, false otherwise.
     */
    boolean checkIfCanNaturalJoin(TableHolder t1, TableHolder t2);

    /**
     * Naturally joins two TableHolders into one new TableHolder. Returns this new TableHolder
     *
     * @param t1 The first TableHolder to be joined.
     * @param t2 The second TableHolder to be joined.
     * @return The new joined TableHolder.
     */
    TableHolder naturalJoin(TableHolder t1, TableHolder t2);

    /**
     * Returns a new TableHolder that has only the desired ColumnHeaders (the proper subset of TableHolder).
     *
     * @param t The big subset TableHolder.
     * @param desiredColumns The proper subset of ColumnHeaders desired.
     * @return The proper subset of ColumnHeaders in the new TableHolder.
     */
    TableHolder generateSubset(TableHolder t, List<ColumnHeader> desiredColumns);
}
