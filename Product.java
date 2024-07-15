package com.jspiders.product.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product")
@Data
public class Product {

	@Id
	private long id ;
    private String title ;
    private long price;
    private String description;
    private String category;
    private String image;
    private boolean sold;
    private String dateOfSale;
}