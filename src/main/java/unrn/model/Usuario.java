package unrn.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "usuarios")
@Setter(AccessLevel.PRIVATE)
public class Usuario {


    static final String ERROR_USERNAME_INVALIDO = "El userName debe tener entre 5 y 25 caracteres";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String userName;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tweet> tweets = new ArrayList<>();

    protected Usuario() {
        // Constructor requerido por JPA
    }

    public Usuario(String userName) {
        assertUserNameValido(userName);
        this.userName = userName;
    }

    private void assertUserNameValido(String userName) {
        if (userName == null || userName.length() < 5 || userName.length() > 25) {
            throw new RuntimeException(ERROR_USERNAME_INVALIDO);
        }
    }

    public void agregarTweet(Tweet tweet) {
        if (tweet == null) {
            throw new RuntimeException("No se puede agregar un tweet nulo");
        }
        tweet.setAutor(this);
        this.tweets.add(tweet);
    }

    public void eliminarTweet(Tweet tweet) {
        this.tweets.remove(tweet);
        tweet.setAutor(null);
    }

    public List<Tweet> obtenerTweets() {
        return Collections.unmodifiableList(tweets);
    }

    public String obtenerUserName() {
        return this.userName;
    }

    public Long getId() {
        return id;
    }

    // Setter solo para JPA
    protected void setUserName(String userName) {
        this.userName = userName;
    }

    // Setter solo para JPA
    protected void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    // Elimina todos los tweets del usuario (para borrado en cascada)
    public void eliminarTodosLosTweets() {
        this.tweets.clear();
    }


}
