package unrn.DTOs;

import java.util.List;

public record FeedResponseDto(
        List<TweetDto> content,
        int totalPages,
        int currentPage,
        int size,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious) {
}
