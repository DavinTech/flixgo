package com.example.flixgo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MoviesController {

	@GetMapping(path = "/home")
	public String index() {
		return "movies/index";
	}

	@GetMapping(path = "/now-playing")
	public String nowplaying(Model model) {
		model.addAttribute("movieStatus", "Now Playing in Cinemas");
		return "movies/index";
	}

	@GetMapping(path = "/upcoming")
	public String upcoming(Model model) {
		model.addAttribute("movieStatus", "Upcoming Movies");
		return "movies/index";
	}
}
