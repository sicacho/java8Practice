package com.company.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * Created by khangtnse60992 on 2/21/2016.
 */
@NodeEntity(label = "Actor")
public class Actor {

    @GraphId
    private Long id;

    @Property(name = "name")
    private String name;

    @Property(name = "imageUrl")
    private String imageUrl;
//
//    @Relationship(type = "ACTED_IN")
//    private Set<Movie> movies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
//
//    public Set<Movie> getMovies() {
//        return movies;
//    }
//
//    public void setMovies(Set<Movie> movies) {
//        this.movies = movies;
//    }

    private String seoName;

    public String getSeoName() {
        seoName = name.replace(" ","-");
        return seoName;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }
}
