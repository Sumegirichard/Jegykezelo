package jegykezelo;

import javax.swing.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        TicketSystem system = new TicketSystem();

        
        
        //Különböző szektorok:
        //NORTH = ÉSZAK; SOUTH = DÉL; WEST = NYUGAT; EAST = KELET
        //NW = ÉSZAKNYUGAT; NE = ÉSZAKKELET; SW = DÉLNYUGAT; SE = DÉLKELET
        Match m = new Match("Magyarország", "Hollandia", LocalDate.of(2026, 6, 15));
        
        m.addSection(new Section("A szektor", 5, 17, 8000, "NORTH"));
        
        m.addSection(new Section("C szektor", 5, 17, 8000, "SOUTH"));
        
        m.addSection(new Section("VIP szektor", 16, 5, 25000, "WEST"));
        
        m.addSection(new Section("B szektor", 16, 5, 12000, "EAST"));

        m.addSection(new Section("E szektor", 5, 5, 5000, "NW"));
        
        m.addSection(new Section("F szektor", 5, 5, 5000, "NE"));
        
        m.addSection(new Section("G szektor", 5, 5, 5000, "SW"));
        
        m.addSection(new Section("H szektor", 5, 5, 5000, "SE"));

        //Pár előre lefoglalt hely a demó kedvéért
        m.reserve("VIP szektor", 8, 2, "Patkó Dávid");
        m.reserve("A szektor", 2, 8, "Szurkoló Józsi");
        
        system.addMatch(m);
        
        

        // GUI indítása
        SwingUtilities.invokeLater(() -> new TicketBookingGUI(system));
    }
}