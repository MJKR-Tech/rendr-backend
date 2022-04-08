package com.mjkrt.rendr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjkrt.rendr.entity.DataContainer;

/**
 * DataContainerRepository.
 *
 * This repository helps provide relevant database query templates relating to DataContainer instances.
 */
public interface DataContainerRepository extends JpaRepository<DataContainer, Long> {
}
