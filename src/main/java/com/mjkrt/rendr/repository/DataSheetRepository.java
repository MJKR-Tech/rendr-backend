package com.mjkrt.rendr.repository;

import com.mjkrt.rendr.entity.DataSheet;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DataSheetRepository.
 *
 * This repository helps provide relevant database query templates relating to DataSheet instances.
 */
public interface DataSheetRepository extends JpaRepository<DataSheet, Long> {
}
