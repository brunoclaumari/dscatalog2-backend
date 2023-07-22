package com.dev.dscatalog2.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.dev.dscatalog2.entities.Product;
import com.dev.dscatalog2.tests.MyFactory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private long existingId;	
	private long nonExistingId;
	private long totalInitialProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 2L;
		nonExistingId = 30L;
		totalInitialProducts = 25L;
	}
	
	@Test
	public void findByIdShouldReturnOptionalProductNotNullWhenIdDoesExists() {
		Optional<Product> prod = repository.findById(existingId);
		System.out.println("Produto: " + prod);
		
		Assertions.assertNotNull(prod);
	}
	
	@Test
	public void findByIdShouldReturnOptionalProductEmptyWhenIdDoesNotExists() {
		Optional<Product> prod = repository.findById(nonExistingId);
		System.out.println("Produto: " + prod);
		
		Assertions.assertEquals(Optional.empty(), prod);
	}
	
	@Test
	public void saveShouldPersistProductWithAutoIncrementWhenIdIsNull() {
		Product product = MyFactory.createInstanceProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(totalInitialProducts + 1, product.getId());		
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenExists() {	
		
		repository.deleteById(existingId);
		
		Optional<Product> prodOptional = repository.findById(existingId);
		
		Assertions.assertFalse(prodOptional.isPresent());		
	}
	
	@Test
	public void deleteShouldThrowsEmptyResultDataAccessExceptionWhenIdDoesNotExists() {	
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, ()->{			
			repository.deleteById(nonExistingId);
		});	
				
	}

}
