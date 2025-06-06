

import React from "react";
import Logo from "@/assets/logo.svg";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuItem,
  DropdownMenuSeparator,
} from "@/components/ui/dropdown-menu";
import keycloakService from "@/services/keycloakService";

export default function Header() {

  return (
    <header className="flex items-center justify-between px-6 py-4 bg-white border-b">
      <div className="flex items-center space-x-2">
        <img src={Logo} alt="Pflegital Logo" className="h-8 w-auto" />
        <span className="text-xl font text-primary">Pflegital</span>
      </div>
      <DropdownMenu>
        <DropdownMenuTrigger>
          <Avatar className="cursor-pointer">
            <AvatarImage src={`https://ui-avatars.com/api/?name=${keycloakService.getTokenParsed()?.preferred_username || "User"}`} />
            <AvatarFallback>{keycloakService.getTokenParsed()?.preferred_username?.[0] || "U"}</AvatarFallback>
          </Avatar>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuLabel>
            Eingeloggt als: {keycloakService.getTokenParsed()?.preferred_username || "Unbekannter Nutzer"}
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuItem 
            className="px-4 py-2 bg-red-500 text-white rounded"
            onClick={() => keycloakService.logout()}
          >
            Logout
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </header>
  );
}