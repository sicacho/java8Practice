package com.company.repository;

import com.company.domain.Type;
import org.springframework.data.neo4j.annotation.Query;

/**
 * Created by khangtnse60992 on 2/23/2016.
 */
public interface TypeRepository extends BaseRepository<Type> {
    @Query("MATCH(t:Type) where upper(t.name) STARTS WITH {0} return t limit {1}")
    public Iterable<Type> getTypeByName(String name, Integer limit);

    @Query("MATCH(t:Type) where upper(t.name) = {0} return t")
    public Iterable<Type> getTypeByName(String name);
}
