# AI-Chatbot PoC

### Applikation starten:
    1. git clone https://github.com/Schubijaner/quarkus-AI-ChatBot.git
    2. API-Key konfigurieren: im Terminal export QUARKUS_LANGCHAIN4J_OPENAI_API_KEY="YOUR_API_KEY"
    3. ./mvnw clean install
    4. /mvnw quarkus:dev oder quarkus dev
    5. "d" im Terminal drücken um Quarkus Dev UI zu öffnen, du erreichst die REST-Endpoints unter http://localhost:8080.

### Endpunkte:
    /chat/reply   -    POST (consumes: application/json) (produces:application/json)
    /chat/start   -    POST (consumes: application/json) (produces:application/json)

### Beispiel-Requests:
    /chat/start
<img width="821" alt="image" src="https://github.com/user-attachments/assets/3bfc5230-65ad-4e40-a139-ab5b4076be8b" /> <br />


    /chat/reply
<img width="807" alt="image" src="https://github.com/user-attachments/assets/41fddd47-21ca-4040-b757-e2b03e687519" />
