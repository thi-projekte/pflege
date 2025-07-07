package de.pflegital.chatbot.tools;

import de.pflegital.chatbot.model.Address;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AddressTool {
    private static final Logger LOG = LoggerFactory.getLogger(AddressTool.class);

    @Tool("Validiert ob eine Adresse gültig ist oder nicht. Die Adresse muss Straße, Hausnummer, Postleitzahl und Stadt enthalten.")
    public boolean isValidAddress(Address address) {
        if (address == null) {
            LOG.error("Die Adresse ist leer!");
            return false;
        }

        if (!address.isStreetValid()) {
            LOG.error("Die Straße ist ungültig!");
            return false;
        }
        if (!address.isHouseNumberValid()) {
            LOG.error("Die Hausnummer ist ungültig!");
            return false;
        }
        if (!address.isZipValid()) {
            LOG.error("Die Postleitzahl ist ungültig!");
            return false;
        }
        if (!address.isCityValid()) {
            LOG.error("Die Stadt ist ungültig!");
            return false;
        }

        // Check if street name contains only valid characters
        if (!address.getStreet().matches("^[a-zA-ZäöüÄÖÜß\\s\\-]+$")) {
            LOG.error("Die Straße enthält ungültige Zeichen!");
            return false;
        }

        // Check if city name contains only valid characters
        if (!address.getCity().matches("^[a-zA-ZäöüÄÖÜß\\s\\-]+$")) {
            LOG.error("Die Stadt enthält ungültige Zeichen!");
            return false;
        }

        LOG.info("Die Adresse ist gültig: {} {} {} {}", address.getStreet(), address.getHouseNumber(), address.getZip(),
                address.getCity());
        return true;
    }

    public String getValidationMessage(Address address) {
        if (!isValidAddress(address)) {
            StringBuilder message = new StringBuilder("Die Adresse ist ungültig. Bitte überprüfen Sie:");

            if (!address.isStreetValid()) {
                message.append("\n- Straße fehlt");
            }
            if (!address.isHouseNumberValid()) {
                message.append("\n- Hausnummer ist ungültig");
            }
            if (!address.isZipValid()) {
                message.append("\n- Postleitzahl ist ungültig");
            }
            if (!address.isCityValid()) {
                message.append("\n- Stadt fehlt");
            }

            return message.toString();
        }
        return null;
    }
}
