package com.example.flixgo.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.example.flixgo.form.MovieApiForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MovieApiController {

	@Value("Bearer ${TMDB_KEY}")
	private String tmdbKey;

	public void nowPlaying() throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1&region=JP"))
				.header("accept", "application/json")
				.header("Authorization",
						tmdbKey)
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());

		//Parse JSON into Objects
		ObjectMapper mapper = new ObjectMapper();
		List<MovieApiForm> list = mapper.readValue(response.body(), new TypeReference<List<MovieApiForm>>() {
		});

		//MovieApiForm list = mapper.readValue(response.body(), MovieApiForm.class);
	}

}
