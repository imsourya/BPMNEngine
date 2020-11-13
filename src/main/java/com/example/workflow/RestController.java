package com.example.workflow;

@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	@org.springframework.web.bind.annotation.RequestMapping(value="/length/{str}",
			method = org.springframework.web.bind.annotation.RequestMethod.GET)
	public int getmapped(String str) {
		return str.length();
	}
}
