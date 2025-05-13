# Pflege – Digitale Pflegeplattform

Willkommen zum Projekt **Pflege**, einer innovativen Plattform, die Pflege digitalisiert und vereinfacht. Dieses Projekt wurde von den Studierenden der THI im Sommersemester 2025 entwickelt.

## Projektübersicht

Pflege ist eine Webanwendung, die Pflegebedürftige und Pflegeanbieter miteinander verbindet. Die Plattform bietet folgende Kernfunktionen:

- **WhatsApp-Integration**: Ein Chatbot, der Pflegeanfragen entgegennimmt und passende Angebote vermittelt.
- **Digitale Formulare**: Einfache und schnelle Pflege-Dokumentation.
- **Produktgalerie**: Visuelle Darstellung von Pflegeangeboten und -lösungen.

## Projektstruktur

Das Projekt hat folgende Verzeichnisstruktur:

```
pflege/
├── api_key.txt          # API-Schlüssel für externe Dienste
├── README.md            # Projektbeschreibung
├── .github/
│   └── workflows/
│       └── deploy.yml   # CI/CD-Workflow für die Bereitstellung
├── homepage/
│   ├── Dockerfile       # Docker-Konfiguration für die Anwendung
│   └── pflegital/
│       ├── datenschutz.html  # Datenschutzerklärung
│       ├── impressum.html    # Impressum
│       ├── index.html        # Hauptseite der Anwendung
│       ├── scripts.js        # JavaScript-Funktionen
│       ├── style.css         # CSS-Styles
│       └── assets/           # Medienressourcen (Bilder, Logos)
│           ├── image.png
│           ├── logo.svg
│           ├── Unterhaltung 1.png
│           ├── Unterhaltung 2.png
│           └── Unterhaltung 3.png
```

## Aktueller Stand

### Implementierte Features

1. **Chatbot-Demo**: 
   - Beispielhafte Chat-Interaktionen in der Sektion `#demo-chat` der `index.html`.
   - Vermittlung von Pflegeangeboten über WhatsApp.

2. **Produktgalerie**:
   - Slider-Komponente mit Bildern und Titeln (`pdp-gallery` in `index.html`).
   - Navigationselemente und visuelle Indikatoren (`style.css`).

3. **Kontaktformular**:
   - Formular zur Kontaktaufnahme mit Pflegeanbietern (`#kontakt` in `index.html`).
   - Integration eines Buttons für WhatsApp-Nachrichten.

4. **Responsive Design**:
   - Optimierte Darstellung für verschiedene Bildschirmgrößen.
   - Verwendung von CSS-Eigenschaften wie `object-fit` und `scrollbar-width`.

### Offene Punkte

- **Backend-Integration**: Aktuell ist die Anwendung rein statisch. Eine Anbindung an ein Backend zur Verarbeitung von Anfragen ist noch erforderlich.
- **Datenschutzprüfung**: Die Datenschutzerklärung (`datenschutz.html`) muss aktualisiert werden.
- **CI/CD**: Automatisierte Containererstellung muss noch getestet werden.