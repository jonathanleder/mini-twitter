package unrn.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import unrn.DTOs.*;
import unrn.service.TwitterService;

import java.util.List;

@RestController
@RequestMapping("/tweets")
@CrossOrigin(origins = "http://localhost:5173")
public class TweetController {
    private final TwitterService service;

    public TweetController(TwitterService service) {
        this.service = service;
    }

    private TweetDto mapTweetToDto(unrn.model.Tweet t) {
        boolean esRetweet = t.getOrigenId() != null;
        return new TweetDto(
                t.getId(),
                t.texto(),
                t.getAutorUsername(),
                t.getFechaCreacion(),
                t.getOrigenId(),
                t.getOrigenFecha(),
                t.getOrigenTexto(),
                t.getOrigenAutorUsername(),
                esRetweet ? t.getAutorUsername() : null,
                esRetweet);
    }

    @PostMapping
    public ResponseEntity<?> crearTweet(@RequestBody NuevoTweet nuevoTweet) {
        service.crearTweet(nuevoTweet.usuarioId(), nuevoTweet.texto());
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/retweet")
    public ResponseEntity<?> crearRetweet(@RequestBody NuevoRetweet nuevoRetweet) {
        service.crearRetweet(nuevoRetweet.usuarioId(), nuevoRetweet.tweetOrigenId());
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarTweetsDeUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "15") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        if (limit < 1 || limit > 100)
            limit = 15;
        if (offset < 0)
            offset = 0;

        List<TweetDto> content = service.listarTweetsDeUsuarioConLimitDto(usuarioId, limit, offset);
        int total = service.contarTweetsDeUsuario(usuarioId);
        boolean hasMore = offset + content.size() < total;

        TweetsUsuarioResponseDto response = new TweetsUsuarioResponseDto(
                content,
                limit,
                offset,
                total,
                hasMore);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> listarTodosLosTweets() {
        List<TweetDto> tweets = service.listarTodosLosTweets().stream()
                .map(this::mapTweetToDto)
                .toList();
        return ResponseEntity.ok(tweets);
    }

    // Feed paginado (tweets normales sin retweets)
    @GetMapping("/feed")
    public ResponseEntity<?> listarFeedPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 0)
            page = 0;
        if (size < 1 || size > 100)
            size = 10;

        List<TweetDto> content = service.listarFeedPaginadoDto(page, size);
        int totalElements = service.contarTweetsNormales();
        int totalPages = (totalElements + size - 1) / size;

        FeedResponseDto response = new FeedResponseDto(
                content,
                totalPages,
                page,
                size,
                totalElements,
                page < totalPages - 1,
                page > 0);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<?> eliminarTweet(@PathVariable Long tweetId) {
        service.eliminarTweet(tweetId);
        return ResponseEntity.noContent().build();
    }
}
