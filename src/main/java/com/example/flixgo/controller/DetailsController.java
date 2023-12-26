package com.example.flixgo.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.flixgo.form.CastApiForm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class DetailsController {

	@Value("Bearer ${TMDB_KEY}")
	private String tmdbKey;

	@GetMapping(path = "/details/{id}")
	public String details(@PathVariable("id") int id, Model model)
			throws IOException, InterruptedException {

		//Movie Details
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(
						"https://api.themoviedb.org/3/movie/" + id
								+ "?append_to_response=credits%2Cvideos%2Crelease_dates&language=en-US"))
				.header("accept", "application/json")
				.header("Authorization",
						tmdbKey)
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		//System.out.println(response.body());

		//Parse JSON
		ObjectMapper mapper = new ObjectMapper();

		JsonNode root = mapper.readTree(response.body());
		String overview = root.get("overview").textValue();
		String posterPath = root.get("poster_path").textValue();
		String backdropPath = root.get("backdrop_path").textValue();
		String originalTitle = root.get("original_title").textValue();
		int runtime = root.get("runtime").intValue();

		//Get Genres
		JsonNode genreResult = root.get("genres");
		List<String> genreList = new ArrayList<String>();
		for (JsonNode genreItem : genreResult) {
			String genre = genreItem.get("name").textValue();
			genreList.add(genre);
		}

		//Get Release Date
		JsonNode releaseDatesResult = root.get("release_dates").get("results");

		String releaseDateJPLatest = null;
		for (JsonNode releaseDates : releaseDatesResult) {
			String region = releaseDates.get("iso_3166_1").textValue();
			if (region.equals("JP")) {
				JsonNode releaseDateJP = releaseDates.get("release_dates");
				releaseDateJPLatest = releaseDateJP.get(releaseDateJP.size() - 1).get("release_date").textValue();
			}
		}

		String releaseDateLocal = releaseDateJPLatest.substring(0, 10);

		//Get Directors
		JsonNode crewResult = root.get("credits").get("crew");

		List<String> directorList = new ArrayList<String>();
		for (JsonNode crew : crewResult) {
			String job = crew.get("job").textValue();
			if (job.equals("Director")) {
				String director = crew.get("name").textValue();
				directorList.add(director);
			}
		}
		//System.out.println(directorList);

		//Get Cast
		JsonNode castResult = root.get("credits").get("cast");

		List<CastApiForm> castList = new ArrayList<CastApiForm>();

		for (JsonNode cast : castResult) {
			String castName = cast.get("name").textValue();
			String profilePath = cast.get("profile_path").textValue();
			String character = cast.get("character").textValue();

			CastApiForm castForm = new CastApiForm();
			castForm.setCastName(castName);
			castForm.setProfilePath("https://image.tmdb.org/t/p/w500" + profilePath);
			castForm.setCharacter(character);
			castList.add(castForm);

		}

		//Get Videos
		JsonNode videoResult = root.get("videos").get("results");

		List<String> videoList = new ArrayList<String>();
		for (JsonNode video : videoResult) {
			String videoType = video.get("type").textValue();
			if (videoType.equals("Trailer")) {
				String videoKey = video.get("key").textValue();
				String videoPath = "https://www.youtube.com/embed/" + videoKey;
				videoList.add(videoPath);
			}
		}
		//System.out.println(videoList);

		Collections.reverse(videoList);

		model.addAttribute("releaseDate", releaseDateLocal);
		model.addAttribute("overview", overview);
		model.addAttribute("posterPath", "https://image.tmdb.org/t/p/w500" + posterPath);
		model.addAttribute("backdropPath", "https://image.tmdb.org/t/p/original" + backdropPath);
		model.addAttribute("originalTitle", originalTitle);
		model.addAttribute("runtime", runtime);
		model.addAttribute("genreList", genreList);
		model.addAttribute("directorList", directorList);
		model.addAttribute("castList", castList);
		model.addAttribute("videoList", videoList);

		return "movies/details";
	}
}
