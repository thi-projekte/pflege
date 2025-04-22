# AI-Chatbot PoC

### Applikation starten:
    1. cd chatbot 
    2. .env Datei konfigurieren
        QUARKUS_LANGCHAIN4J_OPENAI_API_KEY=YOUR_API_KEY
        QUARKUS_OIDC_AUTH-SERVER-URL=https://...
        QUARKUS_OIDC_CREDENTIALS_SECRET=YOUR_CREDENTIALS
        QUARKUS_OIDC_CLIENT_ID=OIDC_CLIENT
    3. mvn clean install
    4. mvn quarkus:dev oder quarkus dev
    5. "d" im Terminal drücken um Quarkus Dev UI zu öffnen, REST-Endpoints unter http://localhost:8080.

### Endpunkte, mit OAuth2 / OIDC geschützt
    /chat/reply   -    POST (consumes: application/json) (produces:application/json)
    /chat/start   -    POST (consumes: application/json) (produces:application/json)

### Token und in Postman testen
    1. Token generieren
            curl -X POST https://keycloak.winfprojekt.de/realms/Pflege/protocol/openid-connect/token \
            -H "Content-Type: application/x-www-form-urlencoded" \
            -d "grant_type=client_credentials" \
            -d "client_id=CLIENT_ID“ \
            -d "client_secret=CLIENT_SECRET"
        
            (CLIENT_ID und CLIENT_SECRET ersetzen)

    2. Endpunkte in Postman testen, unter Tab Authentication mit Auth Type <Bearer Token> 