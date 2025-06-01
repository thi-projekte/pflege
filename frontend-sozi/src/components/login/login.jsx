import React from "react";
import { Button } from "@/components/ui/button";
import keycloakService from "@/services/keycloakService";
import Logo from "@/assets/logo.svg";

export default function Login() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      <img src={Logo} alt="Pflegital Logo" className="mb-4 w-32 h-32" />
      <h1 className="text-2xl mb-6">Willkommen bei Pflegital</h1>
      <Button 
        className="w-full max-w-sm" 
        onClick={() => keycloakService.login()}
      >
        Login with Keycloak
      </Button>
    </div>
  );
}
