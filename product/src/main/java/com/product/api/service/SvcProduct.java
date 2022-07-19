package com.product.api.service;

import java.util.List;

import com.product.api.dto.ApiResponse;
import com.product.api.entity.Product;

public interface SvcProduct {

	public List<Product> getProducts();
	public Product getProduct(String gtin);
	public ApiResponse createProduct(Product in);
	public ApiResponse updateProduct(Product in, Integer id);
	public ApiResponse deleteProduct(Integer id);
	public ApiResponse updateProductCategory(Integer id);

}
