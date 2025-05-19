# Pflegital AI-Chatbot

### Applikation starten:
    1. cd chatbot 
    2. .env Datei konfigurieren
        QUARKUS_LANGCHAIN4J_OPENAI_API_KEY=YOUR_API_KEY
        X_QUARKUS_OIDC_AUTH-SERVER-URL=https://...
        X_QUARKUS_OIDC_CREDENTIALS_SECRET=YOUR_CREDENTIALS
        X_QUARKUS_OIDC_CLIENT_ID=OIDC_CLIENT
    3. mvn clean install
    4. mvn quarkus:dev oder quarkus dev
    5. "d" im Terminal drücken um Quarkus Dev UI zu öffnen, REST-Endpoints unter http://localhost:8080.

### Endpunkte, mit OAuth2 / OIDC geschützt (in Prod)
    /chat/reply   -    POST (consumes: application/json) (produces:application/json)
    /chat/start   -    POST (consumes: application/json) (produces:application/json)

### Endpunkte in Swagger UI testen
    um 401 Not Authorized Fehler zu vermeiden, muss man in der DevUI auf Keycloak Provider drücken, sich mit dem Testaccount einloggen, und dann anschließend die Swagger UI über die DevUI aufrufen

### Formatierung
```bash
mvn formatter:format
```