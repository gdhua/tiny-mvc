package top.qhua.mvc.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import top.qhua.mvc.annaotation.Autowired;
import top.qhua.mvc.annaotation.Controller;
import top.qhua.mvc.annaotation.RequestMaping;
import top.qhua.mvc.annaotation.RequestParam;
import top.qhua.mvc.service.HuaService;
import top.qhua.mvc.service.impl.HuaServiceImpl;

/**
 * 测试控制类实现
 */
@Controller
public class HuaController {

	@Autowired
	private HuaServiceImpl huaService;
	
	@RequestMaping("/query")
	public void query(HttpServletRequest request,HttpServletResponse response,@RequestParam("name") String name){
	
		try {
			PrintWriter pw= response.getWriter();
			String result=huaService.query(name, "1");
			pw.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMaping("/query2")
	public void query2(HttpServletRequest request,HttpServletResponse response){
	
		try {
			PrintWriter pw= response.getWriter();
			pw.write("query2");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMaping("/")
	public void index(HttpServletRequest request,HttpServletResponse response){
	
		try {
			PrintWriter pw= response.getWriter();
			pw.write("webcomel use mvc");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
