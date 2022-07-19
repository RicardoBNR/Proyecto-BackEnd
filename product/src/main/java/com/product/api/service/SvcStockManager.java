package com.product.api.service;

import java.util.List;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.DtoProduct;

public interface SvcStockManager {
	
	public ApiResponse updateProducts(List<DtoProduct> products);

}
