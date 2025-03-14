package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewBookingRequest {
    private long itemId;

    @FutureOrPresent
    private LocalDateTime start;

    @FutureOrPresent
    private LocalDateTime end;

    @AssertTrue
    public boolean isStartEndNotEqual() {
        if (end == null || start == null) {
            return false;
        }

        return !start.isEqual(end);
    }

    @AssertTrue
    public boolean isEndAfterStart() {
        if (end == null || start == null) {
            return false;
        }

        return end.isAfter(start);
    }
}
