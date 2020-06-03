package com.how2java.tmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.how2java.tmall.dao.PropertyValueDAO;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
import com.how2java.tmall.util.SpringContextUtil;

@Service
@CacheConfig(cacheNames="propertyValues")
public class PropertyValueService {
	@Autowired PropertyValueDAO propertyValueDAO;
    @Autowired PropertyService propertyService;
	
    @CacheEvict(allEntries=true)
    public void update(PropertyValue propertyValue) {
    	propertyValueDAO.save(propertyValue);
    }
    @Cacheable(key="'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property,product);
    }
    @Cacheable(key="'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }
    
    public void init(Product product) {
    	PropertyValueService propertyValueService = SpringContextUtil.getBean(PropertyValueService.class);
    	List<Property> property = propertyService.listByCategory(product.getCategory());
    	for(Property propertys:property) {
    		PropertyValue propertyValue = propertyValueService.getByPropertyAndProduct(product, propertys);
    		if(null==propertyValue) {
    			PropertyValue propertyValue2 = new PropertyValue();
    			propertyValue2.setProduct(product);
    			propertyValue2.setProperty(propertys);
    			propertyValueDAO.save(propertyValue2);
    		}
    	}
    }
    }

