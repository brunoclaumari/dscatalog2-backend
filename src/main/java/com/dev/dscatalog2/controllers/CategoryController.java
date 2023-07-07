package com.dev.dscatalog2.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dev.dscatalog2.dto.CategoryDTO;
import com.dev.dscatalog2.services.CategoryService;


@RestController
@RequestMapping(value = "/categories")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping
	public ResponseEntity<List<CategoryDTO>> getAll(){
		List<CategoryDTO> list = new ArrayList<>();

		list = categoryService.findAll();
		
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {

		CategoryDTO dto = categoryService.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO dto) {
		dto = categoryService.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();

		return ResponseEntity.created(uri).body(dto);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
		dto = categoryService.update(id, dto);

		return ResponseEntity.ok().body(dto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> delete(@PathVariable Long id) {
		categoryService.delete(id);

		return ResponseEntity.noContent().build();
		// vai retornar 204 que é que deu certo e o corpo da
		// resposta está vazio.
	}

}