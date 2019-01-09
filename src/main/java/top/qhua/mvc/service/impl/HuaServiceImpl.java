package top.qhua.mvc.service.impl;

import top.qhua.mvc.annaotation.Service;
import top.qhua.mvc.service.HuaService;

@Service
public class HuaServiceImpl implements HuaService {

	public String query(String name, String age) {
		return name;
	}

}
