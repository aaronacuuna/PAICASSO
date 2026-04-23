export  interface Repository {
  id: number;
  name: string;
  language: string;
  url: string;
  vinculado: boolean;
  lastAnalysisDate: string | null;
}

export interface ApiRepository {
  id: number;
  nombre: string;
  lenguajePrincipal: string;
  url: string;
  vinculado: boolean;
}