# Auktionshaus Simulator – Dokumentation

**Projekt:** Auktionshaus Simulator (Portfolioprojekt)  
**Repository:** KeinDatenvolumen/auctionshaus  
**Version:** 2.0.0  
**Datum:** 20.05.2026  

---

## 1. Einleitung und Zielsetzung

Dieses Dokument beschreibt das Konzept, die Architektur sowie die zentralen Design- und Konfigurationsentscheidungen des Projekts **Auktionshaus Simulator**. Ziel des Programms ist die Simulation eines Auktionshauses, in dem Nutzer Auktionen anlegen und in einer realitätsnahen Umgebung (niederländische Auktion) ablaufen lassen können. Im Fokus steht eine nachvollziehbare, didaktische Implementierung der Fachlogik, ergänzt durch eine einfache grafische Oberfläche, Persistenz in JSON und eine parallele Simulation.

Der Bericht richtet sich an Leserinnen und Leser, die nachvollziehen möchten, wie die Anwendung aufgebaut ist, welche fachlichen Konzepte umgesetzt wurden und warum bestimmte technische Entscheidungen getroffen wurden. Die Beschreibung ist bewusst detailliert gehalten, sodass der Code auch ohne tiefes Vorwissen verständlich eingeordnet werden kann.

---

## 2. Projektüberblick

Der **Auktionshaus Simulator** bildet die Abläufe einer niederländischen Auktion ab. Dabei startet eine Auktion mit einem Startpreis und reduziert diesen in festen Schritten, bis ein Bieter zuschlägt oder der Mindestpreis unterschritten wird. Das System unterstützt mehrere Nutzerrollen, Auktionen, Bietstrategien und eine Simulation mit parallel laufenden Auktionen.

**Kernfunktionen im Überblick:**

- Erstellen von Auktionatoren und Bietern
- Anlegen und Verwalten von Auktionen
- Simulation paralleler Auktionen mit einstellbarem Takt (Tick)
- Strategiebasierte Bietentscheidungen
- Reporting (Statistiken über abgeschlossene Auktionen)
- Speichern und Laden des Systemzustands als JSON
- JavaFX-basierte Benutzeroberfläche ohne FXML (bewusst „leichtgewichtig“)

Das Projekt ist als Java-Anwendung mit Maven aufgebaut und nutzt JavaFX für die UI sowie Jackson für JSON-Serialisierung.

### 2.1 Abgrenzung und Annahmen

Das Projekt ist als Simulation konzipiert und bildet bewusst nur den Kern einer Auktionslogik ab. Es gibt keine Benutzerverwaltung mit Login, keine Zahlungsabwicklung und keine externen Schnittstellen. Das System nimmt an, dass alle Akteure innerhalb der Anwendung existieren und dass Auktionen ausschließlich lokal verwaltet werden. Die Persistenz erfolgt über eine Datei, nicht über eine Datenbank oder einen Server.

Diese Abgrenzung ermöglicht eine klare Fokussierung auf die Geschäftslogik der Auktion selbst. Gleichzeitig bleibt genügend Raum für spätere Erweiterungen, beispielsweise durch Integration in eine größere Plattform oder die Anbindung an externe Services.

---

## 3. Fachliches Konzept

### 3.1 Akteure und Rollen

Das System unterscheidet **Auktionatoren** und **Bieter**. Beide Rollen erben von einer gemeinsamen Basisklasse `User`, wodurch eine konsistente Serialisierung und Verarbeitung möglich ist. Diese Rollen sind in der Domäne klar getrennt:

- **Auktionator:** Erstellt Auktionen und wird im Falle eines Verkaufs benachrichtigt.
- **Bieter:** Verfügt über ein Budget und eine Bietstrategie, die seine Kaufentscheidung steuert.

Diese Rollen spiegeln reale Auktionsprozesse wider und sind zentral für die Verständlichkeit der Simulation.

### 3.2 Auktionstyp: Niederländische Auktion

Eine niederländische Auktion startet mit einem **hohen Preis**, der schrittweise reduziert wird. Sobald ein Bieter den aktuellen Preis akzeptiert, ist die Auktion beendet und der Artikel wird verkauft. Fällt der Preis unter den Mindestpreis, wird die Auktion zurückgezogen.

