package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import ru.practicum.shareit.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findByBookerIdAndId(long userId, long bookingId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP")
    List<Booking> findCurrentBookings(long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findPastBookings(long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findFutureBookings(long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP")
    List<Booking> findCurrentBookingsByOwner(long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findPastBookingsByOwner(long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findFutureBookingsByOwner(long userId);

    @Query("select b from Booking b where b.booker.id=?1 and b.status=?2")
    List<Booking> findBookingByBookerIdAndStatus(long userId, String state);

    List<Booking> findByBookerId(long userId);

    List<Booking> findByItemId(long itemId, Sort sort);
}
