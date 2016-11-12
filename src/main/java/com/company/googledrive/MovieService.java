package com.company.googledrive;

import com.company.domain.Actor;
import com.company.domain.Movie;
import com.company.domain.Studio;
import com.company.domain.Type;
import com.company.repository.ActorRepository;
import com.company.repository.MovieRepository;
import com.company.repository.StudioRepository;
import com.company.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Administrator on 8/14/2016.
 */
@Service
public class MovieService {

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    TypeRepository typeRepository;

    @Autowired
    StudioRepository studioRepository;

    @Transactional
    public void insertMovies(List<MovieDTO> movieDTOs) {
        for (int i = 0; i < movieDTOs.size(); i++) {
            String movieCode = movieDTOs.get(i).name;
            Iterable<Movie> movies = movieRepository.findMovieByCode(movieCode);
            if(!movies.iterator().hasNext()){
                List<String> actorsName = movieDTOs.get(i).actors;
                List<String> typesName = movieDTOs.get(i).types;
                MovieDTO movieDTO = movieDTOs.get(i);
                List<Actor> actors = getListActorFromName(actorsName);
                List<Type> types = getListTypeFromName(typesName);
                String studioName = movieDTO.studio;
                studioName = studioName.replace("."," ");
                studioName = studioName.replace("/"," ");
                studioName = studioName.trim().replaceAll("\\s{2,}", " ");
                Iterable<Studio> studios = studioRepository.findByName(studioName.toUpperCase());
                Studio studio = null;
                if(studios.iterator().hasNext()) {
                    studio = studios.iterator().next();
                }
                if(studio==null) {
                    studio = new Studio();
                    studioName = studioName.replace("."," ");
                    studioName = studioName.replace("/"," ");
                    studioName = studioName.trim().replaceAll("\\s{2,}", " ");
                    studio.setName(studioName);
                    studio.getSeoName();
                    studioRepository.save(studio);
                }
//                Studio studio1 = studioRepository.findOne(Long.valueOf(27709));
                Movie movie = new Movie();
                movie.setName(movieDTO.description.replace("%",""));
                movie.setCode(movieDTO.name);
                movie.setActors(actors);
                movie.setTypes(types);
                movie.setUrlImage(movieDTO.image);
                movie.setUrlVideos(new String[]{"https://drive.google.com/open?id="+movieDTO.googleId});
                movie.setStudio(studio);
                movie.setViews(0);
                movie.setCreateDate((new Date()).getTime());
                Movie movieAfterSave = movieRepository.save(movie);
                System.out.println("Insert Movie : " + movieAfterSave.getId());

            }
        }
    }

    private List<Actor> getListActorFromName(List<String> actorsName) {
        List<Actor> actors = new ArrayList<>();
        if(actorsName.size()==0) {
            actorsName.add("unknown");
        }
        for (String s : actorsName) {
            Iterable<Actor> actorDBs = actorRepository.getActorByName(s.toUpperCase());
            Actor actor = null;
            if(actorDBs.iterator().hasNext()) {
                actor = actorDBs.iterator().next();
            }
            if(actor==null) {
                 actor = new Actor();
                 actor.setName(s);
                 actor.getSeoName();
                 actorRepository.save(actor);
            }
            actors.add(actor);
        }
        return actors;
    }

    private List<Type> getListTypeFromName(List<String> typesName) {
        List<Type> types = new ArrayList<>();
        for (String s : typesName) {
            Iterable<Type> typeDBs = typeRepository.getTypeByName(s.toUpperCase());
            Type type =null;
            if(typeDBs.iterator().hasNext()) {
                 type = typeDBs.iterator().next();
            }
            if(type==null) {
                type = new Type();
                String name = s;
                name = name.replaceAll("."," ");
                name = name.replaceAll("//"," ");
                name = name.trim();
                type.setName(name);
                type.getSeoName();
                typeRepository.save(type);
            }
        types.add(type);
        }
        return types;
    }

    @Transactional
    public void updateBackupLink(Map<String, String> nameAndLinks) {
        nameAndLinks.forEach((s, s2) -> updateBackupLink(s,s2));
    }


    private void updateBackupLink(String code,String id) {
        System.out.println(code);
        Movie movie = movieRepository.findMovieByCode(code.toUpperCase()).iterator().next();
        if(movie!=null) {
            String[] urls = movie.getUrlVideos();
            List<String> urlList = new ArrayList<>(Arrays.asList(urls));
            urlList.add("https://drive.google.com/open?id="+id);
            String[] urlNew = new String[2];
            movie.setUrlVideos(urlList.toArray(urlNew));
        }
        movieRepository.save(movie);
    }
}
