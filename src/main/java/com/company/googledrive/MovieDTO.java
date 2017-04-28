package com.company.googledrive;

import java.util.List;

/**
 * Created by Administrator on 8/11/2016.
 */
public class MovieDTO implements Comparable {
    public String googleId;
    public String name;
    public String description;
    public String link;
    public List<String> types;
    public List<String> actors;
    public String image;
    public String studio;
    public Boolean isHD;
    public Boolean uncen;

    public MovieDTO() {
    }


    public MovieDTO(String link, String name) {
        this.link = link;
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        return this.name.compareTo(((MovieDTO)o).name);
    }

    @Override
    public boolean equals(Object obj) {
        return this.name.equals(((MovieDTO)obj).name);
    }

    @Override
    public String toString() {
        return "MovieDTO{" +
                "name='" + name + '\'' +
                ", googleId='" + googleId + '\'' +
                '}';
    }
}
