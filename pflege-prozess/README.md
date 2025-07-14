# 🩺 Pflegital – Pflegeprozess Service

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Quarkus-4695EB?logo=quarkus&logoColor=white" />
  <img src="https://img.shields.io/badge/Kogito-BPMN-blue?logo=apachekafka&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white" />
</p>

---

## 🧭 Überblick

Dieses Modul implementiert die BPMN-gesteuerten Prozesse der **Pflegital-Plattform**. Es automatisiert den **Verhinderungspflegeantrag** und orchestriert alle relevanten Schritte – von der Antragsdatenverarbeitung bis zur Benachrichtigung per E-Mail und WhatsApp.

---

## ⚙️ Architektur

- **Framework:** Quarkus mit Kogito-Extension  
- **Prozesse:** BPMN2 (`pflegeprozess.bpmn2`)  
- **Services:**
  - PDF-Formularerzeugung (`FormFiller`)
  - E-Mail-Versand an Pflegekraft oder Angehörige
  - WhatsApp-Benachrichtigung  
- **Datenmodell:**
  - `Caregiver`, `Carerecipient`, `Period`, `Reason`, `CareType` usw.  
- **Deployment:** Docker (JVM & native Images unterstützt)

---

## 🧾 Datenmodell (Auszug)

```java
Caregiver
Carerecipient
Address
Period
CareType
Reason
ReplacementCare

## BPMN-Prozess

Der zentrale BPMN-Prozess (pflegeprozess.bpmn2) besteht u.a. aus folgenden Schritten:
	•	Formularbefüllung
	•	E-Mail-Versand (Antrag und Pflegekraft)
	•	Entscheidungslogik (z. B. ob Ersatzpflegekraft benötigt wird)
	•	Messaging via WhatsApp
	•	Abschluss oder Fehlerbehandlung

## Projektstruktur 

pflege-prozess/
├── src/
│   ├── main/
│   │   ├── java/org/acme/travels/
│   │   │   ├── Caregiver.java
│   │   │   ├── Carerecipient.java
│   │   │   ├── ...
│   │   ├── resources/
│   │   │   ├── org/acme/travels/pflegeprozess.bpmn2
│   │   │   ├── chatbot/email-template-Pflegekraft.html
│   │   │   └── PflegeAntrag/de015_Antrag_Verhinderungspflege.pdf
├── Dockerfile.jvm
├── Dockerfile.native
└── application.properties

## Entwicklung & Ausführung

### 🔧 Voraussetzungen

- Java 17+  
- Maven 3.9.6+  
- Docker (für containerisierten Betrieb)

### 🛠 Lokale Entwicklung

```bash
mvn clean quarkus:dev

## 📨 Automatisierte Kommunikation
	•	E-Mail-Versand über MailVersand.java, MailVersandAnPflegekraft.java
	•	PDF-Erzeugung via FormFiller.java mit lokalem Antragstemplate (PflegeAntrag/)
	•	WhatsApp-Benachrichtigung durch SendWhatsAppHandler.java 

⸻

## 🛡️ Sicherheit & Zugang
	•	Quarkus + Keycloak für Authentifizierung 
	•	Rollentrennung auf Prozessebene möglich 
