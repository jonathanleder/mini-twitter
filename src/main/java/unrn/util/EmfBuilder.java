package unrn.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.tool.schema.Action;
import unrn.model.Usuario;
import unrn.model.Tweet;

public class EmfBuilder {
    public static final String DB_USER = "app";
    public static final String DB_PWD = "app";
  
    public static final String IN_MEMORY_DB_URL = "jdbc:derby:memory:minitwitter;create=true";
    public static final String CLIENT_DB_URL = "jdbc:derby://localhost:1527/minitwitter;create=true";
    private EntityManagerFactory emf;
    private PersistenceConfiguration config;
    private String testDataFileName = "tests-data.sql";

    public EmfBuilder() {
        config = new PersistenceConfiguration("minitwitter")
                .managedClass(Usuario.class)
                .managedClass(Tweet.class)
                .property(PersistenceConfiguration.JDBC_USER, DB_USER)
                .property(PersistenceConfiguration.JDBC_PASSWORD, DB_PWD)
                .property(JdbcSettings.SHOW_SQL, true)
                .property(JdbcSettings.FORMAT_SQL, true)
                .property(JdbcSettings.HIGHLIGHT_SQL, true)
                .property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION,
                        Action.NONE);
    }

    public EmfBuilder memory() {
        config.property(PersistenceConfiguration.JDBC_URL,
                IN_MEMORY_DB_URL);
        return this;
    }

    public EmfBuilder clientAndServer() {
        config.property(PersistenceConfiguration.JDBC_URL,
                CLIENT_DB_URL);
        return this;
    }

    public EmfBuilder withDropAndCreateDDL() {
        config.property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION,
                Action.SPEC_ACTION_DROP_AND_CREATE);
        return this;
    }

    public EmfBuilder withTestData() {
        config.property(AvailableSettings.JAKARTA_HBM2DDL_LOAD_SCRIPT_SOURCE
                , testDataFileName);
        return this;
    }

    public EntityManagerFactory build() {
        return config.createEntityManagerFactory();
    }

}