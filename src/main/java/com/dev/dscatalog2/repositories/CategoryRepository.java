package com.dev.dscatalog2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.dscatalog2.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}

