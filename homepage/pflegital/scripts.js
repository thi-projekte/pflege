// File: js/main.js
// ----------------------------------------
// Initialisiert AOS, steuert die Galerie, das Mobile-Menü
// und sendet Nachrichten per WhatsApp
// ----------------------------------------

// 1) AOS initialisieren
document.addEventListener("DOMContentLoaded", () => {
  AOS.init({
    duration: 800,
    once: true
  });

  // 2) Galerie-Navigation
  const track = document.getElementById('galleryTrack');
  const prev  = document.querySelector('.gallery-nav.prev');
  const next  = document.querySelector('.gallery-nav.next');

  if (track && prev && next) {
    prev.addEventListener('click', () => {
      track.scrollBy({ left: -track.clientWidth, behavior: 'smooth' });
    });
    next.addEventListener('click', () => {
      track.scrollBy({ left: track.clientWidth, behavior: 'smooth' });
    });
  }

  // 3) Mobile Menü umschalten
  const menuToggle = document.querySelector('.menu-toggle');
  const nav        = document.querySelector('nav');

  if (menuToggle && nav) {
    menuToggle.addEventListener('click', () => {
      nav.classList.toggle('active');
    });
  }
});

// 4) Funktion zum Versenden von Nachrichten per WhatsApp
function sendToWhatsApp() {
  // Eingabefelder holen
  const nameInput    = document.getElementById('name');
  const messageInput = document.getElementById('message');

  const name    = nameInput.value.trim();
  const message = messageInput.value.trim();

  // Validierung
  if (message === '') {
    alert('Bitte gib eine Nachricht ein.');
    return;
  }

  const phoneNumber = '15556422777';

  // Nachricht zusammenbauen (inkl. Name, falls angegeben)
  let fullMessage = message;
  if (name !== '') {
    fullMessage = `Name: ${name}\nNachricht: ${message}`;
  }

  // URL-codieren und WhatsApp öffnen
  const encoded    = encodeURIComponent(fullMessage);
  const whatsappUrl = `https://wa.me/${phoneNumber}?text=${encoded}`;
  window.open(whatsappUrl, '_blank');

  console.log('Nachricht gesendet:', fullMessage);
}
