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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            List<String> actorsName = movieDTOs.get(i).actors;
            List<String> typesName = movieDTOs.get(i).types;
            MovieDTO movieDTO = movieDTOs.get(i);
            List<Actor> actors = getListActorFromName(actorsName);
            List<Type> types = getListTypeFromName(typesName);
            Studio studio = studioRepository.findOne(Long.valueOf(27709));
            Movie movie = new Movie();
            movie.setName(movieDTO.description);
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

    private List<Actor> getListActorFromName(List<String> actorsName) {
        List<Actor> actors = new ArrayList<>();
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
                type.setName(s);
                type.getSeoName();
                typeRepository.save(type);
            }
        types.add(type);
        }
        return types;
    }
}
