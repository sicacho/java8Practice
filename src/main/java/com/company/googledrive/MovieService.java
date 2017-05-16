package com.company.googledrive;

import com.company.configuration.Constant;
import com.company.configuration.Pagination;
import com.company.domain.Actor;
import com.company.domain.Movie;
import com.company.domain.Studio;
import com.company.domain.Type;
import com.company.repository.ActorRepository;
import com.company.repository.MovieRepository;
import com.company.repository.StudioRepository;
import com.company.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
                movie.setHD(movieDTO.isHD);
                movie.setShow(false);
                movie.setUncen(false);
                Movie movieAfterSave = movieRepository.save(movie);
                System.out.println("Insert Movie : " + movieAfterSave.getId());

            }
        }
    }


    @Transactional
    public void insertMoviesTestData(List<MovieDTO> movieDTOs) {
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
                movie.setHD(movieDTO.isHD);
                movie.setShow(true);
                movie.setUncen(true);
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
            String name = s;
            name = name.replace("."," ");
            name = name.replace("/"," ");
            name = name.trim().replaceAll("\\s{2,}", " ");;
            Iterable<Type> typeDBs = typeRepository.getTypeByName(name.toUpperCase());
            Type type =null;
            if(typeDBs.iterator().hasNext()) {
                 type = typeDBs.iterator().next();
            }
            if(type==null) {
                type = new Type();
                name = name.replace("."," ");
                name = name.replace("/"," ");
                name = name.trim().replaceAll("\\s{2,}", " ");;
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

    @Transactional
    public void updateResolutions(HashMap<String,Integer> videosResolutions) {
        videosResolutions.forEach((s, integer) -> updateResolutions(s,integer));
    }

    private void updateResolutions(String code,Integer resolution) {
        Movie movie =null;
        Iterable<Movie> movieIterator = movieRepository.findMovieByCode(code.toUpperCase());
        if(movieIterator.iterator().hasNext()){
            movie  = movieIterator.iterator().next();
                if(resolution > 480) {
                    movie.setHD(true);
                } else {
                    movie.setHD(false);
                }

        } else {
            System.out.println(code);
        }
        if(movie!=null) {
            movieRepository.save(movie);
        }

    }

    public Iterable<Movie> getMovies(Integer page,Integer length,String sortBy) {
        if(null==page || page < 1) {
            page = 1;
        }
        if(length==null) {
            length = Constant.PAGE_SIZE;
        }
        Sort sortId = new Sort(Sort.Direction.DESC,buildSortByForSpringQuery(sortBy));
        return movieRepository.findAll(new Pagination().page(page-1).have(length).sortBy(sortId),0);
    }

    public String buildSortByForSpringQuery(String sortBy) {
        if(null==sortBy) {
            return "create_date";
        }
        if(sortBy.equals("date")) {
            sortBy = "create_date";
        } else if(sortBy.equals("view")) {
            sortBy = "views";
        } else if(sortBy.equals("name")) {
            sortBy = "name";
        } else {
            sortBy = "create_date";
        }
        return sortBy;
    }

    public void updateMovie(Movie movie) {
        movieRepository.save(movie);
    }

    public Movie getMovie(Long id) {
        return movieRepository.findOne(id);
    }

    public Iterable<Movie> getMoviesHaveOriginalLinkNull() {
        return movieRepository.findMovieByOriginalLinkNull();
    }
}
