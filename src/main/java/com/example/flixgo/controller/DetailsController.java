package com.example.flixgo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DetailsController {

	@GetMapping(path = "/details")
	public String index() {
		return "movies/details";
	}

}
