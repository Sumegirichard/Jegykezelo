package jegykezelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Section implements Serializable {
    private String name;
    private int rows;
    private int seatsPerRow;
    private int basePrice;
    private String direction; //"NORTH", "SOUTH", "WEST", "EAST"
    private List<Seat> seats;

    public Section(String nev, int sorok, int szekekSoronkent, int alapAr, String irany) {
        name = nev;
        rows = sorok;
        seatsPerRow = szekekSoronkent;
        basePrice = alapAr;
        direction = irany.toUpperCase();
        seats = new ArrayList<>();

        generateSeats();
    }

    private void generateSeats() {
        int priceStep = 500; //Árlépcső

        for (int sor = 1; sor <= rows; sor++) {
            for (int szék = 1; szék <= seatsPerRow; szék++) {
                int dynamicPrice = basePrice; //Alapérték

                switch (direction) {
                    case "SOUTH": 
                        //DÉLI LELÁTÓ
                        //Az 1. sor van legközelebb -> Legdrágább.
                        dynamicPrice += (rows - sor) * priceStep;
                        break;

                    case "NORTH": 
                        //ÉSZAKI LELÁTÓ
                        //Az utolsó sor van legközelebb -> Legdrágább.
                        dynamicPrice += (sor - 1) * priceStep;
                        break;

                    case "EAST": 
                        //KELETI LELÁTÓ 
                        //Az 1. szék van legközelebb -> Legdrágább.
                        dynamicPrice += (seatsPerRow - szék) * priceStep;
                        break;
                        
                    case "WEST": 
                        //NYUGATI LELÁTÓ
                        //Az utolsó szék (seatsPerRow) van legközelebb -> Legdrágább.
                        dynamicPrice += (szék - 1) * priceStep;
                        break;

                    default:
                        //Ha nincs megadva irány, marad az alapár
                        break;
                }

                seats.add(new Seat(sor, szék, name, dynamicPrice));
            }
        }
    }

    public Seat getSeat(int sor, int szék) {
        for(Seat s : seats) {
            if(s.getId().equals(name + sor + "-" + szék)) {
                return s;
            }
        }
        return null;
    }

    public boolean reserveSeat(int sor, int szék, String vevő) {
        Seat s = getSeat(sor, szék);
        if(s != null && !s.isReserved()) {
            s.reserve(vevő);
            return true;
        }
        return false;
    }

    public boolean cancelReservation(int sor, int szék) {
        Seat s = getSeat(sor, szék);
        if(s != null && s.isReserved()) {
            s.cancel();
            return true;
        }
        return false;
    }

    public List<Seat> getAvailableSeats(){
        List<Seat> free = new ArrayList<>();
        for(Seat s : seats) { if(!s.isReserved()) free.add(s); }
        return free;
    }

    public List<Seat> getReservedSeats(){
        List<Seat> reserved = new ArrayList<>();
        for(Seat s : seats) { if(s.isReserved()) reserved.add(s); }
        return reserved;
    }
    
    public List<Seat> getSeats() { return seats; }
    public String getName() { return name; }
    public int getTotalSeats() { return seats.size(); }
    public int getRows() { return rows; }
    public int getSeatsPerRow() { return seatsPerRow; }
    public int getPrice() { return basePrice; }
    public String getDirection() { return direction; }

    @Override
    public String toString() {
        return String.format("Szektor: %s (%s)", name, direction);
    }
}