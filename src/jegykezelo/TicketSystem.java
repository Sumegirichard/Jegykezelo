package jegykezelo;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class TicketSystem {
    private List<Match> matches;

    public TicketSystem() {
        this.matches = new ArrayList<>();
    }

    public void addMatch(Match meccs) {
        matches.add(meccs);
    }
    
    public List<Match> getMatches() {
        return matches;
    }

    //XML MENTÉS
    public void saveToFile(String filename) throws IOException {
        if (!filename.toLowerCase().endsWith(".xml")) {
            filename += ".xml";
        }

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<ticketSystem>");

            for (Match m : matches) {
                writer.println(String.format("  <match home=\"%s\" away=\"%s\" date=\"%s\">", 
                        m.getHomeTeam(), m.getAwayTeam(), m.getDate().toString()));

                for (Section s : m.getSections()) {
                    writer.println(String.format("    <section name=\"%s\" rows=\"%d\" cols=\"%d\" price=\"%d\" dir=\"%s\">",
                            s.getName(), s.getRows(), s.getSeatsPerRow(), s.getPrice(), s.getDirection()));

                    for (Seat seat : s.getReservedSeats()) {
                        writer.println(String.format("      <reservation row=\"%d\" num=\"%d\" buyer=\"%s\" />",
                                seat.getRow(), seat.getNumber(), seat.getBuyerName()));
                    }
                    writer.println("    </section>");
                }
                writer.println("  </match>");
            }
            writer.println("</ticketSystem>");
        }catch (Exception e) {
        	System.err.println("Hiba az XML mentésekor: " + e.getMessage());
        }
    }

    //XML BETÖLTÉS
    public void loadFromFile(String filename) {
        matches.clear();
        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            InputStream inputStream = new FileInputStream(inputFile);
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList matchList = doc.getElementsByTagName("match");

            for (int i = 0; i < matchList.getLength(); i++) {
                Element matchElement = (Element) matchList.item(i);
                
                String home = matchElement.getAttribute("home");
                String away = matchElement.getAttribute("away");
                LocalDate date = LocalDate.parse(matchElement.getAttribute("date"));
                
                Match currentMatch = new Match(home, away, date);

                NodeList sectionList = matchElement.getElementsByTagName("section");
                for (int j = 0; j < sectionList.getLength(); j++) {
                    Element sectionElement = (Element) sectionList.item(j);
                    
                    String name = sectionElement.getAttribute("name");
                    int rows = Integer.parseInt(sectionElement.getAttribute("rows"));
                    int cols = Integer.parseInt(sectionElement.getAttribute("cols"));
                    int price = Integer.parseInt(sectionElement.getAttribute("price"));
                    String dir = sectionElement.getAttribute("dir");

                    Section currentSection = new Section(name, rows, cols, price, dir);

                    NodeList reservationList = sectionElement.getElementsByTagName("reservation");
                    for (int k = 0; k < reservationList.getLength(); k++) {
                        Element resElement = (Element) reservationList.item(k);
                        int row = Integer.parseInt(resElement.getAttribute("row"));
                        int num = Integer.parseInt(resElement.getAttribute("num"));
                        String buyer = resElement.getAttribute("buyer");
                        
                        currentSection.reserveSeat(row, num, buyer);
                    }
                    
                    currentMatch.addSection(currentSection);
                }
                matches.add(currentMatch);
            }
            inputStream.close(); 
            
        } catch (Exception e) {
            System.err.println("Hiba az XML betöltésekor: " + e.getMessage());
            e.printStackTrace(); 
        }
    }
}