Die Zustände einer Auktion werden als Enum `AuctionStatus` abgebildet:

- `WAITING` – Auktion ist angelegt, wartet auf Start
- `RUNNING` – Auktion läuft, Preis sinkt
- `SOLD` – Auktion verkauft
- `WITHDRAWN` – Auktion beendet ohne Verkauf

Dieses Zustandsmodell ist einfach, aber präzise und unterstützt die spätere Auswertung im Report.

### 3.3 Items und Kategorien

Jede Auktion handelt ein **Item**, das durch Name, Kategorie, Startpreis und Mindestpreis definiert ist. Die Kategorien (`ItemCategory`) sind bewusst generisch gehalten, um die Domain zu strukturieren, ohne sie unnötig zu verengen (z. B. Elektronik, Auto, Buch, Mode, Möbel, Sonstiges).

Die Kategorie ist relevant, da Bieter eine Präferenz haben können. Eine Präferenz beeinflusst die Kaufentscheidung, ohne sie vollständig zu determinieren (Zufallskomponente).

### 3.4 Bietstrategien

Bieter treffen Kaufentscheidungen anhand von Strategien. Die Strategien folgen dem **Strategy Pattern**, wodurch die Entscheidung unabhängig vom Bieter implementiert werden kann. In der aktuellen Version existieren:

- **Aggressive** – kauft eher früh, hohe Wahrscheinlichkeit beim Startpreis
- **Conservative** – wartet länger, akzeptiert niedrigere Preisverhältnisse
- **Random** – zufallsbasiert, abhängig vom Verhältnis zum Startpreis

Strategien sind bewusst einfach, um den Fokus auf die Simulation zu legen und nicht auf komplexe ökonomische Modelle.

### 3.5 Anwendungsfälle und Prozessfluss

Die Anwendung ist auf typische Lern- und Demonstrationsszenarien ausgelegt. Ein Standardablauf umfasst folgende Schritte: Zuerst werden Nutzer angelegt (mindestens ein Auktionator und ein Bieter). Anschließend wird eine Auktion erstellt, bei der ein Item, die Kategorie, Start- und Mindestpreis sowie der Reduktionsschritt definiert werden. Danach kann die Simulation gestartet werden. Während der Simulation werden Statusmeldungen erzeugt, die die Preisentwicklung und Kaufentscheidungen dokumentieren. Abschließend lassen sich Reports abrufen oder der gesamte Zustand als JSON speichern.

Der Prozess ist bewusst linear aufgebaut, um die Komplexität für Einsteiger zu minimieren. Jede Aktion führt direkt zu sichtbaren Änderungen in der Oberfläche, sodass die Simulation nachvollziehbar bleibt. Durch diese klare Prozessführung unterstützt die Anwendung sowohl die Demonstration des Auktionsprinzips als auch die technische Analyse der Implementierung.

---

## 4. Qualitätsziele und Designprinzipien

Die wichtigsten Qualitätsziele im Projekt sind:

1. **Nachvollziehbarkeit:** Die Architektur ist klar strukturiert (UI, Service, Model, Persistence). Dadurch lässt sich der Code leicht verstehen.
2. **Erweiterbarkeit:** Neue Strategien, Kategorien oder zusätzliche Auswertungen können ohne tiefe Eingriffe ergänzt werden.
3. **Robustheit:** Validierungen (z. B. Preise > 0, Budgetprüfung) verhindern inkonsistente Zustände.
4. **Parallelität:** Die Simulation unterstützt mehrere parallele Auktionen und zeigt damit eine realistische Lastverteilung.
5. **Transparenz durch Logging:** Über UI-Logs wird der Simulationverlauf sichtbar gemacht.

Diese Ziele haben die Wahl der technischen Lösungen direkt beeinflusst, insbesondere die Paketstruktur und die Nutzung thread-sicherer Collections.

### 4.1 Funktionale Anforderungen

Aus Sicht der Fachdomäne ergeben sich folgende Kernanforderungen, die das System erfüllen muss:

