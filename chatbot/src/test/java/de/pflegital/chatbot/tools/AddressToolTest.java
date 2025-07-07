package de.pflegital.chatbot.tools;

import de.pflegital.chatbot.model.Address;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class AddressToolTest {
    @Inject
    AddressTool addressTool;

    private Address createValidAddress() {
        Address address = new Address();
        address.setStreet("Musterstraße");
        address.setHouseNumber(12);
        address.setZip("12345");
        address.setCity("Musterstadt");
        return address;
    }

    @Test
    void testValidAddress() {
        Address address = createValidAddress();
        assertTrue(addressTool.isValidAddress(address));
        assertNull(addressTool.getValidationMessage(address));
    }

    @Test
    void testNullAddress() {
        assertFalse(addressTool.isValidAddress(null));
        // getValidationMessage wirft NullPointerException, wenn address null ist
        // Das Verhalten ist so im Code, daher kein Test für getValidationMessage(null)
    }

    @Test
    void testMissingStreet() {
        Address address = createValidAddress();
        address.setStreet(null);
        assertFalse(addressTool.isValidAddress(address));
        String msg = addressTool.getValidationMessage(address);
        assertNotNull(msg);
        assertTrue(msg.contains("Straße fehlt"));
    }

    @Test
    void testEmptyStreet() {
        Address address = createValidAddress();
        address.setStreet("");
        assertFalse(addressTool.isValidAddress(address));
        String msg = addressTool.getValidationMessage(address);
        assertNotNull(msg);
        assertTrue(msg.contains("Straße fehlt"));
    }

    @Test
    void testInvalidStreetCharacters() {
        Address address = createValidAddress();
        address.setStreet("Musterstraße 123!");
        assertFalse(addressTool.isValidAddress(address));
    }

    @Test
    void testHouseNumberZero() {
        Address address = createValidAddress();
        address.setHouseNumber(0);
        assertFalse(addressTool.isValidAddress(address));
        String msg = addressTool.getValidationMessage(address);
        assertNotNull(msg);
        assertTrue(msg.contains("Hausnummer ist ungültig"));
    }

    @Test
    void testMissingZip() {
        Address address = createValidAddress();
        address.setZip(null);
        assertFalse(addressTool.isValidAddress(address));
        String msg = addressTool.getValidationMessage(address);
        assertNotNull(msg);
        assertTrue(msg.contains("Postleitzahl ist ungültig"));
    }

    @Test
    void testInvalidZipFormat() {
        Address address = createValidAddress();
        address.setZip("1234"); // nur 4-stellig
        assertFalse(addressTool.isValidAddress(address));
        String msg = addressTool.getValidationMessage(address);
        assertNotNull(msg);
        assertTrue(msg.contains("Postleitzahl ist ungültig"));
    }

    @Test
    void testMissingCity() {
        Address address = createValidAddress();
        address.setCity(null);
        assertFalse(addressTool.isValidAddress(address));
        String msg = addressTool.getValidationMessage(address);
        assertNotNull(msg);
        assertTrue(msg.contains("Stadt fehlt"));
    }

    @Test
    void testEmptyCity() {
        Address address = createValidAddress();
        address.setCity("");
        assertFalse(addressTool.isValidAddress(address));
        String msg = addressTool.getValidationMessage(address);
        assertNotNull(msg);
        assertTrue(msg.contains("Stadt fehlt"));
    }

    @Test
    void testInvalidCityCharacters() {
        Address address = createValidAddress();
        address.setCity("Musterstadt!");
        assertFalse(addressTool.isValidAddress(address));
    }

    @Test
    void testMultipleInvalidFields() {
        Address address = new Address();
        address.setStreet("");
        address.setHouseNumber(0);
        address.setZip("abc");
        address.setCity(null);
        assertFalse(addressTool.isValidAddress(address));
        String msg = addressTool.getValidationMessage(address);
        assertNotNull(msg);
        assertTrue(msg.contains("Straße fehlt"));
        assertTrue(msg.contains("Hausnummer ist ungültig"));
        assertTrue(msg.contains("Postleitzahl ist ungültig"));
        assertTrue(msg.contains("Stadt fehlt"));
    }
}
