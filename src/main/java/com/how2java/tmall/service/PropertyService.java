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

import com.how2java.tmall.dao.PropertyDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.util.Page4Navigator;

@Service
@CacheConfig(cacheNames="properties")
public class PropertyService {

	@Autowired PropertyDao propertyDAO;
    @Autowired CategoryService categoryService;
    
    @CacheEvict(allEntries=true)
    public void add(Property propertty) {
    	propertyDAO.save(propertty);
    }
    @CacheEvict(allEntries=true)
    public void delete(int  id) {
    	propertyDAO.delete(id);
    }
    @CacheEvict(allEntries=true)
    public void update(Property propertty) {
    	propertyDAO.save(propertty);
    }
    @Cacheable(key="'properties-one-'+ #p0")
    public Property get(int id) {
    	return propertyDAO.findOne(id);
    }
    @Cacheable(key="'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Property> list(int cid, int start, int size,int navigatePages){
    	Category category = categoryService.get(cid);
    	Sort sort = new Sort(Sort.Direction.DESC, "id");
    	Pageable page = new PageRequest(start, size, sort);
    	Page<Property> pageFromJPA = propertyDAO.findByCategory(category, page);
		return new Page4Navigator<>(pageFromJPA, navigatePages);
    	
    }
    @Cacheable(key="'properties-cid-'+ #p0.id")
    public List<Property> listByCategory(Category category){
        return propertyDAO.findByCategory(category);
    }
}
