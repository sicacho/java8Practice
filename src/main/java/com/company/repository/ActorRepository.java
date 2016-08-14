package com.company.repository;


import com.company.domain.Actor;
import org.springframework.data.neo4j.annotation.Query;

/**
 * Created by khangtnse60992 on 2/23/2016.
 */
public interface ActorRepository extends BaseRepository<com.company.domain.Actor> {

    @Query("MATCH(t:Actor) where upper(t.name) STARTS WITH {0} return t limit {1}")
    public Iterable<com.company.domain.Actor> getActorByName(String name, Integer limit);

    @Query("MATCH(t:Actor) where upper(t.name) = {0} return t")
    public Iterable<com.company.domain.Actor> getActorByName(String name);


}
