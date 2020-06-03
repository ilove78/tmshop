package com.how2java.tmall.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.how2java.tmall.comparator.ProductAllComparator;
import com.how2java.tmall.comparator.ProductDateComparator;
import com.how2java.tmall.comparator.ProductPriceComparator;
import com.how2java.tmall.comparator.ProductReviewComparator;
import com.how2java.tmall.comparator.ProductSaleCountComparator;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.pojo.PropertyValue;
import com.how2java.tmall.pojo.Review;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.OrderService;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.service.PropertyValueService;
import com.how2java.tmall.service.ReviewService;
import com.how2java.tmall.service.UserService;
import com.how2java.tmall.util.Result;

@RestController
public class ForeRESTController {

	@Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired OrderItemService orderItemService;
    @Autowired OrderService orderService;
    
    @GetMapping(value="/forehome")
    public Object home() {
    	List<Category> list = categoryService.list();
    	productService.fill(list);
    	productService.fillByRow(list);
    	categoryService.removeCategoryFromProduct(list);
		return list;
    	
    }
    @PostMapping(value="/foreregister")
    public Object register(@RequestBody User user) {
    	String name =  user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);
        
        if(exist){
            String message ="用户名已经被使用,不能使用";
            return Result.fail(message);
        }
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";
         String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();
         user.setSalt(salt);
        user.setPassword(encodedPassword);
        userService.add(user);
		return Result.success();
    	
    }
    @PostMapping(value="/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);
     
        Subject subject = SecurityUtils.getSubject();
	    UsernamePasswordToken token = new UsernamePasswordToken(name,userParam.getPassword()); 
	    try {
	    	subject.login(token);
            User user = userService.getByName(name);
//          subject.getSession().setAttribute("user", user);
            session.setAttribute("user", user);
            return Result.success();
		} catch (AuthenticationException e) {
			String message ="账号密码错误";
            return Result.fail(message);
		}
       
    }
    @GetMapping(value="/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
    	Product product = productService.get(pid);
    	List<ProductImage> listDetailProductImages = productImageService.listDetailProductImages(product);
    	List<ProductImage> listSingleProductImages = productImageService.listSingleProductImages(product);
    	product.setProductDetailImages(listDetailProductImages);
    	product.setProductSingleImages(listSingleProductImages);
    	
    	List<PropertyValue> pvs = propertyValueService.list(product);
    	List<Review> listReview = reviewService.list(product);
    	productService.setSaleAndReviewNumber(product);
    	productImageService.setFirstProdutImage(product);
    	
    	Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", listReview);
		return Result.success(map);
    	
    }
    @GetMapping(value="forecheckLogin")
    public Object checkLogin(HttpSession session) {
//     User	user =(User) session.getAttribute("user");
    	Subject subject = SecurityUtils.getSubject();
     if(subject.isAuthenticated())
    	return Result.success();
     return Result.fail("未登录");
    	
    }
    @GetMapping(value="forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort) {
    	Category c = categoryService.get(cid);
    	productService.fill(c);
    	productService.setSaleAndReviewNumber(c.getProducts());
    	categoryService.removeCategoryFromProduct(c);
    	if(null!=sort) {
    		switch (sort) {
			case "review":
				Collections.sort(c.getProducts(), new ProductReviewComparator());
				break;
            case "date":
            	Collections.sort(c.getProducts(), new ProductDateComparator());
            	break;
            case "saleCount":
            	Collections.sort(c.getProducts(), new ProductSaleCountComparator());
            	break;
            case "price":
            	Collections.sort(c.getProducts(), new ProductPriceComparator());
            	break;
            case "all":
            	Collections.sort(c.getProducts(), new ProductAllComparator());
            	break;
			}
    	}
		return c;
    	
    }
    @PostMapping(value="foresearch")
    public Object search(String keyword) {
    	if(null==keyword)
    		keyword="";
    	List<Product> ps = productService.search(keyword, 0, 20);
    	productImageService.setFirstProdutImages(ps);
    	productService.setSaleAndReviewNumber(ps);
		return ps;
    	
    }
    
    @GetMapping(value="forebuyone")
    public Object buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid,num,session);
    }
    
    private int buyoneAndAddCart(int pid,int num,HttpSession session) {
    	Product product = productService.get(pid);
    	int oiid = 0;
    	
    	User user = (User) session.getAttribute("user");
    	boolean found = false;
    	List<OrderItem> userItem = orderItemService.listByUser(user);
    	for(OrderItem oi:userItem) {
    		if(product.getId()==oi.getProduct().getId()) {
    			oi.setNumber(oi.getNumber()+num);
    			orderItemService.update(oi);
    			found=true;
    			oiid = oi.getId();
                break;
    		}
    	}
    	
    	if(!found) {
    		OrderItem orderItem = new OrderItem();
    		orderItem.setUser(user);
    		orderItem.setProduct(product);
    		orderItem.setNumber(num);
    		orderItemService.add(orderItem);
    		oiid=orderItem.getId();
    	}
		return oiid;
    	
    }
    @GetMapping(value="forebuy")
    public Object buy(HttpSession session,String[] oiid) {
    	List<OrderItem> orderItems = new ArrayList<>();
    	float total = 0;
    	for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.get(id);
            total +=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
        }
    
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
    
        session.setAttribute("ois", orderItems);
    
        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
		return Result.success(map);
    	
    }
    
    @GetMapping(value="foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.success();
    }
    @GetMapping(value="forecart")
    public Object cart(HttpSession session) {
    	User user=(User)session.getAttribute("user");
    	List<OrderItem> ois = orderItemService.listByUser(user);
    	productImageService.setFirstProdutImagesOnOrderItems(ois);
		return ois;
    	
    }
    @GetMapping(value="cors")
    public Object corsTest() {
		return "cors test!";
    	
    }
    @GetMapping(value="forechangeOrderItem")
    public Object changeOrderItem(HttpSession session,int pid,int num) {
    	User user = (User)session.getAttribute("user");
    	if(null==user) {
    		return Result.fail("未登录");
    	}
    	List<OrderItem> orderItem = orderItemService.listByUser(user);
    	for(OrderItem io : orderItem) {
    		if(io.getProduct().getId()==pid) {
    			io.setNumber(num);
    			orderItemService.update(io);
    			break;
    		}
    	}
		return Result.success();
    	
    }
    @GetMapping(value="foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }
    @PostMapping(value="forecreateOrder")
    public Object createOrder(@RequestBody Order order , HttpSession session) {
    	User user =(User) session.getAttribute("user");
    	if(user==null)
    	   return	Result.fail("未登录");
    	String orderCode =new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
    			+RandomUtils.nextInt(10000);
    	order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois= (List<OrderItem>)  session.getAttribute("ois");
        float total=0;
        try {
        	 total = orderService.add(order, ois);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);
		return Result.success(map);
    	
    }
    @GetMapping(value="forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }
    @GetMapping(value="forebought")
    public Object bought(HttpSession session) {
    	User user = (User) session.getAttribute("user");
    	if(null==user)
    		return Result.fail("未登录");
    	List<Order> list = orderService.listByUserWithoutDelete(user);
    	orderService.removeOrderFromOrderItem(list);
		return list;
    	
    }
    @GetMapping(value="foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.cacl(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }
    @GetMapping(value="foreorderConfirmed")
    public Object orderConfirmed(int oid) {
    	Order o = orderService.get(oid);
    	o.setStatus(orderService.waitReview);
    	o.setConfirmDate(new Date());
    	orderService.update(o);
		return Result.success();
    	
    }
    
    @PutMapping(value="foredeleteOrder")
    public Object deleteOrder(int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }
    
    @GetMapping(value="forereview")
    public Object review(int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        Product P = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(P);
        productService.setSaleAndReviewNumber(P);
        Map<String,Object> map = new HashMap<>();
        map.put("p", P);
        map.put("o", order);
        map.put("reviews", reviews);
        return Result.success(map);
    }
    @PostMapping(value="foredoreview")
    public Object doreview(HttpSession session,int oid,int pid,String content) {
    	Order o = orderService.get(oid);
    	o.setStatus(orderService.finish);
    	orderService.update(o);
    	Product p = productService.get(pid);
    	 content = HtmlUtils.htmlEscape(content);
    	 User user =(User)  session.getAttribute("user");
    	    Review review = new Review();
    	    review.setContent(content);
    	    review.setProduct(p);
    	    review.setCreateDate(new Date());
    	    review.setUser(user);
    	    reviewService.add(review);
		return Result.success();
    	
    }
}
