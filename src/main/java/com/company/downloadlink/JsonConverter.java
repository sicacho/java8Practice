package com.company.downloadlink;


import com.company.googledrive.MovieDTO;
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

    public static List<LinkCommentDTO> getData() {
        JsonParser parser = new JsonParser();
        List<LinkCommentDTO> linkCommentDTOs = new ArrayList<>();
        LinkCommentDTO linkCommentDTO = null;
        try {
            JsonArray movies = (JsonArray) parser.parse(new FileReader("D:\\study\\JAVService\\movieList_25082016.txt"));
            for (JsonElement movie : movies)
            {
                linkCommentDTO = new LinkCommentDTO();
                JsonObject temp = movie.getAsJsonObject();
                linkCommentDTO.code_video = temp.get("code_video").getAsString();
                linkCommentDTO.title = temp.get("title").getAsString();
                linkCommentDTO.linkprimary = temp.get("linkprimary").getAsString();
                linkCommentDTOs.add(linkCommentDTO);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return linkCommentDTOs;
    }

}
