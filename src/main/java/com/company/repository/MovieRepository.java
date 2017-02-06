package com.company.repository;

import com.company.domain.Movie;
import org.springframework.data.neo4j.annotation.Query;

import java.util.Iterator;
import java.util.List;

/**
 * Created by khangtnse60992 on 2/23/2016.
 */
public interface MovieRepository extends BaseRepository<Movie> {

    @Query("MATCH(n:Movie) where upper(n.name) CONTAINS {0} AND  return n SKIP {1} LIMIT {2}")
    public Iterable<Movie> findMovieByContain(String keyword,Integer skip,Integer limit);
    //MATCH(a:Actor)-[:ACTED_IN]->(n:Movie)-[:HAS]->(t:Type) where upper(n.name) CONTAINS {0} AND upper(a.name) in {1} AND upper(t.name) in {2}  return n SKIP {1} LIMIT {2}

    @Query("MATCH (n:Movie) return count(n)")
    public Long movieCount();

    @Query("MATCH (n:Movie)-[:HAS]->(t:Type) where (id(t) in {0}) and (not id(n)={1}) with n, count(*) as sum return n order by sum DESC SKIP {2} LIMIT {3}")
    public Iterable<Movie> findMovieRelated(List<Long> types,Long movieId, Integer skip, Integer limit);

    @Query("MATCH (n:Movie)-[:HAS]->(t:Type) where (id(t)={0}) return n  order by (n.create_date) DESC SKIP {1} LIMIT {2}")
    public Iterable<Movie> findMovieByType(Long id,Integer skip,Integer page);

    @Query("MATCH (n:Movie)-[:HAS]->(t:Type) where (id(t)={0}) return count(n)")
    public Long findMovieByTypeCount(Long id);

    @Query("MATCH (n:Movie)-[:BELONG_TO]->(s:Studio) where (id(s)={0}) return n  order by (n.create_date) DESC SKIP {1} LIMIT {2}")
    public Iterable<Movie> findMovieByStudio(Long id,Integer skip,Integer page);

    @Query("MATCH (n:Movie)-[:BELONG_TO]->(s:Studio) where (id(s)={0}) return count(n)")
    public Long findMovieByStudioCount(Long id);

    @Query("MATCH (a:Actor)-[:ACTED_IN]->(n:Movie) where (id(a)={0}) return n  order by (n.create_date) DESC SKIP {1} LIMIT {2}")
    public Iterable<Movie> findMovieByActor(Long id,Integer skip,Integer page);

    @Query("MATCH (a:Actor)-[:ACTED_IN]->(n:Movie) where (id(a)={0}) return count(n)")
    public Long findMovieByActorCount(Long id);

    @Query("MATCH(n:Movie) where upper(n.code) = {0} return n")
    public Iterable<Movie> findMovieByCode(String code);

    @Query("MATCH(n:Movie) where (n.copyOriginalLink) IS NULL  return n")
    public Iterable<Movie> findMovieByOriginalLinkNull();

}
