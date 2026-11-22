package jegykezelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TicketSystemTest {

    @Test
    void testSaveAndLoad(@TempDir Path tempDir) throws Exception {
        TicketSystem system = new TicketSystem();
        Match match = new Match("A", "B", LocalDate.of(2025, 1, 1));
        
        Section section = new Section("S1", 2, 2, 1000, "NORTH");
        match.addSection(section);
        
        match.reserve("S1", 1, 1, "Mentett Béla");
        system.addMatch(match);

        File testFile = tempDir.resolve("teszt_adatok.xml").toFile();
        String path = testFile.getAbsolutePath();

        system.saveToFile(path);
        
        assertTrue(testFile.exists(), "A mentett fájlnak léteznie kell");

        TicketSystem loadedSystem = new TicketSystem();
        loadedSystem.loadFromFile(path);

        assertEquals(1, loadedSystem.getMatches().size(), "Egy meccsnek kell lennie betöltés után");
        Match loadedMatch = loadedSystem.getMatches().get(0);
        
        assertEquals("A vs. B", loadedMatch.getMatchName());
        
        Section loadedSection = loadedMatch.getSections().get(0);
        Seat loadedSeat = loadedSection.getSeat(1, 1);
        
        assertTrue(loadedSeat.isReserved(), "A széknek foglaltnak kell lennie betöltés után");
        assertEquals("Mentett Béla", loadedSeat.getBuyerName(), "A vevő nevének egyeznie kell");
    }
}