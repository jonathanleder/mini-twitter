package unrn.model;

import jakarta.persistence.*;


@Entity
@Table(name = "tweets")
public class Tweet {
    static final String ERROR_TEXTO = "El texto del tweet debe tener entre 1 y 280 caracteres";
    static final String ERROR_RETWEET_PROPIO = "No se puede retweetear un tweet propio";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario autor;

    @Column(name = "texto", nullable = true, length = 280)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_origen_id")
    private Tweet origen;

    protected Tweet() {
        // Constructor requerido por JPA
    }

    // Tweet normal
    public Tweet(Usuario autor, String text) {
        assertTextoValido(text);
        this.autor = autor;
        this.text = text;
        this.origen = null;
    }

    // Retweet
    public Tweet(Usuario autor, Tweet origen) {
        assertRetweetValido(autor, origen);
        this.autor = autor;
        this.text = null;
        this.origen = origen;
    }

    private void assertTextoValido(String text) {
        if (text == null || text.length() < 1 || text.length() > 280) {
            throw new RuntimeException(ERROR_TEXTO);
        }
    }

    private void assertRetweetValido(Usuario autor, Tweet origen) {
        if (origen == null) {
            throw new RuntimeException("El tweet de origen no puede ser nulo");
        }
        if (origen.autor.equals(autor)) {
            throw new RuntimeException(ERROR_RETWEET_PROPIO);
        }
    }

    public Usuario autor() {
        return autor;
    }

    public String texto() {
        return text;
    }

    public Tweet origen() {
        return origen;
    }

    public Long getId() {
        return id;
    }

    // Setter requerido por JPA y para relación bidireccional
    protected void setAutor(Usuario autor) {
        this.autor = autor;
    }

    protected void setText(String text) {
        this.text = text;
    }

    protected void setOrigen(Tweet origen) {
        this.origen = origen;
    }
}
