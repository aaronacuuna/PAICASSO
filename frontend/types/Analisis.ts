export interface ApiAnalisis {
  id: number;
  estado: "EN_PROGRESO" | "COMPLETADO" | "ERROR";
  fechaEjecucion: string;
  bugs: number;
  vulnerabilidades: number;
  codeSmells: number;
  lineasCodigo: number;
  cobertura: number;
  duplicaciones: number;
}