package com.how2java.tmall.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.util.Page4Navigator;

public interface ProductImageDAO extends JpaRepository<ProductImage, Integer> {

	public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product,String type);
}