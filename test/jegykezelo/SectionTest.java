package jegykezelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {

    @Test
    void testNorthSectionPricing() {
        //NORTH (Fent): A pálya alatta van. 
        //Logika: minél nagyobb a sorszám (lejjebb van), annál drágább.
        //Ár = Alap + (sor - 1) * 500
        Section section = new Section("Északi", 10, 5, 1000, "NORTH");
        
        Seat row1 = section.getSeat(1, 1);
        Seat row10 = section.getSeat(10, 1);

        //1. sor (Legtávolabb): 1000 + (1-1)*500 = 1000
        assertEquals(1000, row1.getPrice());
        
        //10. sor (Legközelebb): 1000 + (10-1)*500 = 5500
        assertEquals(5500, row10.getPrice());
    }

    @Test
    void testWestSectionPricing() {
        //WEST (Balra): A pálya jobbra van.
        //Logika: minél nagyobb a székszám (jobbra van), annál drágább.
        //Ár = Alap + (szék - 1) * 500
        Section section = new Section("Nyugati", 5, 10, 2000, "WEST");

        Seat seat1 = section.getSeat(3, 1);  // Bal szél (Messze)
        Seat seat10 = section.getSeat(3, 10); // Jobb szél (Közel a pályához)

        assertEquals(2000, seat1.getPrice());
        assertEquals(2000 + (10 - 1) * 500, seat10.getPrice());
    }

    @Test
    void testReserveSeatInSection() {
        Section section = new Section("Teszt", 5, 5, 1000, "NORTH");
        boolean success = section.reserveSeat(1, 1, "Vevő");
        
        assertTrue(success);
        assertTrue(section.getSeat(1, 1).isReserved());
        
        //Próbáljuk meg újra ugyanazt
        boolean fail = section.reserveSeat(1, 1, "Másik");
        assertFalse(fail, "Már foglalt helyet nem lehet újra lefoglalni");
    }
}