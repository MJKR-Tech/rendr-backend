package com.mjkrt.rendr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjkrt.rendr.entity.DataCell;

/**
 * DataCellRepository.
 * 
 * This repository helps provide relevant database query templates relating to DataCell instances.
 */
public interface DataCellRepository extends JpaRepository<DataCell, Long> {
}
