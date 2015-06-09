package com.shop.entity;

import java.io.Serializable;

public class ShoppingCartItem implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private Product product;
	private int count;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
