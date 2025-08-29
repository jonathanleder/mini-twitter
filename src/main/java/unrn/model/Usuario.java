package unrn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Usuario {


    static final String ERROR_USERNAME_INVALIDO = "El userName debe tener entre 5 y 25 caracteres";
    private final String userName;
    private final List<Tweet> tweets = new ArrayList<>();

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
        this.tweets.add(tweet);
    }

    public void eliminarTweet(Tweet tweet) {
        this.tweets.remove(tweet);
    }

    public List<Tweet> obtenerTweets() {
        return Collections.unmodifiableList(tweets);
    }

    public String obtenerUserName() {
        return this.userName;
    }

    // Elimina todos los tweets del usuario (para borrado en cascada)
    public void eliminarTodosLosTweets() {
        this.tweets.clear();
    }


}
