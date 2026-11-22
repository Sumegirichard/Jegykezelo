package jegykezelo;

public class Seat {
    private int row;
    private int number;
    private String section;
    private boolean reserved;
    private String buyerName;
    private int price;

    public Seat(int sor, int szék, String szektor, int ár){
        row = sor;
        number = szék;
        section = szektor;
        reserved = false;
        buyerName = "";
        price = ár;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void reserve(String vevő) {
        if(!reserved) {
            reserved = true;
            buyerName = vevő;
        } else {
            throw new IllegalStateException("A " + getId() + " hely már foglalt! Kérem válasszon másik széket!");
        }
    }

    public void cancel() {
        if(reserved) {
            reserved = false;
            buyerName = "";
        }
    }

    public String getBuyerName() {
        return buyerName;
    }

    public int getPrice() {
        return price;
    }

    public String getId() {
        return section + row + "-" + number;
    }

    public int getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    public String getSection() {
        return section;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s sor %d szék %d (%s, %d Ft)", section, section, row, number, reserved ? "Foglalt" : "Szabad", price);
    }
}