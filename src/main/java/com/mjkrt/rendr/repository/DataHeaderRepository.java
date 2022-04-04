package com.mjkrt.rendr.repository;

import com.mjkrt.rendr.entity.DataContainer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DataHeaderRepository extends JpaRepository<DataContainer, Long> {
}
