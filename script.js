document.getElementById('whatsappBtn').addEventListener('click', function (event) {
    const messageInput = document.getElementById('userMessage');
    const message = messageInput.value.trim();
  
    if (message === "") {
      alert("Bitte gib eine Nachricht ein.");
      return;
    }
  
    const phoneNumber = "15556422777";
    const encodedMessage = encodeURIComponent(message);
    const whatsappUrl = `https://wa.me/${phoneNumber}?text=${encodedMessage}`;
  
    window.open(whatsappUrl, "_blank");
  
    console.log("Nachricht gesendet:", message);
  });
  