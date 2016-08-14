package com.company.repository;

import com.company.domain.Movie;
import org.springframework.data.neo4j.annotation.Query;

/**
 * Created by KhangTN1 on 6/22/2016.
 */
public interface SearchRepository {

    @Query("MATCH(a:Actor)-[:ACTED_IN]->(n:Movie)-[:HAS]->(t:Type) where (upper(n.name) CONTAINS {0}) AND (upper(t.name) in {1}) AND (upper(a.name) in {2}) with n, count(*) as sum return n order by sum DESC SKIP {3} LIMIT {4}")
    public Iterable<Movie> findMovie(String name, String[] tags, String[] actor, Integer skip, Integer limit);

    @Query("MATCH(a:Actor)-[:ACTED_IN]->(n:Movie)-[:HAS]->(t:Type) where (upper(n.name) CONTAINS {0}) AND (upper(t.name) in {1}) AND (upper(a.name) in {2}) return count(n)")
    public Long findMovieCount(String name, String[] tags, String[] actor);
}
