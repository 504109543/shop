package com.shop.service;

import java.util.List;

import com.shop.entity.PageBean;
import com.shop.entity.ProductSmallType;

public interface ProductSmallTypeService {

	public List<ProductSmallType> findProductSmallTypeList(ProductSmallType s_productSmallType,PageBean pageBean);
	 
	public boolean existSmallTypeWithBigTypeId(int bigTypeId);
	
	public Long getProductSmallTypeCount(ProductSmallType s_productSmallType);
	
	public void saveProductSmallType(ProductSmallType productSmallType);
	
	public void delete(ProductSmallType productSmallType);
	
	public ProductSmallType getProductSmallTypeById(int id);
}
