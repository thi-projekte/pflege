# 🧠 Pflegital AI-Chatbot

Ein intelligenter Chatbot zur Unterstützung bei der Beantragung der **Verhinderungspflege**. Der Bot führt Nutzer:innen Schritt für Schritt durch das Formular und verwendet validierende Tools, um korrekte Eingaben sicherzustellen.

---

## 📚 Inhaltsverzeichnis

- [🚀 Schnellstart](#-schnellstart)
- [🔐 API-Endpunkte](#-api-endpunkte)
- [🎨 Code-Formatierung](#-code-formatierung)
- [✅ Tests & Testabdeckung](#-tests--testabdeckung)
- [🛠️ Tools-Verzeichnis](#-tools-verzeichnis)
- [🧰 Verwendete Technologien](#-verwendete-technologien)

## 🚀 Schnellstart

```bash
# 1. Projektverzeichnis wechseln
cd chatbot

# 2. Umgebungsvariablen konfigurieren (.env Datei)
.env Datei konfigurieren

# 3. Projekt bauen
mvn clean install

# 4. Anwendung im Dev-Modus starten
mvn quarkus:dev
# oder mit Quarkus CLI
quarkus dev
```

👉 Drücke `d` im Terminal, um die **Quarkus Dev UI** zu öffnen:  
[http://localhost:8084](http://localhost:8084)

---

## 🔐 API-Endpunkte


| Methode | Pfad        | Beschreibung                        |
|--------|-------------|-------------------------------------|
| POST   | `/chat/reply` | Chat-Nachricht senden  (nur für Testzwecke)             |
| POST   | `/chat/start` | Chat-Session initialisieren (nur für Testzwecke)         |
| POST   | `/process-webhook` | kann vom  BPMN-Prozess aufgerufen werden (funktioniert nur mit Whatsappnummer )         |
| POSt   | `/webhook` | Für Whatsappnachrichten       |

🧪 Die APIs können mit Swagger UI getestet werden

---

## 🎨 Code-Formatierung

```bash
mvn formatter:format
```

---

## ✅ Tests & Testabdeckung
Die Tests befinden sich hier:
```
chatbot/src/test
```
Testabdeckung mit JaCoCo prüfen:
```bash
mvn verify
```

➡️ Öffne anschließend den Bericht unter:
```
/target/jacoco-report
```

---

## 🛠️ Tools-Verzeichnis

Die Validierungslogik befindet sich hier:
```
chatbot/src/main/java/de/pflegital/chatbot/tools
```

---

## 🧰 Verwendete Technologien

![Java](https://img.shields.io/badge/Java-Language-007396?style=for-the-badge&logo=java&logoColor=white)
![Quarkus](https://img.shields.io/badge/Quarkus-Framework-red?style=for-the-badge&logo=quarkus)
![LangChain4j](https://img.shields.io/badge/LangChain4j-LLM-green?style=for-the-badge)
![Keycloak](https://img.shields.io/badge/Keycloak-Auth-0066CC?style=for-the-badge&logo=keycloak&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build--Tool-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-Test--Coverage-brightgreen?style=for-the-badge)
