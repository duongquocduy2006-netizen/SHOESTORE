package com.ShoeStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ShoeStore.model.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {

}
