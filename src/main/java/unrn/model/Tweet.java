package unrn.model;

import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "tweets")
@Setter(AccessLevel.PRIVATE)
public class Tweet {
    static final String ERROR_TEXTO = "El texto del tweet debe tener entre 1 y 280 caracteres";
    static final String ERROR_RETWEET_PROPIO = "No se puede retweetear un tweet propio";

    @Id
    private Long id;

    // Referencia de objeto en memoria: solo válida en la misma operación en la
    // que se construye el Tweet (por ejemplo dentro de TwitterService). No se
    // persiste ni se rehidrata al leer desde Mongo: para eso están autorId/
    // autorUsername, que sí quedan guardados en el documento.
    @Transient
    private Usuario autor;

    private Long autorId;

    private String autorUsername;

    private String text;

    // Ídem autor: solo disponible justo después de construir el Tweet.
    @Transient
    private Tweet origen;

    private Long origenId;

    private String origenAutorUsername;

    private String origenTexto;

    private LocalDateTime origenFecha;

    private LocalDateTime fechaCreacion;

    protected Tweet() {
        // Constructor requerido por el mapeo de Spring Data
    }

    // Tweet normal
    public Tweet(Usuario autor, String text) {
        assertTextoValido(text);
        this.autor = autor;
        this.autorId = autor.getId();
        this.autorUsername = autor.obtenerUserName();
        this.text = text;
        this.origen = null;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Retweet
    public Tweet(Usuario autor, Tweet origen) {
        assertRetweetValido(autor, origen);
        this.autor = autor;
        this.autorId = autor.getId();
        this.autorUsername = autor.obtenerUserName();
        this.text = null;
        this.origen = origen;
        this.origenId = origen.getId();
        this.origenAutorUsername = origen.getAutorUsername();
        this.origenTexto = origen.texto();
        this.origenFecha = origen.getFechaCreacion();
        this.fechaCreacion = LocalDateTime.now();
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
        // Si el origen todavía tiene el objeto autor en memoria (recién
        // construido) comparamos por identidad, igual que antes. Si viene de
        // Mongo (autor transitorio no rehidratado), comparamos por autorId.
        boolean mismoAutor = origen.autor != null
                ? origen.autor.equals(autor)
                : Objects.equals(origen.autorId, autor.getId());
        if (mismoAutor) {
            throw new RuntimeException(ERROR_RETWEET_PROPIO);
        }
    }

    public Usuario autor() {
        return autor;
    }

    public String texto() {
        return text;
    }

    public String textoDeRetweet() {
        return this.origenTexto;
    }

    public Tweet origen() {
        return origen;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public Long getAutorId() {
        return autorId;
    }

    public String getAutorUsername() {
        return autorUsername;
    }

    public Long getOrigenId() {
        return origenId;
    }

    public String getOrigenAutorUsername() {
        return origenAutorUsername;
    }

    public String getOrigenTexto() {
        return origenTexto;
    }

    public LocalDateTime getOrigenFecha() {
        return origenFecha;
    }

    // Asigna el id generado por la secuencia de Mongo antes de guardar (Mongo
    // no autogenera ids de tipo Long como hacía Hibernate).
    public void asignarId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("El tweet ya tiene un id asignado");
        }
        this.id = id;
    }

    // Setter requerido para la relación bidireccional en memoria (Usuario.agregarTweet/eliminarTweet)
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
