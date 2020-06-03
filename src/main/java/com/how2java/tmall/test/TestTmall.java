package com.how2java.tmall.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.OrderItemService;
@Ignore
public class TestTmall {
	@Autowired OrderItemService orderItemService;
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
	public static void main(String[] args) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/tmall_springboot?useUnicode=true&characterEncoding=utf8", "root", "123456");
			Statement s = c.createStatement();
			
				String sqlFormat = "insert into category values (null, '测试分类')";
//				String sql = String.format(sqlFormat, i);
				s.execute(sqlFormat);
				if(false) {
					throw new RuntimeException();
				}
			System.out.println("已经成功创建1条分类测试数据");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
//	@Transactional
//	public void add() {
//		OrderItem orderItem = new OrderItem();
//		orderItem.setProduct(new Product());
//		orderItem.setOrder(new Order());
//		orderItem.setUser(new User());
//		orderItem.setNumber(1);
//		orderItemService.add(orderItem);
//		if(true) {
//			throw new RuntimeException();
//		}
//	}
}
