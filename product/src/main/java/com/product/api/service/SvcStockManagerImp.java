package com.product.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.DtoProduct;
import com.product.api.entity.Product;
import com.product.api.repository.RepoProduct;
import com.product.exception.ApiException;

@Service
public class SvcStockManagerImp implements SvcStockManager {
	
	@Autowired
	RepoProduct repo;

	@Override
	public ApiResponse updateProducts(List<DtoProduct> products) {
		try {
			for(DtoProduct product: products) {
				Product aux = repo.findByGtinAndStatus(product.getGtin(), 1);
				if(aux.getStock() < product.getStock()) {
					throw new ApiException(HttpStatus.BAD_REQUEST, "Out of stock"); //Hacemos una validación por si algún loco quita más de lo debido
				}else {
					repo.updateProductStock(product.getGtin(),product.getStock());
				}					
			}		
		}catch (DataIntegrityViolationException e) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "product not found");
		}
		return new ApiResponse("product updated");	
	}

}
