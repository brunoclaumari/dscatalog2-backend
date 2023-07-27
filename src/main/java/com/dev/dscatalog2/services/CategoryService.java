package com.dev.dscatalog2.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.dscatalog2.configs.MyMapper;
import com.dev.dscatalog2.dto.CategoryDTO;
import com.dev.dscatalog2.entities.Category;
import com.dev.dscatalog2.repositories.CategoryRepository;
import com.dev.dscatalog2.services.exception.ResourceNotFoundException;



@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private MyMapper mapper;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<CategoryDTO> listDto=new ArrayList<>();
		
//		listDto = categoryRepository.findAll().stream()
//		.map(mapper::categoryToDto)
//		.collect(Collectors.toList());	
		
		listDto = categoryRepository.findAll().stream()
				.map(x -> mapper.categoryToDto(x))
				.collect(Collectors.toList());	
		
		return listDto;
	}
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable){
		Page<Category> list;
		
		list = categoryRepository.findAll(pageable);
		
		return list.map(x -> mapper.categoryToDto(x));
		
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = categoryRepository.findById(id);
		/*
		 * No lugar de um try/catch é usado o método 'orElseThrow' que lança
		 * a exceção personalizada criada caso o 'obj' não traga valores 
		 * na requisição. 
		 * */		
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));		
		
		return mapper.categoryToDto(entity);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.name());
		entity = categoryRepository.save(entity);
		
		return mapper.categoryToDto(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto){
		CategoryDTO categoryDTO = null;
		try {
			Category entity = categoryRepository.getReferenceById(id);
			System.out.println(entity);
			entity.setName(dto.name());
			entity = categoryRepository.save(entity);		
			categoryDTO =  mapper.categoryToDto(entity);
			
		} catch (EntityNotFoundException e) {
			//throw new ResourceNotFoundException("Id not found: "+id);
			//throw new Exception("Id not found: "+id);
			System.out.println("Falhou " + e.getMessage());
		}
		//return dto;
		return categoryDTO;
		
	}
	
	//Único sem Transactional, pois tem que capturar uma exceção e o transactional não deixaria
	public void delete(Long id) {
		try {
			categoryRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			//implementar exceção personalizada abaixo
			//throw new ResourceNotFoundException("Id not found: "+id);
		} catch (DataIntegrityViolationException e) {
			//implementar exceção personalizada abaixo
			//throw new DatabaseException("Integrity Violation!");
		}
		
	}

}
