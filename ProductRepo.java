package com.jspiders.product.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jspiders.product.pojo.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

	List<Product> findProductsByPrice(double price);

	@Query(value = "SELECT product FROM Product product WHERE product.title LIKE %:search% OR product.description LIKE %:search%")
	List<Product> findByTitleContainingOrDescriptionContaining(String search);

}
