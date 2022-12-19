package com.example.automaticirrigationsystem.repo;

import com.example.automaticirrigationsystem.model.Plot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IrrigationRepository extends JpaRepository<Plot,Long> {

}
