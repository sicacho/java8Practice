package com.company.repository;


import com.company.domain.Studio;
import org.springframework.data.neo4j.annotation.Query;

/**
 * Created by khangtnse60992 on 2/23/2016.
 */
public interface StudioRepository extends BaseRepository<Studio> {

    @Query("MATCH(n:Studio) where upper(n.name) = {0} return n")
    public Iterable<Studio> findByName(String name);
}
