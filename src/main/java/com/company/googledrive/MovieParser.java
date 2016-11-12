package com.company.googledrive;

import com.company.downloadlink.LinkCommentDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String studio = doc.getElementById("video_maker").getElementsByTag("a").html();
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.name = name;
        movieDTO.description = description;
        movieDTO.types = types;
        movieDTO.actors = stars;
        movieDTO.image = image;
        movieDTO.studio = studio;
        return movieDTO;
    }

    public static LinkCommentDTO getLinkCommentDTO(String link) {
        System.out.println("Process : " + link);
        Document doc = null;
        List<String> uploaded = null;
        List<String> rapidgator = null;
        LinkCommentDTO linkCommentDTO = null;
        try {
            linkCommentDTO = new LinkCommentDTO();
            doc = Jsoup.connect(link)
                    .header("Referer","http://www.javlibrary.com/en")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36").get();
            Element comments = doc.getElementById("video_comments");
            String textComments = comments.text();
            Pattern pattern = Pattern.compile("http:\\/\\/uploaded\\.net\\/(folder|file)\\/(.*)\\/(?!\\[)");
            Matcher matcher = pattern.matcher(textComments);
            uploaded = new ArrayList<>();
            while (matcher.find()) {
                uploaded.add(matcher.group());
            }
            Pattern pattern2 = Pattern.compile("http:\\/\\/rapidgator\\.net\\/(folder|file)\\/(.*)\\/(?!\\[)");
            Matcher matcher2 = pattern2.matcher(textComments);
            rapidgator = new ArrayList<>();
            while (matcher2.find()) {
                rapidgator.add(matcher2.group());
            }
            linkCommentDTO.uploaded_net = uploaded;
            linkCommentDTO.rapidgator_net = rapidgator;
            linkCommentDTO.numberWantIt = Integer.valueOf(doc.getElementById("subscribed").getElementsByTag("a").text());
            linkCommentDTO.linkprimary = link;
            linkCommentDTO.code_video = doc.getElementById("video_id").getElementsByClass("text").first().html();
        }  catch (Exception ex) {
           ex.printStackTrace();
        }

        return linkCommentDTO;
    }

    private static String getName(Element element) {
        return element.getElementsByClass("id").html();
    }

    private static String getHref(Element element) {
        return element.getElementsByAttribute("href").attr("href");
    }

}
