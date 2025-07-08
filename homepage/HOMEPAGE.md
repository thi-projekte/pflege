## 1. Projektübersicht

Pflegital ist eine digitale Plattform, die Personen per WhatsApp direkt mit Pflegeanbietern verbindet. Die Homepage dient als zentrale Anlaufstelle für potenzielle Nutzer und vermittelt die Kernbotschaft des Dienstes: "Pflege – einfach, schnell, digital".

## 2. Projektstruktur

```
pflege/
  ├── .github/
  │   └── workflows/
  │       └── deploy.yml         # CI/CD-Konfiguration
  └── homepage/
      ├── Dockerfile             # Container-Konfiguration
      ├── HOMEPAGE.md            # Diese Dokumentation
      └── pflegital/             # Hauptverzeichnis der Website
          ├── index.html         # Startseite
          ├── knowledge.html     # Hilfeseite/Anleitung
          ├── impressum.html     # Impressum
          ├── datenschutz.html   # Datenschutzerklärung
          ├── scripts.js         # JavaScript-Funktionen
          ├── style.css          # Stylesheet
          └── assets/            # Medien und Grafiken
              ├── logo.svg
              ├── HeroPage.PNG   # Hero-Hintergrundbild
              ├── Unterhaltung 1.png
              ├── Unterhaltung 2.png
              └── Unterhaltung 3.png
```

## 3. Homepage

Die Pflegital-Homepage ist in einem modernen, benutzerfreundlichen Design gestaltet und umfasst mehrere zentrale Sektionen.

### 3.1 Design-Elemente

**Farbschema:**
- Primärfarbe: #F49B62 (Orange)
- Hintergrundfarbe: #FEFCEB (heller Creme-Ton)
- Textfarbe: #5E5043 (dunkles Braun)

**Typografie:**
- Überschriften: 'Playfair Display', serif
- Fließtext: 'Work Sans', sans-serif

**Responsive Design:**
- Die Seite passt sich an verschiedene Bildschirmgrößen an
- Mobile-First-Ansatz für optimale Darstellung auf Smartphones

**Favicon:**
- Ein passendes Favicon wurde hinzugefügt, um die Website im Browser besser erkennbar zu machen.

### 3.2 Hauptkomponenten

#### Header
- Logo (SVG-Format), das auf die Startseite (`index.html`) verlinkt ist
- Responsive Navigationmenü mit Links zu:
  - Start
  - Features
  - Kontakt
- Hamburger-Menü für die mobile Ansicht

#### Hero-Sektion
- Hauptüberschrift: "Pflege – einfach, schnell, digital"
- Untertext: "Pflegital verbindet Sie per WhatsApp direkt mit Pflegeanbietern, schnell und einfach."
- Call-to-Action-Button: "So gehts"
- Hintergrundbild: HeroPage.PNG

#### Features-Sektion
Zeigt die Hauptfunktionen von Pflegital:
- WhatsApp Chatbot (0815 69420)
- Digitale Formulare (Einfach & schnell ausgefüllt)
- Pflege-Dokumentation (Alles strukturiert & nachvollziehbar)
- Animationen beim Scrollen mit AOS (Animate On Scroll)

#### Demo-Chat
Zeigt einen beispielhaften Chatverlauf, um die Funktionsweise von Pflegital zu demonstrieren:
1. Nutzer: "Hallo, ich suche Hilfe für meine Mutter."
2. Pflegital: "Gerne, wie kann Pflegital Sie unterstützen?"
3. Nutzer: "Sie braucht Unterstützung im Alltag."
4. Pflegital: "Verstanden! Ich schicke Ihnen passende Pflegeangebote per WhatsApp."

#### Bildergalerie
- Zeigt Screenshots der WhatsApp-Interaktionen
- Implementiert als Slider mit:
  - Hauptansicht für das aktuell ausgewählte Bild
  - Thumbnail-Navigation für schnellen Zugriff auf andere Bilder
  - Punktnavigation zur Visualisierung der Position im Slider
  - Vor/Zurück-Schaltflächen

#### Kontaktformular
- Überschrift: "Bereit für die Zukunft der Pflege?"
- Eingabefelder für Name und Nachricht
- Button zum Versenden der Nachricht über WhatsApp
- JavaScript-Funktion `sendToWhatsApp()` zur Verarbeitung der Anfrage

#### Knowledge-Seite
- Bietet eine ausführliche Anleitung zur Nutzung von Pflegital
- Erklärt den Prozess der Verhinderungspflege in 4 einfachen Schritten
- Listet die Vorteile von Pflegital auf
- Zugänglich über den "So gehts"-Button auf der Startseite

#### Back-To-Top-Button
- Ein Back-To-Top-Button wurde auf allen HTML-Seiten (`index.html`, `knowledge.html`, `impressum.html`, `datenschutz.html`) hinzugefügt, um die Benutzerfreundlichkeit zu verbessern.

