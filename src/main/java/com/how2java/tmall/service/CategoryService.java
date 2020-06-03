package com.how2java.tmall.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.how2java.tmall.dao.CategoryDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;

@Service
@CacheConfig(cacheNames="categories")
public class CategoryService {

	@Autowired
	CategoryDao categoryDao;
	
	@Cacheable(key="'categories-all'")
	public List<Category> list(){
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		return categoryDao.findAll(sort);
		
	}
	@Cacheable(key="'categories-page-'+#p0+ '-' + #p1")
	public Page4Navigator<Category> list(int start,int size,int navigatePages){
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable pageable = new PageRequest(start, size, sort);
		Page<Category> pageFromJPA = categoryDao.findAll(pageable);
		return new Page4Navigator<>(pageFromJPA, navigatePages);
		
	}
	@CacheEvict(allEntries=true)
	public void add(Category bean) {
		Category save = categoryDao.save(bean);
	}
	@CacheEvict(allEntries=true)
	public void delete(int id) {
		categoryDao.delete(id);
	}
	@Cacheable(key="'categories-one-'+ #p0")
	public Category get(int id) {
        Category c= categoryDao.findOne(id);
        return c;
    }
	@CacheEvict(allEntries=true)
	public void update(Category bean) {
		 categoryDao.save(bean);
		
	}
	public void removeCategoryFromProduct(List<Category> category) {
		for(Category c : category) {
			removeCategoryFromProduct(c);
		}
	}
	
	public void removeCategoryFromProduct (Category category) {
		List<Product> product = category.getProducts();
		if(null!=product) {
			for(Product p:product) {
				p.setCategory(null);
			}
		}
		List<List<Product>> productsByRow = category.getProductsByRow();
		if(null!=productsByRow) {
			for(List<Product> ps:productsByRow) {
				for(Product pp:ps) {
					pp.setCategory(null);
					
				}
			}
		}
	}
}
