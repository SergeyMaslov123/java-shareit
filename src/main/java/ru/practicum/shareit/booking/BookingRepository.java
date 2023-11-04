package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1")
    List<Booking> getAllBookingsForUserId(Long user);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item, b.booker, b.status from bookings as b " +
            "left join items as i on b.item = i.id where i.owner = ?1 ", nativeQuery = true)
    List<Booking> getAllBookingsForUserItemId(Long booker);

    List<Booking> findByItemId(Long item);


}
