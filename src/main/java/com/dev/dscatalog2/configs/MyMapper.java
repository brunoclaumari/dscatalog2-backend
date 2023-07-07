package com.dev.dscatalog2.configs;

import org.springframework.stereotype.Component;

import com.dev.dscatalog2.dto.CategoryDTO;
import com.dev.dscatalog2.entities.Category;

@Component
public class MyMapper {
	
	
	public CategoryDTO categoryToDto(Category category) {
		return new CategoryDTO(category.getId(), category.getName());
	}
	  

}
