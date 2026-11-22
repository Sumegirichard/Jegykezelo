package jegykezelo;

import java.time.LocalDate;
import java.util.*;

public class Match {
    private String homeTeam;
    private String awayTeam;
    private LocalDate date;
    private List<Section> sections;

    public Match(String hazai, String vendég, LocalDate idopont) {
        homeTeam = hazai;
        awayTeam = vendég;
        date = idopont;
        sections = new ArrayList<>();
    }

    public void addSection(Section szektor) {
        sections.add(szektor);
    }

    public Section getSection(String nev) {
        for(Section s : sections) {
            if(s.getName().equalsIgnoreCase(nev)) {
                return s;
            }
        }
        return null;
    }

    public boolean reserve(String szektorNev, int sor, int szék, String vevő) {
        Section sec = getSection(szektorNev);
        if(sec != null) {
            return sec.reserveSeat(sor, szék, vevő);
        }
        return false;
    }

    public boolean cancel(String szektorNev, int sor, int szék) {
        Section sec = getSection(szektorNev);
        if (sec != null) {
            return sec.cancelReservation(sor, szék);
        }
        return false;
    }

    public int getTotalReservedSeats() {
        int total = 0;
        for (Section s : sections) {
            total += s.getReservedSeats().size();
        }
        return total;
    }

    public int getTotalFreeSeats() {
        int total = 0;
        for (Section s : sections) {
            total += s.getAvailableSeats().size();
        }
        return total;
    }

    public int calculateIncome() {
        int income = 0;
        for (Section s : sections) {
            for (Seat seat : s.getReservedSeats()) {
                income += seat.getPrice();
            }
        }
        return income;
    }

    public List<Section> getSections() {
        return sections;
    }

    public String getMatchName() {
        return homeTeam + " vs. " + awayTeam;
    }

    public LocalDate getDate() {
        return date;
    }
    
    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    @Override
    public String toString() {
        return String.format("%s vs. %s (%s)", homeTeam, awayTeam, date.toString());
    }
}