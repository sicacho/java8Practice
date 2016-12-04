package com.company.configuration;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by khangtnse60992 on 2/21/2016.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = {"com.company.domain","com.company.repository"})
@EnableTransactionManagement
public class RepositoryConfiguration extends Neo4jConfiguration{

    @Autowired
    Environment environment;

    @Override
    public SessionFactory getSessionFactory() {
        return new SessionFactory("com.company.domain", "BOOT-INF.classes.com.company.domain");
    }

    @Override
    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Session getSession() throws Exception {
        return super.getSession();
    }
}
