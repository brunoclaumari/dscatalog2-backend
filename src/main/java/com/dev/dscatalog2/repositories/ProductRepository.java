package com.dev.dscatalog2.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dev.dscatalog2.entities.Category;
import com.dev.dscatalog2.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT DISTINCT p FROM Product p "
			+ "INNER JOIN p.categories cats WHERE "
			+ "(:categories IS NULL OR cats IN :categories) AND "
			+ "(LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) )")
	Page<Product> findAllPagedQuery(
			@Param("categories") List<Category> categories,
			@Param("name") String name, 
			Pageable pageable);
}

