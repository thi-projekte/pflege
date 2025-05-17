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
    const prev = document.querySelector('.gallery-nav.prev');
    const next = document.querySelector('.gallery-nav.next');

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
    const nav = document.querySelector('nav');

    if (menuToggle && nav) {
        menuToggle.addEventListener('click', () => {
            nav.classList.toggle('active');
        });
    }
});

// 4) Funktion zum Versenden von Nachrichten per WhatsApp
function sendToWhatsApp() {
    // Eingabefelder holen
    const nameInput = document.getElementById('name');
    const messageInput = document.getElementById('message');

    const name = nameInput.value.trim();
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
    const encoded = encodeURIComponent(fullMessage);
    const whatsappUrl = `https://wa.me/${phoneNumber}?text=${encoded}`;
    window.open(whatsappUrl, '_blank');

    console.log('Nachricht gesendet:', fullMessage);
}

document.addEventListener("DOMContentLoaded", () => {
    const mainTrack = document.querySelector(".pdp-gallery__track");
    const thumbsTrack = document.querySelector(".pdp-gallery__thumb-track");
    const slides = [...mainTrack.children];
    const thumbs = [...thumbsTrack.children];
    // === Dots erzeugen ===
    const dotsContainer = document.querySelector(".pdp-gallery__dots");
    const dots = slides.map((_, i) => {
        const dot = document.createElement("button");
        dot.className = "pdp-gallery__dot";
        dot.dataset.index = i;
        dot.addEventListener("click", () => updateActive(i));
        dotsContainer.appendChild(dot);
        return dot;
    });

    let currentIndex = 0;

    // Hilfsfunktion: zentriert ein Kind-Element horizontal im Track
    function centerInTrack(track, child) {
        const offset = child.offsetLeft - (track.clientWidth - child.clientWidth) / 2;
        track.scrollTo({ left: offset, behavior: "smooth" });
    }

    const updateActive = idx => {
        centerInTrack(mainTrack, slides[idx]);
        thumbs.forEach((t, i) => t.classList.toggle("active", i === idx));
        centerInTrack(thumbsTrack, thumbs[idx]);
        currentIndex = idx;
        // Dots aktivieren
        dots.forEach((d, i) => d.classList.toggle("active", i === idx));

        currentIndex = idx;
    };

    document.querySelector(".pdp-gallery__nav--prev")
        .addEventListener("click", () => updateActive(Math.max(0, currentIndex - 1)));
    document.querySelector(".pdp-gallery__nav--next")
        .addEventListener("click", () => updateActive(Math.min(slides.length - 1, currentIndex + 1)));

    thumbs.forEach((thumb, i) =>
        thumb.addEventListener("click", () => updateActive(i))
    );

    updateActive(0);
});

document.addEventListener("DOMContentLoaded", () => {
  const chatRows = document.querySelectorAll(".chat-row");

  function restartChatAnimation() {
    chatRows.forEach((row, index) => {
      row.style.animation = "none"; // Animation stoppen
      void row.offsetWidth; // Reflow erzwingen
      row.style.animation = `fadeInUp 1s ease forwards`; // Animation neu starten
      row.style.animationDelay = `${index * 1.5}s`; // Verzögerung für jede Zeile
    });
  }

  // Starte die Animation alle 10 Sekunden neu
  setInterval(restartChatAnimation, 10000);

  // Initiale Animation starten
  restartChatAnimation();
});

// Warte, bis das DOM geladen ist
document.addEventListener('DOMContentLoaded', () => {
  const btn = document.getElementById('backToTop');
  if (!btn) return; // Falls die ID fehlt, nicht weiter machen

  // Scroll-Listener zum Ein-/Ausblenden
  window.addEventListener('scroll', () => {
    if (window.pageYOffset > 200) {
      btn.classList.add('show');
    } else {
      btn.classList.remove('show');
    }
  });

  // Klick-Listener zum sanften Scrollen nach oben
  btn.addEventListener('click', (e) => {
    e.preventDefault();
    // Funktionert in allen modernen Browsern:
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });
});

