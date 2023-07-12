package com.dev.dscatalog2.dto;

import java.time.Instant;
import java.util.List;

public record ProductDTO(		
		Long id,		
		String name,
		String description,
		Double price,
		String imgUrl	,
		Instant date
		,List<CategoryDTO> categories
		) 
{

}
