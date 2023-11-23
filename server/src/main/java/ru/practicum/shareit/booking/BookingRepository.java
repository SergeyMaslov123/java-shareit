package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    Page<Booking> findAllByBooker_Id(Long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, Instant end);

    Page<Booking> findAllByBooker_idAndEndIsBefore(Long bookerId, Instant end, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status);

    Page<Booking> findByBooker_idAndStatus(Long bookerId, Status status, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, Instant start, Instant end);

    Page<Booking> findByBooker_idAndStartIsBeforeAndEndIsAfter(Long bookerId, Instant start, Instant end, Pageable pageable);


    List<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, Instant start);

    Page<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, Instant start, Pageable pageable);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    Page<Booking> findByItem_Owner_Id(Long ownerId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, Instant end);

    Page<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, Instant end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, Status status);

    Page<Booking> findByItem_Owner_IdAndStatus(Long ownerId, Status status, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, Instant start, Instant end);

    Page<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId, Instant start, Instant end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, Instant start);

    Page<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, Instant start, Pageable pageable);

    boolean existsByBooker_IdAndItem_IdAndStatusAndEndIsBefore(Long bookerId, Long itemId, Status status, Instant end);

    List<Booking> findAllByItem_IdInAndStatusAndStartLessThanEqualOrderByStartDesc(Set<Long> ids, Status status, Instant instant);

    List<Booking> findAllByItem_IdInAndStatusAndStartIsAfterOrderByStartAsc(Set<Long> ids, Status status, Instant instant);

    Booking findFirstByItem_IdAndStatusAndStartLessThanEqualOrderByStartDesc(Long itemId, Status status, Instant start);

    Booking findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(Long itemId, Status status, Instant start);


}
