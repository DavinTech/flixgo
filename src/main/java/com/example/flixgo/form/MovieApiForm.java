package com.example.flixgo.form;

import lombok.Data;

@Data
public class MovieApiForm {

	//Movie Lists - Now Playing and Movie Lists - Upcoming
	private int id;

	private String originalTitle;

	private String posterPath;

}
