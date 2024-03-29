package com.dev.dscatalog2.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dev.dscatalog2.dto.ProductDTO;
import com.dev.dscatalog2.services.ProductService;


@RestController
@RequestMapping(value = "/products")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
//	@GetMapping //jeito antigo de paginação
//	public ResponseEntity<Page<ProductDTO>> findAll(
//			@RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
//			@RequestParam(value = "name", defaultValue = " ") String name,
//			@RequestParam(value = "page", defaultValue = "0") Integer page,
//			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
//			@RequestParam(value = "direction", defaultValue = "ASC") String direction,
//			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy// vai ordenar pelo atributo 'name'
//			){
//		//define o objeto de paginação
//		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
//		
//		Page<ProductDTO> list = productService.findAllPagedQuery(categoryId, name.trim(), pageRequest);
//		
//		return ResponseEntity.ok().body(list);
//	}
	@GetMapping //jeito novo de paginação
	public ResponseEntity<Page<ProductDTO>> findAll(
			@RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
			@RequestParam(value = "name", defaultValue = " ") String name,
			Pageable pageable
			){
		//define o objeto de paginação
		//PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		Page<ProductDTO> list = productService.findAllPagedQuery(categoryId, name.trim(), pageable);
		
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {

		ProductDTO dto = productService.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO dto) {
		dto = productService.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();

		return ResponseEntity.created(uri).body(dto);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
		dto = productService.update(id, dto);

		return ResponseEntity.ok().body(dto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> delete(@PathVariable Long id) {
		productService.delete(id);

		return ResponseEntity.noContent().build();
		// vai retornar 204 que é que deu certo e o corpo da
		// resposta está vazio.
	}

}
