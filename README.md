#Pflegital – Dein digitaler Pflegeantragsbegleiter

Pflegital ist eine prozessgesteuerte Anwendung zur digitalen Beantragung von Pflegeleistungen, die Pflegebedürftige und Angehörige befähigt, Leistungen wie die Verhinderungspflege einfach, transparent und intuitiv zu beantragen. Durch den Einsatz von BPMN-gestützten Prozessen mit Kogito wird eine durchgängige Orchestrierung aller fachlichen Abläufe ermöglicht. Unterstützt durch KI-gestützte Beratung und abgestimmt auf individuelle Bedürfnisse, schafft Pflegital eine zentrale, medienbruchfreie Lösung für den digitalen Wandel in der Pflege.

#Architektur
Pflege-Plattform Architekturübersicht (Microservice-basiert)

Kernkomponenten der Architektur:

1. NGINX Proxy
Dient als (Reverse) Proxy. Leitet HTTPS-Anfragen der Benutzer an die entsprechenden internen Services weiter.
2. AI Chat Service
Zentrale Komponente für die Chat-Interaktion. Dieser Service verarbeitet REST-Anfragen und kommuniziert mit weiteren Backend-Systemen (z. B. Kogito, OpenAPI, WhatsApp API).
3. WhatsApp Business API
Externer Kommunikationskanal zur Nutzerinteraktion über WhatsApp. Kommuniziert per REST mit dem AI Chat Service.
4. OpenAPI
Externer API-Dienst, über den der AI Chat Service zusätzliche Daten oder Funktionen integrieren kann.
5. Kogito
Prozessdienst, der über REST mit dem AI Chat Service kommuniziert. Dient der Prozesssteuerung.
6. Keycloak
Zuständig für Authentifizierung und Autorisierung via OIDC (OpenID Connect). Steuert den Zugriff auf interne Services.
7. Portainer
Verwaltungsoberfläche für Container-basierte Infrastruktur. Übernimmt das Management der Deployment-Umgebung.

#Setup 

Voraussetzungen:

Docker & Docker Compose
Node.js & npm 
Java JDK & Maven
Git



#Tech-Stack

Containerisierung: Docker, Docker Compose
Webserver/Reverse Proxy: NGINX
Frontend: React, Vite, JavaScript, CSS 
Backend (Data-Service): Quarkus (Java), REST-APIs
Prozess-Engine: Kogito (BPMN)
Authentifizierung: Keycloak
CI/CD: GitHub Actions
Deployment-Management (Server): Portainer




