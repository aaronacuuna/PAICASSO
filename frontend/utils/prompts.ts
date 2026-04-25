interface PredefinedPrompt {
  title: string;
  prompt: string;
}

export const predefinedPrompts: PredefinedPrompt[] = [
    {
      title: "Analizar seguridad del código",
      prompt:
        "¿Puedes revisar este código y decirme si encuentras vulnerabilidades de seguridad?",
    },
    {
      title: "Optimizar para mayor rendimiento",
      prompt:
        "¿Puedes revisar este código y sugerirme optimizaciones para mejorar su rendimiento?",
    },
    {
      title: "Mejorar legibilidad",
      prompt:
        "¿Puedes revisar este código y sugerirme cambios para mejorar su legibilidad y mantenibilidad?",
    },
    {
      title: "Crear tests unitarios",
      prompt:
        "¿Puedes revisar este código y sugerirme casos de prueba unitarios que debería tener para asegurar su correcto funcionamiento?",
    },
  ];