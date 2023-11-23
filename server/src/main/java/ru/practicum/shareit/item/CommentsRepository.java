package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(long item);

    List<Comment> findAllByItemIdInOrderByCreatedAsc(Set<Long> ids);
}
