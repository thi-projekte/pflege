package de.pflegital.chatbot.whatsapp;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey="whatsapp-graph-api") // Verweist auf Config Key in application.properties
@Path("/{phoneNumberId}/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface WhatsAppClient {

    @POST
    Response sendMessage(
            @PathParam("phoneNumberId") String phoneNumberId,
            @HeaderParam("Authorization") String authorizationHeader,
            WhatsAppService.SendMessageRequest requestBody
    );
}