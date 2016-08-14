package com.company.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

/**
 * Created by Administrator on 8/5/2016.
 */
@NodeEntity(label = "User")
public class User {

    @GraphId
    private Long id;

    private String username;

    private String password;

    private String email;

    @Relationship(type = "HAS")
    private List<Notification> notificationList;

    @Relationship(type = "HAS")
    private List<Movie> requestMovies;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public List<Movie> getRequestMovies() {
        return requestMovies;
    }

    public void setRequestMovies(List<Movie> requestMovies) {
        this.requestMovies = requestMovies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
