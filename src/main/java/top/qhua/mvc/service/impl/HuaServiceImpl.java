package top.qhua.mvc.service.impl;

import top.qhua.mvc.annaotation.Service;
import top.qhua.mvc.service.HuaService;

/**
 * 测试框架服务类
 */
@Service
public class HuaServiceImpl implements HuaService {

	public String query(String name, String age) {
		return name;
	}

}
