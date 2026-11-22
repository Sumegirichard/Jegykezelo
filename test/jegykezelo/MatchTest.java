package jegykezelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private Match match;

    @BeforeEach
    void setUp() {
        match = new Match("Hazai", "Vendég", LocalDate.now());
        //Létrehozunk egy 2 soros, 2 oszlopos szektort. Alapár: 1000.
        //Irány NORTH: 1. sor=1000, 2. sor=1500. Összes szék: 4 db.
        match.addSection(new Section("TesztSzektor", 2, 2, 1000, "NORTH"));
    }

    @Test
    void testInitialState() {
        assertEquals(4, match.getTotalFreeSeats());
        assertEquals(0, match.getTotalReservedSeats());
        assertEquals(0, match.calculateIncome());
    }

    @Test
    void testBookingUpdatesStatistics() {
        //Lefoglalunk egyet az 1. sorban (1000 Ft)
        match.reserve("TesztSzektor", 1, 1, "Vevő 1");
        
        assertEquals(3, match.getTotalFreeSeats());
        assertEquals(1, match.getTotalReservedSeats());
        assertEquals(1000, match.calculateIncome());

        //Lefoglalunk egyet a 2. sorban (1500 Ft)
        match.reserve("TesztSzektor", 2, 1, "Vevő 2");

        assertEquals(2, match.getTotalFreeSeats());
        assertEquals(2500, match.calculateIncome()); // 1000 + 1500
    }
    
    @Test
    void testMatchName() {
        assertEquals("Hazai vs. Vendég", match.getMatchName());
    }
}