package com.mjkrt.rendr.repository;

import com.mjkrt.rendr.entity.DataTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DataTemplateRepository.
 *
 * This repository helps provide relevant database query templates relating to DataTemplate instances.
 */
public interface DataTemplateRepository extends JpaRepository<DataTemplate, Long> {
}
