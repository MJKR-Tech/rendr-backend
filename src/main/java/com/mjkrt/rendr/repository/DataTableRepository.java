package com.mjkrt.rendr.repository;

import com.mjkrt.rendr.entity.DataTable;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DataTableRepository.
 *
 * This repository helps provide relevant database query templates relating to DataTable instances.
 */
public interface DataTableRepository extends JpaRepository<DataTable, Long> {
}
