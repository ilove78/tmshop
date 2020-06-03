package com.how2java.tmall.web;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.OrderService;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.Result;

@RestController
public class OrderController {

	@Autowired OrderService orderService;
    @Autowired OrderItemService orderItemService;
    
    @GetMapping(value="/orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start", defaultValue = "0") int start,@RequestParam(value = "size", defaultValue = "5") int size){
    	start=start<0?0:start;
    	Page4Navigator<Order> page = orderService.list(start, size, 5);
    	orderItemService.fill(page.getContent());
    	orderService.removeOrderFromOrderItem(page.getContent());
		return page;
    	
    }
    @PutMapping(value="deliveryOrder/{oid}")
    public Object deliveryOrder(@PathVariable("oid") int oid) {
    	Order o = orderService.get(oid);
    	o.setDeliveryDate(new Date());
    	o.setStatus(orderService.waitConfirm);
    	orderService.update(o);
		return Result.success();
    	
    }
    
    
}
