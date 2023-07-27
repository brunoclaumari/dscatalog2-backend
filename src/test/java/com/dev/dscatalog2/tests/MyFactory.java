package com.dev.dscatalog2.tests;

import java.time.Instant;

import com.dev.dscatalog2.configs.MyMapper;
import com.dev.dscatalog2.dto.ProductDTO;
import com.dev.dscatalog2.entities.Category;
import com.dev.dscatalog2.entities.Product;


public class MyFactory {
		
	//private MyMapper mapper = new MyMapper();
	
	public static Product createInstanceProduct() {
		Product product = new Product(1L, "PlayStation", "Console Play Station", 4500.0, "https://img.com/img.png", Instant.parse("2020-07-13T20:50:07.145Z"));
		product.getCategories().add(new Category(2L, "Electronics"));
		
		return product;
	}
	
	public static ProductDTO createInstanceProductDTO() {
		Product product = createInstanceProduct();		
		ProductDTO dto = getMapper().productToDto(product, product.getCategories());
		
		return dto;
	}
	
	public static MyMapper getMapper() {
		return new MyMapper();
	}

}
