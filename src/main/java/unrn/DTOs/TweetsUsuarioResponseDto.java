package unrn.DTOs;

import java.util.List;

public record TweetsUsuarioResponseDto(
        List<TweetDto> tweets,
        int limit,
        int offset,
        int total,
        boolean hasMore) {
}
