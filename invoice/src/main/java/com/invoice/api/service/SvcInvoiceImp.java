package com.invoice.api.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoProduct;
import com.invoice.api.entity.Cart;
import com.invoice.api.entity.Invoice;
import com.invoice.api.entity.Item;
import com.invoice.api.repository.RepoCart;
import com.invoice.api.repository.RepoInvoice;
import com.invoice.api.repository.RepoItem;
import com.invoice.configuration.client.ProductClient;
import com.invoice.exception.ApiException;

@Service
public class SvcInvoiceImp implements SvcInvoice {

	@Autowired
	RepoInvoice repo;
	
	@Autowired
	RepoItem repoItem;
	
	@Autowired
	RepoCart repoCart;
	
	@Autowired
	ProductClient productPl;

	@Override
	public List<Invoice> getInvoices() {
		return repo.findByStatus(1);
	}
	
	@Override
	public List<Invoice> getInvoices(String rfc) {
		return repo.findByRfcAndStatus(rfc, 1);
	}

	@Override
	public List<Item> getInvoiceItems(Integer invoice_id) {
		return repoItem.getInvoiceItems(invoice_id);
	}

	@Override
	public ApiResponse generateInvoice(String rfc) {
		Double subtotal = 0.0;
		Double taxes = 0.0;
		Double total = 0.0;
		Double it_subtotal = 0.0;
		Double it_taxes = 0.0;
		Double it_total = 0.0;
		
		DtoProduct product;		
		List<DtoProduct> productos = new ArrayList<DtoProduct>();		
		
		List<Cart> carritos = repoCart.findByRfcAndStatus(rfc,1);
		
		if(carritos.isEmpty())
			throw new ApiException(HttpStatus.NOT_FOUND, "Cart has no items");
		else {
			Invoice invoice = new Invoice();
			invoice = repo.save(invoice);	
			
			for(Cart carrito :carritos) {
				ResponseEntity<DtoProduct> response = productPl.getProduct(carrito.getGtin());		
				product = response.getBody();
				it_total = (carrito.getQuantity()*product.getPrice());
				it_subtotal = it_total*.84;
				it_taxes = it_total*.16;				
				subtotal +=  it_subtotal ;
				taxes += it_taxes;
				total += it_total;
				Item item = new Item(invoice.getInvoice_id(),
									 carrito.getGtin(),
									 carrito.getQuantity(),
									 product.getPrice(),
									 it_subtotal,
									 it_taxes,
									 it_total,
									 1);
				repoItem.save(item);
				product.setStock(carrito.getQuantity());
				productos.add(product);
			}
			repo.updateInvoice(invoice.getInvoice_id(),rfc, subtotal, taxes, total);			
			productPl.updateProduct(productos);	//Esto va en un try/catch pero no sé como manejar los errores de feign.	
			repoCart.clearCart(rfc);
			
		}	
		return new ApiResponse("invoice generated");
	}

	

}
