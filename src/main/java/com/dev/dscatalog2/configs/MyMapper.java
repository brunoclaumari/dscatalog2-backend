package com.dev.dscatalog2.configs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dev.dscatalog2.dto.CategoryDTO;
import com.dev.dscatalog2.dto.ProductDTO;
import com.dev.dscatalog2.entities.Category;
import com.dev.dscatalog2.entities.Product;

@Component
public class MyMapper {
	
	public CategoryDTO categoryToDto(Category category) {
		return new CategoryDTO(category.getId(), category.getName());
	}
	
	
	public Category dtoToCategory(CategoryDTO dto) {
		
		return new Category(dto.id(), dto.name());
	}
	
	
	public ProductDTO productToDto(Product product, Set<Category> categories) {
		
		List<CategoryDTO> dtoCategories = new ArrayList<>();
		if(categories != null && categories.size() > 0)
			categories.forEach(x -> dtoCategories.add(categoryToDto(x)));
		
		
		return new ProductDTO(
				product.getId(), 
				product.getName(), 
				product.getDescription(), 
				product.getPrice(), 
				product.getImgUrl(), 
				product.getDate() 
				,dtoCategories
				);		
		
	}
	
	
	public Product dtoToProduct(ProductDTO dto) {
		Product product = new Product();
		product.setId(dto.id());
		product.setName(dto.name());
		product.setDescription(dto.description());
		product.setPrice(dto.price());
		product.setImgUrl(dto.imgUrl());
		product.setDate(dto.date());
		
		dto.categories().forEach(x -> product.getCategories().add(dtoToCategory(x)));
		
		return product;
	}
	  

}
