import React from "react";
import { Button } from "@/components/ui/button";
import keycloakService from "@/services/keycloakService";

export default function Login() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
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
