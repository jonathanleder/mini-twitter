package unrn.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.tool.schema.Action;
import unrn.model.Usuario;
import unrn.model.Tweet;

public class EmfMySQLBuilder {
    private EntityManagerFactory emf;
    private PersistenceConfiguration config;

    public EmfMySQLBuilder() {
        config = new PersistenceConfiguration("minitwitter")
                .managedClass(Usuario.class)
                .managedClass(Tweet.class)
                .property(JdbcSettings.SHOW_SQL, false)
                .property(JdbcSettings.FORMAT_SQL, true)
                .property(JdbcSettings.HIGHLIGHT_SQL, true)
                .property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION,
                        Action.CREATE);
    }

    public EmfMySQLBuilder mysql(String url, String user, String password) {
        config.property(PersistenceConfiguration.JDBC_URL, url)
                .property(PersistenceConfiguration.JDBC_USER, user)
                .property(PersistenceConfiguration.JDBC_PASSWORD, password)
                .property(JdbcSettings.DRIVER, "com.mysql.cj.jdbc.Driver")
                .property("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return this;
    }

    public EntityManagerFactory build() {
        return config.createEntityManagerFactory();
    }
}
