package com.how2java.tmall;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import com.how2java.tmall.util.PortUtil;

@SpringBootApplication
//@EnableCaching 启动redis缓存
@ServletComponentScan
public class Application extends SpringBootServletInitializer{
	
	static {
		PortUtil.checkPort(6379,"Redis 服务端",true);
//		PortUtil.checkPort(9300,"ElasticSearch 服务端",true);
//        PortUtil.checkPort(5601,"Kibana 工具", true);
	}
	
	
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);    	
    }

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return builder.sources(Application.class);
	}
}
