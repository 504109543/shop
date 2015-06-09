package com.shop.action;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

import com.shop.entity.Order;
import com.shop.entity.OrderProduct;
import com.shop.entity.PageBean;
import com.shop.entity.Product;
import com.shop.entity.ProductBigType;
import com.shop.entity.ProductSmallType;
import com.shop.entity.ShoppingCart;
import com.shop.entity.ShoppingCartItem;
import com.shop.entity.User;
import com.shop.service.OrderService;
import com.shop.util.DateUtil;
import com.shop.util.ResponseUtil;
import com.shop.util.StringUtil;
import com.opensymphony.xwork2.ActionSupport;

@Controller
public class OrderAction extends ActionSupport implements ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HttpServletRequest request;
	
	@Resource
	private OrderService orderService;
	
	private Order s_order;
	private List<Order> orderList;
	
	private int status;
	private String orderNo;
	
	private String page;
	private String rows;
	
	private String id;
	
	private String orderNos;
	
	
	
	
	
	public String getOrderNos() {
		return orderNos;
	}

	public void setOrderNos(String orderNos) {
		this.orderNos = orderNos;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Order getS_order() {
		return s_order;
	}

	public void setS_order(Order s_order) {
		this.s_order = s_order;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}

	

	public String save()throws Exception{
		HttpSession session=request.getSession();
		Order order=new Order();
		User currentUsre=(User)session.getAttribute("currentUser");
		order.setUser(currentUsre);
		order.setCreateTime(new Date());
		order.setOrderNo(DateUtil.getCurrentDateStr());
		order.setStatus(1);
		ShoppingCart shoppingCart=(ShoppingCart) session.getAttribute("shoppingCart");
		List<ShoppingCartItem> shoppingCartItemList=shoppingCart.getShoppingCartItems();
		float cost=0;
		List<OrderProduct> orderProductList=new LinkedList<OrderProduct>();
		for(ShoppingCartItem shoppingCartItem:shoppingCartItemList){
			Product product=shoppingCartItem.getProduct();
			OrderProduct orderProduct=new OrderProduct();
			orderProduct.setNum(shoppingCartItem.getCount());
			orderProduct.setOrder(order);
			orderProduct.setProduct(product);
			cost+=product.getPrice()*shoppingCartItem.getCount();
			orderProductList.add(orderProduct);
		}
		order.setOrderProductList(orderProductList);
		order.setCost(cost);
		orderService.saveOrder(order);
		session.removeAttribute("shoppingCart");	
		JSONObject result=new JSONObject()/*.fromObject(shoppingCart, jsonConfig)*/;		
		result.put("success", "true");
		//result.put("shoppingCart", shoppingCart);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		
		return null;
	}
	
	public String findOrder()throws Exception{
		HttpSession session=request.getSession();
		User currentUser=(User) session.getAttribute("currentUser");
		if(s_order==null){
			s_order=new Order();
		}
		s_order.setUser(currentUser);
		orderList=orderService.findOrder(s_order, null);
		JsonConfig jsonConfig=new JsonConfig();
		jsonConfig.setExcludes(new String[]{"user"});//不需要转换
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerPropertyExclusions(Product.class, new String[]{"orderProductList"});
		jsonConfig.registerJsonValueProcessor(ProductBigType.class,new ObjectJsonValueProcessor(new String[]{"id","name"}, ProductBigType.class));
		jsonConfig.registerJsonValueProcessor(ProductSmallType.class,new ObjectJsonValueProcessor(new String[]{"id","name"}, ProductSmallType.class));
		JSONArray order=JSONArray.fromObject(orderList, jsonConfig);
		JSONObject result=new JSONObject();
		result.put("orderList", order);
		//System.out.println(result);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	public String confirmReceive()throws Exception{
		orderService.updateOrderStatus(status, orderNo);
		JSONObject result=new JSONObject();
		result.put("success", true);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	
	public String list()throws Exception{
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		List<Order> orderList=orderService.findOrder(s_order, pageBean);
		long total=orderService.getOrderCount(s_order);
		JsonConfig jsonConfig=new JsonConfig();
		jsonConfig.setExcludes(new String[]{"orderProductList"});
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		jsonConfig.registerJsonValueProcessor(User.class,new ObjectJsonValueProcessor(new String[]{"id","userName"}, User.class));
		JSONArray rows=JSONArray.fromObject(orderList, jsonConfig);
		JSONObject result=new JSONObject();
		result.put("rows", rows);
		result.put("total", total);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	public String findProductListByOrderId()throws Exception{
		if(StringUtil.isEmpty(id)){
			return null;
		}
		Order order=orderService.getOrderById(Integer.parseInt(id));
		List<OrderProduct> orderProductList=order.getOrderProductList();
		JSONObject result=new JSONObject();
		JSONArray rows=new JSONArray();
		for(OrderProduct orderProduct:orderProductList){
			Product product=orderProduct.getProduct();
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("productName", product.getName());
			jsonObject.put("proPic", product.getProPic());
			jsonObject.put("price", product.getPrice());
			jsonObject.put("num", orderProduct.getNum());
			jsonObject.put("subtotal", product.getPrice()*orderProduct.getNum());
			rows.add(jsonObject);
		}
		result.put("rows", rows);
		result.put("total", rows.size());
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	
	public String modifyOrderStatus()throws Exception{
		String []orderNoStr=orderNos.split(",");
		for(int i=0;i<orderNoStr.length;i++){
			orderService.updateOrderStatus(status, orderNoStr[i]);
		}
		JSONObject result=new JSONObject();
		result.put("success", true);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request=request;
	}

	
}
