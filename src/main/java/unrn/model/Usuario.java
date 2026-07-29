package unrn.model;

import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;


@Document(collection = "usuarios")
@Setter(AccessLevel.PRIVATE)
public class Usuario {


    static final String ERROR_USERNAME_INVALIDO = "El userName debe tener entre 5 y 25 caracteres";

    @Id
    private Long id;

    @Indexed(unique = true)
    private String userName;

    // Los tweets de un usuario se consultan a la colección "tweets" por
    // autorId; no se persisten embebidos en el documento del usuario.
    @Transient
    private List<Tweet> tweets = new ArrayList<>();

    protected Usuario() {
        // Constructor requerido por el mapeo de Spring Data
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

    // Asigna el id generado por la secuencia de Mongo antes de guardar (Mongo
    // no autogenera ids de tipo Long como hacía Hibernate).
    public void asignarId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("El usuario ya tiene un id asignado");
        }
        this.id = id;
    }

    protected void setUserName(String userName) {
        this.userName = userName;
    }

    protected void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    // Elimina todos los tweets del usuario (para borrado en cascada)
    public void eliminarTodosLosTweets() {
        this.tweets.clear();
    }


}
