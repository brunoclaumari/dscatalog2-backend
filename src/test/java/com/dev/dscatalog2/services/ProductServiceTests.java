package com.dev.dscatalog2.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dev.dscatalog2.entities.Product;
import com.dev.dscatalog2.repositories.ProductRepository;
import com.dev.dscatalog2.services.exception.DatabaseException;
import com.dev.dscatalog2.services.exception.ResourceNotFoundException;
import com.dev.dscatalog2.tests.MyFactory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	long existingId;	
	long nonExistingId;
	long dependentId;
	Product product;
	PageImpl<Product> page;
	
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = MyFactory.createInstanceProduct();
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		//Configura comportamento simulado no objeto mockado
		Mockito.doNothing().when(repository).deleteById(existingId);
		
		Mockito.doThrow(EmptyResultDataAccessException.class)
		.when(repository).deleteById(nonExistingId);
		
		Mockito.doThrow(DataIntegrityViolationException.class)
		.when(repository).deleteById(dependentId);
		
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {		
		
		Assertions.assertThrows(DatabaseException.class,()->{
			service.delete(dependentId);			
		});
		
		//Verifica se o "deleteById" do repository foi chamado na execução
		//da assertion acima
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {		
		
		Assertions.assertThrows(ResourceNotFoundException.class,()->{
			service.delete(nonExistingId);			
		});
		
		//Verifica se o "deleteById" do repository foi chamado na execução
		//da assertion acima
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {		
		
		Assertions.assertDoesNotThrow(()->{
			service.delete(existingId);			
		});
		
		//Verifica se o "deleteById" do repository foi chamado na execução
		//da assertion acima
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	

}
