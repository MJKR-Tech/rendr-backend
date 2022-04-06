package com.mjkrt.rendr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjkrt.rendr.entity.DataContainer;

public interface DataContainerRepository extends JpaRepository<DataContainer, Long> {
}
