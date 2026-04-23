import { useState } from "react";

export interface UserSession {
  nombre: string;
  fotoPerfil: string;
  githubId?: number;
}

export function useJwtToken() {
  const [user] = useState<UserSession | null>(() => {
    const token = localStorage.getItem("paicasso_token");
    if (token) {
      try {
        const base64Url = token.split(".")[1];
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
        const jsonPayload = decodeURIComponent(
          window
            .atob(base64)
            .split("")
            .map(function (c) {
              return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
            })
            .join(""),
        );
        const decoded = JSON.parse(jsonPayload);
        return {
          nombre: decoded.nombre || "Usuario",
          fotoPerfil: decoded.fotoPerfil || "/paicasso.png",
          githubId: decoded.githubId,
        };
      } catch (error) {
        console.error("Error al decodificar el token JWT:", error);
      }
    }
    return null;
  });

  return user;
}