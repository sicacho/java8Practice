package com.company.repository;

import com.company.domain.User;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;

/**
 * Created by Administrator on 8/5/2016.
 */
public interface UserRepository extends BaseRepository<User>{
    @Query("MATCH(u:User) where u.username = {0} and u.password = {1} return u")
    public List<User> findUsersHaveUsernameAndPassword(String username, String password);

    @Query("MATCH(u:User) where u.username = {0} return u")
    public List<User> findUsersHaveUsername(String username);

    @Query("MATCH(u:User) where u.email = {0} return u")
    public List<User> findUsersHaveEmail(String email);

}
