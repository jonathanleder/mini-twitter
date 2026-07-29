package unrn.DTOs;

import java.time.LocalDateTime;

public record TweetDto(
        Long id,
        String texto,
        String autorUsername,
        LocalDateTime fecha,
        Long origenId,
        LocalDateTime origenFecha,
        String tweetOriginalTexto,
        String usuarioOriginal,
        String usuarioRetweet,
        Boolean esRetweet) {
}
