package com.dev.dscatalog2.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dev.dscatalog2.configs.MyMapper;
import com.dev.dscatalog2.dto.ProductDTO;
import com.dev.dscatalog2.entities.Product;
import com.dev.dscatalog2.repositories.CategoryRepository;
import com.dev.dscatalog2.repositories.ProductRepository;
import com.dev.dscatalog2.services.exception.DatabaseException;
import com.dev.dscatalog2.services.exception.ResourceNotFoundException;
import com.dev.dscatalog2.tests.MyFactory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private MyMapper myMapper;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository catRepository;
	
	long existingId;	
	long nonExistingId;
	long dependentId;
	Product product;
	PageImpl<Product> page;
	ProductDTO productDTO;
	
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = MyFactory.createInstanceProduct();		
		product.setId(existingId);
		page = new PageImpl<>(List.of(product));
		productDTO = MyFactory.createInstanceProductDTO();
		
		//Configura comportamento simulado no objeto mockado
		//Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.findAllPagedQuery(ArgumentMatchers.any(), ArgumentMatchers.anyString(),(Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		//repository Save
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		//repository findById
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		//repository getReferenceById (substitui o getOne) 
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingId);
		
		//Simulando comportamento da injeção do mapper
		Mockito.when(myMapper.productToDto(product, product.getCategories())).thenReturn(productDTO);
		Mockito.when(myMapper.dtoToProduct(productDTO)).thenReturn(product);
		
				
		Mockito.doNothing().when(repository).deleteById(existingId);
		
		Mockito.doThrow(EmptyResultDataAccessException.class)
		.when(repository).deleteById(nonExistingId);
		
		Mockito.doThrow(DataIntegrityViolationException.class)
		.when(repository).deleteById(dependentId);
		
	}
	
	//Exercício Mockito 1 
	@Test
	public void updateShouldResourceNotFoundExceptionWhenIdDoesNotExists() {		
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			@SuppressWarnings("unused")
			ProductDTO productDTOMock = service.update(nonExistingId, productDTO);			
		});
		
		Mockito.verify(repository, Mockito.times(1)).getReferenceById(nonExistingId);
	}
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() {			
		
		ProductDTO productDTOMock = service.update(existingId, productDTO);
		System.out.println("Teste bruno: " + productDTOMock);
		
		Assertions.assertNotNull(productDTOMock);
		Assertions.assertNotNull(productDTOMock.id());		
		
		Mockito.verify(repository, Mockito.times(1)).getReferenceById(existingId);
	}
	

	@Test
	public void findByIdShouldResourceNotFoundExceptionWhenIdDoesNotExists() {		
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			@SuppressWarnings("unused")
			ProductDTO productDTO = service.findById(nonExistingId);			
		});
		
		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() {
		
		ProductDTO productDTO = service.findById(existingId);
		
		//Assertions.assertNotNull(new ProductDTO(1L, null, null, null, null, null, null));
		Assertions.assertTrue(productDTO != null);
		
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}
	
	//Exercício Mockito 1 fim
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPagedQuery(0L, "", pageable);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).findAllPagedQuery(null, "", pageable);
		
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
