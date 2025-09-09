package unrn.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import unrn.DTOs.NuevoRetweet;
import unrn.DTOs.NuevoTweet;
import unrn.DTOs.TweetDto;
import unrn.service.TwitterService;

import java.util.List;

@RestController
@RequestMapping("/tweets")
public class TweetController {
    private final TwitterService service;

    public TweetController(TwitterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> crearTweet(@RequestBody NuevoTweet nuevoTweet) {
        try {
            service.crearTweet(nuevoTweet.usuarioId(), nuevoTweet.texto());
            return ResponseEntity.status(201).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/retweet")
    public ResponseEntity<?> crearRetweet(@RequestBody NuevoRetweet nuevoRetweet) {
        try {
            service.crearRetweet(nuevoRetweet.usuarioId(), nuevoRetweet.tweetOrigenId());
            return ResponseEntity.status(201).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarTweetsDeUsuario(@PathVariable Long usuarioId) {
        try {
            List<TweetDto> tweets = service.listarTweetsDeUsuario(usuarioId).stream()
                    .map(t -> new TweetDto(
                            t.getId(),
                            t.texto(),
                            t.origen() != null ? t.origen().getId() : null
                    )).toList();
            return ResponseEntity.ok(tweets);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
