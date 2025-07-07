# Pflegital – einfach, schnell, digital

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?logo=java&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Quarkus-4695EB?logo=quarkus&logoColor=white" alt="Quarkus" />
  <img src="https://img.shields.io/badge/React-61DAFB?logo=react&logoColor=black" alt="React" />
  <img src="https://img.shields.io/badge/Vite-646CFF?logo=vite&logoColor=white" alt="Vite" />
  <img src="https://img.shields.io/badge/Tailwind_CSS-06B6D4?logo=tailwindcss&logoColor=white" alt="Tailwind CSS" />
  <img src="https://img.shields.io/badge/BPMN-0052CC?logo=apachekafka&logoColor=white" alt="BPMN" />
  <img src="https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Keycloak-0066FF?logo=keycloak&logoColor=white" alt="Keycloak" />
  <img src="https://img.shields.io/badge/NGINX-009639?logo=nginx&logoColor=white" alt="NGINX" />
</p>

---

## Überblick

**Pflegital** ist eine prozess- und KI-gesteuerte Plattform zur digitalen Beantragung von Pflegeleistungen. Sie unterstützt Pflegebedürftige und Angehörige dabei, die Verhinderungspflege einfach, transparent und intuitiv zu beantragen.  
Die Plattform kombiniert moderne Webtechnologien, KI-gestützte Beratung via Whatsapp und BPMN-basierte Prozessautomatisierung.

---

## Architektur

- **Frontend:**  
  Moderne Single-Page-App mit [React](https://react.dev/), [Vite](https://vitejs.dev/) und [Tailwind CSS](https://tailwindcss.com/).
- **Backend:**  
  [Quarkus](https://quarkus.io/) (Java) REST-API, KI-Integration mit OpenAI Structured API, WhatsApp-Anbindung.
- **Prozess-Engine:**  
  [Kogito](https://kogito.kie.org/) mit BPMN2-Prozessen.
- **Authentifizierung:**  
  [Keycloak](https://www.keycloak.org/) für OIDC-basierte Authentifizierung.
- **Containerisierung:**  
  [Docker](https://www.docker.com/) & Docker Compose.
- **Reverse Proxy:**  
  [NGINX](https://www.nginx.com/) als zentraler Einstiegspunkt.

---

## Projektstruktur

```
projekt_semester6/
├── chatbot/           # Quarkus Backend (Java)
├── frontend-sozi/     # React Frontend (JSX, Vite, Tailwind)
├── pflege-prozess/    # Kogito BPMN Prozesse (Java, BPMN2)
├── homepage/          # Statische Landingpage
```

---

## Setup & Entwicklung

### Voraussetzungen

- Docker & Docker Compose
- Node.js & npm
- Java JDK 17+
- Maven

### Schnellstart

```bash
# Backend (Quarkus)
cd chatbot
./mvnw quarkus:dev

# Frontend (React)
cd ../frontend-sozi
npm install
npm run dev

# Prozess-Engine (Kogito)
cd ../pflege-prozess
./mvnw quarkus:dev


---

## Tech-Stack

| Bereich         | Technologie         |
|-----------------|--------------------|
| Frontend        | ![React](https://img.shields.io/badge/React-61DAFB?logo=react&logoColor=black) ![Vite](https://img.shields.io/badge/Vite-646CFF?logo=vite&logoColor=white) ![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-06B6D4?logo=tailwindcss&logoColor=white) |
| Backend         | ![Java](https://img.shields.io/badge/Java-ED8B00?logo=java&logoColor=white) ![Quarkus](https://img.shields.io/badge/Quarkus-4695EB?logo=quarkus&logoColor=white) |
| Prozess-Engine  | ![Kogito](https://img.shields.io/badge/BPMN-0052CC?logo=apachekafka&logoColor=white) |
| Authentifizierung | ![Keycloak](https://img.shields.io/badge/Keycloak-0066FF?logo=keycloak&logoColor=white) |
| Containerisierung | ![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white) |
| Reverse Proxy   | ![NGINX](https://img.shields.io/badge/NGINX-009639?logo=nginx&logoColor=white) |


## Lizenz

MIT License

---

> **Pflegital** – einfach, schnell, digital




