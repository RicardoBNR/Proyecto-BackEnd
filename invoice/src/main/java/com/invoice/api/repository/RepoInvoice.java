package com.invoice.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.invoice.api.entity.Invoice;

@Repository
public interface RepoInvoice extends JpaRepository<Invoice, Integer>{

	List<Invoice> findByRfcAndStatus(String rfc, Integer status);
	
	
	@Query(value = "SELECT * FROM invoice WHERE status = :status", nativeQuery = true)
	List<Invoice> findByStatus(Integer status);
	
	@Modifying
	@Transactional
	@Query(value ="UPDATE invoice SET rfc=:rfc, subtotal =:subtotal, taxes =:taxes, total =:total  WHERE invoice_id=:invoice_id AND status = 1", nativeQuery = true)
	Integer updateInvoice(@Param("invoice_id") Integer invoice_id, @Param("rfc") String rfc, @Param("subtotal") Double subtotal, @Param("taxes") Double taxes, @Param("total") Double total);

}