- Nutzerverwaltung für Auktionatoren und Bieter
- Erstellen von Auktionen mit Start- und Mindestpreisen
- Abbildung einer niederländischen Auktion mit Preisreduktion
- Mehrere Auktionen parallel ausführbar
- Strategiebasierte Kaufentscheidungen der Bieter
- Statistische Auswertung nach Abschluss der Simulation
- Persistenz des vollständigen Systemzustands in einer Datei

Die Umsetzung dieser Anforderungen bildet den Kern des Projekts und dient als Bezugspunkt für alle Designentscheidungen.

### 4.2 Nicht-funktionale Anforderungen

Neben den fachlichen Anforderungen wurden auch nicht-funktionale Ziele berücksichtigt: Die Anwendung soll ohne zusätzliche Infrastruktur (z. B. Datenbank) laufen, auf Standard-Desktop-Systemen starten und stabil auf Parallelität reagieren. Ebenso wurde Wert auf eine einfache Bedienbarkeit gelegt, weshalb die UI bewusst minimalistisch gehalten ist.

---

## 5. Architektur und Paketstruktur

Die Anwendung folgt einer klaren, schichtartigen Architektur. Die wichtigsten Pakete sind:

| Paket | Zweck |
|------|-------|
| `org.auctionsproject.ui` | JavaFX UI (MainView, App, Launcher) |
| `org.auctionsproject.service` | Fachlogik und Simulation (AuctionHouse, SimulationReport) |
| `org.auctionsproject.model` | Domänenmodelle (Auktion, Bieter, Item, Strategien) |
| `org.auctionsproject.persistence` | JSON-Speicher/Ladefunktionen |
| `org.auctionsproject.exception` | Domänenspezifische Fehler |

### 5.1 UI-Schicht

Die UI ist bewusst ohne FXML umgesetzt. Die Oberfläche wird direkt in Java gebaut (`MainView`), was die Komplexität reduziert und den Einstieg vereinfacht. Die UI zeigt drei Tabs:

1. **Nutzer** – Verwaltung von Auktionatoren und Bietern
2. **Auktionen** – Erstellung und Anzeige von Auktionen
3. **Simulation & Report** – Start der Simulation, Speicherung, Laden und Reportausgabe

Dieses Design bietet ein lineares, leicht verständliches Bedienkonzept.

### 5.2 Service-Schicht

Die zentrale Fachlogik liegt in der Klasse `AuctionHouse`. Sie bündelt:

- Nutzerverwaltung
- Auktionserstellung
- Simulation der Auktionen
- Report-Erzeugung
- Provisionserfassung

Das ist eine bewusste Designentscheidung: Eine zentrale Service-Klasse erleichtert das Verständnis und die Kontrolle von Abläufen, da alle wichtigen Aktionen im selben Modul sichtbar sind.

### 5.3 Modell-Schicht

In der Modellschicht werden alle Geschäftsobjekte definiert. Die Modelle sind bewusst schlank gehalten, damit Logik vor allem in `AuctionHouse` und den Strategien verbleibt. Diese Trennung unterstützt Wartbarkeit und erleichtert Tests.

### 5.4 Persistenz-Schicht

Der JSON-Speicherdienst (`JsonStorageService`) kapselt alle Ein-/Ausgabeoperationen. Es wird bewusst keine Datenbank verwendet, um die Anwendung leichtgewichtig und portabel zu halten. Die JSON-Datei enthält einen vollständigen Snapshot des Systems.

### 5.5 Datenfluss und Verantwortlichkeiten

Der Datenfluss beginnt in der UI: Benutzeraktionen (z. B. „Auktion erstellen“) lösen UI-Events aus, die direkt Service-Methoden aufrufen. Der Service (`AuctionHouse`) erstellt oder aktualisiert Domänenobjekte und informiert die UI über Statusänderungen. Die UI reagiert darauf durch Aktualisierung der Tabellen und Logausgaben.

Persistenz ist bewusst getrennt gehalten: Die UI fordert das Speichern oder Laden explizit an, der Service liefert die aktuellen Objekte, und die Persistenzschicht kümmert sich ausschließlich um Serialisierung und Re-Linking. Diese klare Trennung reduziert Seiteneffekte und erleichtert spätere Erweiterungen (z. B. Austausch der Persistenz durch eine Datenbank).

