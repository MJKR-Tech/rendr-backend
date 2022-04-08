package com.mjkrt.rendr.service.template;

import java.util.List;

import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.DataTemplate;

/**
 * DataTemplateService.
 * 
 * This service helps to provide an interface between other services and the DataTemplate repository.
 */
public interface DataTemplateService {
    
    /**
     * Lists all DataTemplates present.
     * 
     * @return a list of DataTemplates
     */
    List<DataTemplate> listAll();

    /**
     * Lists all DataTemplate IDs present.
     * 
     * @return a list of IDs
     */
    List<Long> listAllIds();

    /**
     * Finds a DataTemplate based on an ID.
     * If not present, an EntityNotFound exception will be thrown.
     * 
     * @param id desired DataTemplate ID
     * @return a DataTemplate instance
     */
    DataTemplate findById(long id);

    /**
     * Checks if a DataTemplate is present based on a given ID.
     * 
     * @param id desired DataTemplate ID
     * @return true if DataTemplate with specified ID is present, else false
     */
    boolean isPresent(long id);

    /**
     * Saves a DataTemplate instance.
     * IDs of the DataTemplate and children classes will be automatically assigned.
     * 
     * @param dataTemplate DataTemplate to save
     * @return updated DataTemplate with assigned IDs
     */
    DataTemplate save(DataTemplate dataTemplate);

    /**
     * Deletes a DataTemplate instance by its ID if present.
     * If not present, an EntityNotFound exception will be thrown.
     * 
     * @param id DataTemplate ID to delete
     */
    void deleteById(long id);

    /**
     * Deletes all DataTemplates present.
     */
    void deleteAll();

    /**
     * Finds all DataTables with the specified DataTemplate ID.
     * If not present, an EntityNotFound exception will be thrown.
     * 
     * @param id DataTemplate ID in question
     * @return list of associated DataTables
     */
    List<DataTable> findDataTablesWithTemplateId(long id);

    /**
     * Finds all DataCells with the specified DataTemplate ID.
     * If not present, an EntityNotFound exception will be thrown.
     *
     * @param id DataTemplate ID in question
     * @return list of associated DataCells
     */
    List<DataCell> findDataCellsWithTemplateId(long id);

    /**
     * Finds all DataSheets with the specified DataTemplate ID.
     * If not present, an EntityNotFound exception will be thrown.
     *
     * @param id DataTemplate ID in question
     * @return list of associated DataSheets
     */
    List<DataSheet> findDataSheetsWithTemplateId(long id);
}
