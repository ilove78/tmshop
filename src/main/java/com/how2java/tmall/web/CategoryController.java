package com.how2java.tmall.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.Page4Navigator;

@RestController
public class CategoryController {

	@Autowired CategoryService categoryService;
	
	@GetMapping(value="/categories")
	public Page4Navigator<Category> list(@RequestParam(value="start",defaultValue="0") int start,@RequestParam(value="size",defaultValue="5") int size){
		start=start<0?0:start; 
		 Page4Navigator<Category> page = categoryService.list(start, size, 5);
////5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
		return page;
		
	}
	@PostMapping(value="/categories")
	public Object add(Category bean,MultipartFile image,HttpServletRequest ret) throws IllegalStateException, IOException {
		categoryService.add(bean);
		saveOrUpdateImageFile(bean, image, ret);
		return bean;
	}
	
	public void saveOrUpdateImageFile(Category bean,MultipartFile image,HttpServletRequest ret) throws IllegalStateException, IOException {
		File imageFolder = new File(ret.getServletContext().getRealPath("img/category"));
		File file = new File(imageFolder, bean.getId()+".jpg");
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		image.transferTo(file);
		BufferedImage img = ImageUtil.change2jpg(file);
		ImageIO.write(img, "jpg", file);
	}
	@DeleteMapping(value="/categories/{id}")
	public String delete(@PathVariable("id") String  id,HttpServletRequest ret) {
		int idd = Integer.parseInt(id);
		categoryService.delete(idd);
		File imageFolder = new File(ret.getServletContext().getRealPath("img/category"));
		File file = new File(imageFolder, idd+".jpg");
		file.delete();
		return null;
		
	}
	@GetMapping(value="/categories/{id}")
	public Category get(@PathVariable("id") int id) {
		Category bean = categoryService.get(id);
		return bean;
		
	}
	@PutMapping(value="/categories/{id}")
	public Category update(HttpServletRequest ret,Category c,MultipartFile image) throws IllegalStateException, IOException {
		String name = ret.getParameter("name");
		c.setName(name);
		categoryService.update(c);
		if(image!=null) {
			saveOrUpdateImageFile(c, image, ret);
		}
		return c;
		
	}
}
