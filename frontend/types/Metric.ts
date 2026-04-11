export default interface Metric {
  name: "Bugs" | "Code Smells" | "Vulnerabilidades" | "Líneas" | "Cobertura" | "Duplicaciones";
  value: number;
  label?: "A" | "B" | "C" | "D" | "E" | "F";
  percentage?: boolean;
  repositoryId: number;
}
