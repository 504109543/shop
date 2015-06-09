package com.shop.action;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.springframework.stereotype.Controller;

import com.shop.entity.PageBean;
import com.shop.entity.ProductBigType;
import com.shop.entity.ProductSmallType;
import com.shop.entity.User;
import com.shop.service.UserService;
import com.shop.util.ResponseUtil;
import com.opensymphony.xwork2.ActionSupport;

@Controller
public class UserAction extends ActionSupport implements ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Resource
	private UserService userService;
	
	private HttpServletRequest request;
	
	private String userName;
	private User user;
	private String error;
	
	private User s_user;
	
	private String page;
	private String rows;
	
	private String ids;
	
	private static JSONObject result=new JSONObject();
	
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public User getS_user() {
		return s_user;
	}
	public void setS_user(User s_user) {
		this.s_user = s_user;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	
	
	
	
	public String existUserWithUserName()throws Exception{
		boolean exist=userService.existUserWithUserName(userName);
		JSONObject result=new JSONObject();
		if(exist){
			result.put("exist", true);
		}else{
			result.put("exist", false);
		}
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	public String login()throws Exception{
		HttpSession session=request.getSession();
		User currentUser=userService.login(user);
		
		
		/* Class cls = currentUser.getClass();
		    Field[] fields = cls.getDeclaredFields();
		    System.out.println("###################### " + currentUser.getClass().getName() + " ####################");
		    for (Field field : fields) {
		        char[] buffer = field.getName().toCharArray();
		        buffer[0] = Character.toUpperCase(buffer[0]);
		        String mothodName = "get" + new String(buffer);
		        try {
		          Method method = cls.getDeclaredMethod(mothodName);
		          Object resutl = method.invoke(currentUser, null);
		          System.out.println(field.getName() + ": " + resutl);
		        } catch (Exception e) {
		          e.printStackTrace();
		        }
		    }
		    System.out.println("###################### End ####################");*/
		  
	
	
		/*if(!imageCode.equals(session.getAttribute("sRand"))){
			error="验证码错误！";
			if(user.getStatus()==2){
				return "adminError";
			}else{
				return ERROR;				
			}
		}else */
		    if(currentUser==null){	
			    error="用户名或密码错误！";
			if(user.getStatus()==2){
				return "adminError";
			}else{
				return ERROR;				
			}
		}else{
			
			session.setAttribute("currentUser", currentUser);
			JsonConfig jsonConfig=new JsonConfig();
			jsonConfig.setExcludes(new String[]{"orderProductList"});//不需要转换
			jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray data=JSONArray.fromObject(currentUser, jsonConfig);						
			JSONObject resultuser=new JSONObject();
			//Cookie[] cookies = request.getCookies();
			JSONObject cookie=new JSONObject();			
			cookie.put("uid", session.getId());
			System.out.println("cookid的值为：  "+session.getId());			
			JSONArray session1=JSONArray.fromObject(cookie);
            /*for(Cookie c :cookies ){
                System.out.println(c.getName()+"--->"+c.getValue());
            }*/
            resultuser.put("user", data);
            resultuser.put("session", cookie);          
            result.put("data", resultuser);
            ResponseUtil.write(ServletActionContext.getResponse(), result);
			System.out.println("登陆成功");
			
		}
		/*if(user.getStatus()==2){
			return "adminLogin";
		}else{
			return "login";			
		}*/
			return null;
	}
	
	public String adlogin()throws Exception{
		
		HttpSession session=request.getSession();
		User currentUser=userService.login(user);
		if(currentUser==null){
			error="用户名或密码错误！";
			if(user.getStatus()==2){
				return "adminError";
			}else{
				return ERROR;				
			}
		}else{
			session.setAttribute("currentUser", currentUser);
		}
		if(user.getStatus()==2){
			return "adminLogin";
		}else{
			return "login";			
		}
	}
	
	public String register()throws Exception{
		userService.saveUser(user);
		JSONObject register=new JSONObject();
		register.put("success", true);
        ResponseUtil.write(ServletActionContext.getResponse(), register);
		return null;
	}
	
	public String logout()throws Exception{
		request.getSession().invalidate();
		return "logout";
	}
	public String logout2()throws Exception{
		request.getSession().invalidate();
		return "logout2";
	}
	
	public String userCenter()throws Exception{
		return null;
	}
	
	public String getUserInfo()throws Exception{
		HttpSession session=request.getSession();
		user=(User) session.getAttribute("currentUser");
		System.out.println("getUserInfo()"+session.getId());
		//System.out.println("getUserInfo2()"+user.getPassword());
		if(result==null)
		ResponseUtil.write(ServletActionContext.getResponse(), null);
		else
			ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	
	
	public String save()throws Exception{
		HttpSession session=request.getSession();
		userService.saveUser(user);
		session.setAttribute("currentUser", user);
		return null;
	}
	
	public String list()throws Exception{
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		List<User> userList=userService.findUserList(s_user, pageBean);
		long total=userService.getUserCount(s_user);
		JsonConfig jsonConfig=new JsonConfig();
		jsonConfig.setExcludes(new String[]{"orderList"});
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		JSONArray rows=JSONArray.fromObject(userList, jsonConfig);
		JSONObject result=new JSONObject();
		result.put("rows", rows);
		result.put("total", total);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	public String deleteUser()throws Exception{
		JSONObject result=new JSONObject();
		String []idsStr=ids.split(",");
		for(int i=0;i<idsStr.length;i++){
			User u=userService.getUserById(Integer.parseInt(idsStr[i]));
			userService.delete(u);
		}
		result.put("success", true);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	public String saveUser()throws Exception{
		userService.saveUser(user);
		JSONObject result=new JSONObject();
		result.put("success", true);
		ResponseUtil.write(ServletActionContext.getResponse(), result);
		return null;
	}
	
	public String modifyPassword()throws Exception{
		User u=userService.getUserById(user.getId());
		u.setPassword(user.getPassword());
		userService.saveUser(u);
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
