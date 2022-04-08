package com.mjkrt.rendr.service.file;

/**
 * FileScheduleService.
 * 
 * This service helps to provide CRON jobs on the files stored locally from user uploads.
 */
public interface FileScheduleService {

    /**
     * Removes redundant files present in storage.
     */
    void removeRedundantFiles();
}
