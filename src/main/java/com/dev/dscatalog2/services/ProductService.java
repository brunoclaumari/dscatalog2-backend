package com.dev.dscatalog2.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dscatalog2.configs.MyMapper;
import com.dev.dscatalog2.dto.ProductDTO;
import com.dev.dscatalog2.entities.Category;
import com.dev.dscatalog2.entities.Product;
import com.dev.dscatalog2.repositories.CategoryRepository;
import com.dev.dscatalog2.repositories.ProductRepository;
import com.dev.dscatalog2.services.exception.DatabaseException;
import com.dev.dscatalog2.services.exception.ResourceNotFoundException;



@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private MyMapper mapper;
	
	@Transactional(readOnly = true)
	public List<ProductDTO> findAll(){
		List<ProductDTO> listDto=new ArrayList<>();	

		
		listDto = productRepository.findAll().stream()
				.map(x -> mapper.productToDto(x, x.getCategories()))
				.collect(Collectors.toList());	
		
		return listDto;
	}
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		Page<Product> list;
		
		list = productRepository.findAll(pageRequest);
		
		return list.map(x -> mapper.productToDto(x, x.getCategories()));
		
	}
	
//	@Transactional(readOnly = true) //jeito antigo
//	public Page<ProductDTO> findAllPagedQuery(Long categoryId, String name,PageRequest pageRequest){
//		List<Category> categories = categoryId == 0 ? null : Arrays.asList(categoryRepository.findById(categoryId).get());
//		
//		Page<Product> list;
//		
//		//list = productRepository.findAll(pageRequest);
//		list = productRepository.findAllPagedQuery(categories, name, pageRequest);
//		
//		return list.map(x -> mapper.productToDto(x, x.getCategories()));
//		
//	}
	
	@Transactional(readOnly = true)//jeito novo de paginação
	public Page<ProductDTO> findAllPagedQuery(Long categoryId, String name,Pageable pageable){
		List<Category> categories = categoryId == 0 ? null : Arrays.asList(categoryRepository.findById(categoryId).get());
		
		Page<Product> list;
		
		//list = productRepository.findAll(pageRequest);
		list = productRepository.findAllPagedQuery(categories, name, pageable);
		
		return list.map(x -> mapper.productToDto(x, x.getCategories()));
		
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = productRepository.findById(id);
		/*
		 * No lugar de um try/catch é usado o método 'orElseThrow' que lança
		 * a exceção personalizada criada caso o 'obj' não traga valores 
		 * na requisição. 
		 * */		
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		
		return mapper.productToDto(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		entity = mapper.dtoToProduct(dto);		
		entity = productRepository.save(entity);
		
		return mapper.productToDto(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto){
		ProductDTO productDTO = null;
		try {
			Product entity = productRepository.getReferenceById(id);
			
			entity = mapper.dtoToProduct(dto);
			entity = productRepository.save(entity);		
			productDTO =  mapper.productToDto(entity, entity.getCategories());
			
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: "+ id);			
		}
		//return dto;
		return productDTO;
		
	}
	
	//Único sem Transactional, pois tem que capturar uma exceção e o transactional não deixaria
	public void delete(Long id) {
		try {
			productRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {//lançada na camada repository	
			//exception abaixo da camada de service
			throw new ResourceNotFoundException("Id not found: " + id);
		} catch (DataIntegrityViolationException e) {	//lançada na camada repository	
			//exception abaixo da camada de service
			throw new DatabaseException("Integrity Violation!");
		}
		
	}

}
