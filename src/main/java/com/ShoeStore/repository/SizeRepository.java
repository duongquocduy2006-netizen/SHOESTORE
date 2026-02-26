package com.ShoeStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ShoeStore.model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {

}