---

## 6. Zentrale Fachlogik: AuctionHouse

Die Klasse `AuctionHouse` ist das Herzstück der Anwendung. Wesentliche Funktionen:

1. **Registrierung von Nutzern** – Nutzer werden in einer Thread-sicheren Liste gehalten (`CopyOnWriteArrayList`).
2. **Erstellen von Auktionen** – Auktionen werden erzeugt, verwaltet und über Event-Logs an die UI gemeldet.
3. **Simulation** – Auktionen laufen parallel in Worker-Threads.
4. **Reporting** – Kennzahlen wie Anzahl verkaufter Artikel oder Gesamtprovision werden berechnet.

### 6.1 Simulation als Producer/Consumer

Die Simulation nutzt ein klassisches **Producer/Consumer-Muster**:

- Ein Producer legt wartende Auktionen in eine Queue.
- Mehrere Worker (Consumer) entnehmen Auktionen und führen sie aus.

Diese Entscheidung erlaubt parallele Verarbeitung und demonstriert den Umgang mit Nebenläufigkeit. Die Anzahl paralleler Auktionen ist konfigurierbar, was eine flexible Skalierung ermöglicht.

### 6.2 Thread-Sicherheit

Durch die Verwendung von `CopyOnWriteArrayList` und `AtomicBoolean` wird sichergestellt, dass paralleler Zugriff konsistent bleibt. Diese Collections sind zwar nicht die performantesten Varianten, bieten aber für die überschaubare Datenmenge eine sehr hohe Sicherheit und einfache Handhabung.

### 6.3 Preisreduktion und Verkauf

Während der Auktion wird der Preis in festen Schritten reduziert. Nach jedem Schritt wird geprüft, ob ein Bieter kaufen möchte. Der Ablauf ist bewusst deterministisch mit Zufallselementen (Strategien), um sowohl reproduzierbar als auch realistisch zu wirken.

### 6.4 Provision und Reporting

Das Auktionshaus erhebt eine feste Provision von **1 %** auf den Verkaufspreis. Diese Entscheidung ist bewusst einfach gehalten, um die Berechnung klar nachvollziehbar zu machen und gleichzeitig eine realistische Kennzahl in den Report aufzunehmen. Die Provision wird in einem `DoubleAdder` gesammelt, damit parallele Auktionen den Wert thread-sicher erhöhen können.

Der Report (`SimulationReport`) aggregiert die wichtigsten Kennzahlen: Gesamtzahl abgeschlossener Auktionen, Verkäufe, Rückzüge, durchschnittliche Bieteranzahl sowie die Zahl eindeutiger Auktionatoren und Bieter. Diese Werte unterstützen die Auswertung und machen das Ergebnis der Simulation transparent.

---

## 7. Domänenmodell im Detail

### 7.1 Auction

Die Klasse `Auction` modelliert die niederländische Auktion. Wichtige Attribute:

- `currentPrice`, `decrementStep` – steuern den Preisverlauf
- `status` – Zustand der Auktion
- `winner`, `soldPrice` – Ergebnis der Auktion

Die Klasse kapselt die Methoden `start()`, `trySellToBidder()` und `decreasePrice()`. Dadurch ist der Ablauf der Auktion in einem klaren, überschaubaren Objekt gebündelt.

### 7.2 Bidder

Bieter besitzen ein Budget, eine Strategie und eine Präferenzkategorie. Entscheidungslogik:

1. Strategy entscheidet grundsätzlich über Akzeptanz.
2. Bei unpassender Kategorie wird die Chance halbiert.

Damit entsteht ein Verhalten, das einfach, aber ausreichend variabel ist.

### 7.3 Item

Items sind bewusst simpel gehalten. Validierungen verhindern, dass Startpreis und Mindestpreis in falscher Relation stehen. Diese Validierungen sind in der Konstruktorlogik verankert und werfen `InvalidPriceException`, um Fehler frühzeitig sichtbar zu machen.

### 7.4 Validierung und Fehlerbehandlung

