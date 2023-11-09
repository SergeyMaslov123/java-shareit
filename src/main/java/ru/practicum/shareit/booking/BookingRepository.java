package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, Instant end);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartAsc(Long bookerId, Instant start, Instant end);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, Instant start);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, Instant end);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, Instant start, Instant end);

    List<Booking> findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, Instant start);

    boolean existsByBooker_IdAndItem_IdAndStatusAndEndIsBefore(Long bookerId, Long itemId, Status status, Instant end);

    List<Booking> findByItem_IdAndStatusAndStartIsBeforeOrderByStartDesc(Long itemId, Status status, Instant start);

    List<Booking> findByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(Long itemId, Status status, Instant start);

    List<Booking> findAllByItem_IdInAndStatusAndStartIsBeforeOrderByStartDesc(Set<Long> ids, Status status, Instant instant);

    List<Booking> findAllByItem_IdInAndStatusAndStartIsAfterOrderByStartAsc(Set<Long> ids, Status status, Instant instant);

}
