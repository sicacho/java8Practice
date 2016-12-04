package com.company.googledrive;

import com.company.domain.Movie;
import com.company.repository.MovieRepository;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Administrator on 8/13/2016.
 */
public class MovieLinkConsumerThread implements Runnable {

    BlockingQueue<String> movieLinks ;
    List<MovieDTO> movieDTOList;
    Queue<String> pageLinks;
    List<MovieDTO> movieVideoURLs;
    boolean running = true;

    public MovieLinkConsumerThread(BlockingQueue<String> movieLinks,
                                   List<MovieDTO> movieDTOList,
                                   Queue<String> pageLinks,
                                   List<MovieDTO> movieVideoURLs) {
        this.movieLinks = movieLinks;
        this.movieDTOList = movieDTOList;
        this.pageLinks = pageLinks;
        this.movieVideoURLs = movieVideoURLs;
    }

    @Override
    public void run() {
        while (running) {
            for (int i = 0; i < movieLinks.size(); i++) {
                String link  = movieLinks.poll();
                if(link!=null) {
                    System.out.println("Thread "+ Thread.currentThread().getName() +" is running with Parse Link = " + link);
                    MovieDTO movieDTO = MovieParser.getMovieFromDetailLink(link);
                    movieVideoURLs.stream().forEach(movieHasVideo -> {if(movieHasVideo.name.toUpperCase().equals(movieDTO.name.toUpperCase())){
                        movieDTO.googleId = movieHasVideo.googleId;
                        movieDTO.isHD = movieHasVideo.isHD;
                    }});
                    movieDTOList.add(movieDTO);
                    System.out.println("Movie Size " + movieDTOList.size());
                }
            }
            stop();
        }
    }

    public void stop() {
        if(pageLinks.isEmpty()) {
            if(movieDTOList.size()==Main.movieCounter){
                System.out.println("Kill consumer " + Thread.currentThread().getName() +  " - Total movie = " + movieDTOList.size());
                running = false;

            }
        }

    }
}
