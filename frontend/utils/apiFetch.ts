/// <reference types="vite/client" />
const baseUrl = import.meta.env.VITE_API_URL;

export const apiFetch = async (endpoint: string, options: RequestInit = {}) => {
  const token = localStorage.getItem("paicasso_token");

  const headers = new Headers(options.headers);
  if (!headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(`${baseUrl}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 401 || response.status === 403) {
    console.error("Token caducado o inválido. Cerrando sesión...");
    localStorage.removeItem("paicasso_token");
    window.location.href = "/login";
    throw new Error("No autorizado");
  }

  if (!response.ok) {
    let backendMessage = "";
    try {
      const errBody = await response.clone().json();
      backendMessage = errBody?.error || errBody?.message || "";
    } catch {
      backendMessage = await response.clone().text().catch(() => "");
    }
    throw new Error(backendMessage || `Error en la petición: ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
};
