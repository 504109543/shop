package com.shop.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.springframework.stereotype.Controller;

import com.shop.entity.Product;
import com.shop.entity.ProductBigType;
import com.shop.entity.ProductSmallType;
import com.shop.entity.ShoppingCart;
import com.shop.entity.ShoppingCartItem;
import com.shop.entity.User;
import com.shop.service.ProductService;
import com.shop.util.ResponseUtil;
import com.opensymphony.xwork2.ActionSupport;

@Controller
public class ShoppingAction extends ActionSupport implements ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HttpServletRequest request;
	Map<String, Object> map = new HashMap<String, Object>();
	private int count;
	
	
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	
	@Resource
	private ProductService productService;
	
	private int productId;
	
	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String addShoppingCartItem()throws Exception{
		
		HttpSession session=request.getSession();
		Product product=productService.getProductById(productId);
		ShoppingCart shoppingCart=(ShoppingCart) session.getAttribute("shoppingCart");
		if(shoppingCart==null){
			shoppingCart=new ShoppingCart();
			User currentUser=(User) session.getAttribute("currentUser");
			shoppingCart.setUserId(currentUser.getId());
		}
		List<ShoppingCartItem> shoppingCartItemList=shoppingCart.getShoppingCartItems();		
		boolean flag=true;
		for(ShoppingCartItem scI:shoppingCartItemList){
			if(scI.getProduct().getId()==product.getId()){
				scI.setCount(scI.getCount()+1);
				flag=false;
				break;
			}
		}
		ShoppingCartItem shoppingCartItem=new ShoppingCartItem();
		
		if(flag){
			shoppingCartItem.setProduct(product);
			shoppingCartItem.setCount(1);
			shoppingCartItemList.add(shoppingCartItem);
		}
		
		session.setAttribute("shoppingCart", shoppingCart);
		JsonConfig jsonConfig=new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONObject result=new JSONObject()/*.fromObject(shoppingCart, jsonConfig)*/;		
		result.put("success", true);
		//result.put("shoppingCart", shoppingCart);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	public String buy()throws Exception{
		HttpSession session=request.getSession();
		Product product=productService.getProductById(productId);
		
		ShoppingCart shoppingCart=(ShoppingCart) session.getAttribute("shoppingCart");
		if(shoppingCart==null){
			shoppingCart=new ShoppingCart();
			User currentUser=(User) session.getAttribute("currentUser");
			shoppingCart.setUserId(currentUser.getId());
		}
		List<ShoppingCartItem> shoppingCartItemList=shoppingCart.getShoppingCartItems();
		
		boolean flag=true;
		for(ShoppingCartItem scI:shoppingCartItemList){
			if(scI.getProduct().getId()==product.getId()){
				scI.setCount(scI.getCount()+1);
				flag=false;
				break;
			}
		}
		
		ShoppingCartItem shoppingCartItem=new ShoppingCartItem();
		
		if(flag){
			shoppingCartItem.setProduct(product);
			shoppingCartItem.setCount(1);
			shoppingCartItemList.add(shoppingCartItem);
		}
		
		session.setAttribute("shoppingCart", shoppingCart);
		return SUCCESS;
	}
	
	public String updateShoppingCartItem()throws Exception{
		HttpSession session=request.getSession();
		//System.out.println("添加商品 的id"+productId);
		Product product=productService.getProductById(productId);
		System.out.println("添加商品 的名字"+product.getName());
		ShoppingCart shoppingCart=(ShoppingCart) session.getAttribute("shoppingCart");
		List<ShoppingCartItem> shoppingCartItemList=shoppingCart.getShoppingCartItems();
		for(ShoppingCartItem scI:shoppingCartItemList){
			if(scI.getProduct().getId()==product.getId()){
				scI.setCount(count);
				break;
			}
		}
		
		session.setAttribute("shoppingCart", shoppingCart);
		
		JSONObject result=new JSONObject();
		result.put("success", true);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		
		return null;
	}
	
	public String removeShoppingCartItem()throws Exception{
		HttpSession session=request.getSession();
		JSONObject result=new JSONObject();
		System.out.println("要删除的ID"+productId);
		ShoppingCart shoppingCart=(ShoppingCart) session.getAttribute("shoppingCart");
		List<ShoppingCartItem> shoppingCartItemList=shoppingCart.getShoppingCartItems();
		for(int i=0;i<shoppingCartItemList.size();i++){
			if(productId==shoppingCartItemList.get(i).getProduct().getId()){
				shoppingCartItemList.remove(i);
				break;
			}
		}
		shoppingCart.setShoppingCartItems(shoppingCartItemList);
		session.setAttribute("shoppingCart", shoppingCart);
		result.put("success", true);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	public String list(){
		HttpSession session=request.getSession();
		ShoppingCart shoppingCart=(ShoppingCart) session.getAttribute("shoppingCart");
		//System.out.println("list UserId"+shoppingCart.getUserId());
		/*if(shoppingCart.getShoppingCartItems()!=null)
		for(ShoppingCartItem scI:shoppingCart.getShoppingCartItems()){
			System.out.println("里面有什么系列：名字"+scI.getProduct().getName()+"数量"+scI.getCount());			
		}*/
		try {
			if(!shoppingCart.getShoppingCartItems().isEmpty())
			{
			JsonConfig jsonConfig=new JsonConfig();
			jsonConfig.setExcludes(new String[]{"orderProductList","productList","smallTypeList"});//不需要转换
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);  
			jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
			new JSONObject();
			JSONObject result=JSONObject.fromObject(shoppingCart, jsonConfig);
			ResponseUtil.write(ServletActionContext.getResponse(), result);
			}
			else
				ResponseUtil.write(ServletActionContext.getResponse(), "null");
			return null;
			
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
		
		
	
		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request=request;
	}

}