Die Anwendung setzt auf wenige, aber klare Validierungen. Bei ungültigen Preisen (z. B. Mindestpreis höher als Startpreis) wird eine `InvalidPriceException` geworfen. Bieter, deren Budget nicht ausreicht, lösen eine `InsufficientBudgetException` aus. Diese Fehlerbehandlung ist bewusst früh im Ablauf verankert, um fehlerhafte Zustände gar nicht erst zu erzeugen.

Die Exceptions sind bewusst als Runtime-Exceptions umgesetzt. Dadurch bleibt der Code der UI übersichtlich und die Validierung folgt dem Prinzip „fail fast“: Fehler werden früh signalisiert, statt sich unbemerkt durch den Ablauf zu ziehen.

---

## 8. Persistenz und Serialisierung

### 8.1 JSON Snapshot

Das System speichert seinen Zustand in einer JSON-Datei. Gespeichert werden:

- Nutzerliste
- Auktionen
- Report

Die Klasse `StateSnapshot` fungiert als DTO. Diese Bündelung hat den Vorteil, dass ein einziger JSON-Export den gesamten Zustand abbildet.

### 8.2 Relinking nach dem Laden

Nach dem Laden sind die Objekte zwar vorhanden, aber Beziehungen können „gebrochen“ sein (z. B. Auktionen referenzieren eigene Kopien von Nutzern). Die Methode `relink()` stellt sicher, dass alle Auktionen auf die gleichen Nutzerinstanzen zeigen. Diese Entscheidung verhindert Inkonsistenzen und reduziert Speicherverbrauch.

### 8.3 Struktur der JSON-Datei

Die JSON-Datei besteht aus drei Hauptbereichen: `users`, `auctions` und `report`. Nutzer werden mit einem Typfeld serialisiert, sodass die Deserialisierung zwischen Auktionator und Bieter unterscheiden kann. Auktionen enthalten Referenzen auf Auktionator, Bieter und Gewinner. Durch das anschließende Relinking wird sichergestellt, dass diese Referenzen wieder auf die zentralen Nutzerobjekte zeigen.

Diese Struktur ermöglicht eine vollständige Wiederherstellung des Zustands. Gleichzeitig bleibt die Datei überschaubar und kann bei Bedarf manuell inspiziert oder erweitert werden.

### 8.4 ObjectMapper-Konfiguration

Der ObjectMapper ist mit dem `JavaTimeModule` vorbereitet und nutzt `INDENT_OUTPUT`. Dadurch ist die JSON-Datei nicht nur maschinenlesbar, sondern auch für Menschen verständlich.

---

## 9. Benutzeroberfläche (JavaFX)

Die UI ist bewusst minimalistisch umgesetzt. JavaFX wurde gewählt, weil:

- Es ein fester Bestandteil des Java-Ökosystems ist.
- Es ohne externe Frameworks eine solide Desktop-Oberfläche ermöglicht.
- Es gute Unterstützung für Tabellen und Dialoge bietet.

### 9.1 Aufbau der UI

- **Tab „Nutzer“**: Erstellen von Auktionatoren und Bietern, Anzeige in Tabellen.
- **Tab „Auktionen“**: Erstellen und Anzeigen laufender Auktionen.
- **Tab „Simulation & Report“**: Start der Simulation, Anzeige des Reports und Speichern/Laden von JSON.

### 9.2 UI-Entscheidung: Kein FXML

FXML erlaubt die Trennung von Logik und Layout, erhöht jedoch die Komplexität. Für ein überschaubares Lernprojekt war eine Java-basierte UI einfacher zu pflegen. Die Entscheidung ermöglicht zudem, dass der gesamte UI-Code in einer Klasse liegt, was den Einstieg erleichtert.

### 9.3 Interaktionslogik und UI-Events

Die UI verwendet einen Event-Listener, den das `AuctionHouse` aufrufen kann, um Statusmeldungen zu liefern. Diese Meldungen werden im Log-Feld angezeigt und ermöglichen es, den Ablauf der Auktionen in Echtzeit zu verfolgen. Zusätzlich werden die Tabellen nach relevanten Aktionen aktualisiert, um den Zustand konsistent darzustellen.

