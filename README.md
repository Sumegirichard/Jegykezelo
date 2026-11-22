# Jegykezelő

Java Swing alapú jegyfoglaló alkalmazás stadionmérkőzésekhez. Programozás alapjai 3. nagy házi feladat.

Az alkalmazás egy grafikus stadiontérképen jeleníti meg a szektorokat és a székeket. A felhasználó kijelölheti a kívánt helyeket, majd lefoglalhatja őket. Az állapot XML fájlba menthető és onnan visszatölthető.

## Funkciók

- **Stadion térkép**: szektorok (északi, déli, keleti, nyugati és a négy sarok) és székek grafikus megjelenítése, a székekre kattintva kijelölhetők.
- **Foglalás**: a kijelölt helyek egy gombbal, a vevő nevének megadásával foglalhatók le.
- **Dinamikus árazás**: a szektor alapárához képest a pályához közelebbi székek drágábbak (500 Ft-os lépcsőkkel).
- **Nagyítás**: csúszkával állítható a térkép mérete.
- **Statisztika**: diagramok a foglaltságról.
- **Meccsadatok**: szöveges összefoglaló a kiválasztott mérkőzésről.
- **Mentés / betöltés**: a teljes állapot (mérkőzések, szektorok, foglalások) UTF-8 kódolású XML fájlba menthető, és onnan betölthető (Menü → Mentés / Betöltés).

## Követelmények

- JDK 11 vagy újabb (a fejlesztés során JDK 24-gyel lett kipróbálva)
- A tesztek futtatásához JUnit 5

## Futtatás

### Parancssorból

A projekt gyökeréből:

```bash
javac -encoding UTF-8 -d out src/jegykezelo/*.java
java -cp out jegykezelo.Main
```

A `-encoding UTF-8` kapcsoló azért kell, mert a forrásfájlok ékezetes karaktereket tartalmaznak.

### Eclipse-ben

1. File → Import → General → Existing Projects into Workspace
2. Válaszd ki a projekt mappáját
3. Futtatás: jobb klikk a `Main.java`-n → Run As → Java Application

A projekt tartalmazza az Eclipse `.project` és `.classpath` fájlokat, a `src` és a `test` mappa is forrásmappaként van beállítva.

## Projekt felépítése

```
src/jegykezelo/
├── Main.java              Belépési pont, demó adatok létrehozása
├── TicketSystem.java      Mérkőzések kezelése, XML mentés és betöltés
├── Match.java             Egy mérkőzés (csapatok, dátum, szektorok)
├── Section.java           Egy szektor, székek generálása és árazása
├── Seat.java              Egy szék (sor, szám, ár, foglaltság)
└── TicketBookingGUI.java  Swing felület

test/jegykezelo/
├── MatchTest.java
├── SeatTest.java
├── SectionTest.java
└── TicketSystemTest.java
```

## Tesztek

A `test/` mappa JUnit 5 tesztjei Eclipse-ben futtathatók: jobb klikk a `test` mappán → Run As → JUnit Test.

## Demó adatok

Indításkor egy mérkőzés töltődik be (Magyarország – Hollandia, 2026. 06. 15.) nyolc szektorral, és két előre lefoglalt hellyel a bemutatáshoz. Új mérkőzések és szektorok a `Main.java`-ban vehetők fel.
