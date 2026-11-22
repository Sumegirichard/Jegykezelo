package jegykezelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    void testSeatCreation() {
        Seat seat = new Seat(5, 10, "A szektor", 5000);
        
        assertAll("Szék tulajdonságainak ellenőrzése",
            () -> assertEquals(5, seat.getRow()),
            () -> assertEquals(10, seat.getNumber()),
            () -> assertEquals(5000, seat.getPrice()),
            () -> assertFalse(seat.isReserved(), "Létrehozáskor szabadnak kell lennie")
        );
    }

    @Test
    void testReservationSuccess() {
        Seat seat = new Seat(1, 1, "A", 1000);
        seat.reserve("Teszt Elek");
        
        assertTrue(seat.isReserved());
        assertEquals("Teszt Elek", seat.getBuyerName());
    }

    @Test
    void testReservationFailureWhenAlreadyBooked() {
        Seat seat = new Seat(1, 1, "A", 1000);
        seat.reserve("Első Vevő");

        //Ha újra megpróbáljuk lefoglalni, hibát kell dobnia
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            seat.reserve("Második Vevő");
        });

        assertTrue(exception.getMessage().contains("már foglalt"));
    }

    @Test
    void testCancellation() {
        Seat seat = new Seat(1, 1, "A", 1000);
        seat.reserve("Vevő");
        assertTrue(seat.isReserved());

        seat.cancel();
        assertFalse(seat.isReserved());
        assertEquals("", seat.getBuyerName());
    }
}