#### Footer
- Kontaktinformationen
- E-Mail-Adresse: kontakt@pflegital.de
- Links zum Impressum und zur Datenschutzerklärung
- Hintergrund im Farbverlauf von #F49B62

### 3.3 JavaScript-Funktionalitäten

Die Datei scripts.js enthält verschiedene Funktionen:

1. **AOS-Initialisierung:**
   - Animiert Elemente beim Scrollen
   - Konfiguriert mit 800ms Dauer

2. **Galerie-Navigation:**
   - Unterstützt horizontales Scrollen durch die Bildergalerie
   - Implementiert Punktnavigation und Thumbnail-Ansicht

3. **Mobile-Menü:**
   - Toggle-Funktion für das Hamburger-Menü auf mobilen Geräten

4. **WhatsApp-Integration:**
   - `sendToWhatsApp()`-Funktion zur Weiterleitung von Nutzernachrichten

5. **Back-To-Top-Button:**
   - Funktionalität, um den Benutzer mit einem Klick zurück zum Seitenanfang zu bringen

6. **Chat-Animation:**
   - Animiert den Demo-Chat-Bereich mit regelmäßiger Wiederholung
   - Stellt eine realistische Chat-Interaktion dar

## 4. CI/CD-Pipeline

Die CI/CD-Pipeline wird über GitHub Actions mit der Datei deploy.yml konfiguriert.

### 4.1 Trigger

Die Pipeline wird automatisch ausgelöst bei Änderungen an:
- Dateien im Verzeichnis `homepage/pflegital/`
- Der Dockerfile

```yml
on:
  push:
    paths:
      - 'homepage/pflegital/**'
      - 'homepage/Dockerfile'
```

### 4.2 Pipeline-Schritte

1. **Code auschecken:**
   - Lädt den aktuellsten Code aus dem Repository

2. **Bei der GitHub Container Registry anmelden:**
   - Verwendet die Docker-Login-Action
   - Authentifiziert sich mit den GitHub-Anmeldedaten

3. **Docker-Image bauen und pushen:**
   - Erstellt ein Container-Image basierend auf der Dockerfile
   - Tagging: `ghcr.io/thi-projekte/pflege/homepage:latest`
   - Lädt das Image in die GitHub Container Registry hoch

4. **Redeploy des Containers über Portainer API:**
   - Ermittelt die Container-ID über Labels
   - Stoppt und entfernt den alten Container
   - Erstellt einen neuen Container mit dem aktuellen Image
   - Startet den neuen Container

```yml
  - name: Redeploy Container via Portainer API
    env:
      API_URL: https://winfprojekt.de:9443/api
      API_KEY: ${{ secrets.PORTAINER_API_KEY }}
      ENDPOINT_ID: 1
      PROJECT_LABEL: pflegital
      SERVICE_LABEL: homepage
    run: |
      # 1) Container-ID ermitteln über Labels
      # 2) Pull latest image
      # 3) Stop & remove old container
      # 4) Create new container with labels
      # 5) Start new container
```

### 4.3 Container-Konfiguration

Die Dockerfile definiert, wie der Container für die Homepage gebaut wird:

```dockerfile
FROM nginx:alpine
COPY pflegital /usr/share/nginx/html
```

Diese Konfiguration:
1. Verwendet NGINX mit Alpine Linux als Basis (leichtgewichtige Option)
2. Kopiert alle Dateien aus dem `pflegital`-Verzeichnis in das Webroot-Verzeichnis von NGINX

## 5. Wartung und Weiterentwicklung

### 5.1 Änderungen an der Website

1. Bearbeiten Sie die HTML-, CSS- oder JavaScript-Dateien im `homepage/pflegital/`-Verzeichnis
2. Commit und Push der Änderungen lösen automatisch die Pipeline aus
3. Das neue Docker-Image wird gebaut und in die Registry hochgeladen
4. Der Container wird automatisch über die Portainer API neu erstellt und gestartet

### 5.2 Deployment-Prozess

Die Bereitstellung des aktualisierten Images erfolgt vollautomatisch nach dem Push in die Registry durch die Integration der Portainer API in der GitHub Actions Pipeline.

### 5.3 Sicherheitsaspekte

- API-Schlüssel und Zugangsdaten werden als GitHub Secrets gespeichert
- Die Berechtigungen in der Pipeline sind auf das Notwendige beschränkt:
  ```yml
  permissions:
    contents: read
    packages: write
  ```

## 6. Quellenverweise

- **Website-Frameworks:**
  - AOS: https://michalsnik.github.io/aos/
  - Google Fonts: https://fonts.google.com/

- **Container-Technologien:**
  - NGINX: https://nginx.org/
  - Docker: https://www.docker.com/

- **CI/CD-Tools:**
  - GitHub Actions: https://github.com/features/actions
  - GitHub Container Registry: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry
  - Portainer API: https://docs.portainer.io/api/docs