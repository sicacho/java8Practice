package com.company.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * Created by khangtnse60992 on 2/21/2016.
 */
@NodeEntity(label = "Type")
public class Type {

    @GraphId
    private Long id;

    @Property(name = "name")
    private String name;

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

    private String seoName;

    public String getSeoName() {
        seoName = name.replace(" ","-");
        return seoName;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }
}
