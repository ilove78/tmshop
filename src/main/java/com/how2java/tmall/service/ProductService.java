package com.how2java.tmall.service;

import java.util.ArrayList;
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

import com.how2java.tmall.dao.ProductDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.SpringContextUtil;

@Service
@CacheConfig(cacheNames="products")
public class ProductService {

	@Autowired
	ProductDao productDao;
	@Autowired
	CategoryService categoryService;
	@Autowired
	ProductImageService productImageService;
	@Autowired
	OrderItemService orderImageService;
	@Autowired
	ReviewService reviewService;

	@CacheEvict(allEntries=true)
	public void add(Product product) {
		productDao.save(product);
	}
	@CacheEvict(allEntries=true)
	public void delete(int  id) {
		productDao.delete(id);
	}
	@CacheEvict(allEntries=true)
	public void update(Product product) {
		productDao.save(product);
	}
	@Cacheable(key="'products-one-'+ #p0")
	public Product get(int  id) {
		return productDao.findOne(id);
	}
	@Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
	public Page4Navigator<Product> list (int cid,int start,int size,int navigatePages){
		Category category = categoryService.get(cid);
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable page = new PageRequest(start, size, sort);
		Page<Product> findByCategory = productDao.findByCategory(category, page);
		return new Page4Navigator<>(findByCategory, navigatePages);
		
	}
	public void fill(List<Category> category) {
		for(Category c :category) {
			fill(c);
		}
	}
	public void fill(Category category) {
		ProductService productService = SpringContextUtil.getBean(ProductService.class);
		List<Product> product = productService.listByCategory(category);
		productImageService.setFirstProdutImages(product);
		category.setProducts(product);
	}
	@Cacheable(key="'products-cid-'+ #p0.id")
	public List<Product> listByCategory(Category category){
        return productDao.findByCategoryOrderById(category);
    }
	public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }
	public void setSaleAndReviewNumber(Product product) {
		int saleOrder = orderImageService.getSaleCount(product);
		product.setSaleCount(saleOrder);
		
		int countReview = reviewService.getCount(product);
		product.setReviewCount(countReview);
	}
	
	public void setSaleAndReviewNumber(List<Product> products) {
		for (Product product : products)
            setSaleAndReviewNumber(product);
	}
	
	//由sql like模糊搜索 更改为es搜索
	public List<Product> search(String keyword,int start,int size){
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		PageRequest pageable = new PageRequest(start, size, sort);
		List<Product> products = productDao.findByNameLike("%"+keyword+"%", pageable);
		return products;
		
	}

}
