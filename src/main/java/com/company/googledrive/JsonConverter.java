package com.company.googledrive;


import com.company.downloadlink.LinkCommentDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 8/11/2016.
 */
public class JsonConverter {

    public static List<MovieDTO> getData(String jsonUrl) {
        JsonParser parser = new JsonParser();
        List<MovieDTO> movieDTOs = new ArrayList<>();
        MovieDTO movieDTO ;
        try {
            JsonArray movies = (JsonArray) parser.parse(new FileReader(jsonUrl));
            for (JsonElement movie : movies)
            {
                movieDTO = new MovieDTO();
                JsonObject temp = movie.getAsJsonObject();
                movieDTO.googleId = temp.get("id").getAsString();
                movieDTO.name = temp.get("name").getAsString();
                if(movieDTO.name.contains(".")) {
                    movieDTO.name = movieDTO.name.substring(0,movieDTO.name.indexOf("."));
                }
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

    public static List<LinkCommentDTO> getLinkData(String jsonUrl) {
        JsonParser parser = new JsonParser();
        List<LinkCommentDTO> commentDTOs = new ArrayList<>();
        try {
            JsonArray movies = (JsonArray) parser.parse(new FileReader(jsonUrl));
            LinkCommentDTO linkCommentDTO = null;
            for (JsonElement movie : movies)
            {
                linkCommentDTO = new LinkCommentDTO();
                JsonObject temp = movie.getAsJsonObject();
                if(temp.get("linkprimary")!=null) {
                    linkCommentDTO.linkprimary = temp.get("linkprimary").getAsString();
                    linkCommentDTO.code_video = temp.get("code_video").getAsString();

                    JsonArray jsonUploaded_nets = temp.get("uploaded_net").getAsJsonArray();
                    List<String> uploaded_net = new ArrayList<>();
                    jsonUploaded_nets.forEach(jsonElement -> uploaded_net.add(jsonElement.getAsString()));
                    linkCommentDTO.uploaded_net = uploaded_net;

                    JsonArray jsonRapidgator = temp.get("rapidgator_net").getAsJsonArray();
                    List<String> rapidgator = new ArrayList<>();
                    jsonRapidgator.forEach(jsonElement -> rapidgator.add(jsonElement.getAsString()));
                    linkCommentDTO.rapidgator_net = rapidgator;

                    linkCommentDTO.numberWantIt = temp.get("numberWantIt").getAsInt();

                    commentDTOs.add(linkCommentDTO);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commentDTOs;

    }

}
