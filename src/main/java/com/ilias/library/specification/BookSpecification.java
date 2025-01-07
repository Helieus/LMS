package com.ilias.library.specification;
import com.ilias.library.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                title == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> authorNameContains(String authorName) {
        return (root, query, criteriaBuilder) -> {
            if (authorName == null) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.join("author").get("name")), "%" + authorName.toLowerCase() + "%");
        };
    }

    public static Specification<Book> genreContains(String genre) {
        return (root, query, criteriaBuilder) ->
                genre == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("genre")), "%" + genre.toLowerCase() + "%");
    }
}