Da die Simulation in Hintergrund-Threads läuft, werden UI-Updates über `Platform.runLater()` in den JavaFX-Thread synchronisiert. Dieses Vorgehen verhindert Race-Conditions und stellt sicher, dass UI-Operationen nur im dafür vorgesehenen Thread ausgeführt werden.

---

## 10. Konfiguration und Build

### 10.1 Maven-Projekt

Das Projekt basiert auf Maven. Die wichtigsten Eigenschaften:

- **Java-Version:** 25 (Compiler Source/Target)
- **JavaFX-Version:** 21.0.2
- **JUnit-Version:** 5.10.2

### 10.2 Build-Plugins

- **javafx-maven-plugin** – Start der JavaFX-Anwendung
- **maven-surefire-plugin** – Testausführung
- **maven-javadoc-plugin** – Javadoc-Generierung (private Methoden sichtbar)
- **maven-jar-plugin** / **maven-shade-plugin** – Erstellung eines ausführbaren JARs

Diese Konfiguration ermöglicht es, sowohl eine Entwicklungsumgebung als auch ein ausführbares Paket zu erzeugen.

### 10.3 Startkonfiguration

Der Einstiegspunkt der Anwendung ist `org.auctionsproject.ui.Launcher`. Die Klasse delegiert an die JavaFX-Application-Klasse `App`. Dieser zweistufige Einstieg ist eine übliche Lösung, um JavaFX sauber zu starten und mögliche Classpath-Probleme zu vermeiden.

### 10.4 Abhängigkeiten und Plattformbezug

JavaFX wird als externe Bibliothek eingebunden, da es seit Java 11 nicht mehr Bestandteil der Standarddistribution ist. Neben dem generischen `javafx-controls`-Artifact wird ein plattformspezifisches Artifact mit `classifier` eingebunden. Diese Entscheidung ermöglicht den Betrieb auf dem jeweiligen Zielsystem, erfordert jedoch eine konsistente Java-Version. Für das Projekt wurde Java 25 als Target gewählt, um moderne Sprachfeatures nutzen zu können.

### 10.5 Packaging und Auslieferung

Durch das `maven-jar-plugin` und `maven-shade-plugin` kann eine ausführbare JAR-Datei erzeugt werden, die alle Abhängigkeiten bündelt. Dies ist insbesondere für Demonstrationszwecke hilfreich, da die Anwendung ohne Maven in einer Zielumgebung gestartet werden kann. Die finale JAR-Datei enthält bereits den Main-Class-Eintrag, sodass sie direkt ausführbar ist.

---

## 11. Tests

Die Anwendung enthält JUnit-Tests für Kernkomponenten:

- `AuctionHouseTest` – Überprüft zentrale Logik (z. B. Erstellen, Simulation)
- `AuctionTest` – Prüft Preisverhalten und Statuswechsel
- `JsonStorageService` – Überprüft Speicher/Ladeprozess

Die Tests sind bewusst auf die Kernlogik fokussiert, da UI-Tests für JavaFX deutlich höheren Aufwand bedeuten würden. Dies entspricht dem Ziel einer pragmatischen, lehrorientierten Umsetzung.

### 11.1 Teststrategie und Grenzen

Die Teststrategie priorisiert die Geschäftslogik, weil sie die wichtigsten Fehlerquellen und den größten Wertbeitrag liefert. UI-Tests würden zusätzliche Frameworks und erheblich mehr Setup erfordern. Für ein Portfolioprojekt ist diese Abwägung vertretbar, da die UI weitgehend aus Standardkomponenten besteht und der Fokus auf der Simulation liegt.

Gleichzeitig bleibt die Testbarkeit durch die klare Trennung von UI und Service hoch: Die Kernlogik ist in isolierten Klassen untergebracht und kann unabhängig getestet werden.

---

## 12. Nutzung und Bedienung

### 12.1 Anwendung starten

1. Maven-Build ausführen
2. JavaFX-Anwendung starten (`Launcher`)

### 12.2 Ablauf in der UI

