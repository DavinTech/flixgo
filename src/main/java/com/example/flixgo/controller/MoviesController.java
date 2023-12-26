package com.example.flixgo.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.flixgo.form.MovieApiForm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MoviesController {

	@Value("Bearer ${TMDB_KEY}")
	private String tmdbKey;

	@GetMapping(path = { "/home", "/now-playing" })
	public String nowPlaying(Model model) throws IOException, InterruptedException {

		List<MovieApiForm> list = new ArrayList<MovieApiForm>();

		for (int i = 1; i <= 4; i++) {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(
							"https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=" + i + "&region=JP"))
					.header("accept", "application/json")
					.header("Authorization",
							tmdbKey)
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request,
					HttpResponse.BodyHandlers.ofString());
			//System.out.println(response.body());

			//Parse JSON
			ObjectMapper mapper = new ObjectMapper();

			//JSON pretty printer
			/*
			Object jsonObject = mapper.readValue(response.body(), Object.class);
			String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			System.out.println(prettyJson);
			*/

			//---JsonNode---

			JsonNode root = mapper.readTree(response.body());
			JsonNode results = root.get("results");

			for (JsonNode item : results) {
				int id = item.get("id").intValue();
				String posterPath = item.get("poster_path").textValue();
				String originalTitle = item.get("original_title").textValue();
				//System.out.println(originalTitle);

				MovieApiForm form = new MovieApiForm();
				form.setId(id);
				form.setPosterPath("https://image.tmdb.org/t/p/w500" + posterPath);
				form.setOriginalTitle(originalTitle);

				list.add(form);
			}
		}
		//System.out.println(list);

		model.addAttribute("list", list);
		model.addAttribute("movieStatus", "Now Playing in Cinemas");
		return "movies/index";
	}

	@GetMapping(path = "/upcoming")
	public String upcoming(Model model) throws IOException, InterruptedException {

		List<MovieApiForm> list = new ArrayList<MovieApiForm>();

		for (int i = 1; i <= 2; i++) {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(
							"https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=" + i + "&region=JP"))
					.header("accept", "application/json")
					.header("Authorization",
							tmdbKey)
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request,
					HttpResponse.BodyHandlers.ofString());
			//System.out.println(response.body());

			//Parse JSON
			ObjectMapper mapper = new ObjectMapper();

			JsonNode root = mapper.readTree(response.body());
			JsonNode results = root.get("results");

			for (JsonNode item : results) {
				int id = item.get("id").intValue();
				String posterPath = item.get("poster_path").textValue();
				String originalTitle = item.get("original_title").textValue();
				//System.out.println(originalTitle);

				MovieApiForm form = new MovieApiForm();
				form.setId(id);
				form.setPosterPath("https://image.tmdb.org/t/p/w500" + posterPath);
				form.setOriginalTitle(originalTitle);

				list.add(form);
			}
		}
		//System.out.println(list);

		model.addAttribute("list", list);
		model.addAttribute("movieStatus", "Upcoming Movies");
		return "movies/index";
	}

}
