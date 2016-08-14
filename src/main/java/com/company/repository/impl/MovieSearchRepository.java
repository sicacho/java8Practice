package com.company.repository.impl;

import com.company.domain.Movie;
import com.company.repository.SearchRepository;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KhangTN1 on 6/22/2016.
 */
@Repository
public class MovieSearchRepository  implements SearchRepository {

    @Autowired
    Session session;

    @Override
    public Iterable<Movie> findMovie(String name, String[] tags, String[] actor, Integer skip, Integer limit) {
        StringBuilder query =  new StringBuilder();
        if(tags.length!=0 && actor.length!=0) {
            query.append("MATCH(a:Actor)-[:ACTED_IN]->(n:Movie)-[:HAS]->(t:Type) where (upper(n.name) CONTAINS {0})");
            query.append(" AND (upper(t.name) in {1})");
            query.append(" AND (upper(a.name) in {2})");
            query.append(" with n, count(*) as sum return n order by sum DESC");
        } else if(tags.length!=0 && actor.length==0 ) {
            query.append("MATCH(n:Movie)-[:HAS]->(t:Type) where (upper(n.name) CONTAINS {0})");
            query.append(" AND (upper(t.name) in {1})");
            query.append(" with n, count(*) as sum return n order by sum DESC");
        } else if(actor.length!=0 && tags.length==0) {
            query.append("MATCH(a:Actor)-[:ACTED_IN]->(n:Movie) where (upper(n.name) CONTAINS {0})");
            query.append(" AND (upper(a.name) in {2})");
            query.append(" with n, count(*) as sum return n order by sum DESC");
        } else {
            query.append("MATCH(n:Movie) where (upper(n.name) CONTAINS {0})");
            query.append(" return n ");
        }
        query.append(" SKIP {3} LIMIT {4}");
        Map<String,Object> params = new HashMap<>();
        params.put("0",name);
        if(tags.length!=0) {
            params.put("1",tags);
        }
        if(actor.length!=0) {
            params.put("2",actor);
        }
        params.put("3",skip);
        params.put("4",limit);
        Iterable<Movie> movies = session.query(Movie.class, query.toString(), params);
        return movies;
    }

    @Override
    public Long findMovieCount(String name, String[] tags, String[] actor) {
        StringBuilder query =  new StringBuilder();
        if(tags.length!=0 && actor.length!=0) {
            query.append("MATCH(a:Actor)-[:ACTED_IN]->(n:Movie)-[:HAS]->(t:Type) where (upper(n.name) CONTAINS {0})");
            query.append(" AND (upper(t.name) in {1})");
            query.append(" AND (upper(a.name) in {2})");
        } else if(tags.length!=0 && actor.length==0 ) {
            query.append("MATCH(n:Movie)-[:HAS]->(t:Type) where (upper(n.name) CONTAINS {0})");
            query.append(" AND (upper(t.name) in {1})");
        } else if(actor.length!=0 && tags.length==0) {
            query.append("MATCH(a:Actor)-[:ACTED_IN]->(n:Movie) where (upper(n.name) CONTAINS {0})");
            query.append(" AND (upper(a.name) in {2})");
        } else {
            query.append("MATCH(n:Movie) where (upper(n.name) CONTAINS {0})");
        }
        query.append(" return count(n)");
        Map<String,Object> params = new HashMap<>();
        params.put("0",name);
        if(tags.length!=0) {
            params.put("1",tags);
        }
        if(actor.length!=0) {
            params.put("2",actor);
        }
        Long count = session.queryForObject(Long.class,query.toString(),params);
        return count;
    }


}
