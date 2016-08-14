package com.company.googledrive;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 8/11/2016.
 */
public class JsonConverter {

    public static List<MovieDTO> getData() {
        JsonParser parser = new JsonParser();
        List<MovieDTO> movieDTOs = new ArrayList<>();
        MovieDTO movieDTO ;
        try {
            JsonArray movies = (JsonArray) parser.parse(new FileReader("C:\\Users\\Administrator\\Desktop\\New folder\\jsonvideo.json"));
            for (JsonElement movie : movies)
            {
                movieDTO = new MovieDTO();
                JsonObject temp = movie.getAsJsonObject();
                movieDTO.googleId = temp.get("id").getAsString();
                movieDTO.name = temp.get("name").getAsString();

                try {
                    movieDTO.description = temp.get("description").getAsString();
                } catch (NullPointerException e) {
                    movieDTO.description = temp.get("name").getAsString();
                }
                movieDTOs.add(movieDTO);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return movieDTOs;
    }

}