1. Zuerst Nutzer anlegen (Auktionator + Bieter)
2. Auktion erstellen (Artikel, Kategorie, Preise, Schrittgröße)
3. Simulation starten (Parallelität & Tick einstellen)
4. Report ausgeben oder Zustand speichern

Diese Reihenfolge spiegelt die fachliche Logik des Systems wider und ist daher in der UI direkt nachvollziehbar.

### 12.3 Konfigurationsparameter

Die Simulation kann durch zwei Parameter gesteuert werden: **Parallel-Auktionen** und **Tick (ms)**. Die Anzahl paralleler Auktionen bestimmt die Größe des Worker-Pools und damit die gleichzeitige Ausführung. Der Tick definiert den zeitlichen Abstand zwischen Preisreduzierungen. Kleine Werte führen zu schnellen Simulationen, größere Werte ermöglichen eine detailliertere Beobachtung des Ablaufs.

Diese Parameter sind bewusst frei wählbar, damit das Verhalten je nach Szenario skaliert werden kann. Dadurch kann man sowohl schnelle Tests als auch ausführliche Demonstrationen durchführen.

### 12.4 Beispielhafter Ablauf

Ein typischer Ablauf ist: Nutzer anlegen → Auktion erstellen → Simulation starten → Report anzeigen → Zustand speichern. Dieser Ablauf ist in der Oberfläche direkt nachvollziehbar. Besonders hilfreich ist das Log-Feld, das jeden Schritt dokumentiert und so einen „Protokollcharakter“ besitzt.

---

## 13. Designentscheidungen im Überblick

1. **Strategy Pattern** für Bietstrategien → klare Erweiterbarkeit.
2. **Producer/Consumer** für Simulation → parallele Auktionen möglich.
3. **Thread-sichere Collections** → sichere Nebenläufigkeit.
4. **JSON-Snapshot statt Datenbank** → Portabilität und einfache Nutzung.
5. **UI ohne FXML** → geringere Komplexität und schneller Einstieg.
6. **Zentrale Service-Klasse** → klare Fachlogik in einem Modul.

Diese Entscheidungen wurden bewusst getroffen, um das Projekt verständlich, stabil und erweiterbar zu gestalten.

---

## 14. Erweiterungsmöglichkeiten

Das Projekt bietet eine solide Basis, die leicht ausgebaut werden kann. Beispiele:

- Einführung weiterer Auktionstypen (englische Auktion, verdeckte Auktion)
- Komplexere Bietstrategien (Budget-Management, Lernstrategien)
- Persistenz in einer Datenbank statt JSON
- UI-Verbesserungen (Filter, Charts, Auswertungen)
- Netzwerkfähigkeit (mehrere Clients)

Die bestehende Architektur unterstützt solche Erweiterungen, da Fachlogik, UI und Persistenz bereits sauber getrennt sind. Besonders die Strategie-Schnittstelle ermöglicht es, komplexere Entscheidungsmodelle nachzurüsten, ohne die Kernlogik zu verändern. Ebenso kann das Reporting durch zusätzliche Kennzahlen (z. B. Umsatz pro Kategorie oder durchschnittliche Auktiondauer) erweitert werden.

Auch die UI bietet Potenzial: Diagramme oder Timeline-Ansichten könnten den Verlauf einzelner Auktionen visualisieren. Für den Einsatz in Lehrveranstaltungen wäre zudem eine „Schritt-für-Schritt“-Ansicht denkbar, die jeden Tick der Simulation einzeln darstellt und damit den Algorithmus transparent macht.

---

## 15. Fazit

Der **Auktionshaus Simulator** ist ein leichtgewichtiges, aber fachlich vollständiges Modell eines Auktionshauses. Die Kombination aus klarer Domänenlogik, einfacher UI und parallelisierter Simulation macht das Projekt sowohl lehrreich als auch praktisch nutzbar. Durch die bewussten Designentscheidungen ist der Code nachvollziehbar und bildet eine solide Grundlage für spätere Erweiterungen.

Insgesamt erfüllt die Anwendung die Anforderungen an eine kurze, strukturierte Dokumentation und zeigt gleichzeitig, wie sich eine reale Domäne mit überschaubaren Mitteln in einer Java-Anwendung umsetzen lässt.
