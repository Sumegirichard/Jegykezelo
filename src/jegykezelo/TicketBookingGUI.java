package jegykezelo;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TicketBookingGUI extends JFrame {
    private final TicketSystem system;
    private JComboBox<Match> matchComboBox;
    
    private JPanel mainContainer;
    private CardLayout cardLayout;
    
    private JComboBox<String> viewSelector;

    //ZOOM Változók
    private double zoomFactor = 1.0; 
    private JSlider zoomSlider;

    //FOGLALÁS LOGIKA
    private Set<Seat> selectedSeats = new HashSet<>(); //A kijelölt székek tárolója
    private JButton bookButton; //A "Lefoglalom" gomb

    //1. NÉZET: STADION
    private JScrollPane stadiumScrollPane; 
    private JPanel stadiumPanel;         

    //2. NÉZET: STATISZTIKA
    private JPanel statsPanel;
    private JScrollPane statsScrollPane;

    //3. NÉZET: LISTA
    private JTextArea infoArea;
    
    private JLabel statusLabel;

    public TicketBookingGUI(TicketSystem system) {
        super("Stadion Jegyfoglaló Rendszer");
        this.system = system;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 950); 
        setLocationRelativeTo(null);

        //MENÜ
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Menü");
        JMenuItem save = new JMenuItem("Mentés");
        JMenuItem load = new JMenuItem("Betöltés");
        JMenuItem exit = new JMenuItem("Kilépés");
        menu.add(save); menu.add(load); menu.addSeparator(); menu.add(exit);
        mb.add(menu);
        setJMenuBar(mb);
        
        save.addActionListener(e -> onSave());
        load.addActionListener(e -> onLoad());
        exit.addActionListener(e -> System.exit(0));

        //FELSŐ SÁV
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        //1. Meccs választó
        top.add(new JLabel("Mérkőzés: "));
        matchComboBox = new JComboBox<>(system.getMatches().toArray(new Match[0])); //JCbomboBox nem tud List-et fogadni csak tömböt
        matchComboBox.addActionListener(e -> {
            selectedSeats.clear(); //Meccsváltáskor töröljük a kijelölést
            updateBookButton(); //Frissítjük a foglaló gombot
            refreshAllViews(); //plust az összes nézetet
        });
        top.add(matchComboBox);
        
        top.add(Box.createHorizontalStrut(20)); //Ezzel tudom azt állítani hogy hány pixellel jobbra kezdődjön a következő dolog

        //2. Nézet választó
        //U.a a logika mint előbb
        top.add(new JLabel("Nézet: "));
        String[] views = { "Stadion Térkép", "Statisztika (Diagramok)", "Meccsadatok" };
        viewSelector = new JComboBox<>(views);
        viewSelector.addActionListener(e -> switchView());
        top.add(viewSelector);

        top.add(Box.createHorizontalStrut(20));

        //3. LEFOGLALOM GOMB
        bookButton = new JButton("Kijelöltek lefoglalása (0)");
        bookButton.setFont(new Font("Arial", Font.BOLD, 12));
        bookButton.setBackground(new Color(255, 140, 0));
        bookButton.setForeground(Color.BLACK);
        bookButton.setFocusPainted(false); //Ezzel csak annyit csinálok, hogy a szöveg körül nem lész egy keret
        bookButton.addActionListener(e -> showBookingDialog());
        top.add(bookButton);

        top.add(Box.createHorizontalStrut(20));

        //4. Zoom
        top.add(new JLabel("Nagyítás:"));
        zoomSlider = new JSlider(10, 300, 100);
        zoomSlider.setPreferredSize(new Dimension(300, 40)); //mekkora legyen a csúszka
        zoomSlider.setMajorTickSpacing(50); //Nagyobb vonalak
        zoomSlider.setMinorTickSpacing(10); //kisebb vonalak
        zoomSlider.setPaintTicks(true); //látszódjanak is
        zoomSlider.addChangeListener(e -> {
            zoomFactor = zoomSlider.getValue() / 100.0;
            if (viewSelector.getSelectedIndex() == 0) { //csak akkor rajzoljuk a stadiont ha abban a nézetben vagyunk
                updateStadiumView((Match) matchComboBox.getSelectedItem());
            }
        });
        top.add(zoomSlider);

        add(top, BorderLayout.NORTH); //ezzel lezárjuk a felső sávot

        //KÖZÉPSŐ TARTALOM
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        //STADION NÉZET
        stadiumPanel = new JPanel();
        stadiumPanel.setBackground(Color.DARK_GRAY); 
        stadiumScrollPane = new JScrollPane(stadiumPanel);
        stadiumScrollPane.getVerticalScrollBar().setUnitIncrement(16); //Ezekkel azt állítom be, hogy görgetésre egyszerre 16 pixelt ugorgon, hogy gyorsabb legyen
        stadiumScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        mainContainer.add(stadiumScrollPane, "VIEW_STADION");

        //STATISZTIKA NÉZET
        statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(0, 3, 20, 20)); 
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsScrollPane = new JScrollPane(statsPanel);
        mainContainer.add(statsScrollPane, "VIEW_STATS");

        //INFO NÉZET
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        mainContainer.add(infoPanel, "VIEW_INFO");

        add(mainContainer, BorderLayout.CENTER);

        //ALSÓ SÁV
        statusLabel = new JLabel("Kész.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);

        if (system.getMatches().size() > 0) matchComboBox.setSelectedIndex(0);
        refreshAllViews();

        setVisible(true);
    }

    private void switchView() {
        int index = viewSelector.getSelectedIndex();
        switch (index) {
            case 0: cardLayout.show(mainContainer, "VIEW_STADION"); zoomSlider.setEnabled(true); break;
            case 1: cardLayout.show(mainContainer, "VIEW_STATS"); zoomSlider.setEnabled(false); break;
            case 2: cardLayout.show(mainContainer, "VIEW_INFO"); zoomSlider.setEnabled(false); break;
        }
    }

    private void refreshAllViews() {
        Match m = (Match) matchComboBox.getSelectedItem();
        if (m == null) return;
        updateStadiumView(m);
        updateStatsView(m); 
        updateInfoView(m);
        updateStatus(m);
    }

    //FOGLALÁS LOGIKA
    
    //Gombnyomás kezelése (Kiválasztás vagy Törlés)
    private void handleSeatClick(Seat seat, JButton b) {
        //Ha FOGLALT helyre kattintunk, lemondás
        if (seat.isReserved()) {
            int ans = JOptionPane.showConfirmDialog(this, 
                    "Le szeretné mondani " + seat.getBuyerName() + " foglalását?", 
                    "Lemondás", JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION) {
                seat.cancel();
                refreshAllViews();
            }
            return;
        }

        //Ha SZABAD helyre kattintunk, Kijelölés / Kijelölés megszüntetése
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat); //Ha már ki volt jelölve, akkor levesszük
        } else {
            selectedSeats.add(seat); //egyébként hozzáadjuk
        }
        
        //Frissítjük a gomb színét
        updateButtonColor(b, seat);
        updateBookButton();
    }

    //A felső gomb frissítése
    private void updateBookButton() {
        bookButton.setText("Kijelöltek lefoglalása (" + selectedSeats.size() + ")");
        
        
        //Színváltás: ha van kijelölve, élénkebb legyen
        if (!selectedSeats.isEmpty()) {
            bookButton.setBackground(new Color(255, 69, 0));
        } else {
            bookButton.setBackground(new Color(255, 140, 0));
        }
        
        //Ez biztosítja, hogy mindig fekete legyen a szöveg
        bookButton.setForeground(Color.BLACK);
    }

    //Az Összesítő ablak megjelenítése és Foglalás
    private void showBookingDialog() {
        if (selectedSeats.isEmpty()) return;

        // Összesítés készítése
        StringBuilder sb = new StringBuilder();
        int totalSum = 0;
        sb.append("Kiválasztott helyek:\n\n");
        
        for (Seat s : selectedSeats) {
            sb.append(String.format("- %s, %d. sor, %d. szék (%d Ft)\n", 
                    s.getSection(), s.getRow(), s.getNumber(), s.getPrice()));
            totalSum += s.getPrice();
        }
        sb.append("\n------------------------------\n");
        sb.append("VÉGÖSSZEG: ").append(totalSum).append(" Ft\n\n");
        sb.append("Kérem adja meg a vásárló nevét:");

        //Komponensek az ablakhoz
        JTextArea summaryArea = new JTextArea(sb.toString());
        summaryArea.setEditable(false);
        summaryArea.setBackground(null);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JTextField nameField = new JTextField();
        
        //Panel összeállítása
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        panel.add(nameField, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(350, 250));

        //Dialogus megjelenítése
        int result = JOptionPane.showConfirmDialog(this, panel, "Foglalás véglegesítése", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                //Tényleges foglalás végrehajtása minden kijelölt székre
                for (Seat s : selectedSeats) {
                    try {
                        s.reserve(name);
                    } catch (Exception ex) {
                        System.err.println("Hiba foglaláskor: " + ex.getMessage());
                    }
                }
                JOptionPane.showMessageDialog(this, "Sikeres foglalás " + selectedSeats.size() + " helyre!");
                selectedSeats.clear();
                updateBookButton();
                refreshAllViews();
            } else {
                JOptionPane.showMessageDialog(this, "Nem adott meg nevet! A foglalás megszakadt.", "Hiba", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    //Gomb színek frissítése
    private void updateButtonColor(JButton b, Seat seat) {
        if (seat.isReserved()) {
            //FOGLALT (Piros)
            b.setBackground(Color.RED);
            b.setForeground(Color.WHITE); 
            b.setToolTipText("Foglalt: " + seat.getBuyerName());
        } else if (selectedSeats.contains(seat)) {
            //KIJELÖLT (Sárga)
            b.setBackground(Color.YELLOW);
            b.setForeground(Color.BLACK);
            b.setToolTipText("Kijelölve foglalásra (" + seat.getPrice() + " Ft)");
        } else {
            //SZABAD (Zöld)
            b.setBackground(new Color(50, 205, 50)); 
            b.setForeground(Color.BLACK);
            b.setToolTipText("Ár: " + seat.getPrice() + " Ft");
        }
        b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false); b.setFocusPainted(false);      
    }

    //STADION NÉZET
    private void updateStadiumView(Match m) {
        stadiumPanel.removeAll();
        stadiumPanel.setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();

        FootballPitchPanel pitchPanel = new FootballPitchPanel();
        int pitchWidth = (int) (600 * zoomFactor);
        int pitchHeight = (int) (400 * zoomFactor);
        pitchPanel.setPreferredSize(new Dimension(pitchWidth, pitchHeight));
        
        gbc.gridx = 1; gbc.gridy = 1; //3x3 rácsank ez a közepe
        gbc.insets = new Insets(0,0,0,0); 
        gbc.fill = GridBagConstraints.BOTH;
        stadiumPanel.add(pitchPanel, gbc);

        for (Section s : m.getSections()) {
            JPanel panel = createStandPanel(s);
            switch (s.getDirection()) {
                case "NORTH": gbc.gridx = 1; gbc.gridy = 0; break;
                case "SOUTH": gbc.gridx = 1; gbc.gridy = 2; break;
                case "WEST":  gbc.gridx = 0; gbc.gridy = 1; break;
                case "EAST":  gbc.gridx = 2; gbc.gridy = 1; break;
                case "NW":    gbc.gridx = 0; gbc.gridy = 0; break;
                case "NE":    gbc.gridx = 2; gbc.gridy = 0; break;
                case "SW":    gbc.gridx = 0; gbc.gridy = 2; break;
                case "SE":    gbc.gridx = 2; gbc.gridy = 2; break;
            }
            stadiumPanel.add(panel, gbc);
        }
        stadiumPanel.revalidate();
        stadiumPanel.repaint();
    }
    
    //LELÁTÓ PANEL
    private JPanel createStandPanel(Section s) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), s.getName(),
                javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.PLAIN, 10)));
        container.setBackground(Color.LIGHT_GRAY); 

        JPanel grid = new JPanel(new GridLayout(s.getRows(), s.getSeatsPerRow(), 1, 1));
        grid.setBackground(Color.LIGHT_GRAY);
        
        for (Seat seat : s.getSeats()) {
            grid.add(createSeatButton(seat));
        }
        container.add(grid, BorderLayout.CENTER);
        return container;
    }

    private JButton createSeatButton(Seat seat) {
        JButton b = new JButton();
        int btnW = (int) (35 * zoomFactor); //int-re kasztolunk hogy egész szám legyen (pixel miatt)
        int btnH = (int) (25 * zoomFactor);
        b.setPreferredSize(new Dimension(btnW, btnH));
        int fontSize = Math.max(1, (int) (9 * zoomFactor)); 
        b.setFont(new Font("Arial", Font.PLAIN, fontSize));
        if(fontSize > 5) b.setText(String.valueOf(seat.getNumber())); //Csak akkor írjuk rá a számot ha a betűméret nagyobb mintt 5 px
        b.setMargin(new Insets(0,0,0,0));
        
        updateButtonColor(b, seat); //Itt állítjuk be a színt (Zöld/Sárga/Piros)
        
        b.addActionListener(ev -> handleSeatClick(seat, b));
        return b;
    }

    
    //Kördiagrammok
    private void updateStatsView(Match m) {
        statsPanel.removeAll();
        for (Section s : m.getSections()) {
            PieChartPanel chart = new PieChartPanel(s.getName(), s.getTotalSeats(), s.getReservedSeats().size());
            chart.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            chart.setBackground(Color.WHITE);
            statsPanel.add(chart);
        }
        statsPanel.revalidate(); statsPanel.repaint();
    }

    private void updateInfoView(Match m) {
        StringBuilder sb = new StringBuilder();
        sb.append("Mérkőzés: ").append(m.getMatchName()).append("\n");
        sb.append("Dátum:    ").append(m.getDate()).append("\n\n");
        sb.append(String.format("%-25s | %-10s | %-10s | %-15s\n", "SZEKTOR", "FOGLALT", "ÖSSZES", "BEVÉTEL"));
        sb.append("----------------------------------------------------------------------\n");
        for (Section s : m.getSections()) {
            int inc = 0; for(Seat st: s.getReservedSeats()) inc+=st.getPrice();
            sb.append(String.format("%-25s | %-10d | %-10d | %d Ft\n", s.getName(), s.getReservedSeats().size(), s.getTotalSeats(), inc));
        }
        sb.append("\nÖSSZBEVÉTEL: ").append(m.calculateIncome()).append(" Ft\n");
        infoArea.setText(sb.toString());
    }

    private void updateStatus(Match m) {
        if (m != null) statusLabel.setText(String.format("%s — Szabad: %d, Foglalt: %d", m.getMatchName(), m.getTotalFreeSeats(), m.getTotalReservedSeats()));
    }

    private static class PieChartPanel extends JPanel {
        private String title; private int total; private int reserved;
        public PieChartPanel(String title, int total, int reserved) {
            this.title = title; this.total = total; this.reserved = reserved;
            this.setPreferredSize(new Dimension(250, 300)); 
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            int d = Math.min(w, h - 80) - 20; 
            int x = (w - d) / 2; int y = 40;
            g2.setColor(Color.BLACK); g2.setFont(new Font("Arial", Font.BOLD, 14));
            int tx = (w - g2.getFontMetrics().stringWidth(title)) / 2; g2.drawString(title, tx, 25);
            g2.setColor(new Color(50, 205, 50)); g2.fillOval(x, y, d, d);
            if (total > 0 && reserved > 0) {
                int a = (int) Math.round(((double) reserved / total) * 360);
                g2.setColor(Color.RED); g2.fillArc(x, y, d, d, 90, -a);
            }
            g2.setColor(Color.GRAY); g2.drawOval(x, y, d, d);
            String s = String.format("%d foglalt / %d összes", reserved, total);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            tx = (w - g2.getFontMetrics().stringWidth(s)) / 2;
            g2.setColor(Color.DARK_GRAY); g2.drawString(s, tx, y + d + 20);
        }
    }
    
    private static class FootballPitchPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(); int h = getHeight();
            
            //1. Alapszín (Világosabb zöld)
            g2.setColor(new Color(34, 139, 34)); 
            g2.fillRect(0, 0, w, h);
            
            //2. Sötétebb csíkok
            g2.setColor(new Color(30, 120, 30)); 
            int stripWidth = w / 10; //A pálya szélességének tizede
            for(int i=0; i<10; i+=2) { //Minden második sávba rajzolunk
                g2.fillRect(i*stripWidth, 0, stripWidth, h);
            }
            
            g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2)); 
            int margin = 5;
            
            //Külső keret és felező
            g2.drawRect(margin, margin, w - 2*margin, h - 2*margin); 
            g2.drawLine(w / 2, margin, w / 2, h - margin);
            
            //Középkör, kezdőpont
            int radius = h / 6; //A kör sugara a magasság hatoda
            //A bal felső sarkot kell megadni, ezért a középpontból kivonjuk a sugarat
            g2.drawOval((w / 2) - radius, (h / 2) - radius, radius * 2, radius * 2); 
            g2.fillOval((w/2)-3, (h/2)-3, 6, 6); //kezdőpont
            
            //tizenhatosok
            int boxWidth = w / 6; 
            int boxHeight = h / 2; 
            int boxY = (h - boxHeight) / 2; //Hogy függőlegesen középen legyen

            //Bal oldali 16-os
            g2.drawRect(margin, boxY, boxWidth, boxHeight); 
            //Jobb oldali 16-os (A szélességből ki kell vonni a doboz szélességét)
            g2.drawRect(w - margin - boxWidth, boxY, boxWidth, boxHeight);
            
            //ötösök, ugyanazzal a logikával csináltam
            int smallBoxH = h / 4; int smallBoxW = w / 12; int smallBoxY = (h - smallBoxH) / 2;
            g2.drawRect(margin, smallBoxY, smallBoxW, smallBoxH); 
            g2.drawRect(w - margin - smallBoxW, smallBoxY, smallBoxW, smallBoxH); 
            
            //Szögletívek
            int cS = 20; //Az ív mérete
            //Bal felső sarok (270 foktól indul, 90 fokot megy)
            g2.drawArc(margin - cS/2, margin - cS/2, cS, cS, 270, 90);
            //Bal alsó
            g2.drawArc(margin - cS/2, h - margin - cS/2, cS, cS, 0, 90);
            //Jobb felső
            g2.drawArc(w - margin - cS/2, margin - cS/2, cS, cS, 180, 90);
            //Jobb alsó
            g2.drawArc(w - margin - cS/2, h - margin - cS/2, cS, cS, 90, 90);
        }
    }
	
    private void onSave() { 
        JFileChooser fc = new JFileChooser(); 
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
            try { system.saveToFile(fc.getSelectedFile().getAbsolutePath()); JOptionPane.showMessageDialog(this,"Mentve!"); } 
            catch(Exception e) { JOptionPane.showMessageDialog(this, "Hiba: " + e.getMessage()); }
        } 
    }
    private void onLoad() { 
        JFileChooser fc = new JFileChooser(); 
        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            try { 
                system.loadFromFile(fc.getSelectedFile().getAbsolutePath()); 
                matchComboBox.setModel(new DefaultComboBoxModel<>(system.getMatches().toArray(new Match[0])));
                if(system.getMatches().size()>0) matchComboBox.setSelectedIndex(0);
                selectedSeats.clear(); //Betöltéskor is töröljük a kijelölést!
                updateBookButton();
                refreshAllViews();
            } catch(Exception e) { JOptionPane.showMessageDialog(this, "Hiba: " + e.getMessage()); }
        } 
    }
}