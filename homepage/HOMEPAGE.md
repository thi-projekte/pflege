## 1. Projektübersicht

Pflegital ist eine digitale Plattform, die Personen per WhatsApp direkt mit Pflegeanbietern verbindet. Die Homepage dient als zentrale Anlaufstelle für potenzielle Nutzer und vermittelt die Kernbotschaft des Dienstes: "Pflege – einfach, schnell, digital".

## 2. Projektstruktur

```
pflege/
  ├── .github/
  │   └── workflows/
  │       └── deploy.yml         # CI/CD-Konfiguration
  └── homepage/
      ├── api_key.txt            # API-Schlüssel (nicht in Version Control!)
      ├── Dockerfile             # Container-Konfiguration
      ├── HOMEPAGE.md            # Diese Dokumentation
      └── pflegital/             # Hauptverzeichnis der Website
          ├── index.html         # Startseite
          ├── impressum.html     # Impressum
          ├── datenschutz.html   # Datenschutzerklärung
          ├── scripts.js         # JavaScript-Funktionen
          ├── style.css          # Stylesheet
          └── assets/            # Medien und Grafiken
              ├── image.png
              ├── logo.svg
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

### 3.2 Hauptkomponenten

#### Header
- Logo (SVG-Format)
- Responsive Navigationmenü mit Links zu:
  - Start
  - Features
  - Kontakt
- Hamburger-Menü für die mobile Ansicht

#### Hero-Sektion
- Hauptüberschrift: "Pflege – einfach, schnell, digital"
- Untertext: "Pflegital verbindet Sie per WhatsApp direkt mit Pflegeanbietern, schnell und einfach."
- Call-to-Action-Button: "Jetzt starten"
- Hintergrundbild aus einer Unsplash-Quelle

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

3. **Docker-Image bauen:**
   - Erstellt ein Container-Image basierend auf der Dockerfile
   - Tagging: `ghcr.io/thi-projekte/pflege/homepage:latest`

4. **Image pushen:**
   - Lädt das erstellte Image in die GitHub Container Registry hoch

```yml
steps:
  - name: Checkout code
    uses: actions/checkout@v4

  - name: Log in to GitHub Container Registry
    uses: docker/login-action@v3
    with:
      registry: ghcr.io
      username: ${{ github.actor }}
      password: ${{ secrets.GITHUB_TOKEN }}

  - name: Build Docker image
    run: |
      IMAGE_NAME=ghcr.io/thi-projekte/pflege/homepage:latest
      docker build -f homepage/Dockerfile -t $IMAGE_NAME homepage
      echo "IMAGE_NAME=$IMAGE_NAME" >> $GITHUB_ENV

  - name: Push Docker image
    run: |
      docker push $IMAGE_NAME
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

### 5.2 Deployment-Prozess

Die Bereitstellung des aktualisierten Images erfolgt automatisch nach dem Push in die Registry. Weitere Details zur Portainer-API-Integration finden sich im api_key.txt-File, welches **nicht** im Version-Control gehalten werden sollte.

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