package com.dev.dscatalog2.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.dev.dscatalog2.configs.MyMapper;
import com.dev.dscatalog2.dto.ProductDTO;
import com.dev.dscatalog2.entities.Product;
import com.dev.dscatalog2.services.ProductService;
import com.dev.dscatalog2.services.exception.ResourceNotFoundException;
import com.dev.dscatalog2.tests.MyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@MockBean
	private MyMapper myMapper;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private PageImpl<ProductDTO> page;
	
	private ProductDTO productDTO;
	long existingId;	
	long nonExistingId;
	Product product;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		
		product = MyFactory.createInstanceProduct();		
		product.setId(existingId);
		
		productDTO = MyFactory.createInstanceProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPagedQuery(ArgumentMatchers.any(), 
				ArgumentMatchers.anyString(),(Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(myMapper.productToDto(product, product.getCategories())).thenReturn(productDTO);
		Mockito.when(myMapper.dtoToProduct(productDTO)).thenReturn(product);
		
		when(service.update(eq(existingId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}	

	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId)
				.accept(org.springframework.http.MediaType.APPLICATION_JSON)); 

		result.andExpect(status().isNotFound());
		
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId)
						.accept(org.springframework.http.MediaType.APPLICATION_JSON)); 
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	
	@Test
	public void findAllShouldReturnPage() throws Exception{
		
		ResultActions result = 
				mockMvc.perform(get("/products")
						.accept(org.springframework.http.MediaType.APPLICATION_JSON)
						); 
		
		result.andExpect(status().isOk());
		
	}
	

}
