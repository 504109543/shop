package com.shop.service;

import java.util.List;

import com.shop.entity.PageBean;
import com.shop.entity.Product;

public interface ProductService {

	public List<Product> findProductList(Product s_product,PageBean pageBean);
	
	public Long getProductCount(Product s_product);
	
	public Product getProductById(int productId);
	
	public void saveProduct(Product product);
	
	public void deleteProduct(Product product);
	
	public boolean existProductWithSmallTypeId(int smallTypeId);
	
}
 