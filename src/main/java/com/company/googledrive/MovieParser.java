package com.company.googledrive;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 8/13/2016.
 */
public class MovieParser {

    public static List<MovieDTO> getMoviesFromLink(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link)
            .header("Referer","http://www.javlibrary.com/en")
            .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<MovieDTO> movieDTOs = new ArrayList<>();
        Elements ids = doc.select(".video > a");
        ids.forEach(element -> movieDTOs.add(new MovieDTO(getHref(element).replace(".","http://www.javlibrary.com/en")
                , getName(element))));
        return movieDTOs;
    }

    public static MovieDTO getMovieFromDetailLink(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link)
                    .header("Referer", "http://www.javlibrary.com/en")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                    .get();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String name = doc.getElementById("video_id").getElementsByClass("text").first().html();
        String description = doc.getElementById("video_title").getElementsByTag("a").html();
        Elements typeSpans = doc.getElementById("video_genres").getElementsByClass("genre");
        List<String> types = new ArrayList<>();
        typeSpans.stream().forEach(element -> types.add(element.getElementsByTag("a").html()));
        Elements starSpans = doc.getElementById("video_cast").getElementsByClass("star");
        List<String> stars = new ArrayList<>();
        starSpans.stream().forEach(element -> stars.add(element.getElementsByTag("a").html()));
        String image = doc.getElementById("video_jacket_img").attr("src");
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.name = name;
        movieDTO.description = description;
        movieDTO.types = types;
        movieDTO.actors = stars;
        movieDTO.image = image;
        return movieDTO;
    }

    private static String getName(Element element) {
        return element.getElementsByClass("id").html();
    }

    private static String getHref(Element element) {
        return element.getElementsByAttribute("href").attr("href");
    }

}
