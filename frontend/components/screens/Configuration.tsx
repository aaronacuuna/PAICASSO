import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import "../../styles/screens/Configuration.css";
import { FaLongArrowAltLeft } from "react-icons/fa";

export default function Configuration() {
  const { repoName } = useParams();
  const [selectedLevel, setSelectedLevel] = useState<string | null>(null);
  const [selectedPriorities, setSelectedPriorities] = useState<string[]>([]);
  const [addComments, setAddComments] = useState<boolean>(false);
  const [translateSpanish, setTranslateSpanish] = useState<boolean>(false);

  const togglePriority = (priority: string) => {
    if (selectedPriorities.includes(priority)) {
      setSelectedPriorities(selectedPriorities.filter((p) => p !== priority));
    } else {
      setSelectedPriorities([...selectedPriorities, priority]);
    }
  };

  const handleSaveSettings = () => {
    const settings = {
      experienceLevel: selectedLevel,
      priorities: selectedPriorities,
      addComments,
      translateSpanish,
    };
    console.log("Configuración guardada:", settings);
    alert(`Configuración guardada: ${JSON.stringify(settings)}`);
  };

  return (
    <div>
      <div className="configuration-container">
        <Link to={`/dashboard/${repoName ? encodeURIComponent(repoName) : ""}`}>
          <FaLongArrowAltLeft /> Volver al Dashboard
        </Link>
        <h2>Configuración del Asistente Inteligente</h2>
      </div>

      {/* Nivel de experiencia */}
      <p className="section-title">¿Cuál es tu nivel de experiencia?</p>
      <div className="experience-options">
        <div
          className={`exp-option ${selectedLevel === "junior" ? "selected" : ""}`}
          onClick={() => setSelectedLevel("junior")}
        >
          <h3>Junior</h3>
          <p>Explicaciones paso a paso, con analogías y conceptos básicos</p>
        </div>
        <div
          className={`exp-option ${selectedLevel === "mid" ? "selected" : ""}`}
          onClick={() => setSelectedLevel("mid")}
        >
          <h3>Mid-Level</h3>
          <p>Directo al grano pero con contexto técnico</p>
        </div>
        <div
          className={`exp-option ${selectedLevel === "senior" ? "selected" : ""}`}
          onClick={() => setSelectedLevel("senior")}
        >
          <h3>Senior</h3>
          <p>Solo el código y documentación técnica</p>
        </div>
      </div>

      {/* Preferencias */}
      <p className="section-title">
        ¿A qué quieres que la IA le dé más prioridad al sugerir código?
      </p>
      <div className="experience-options">
        <div
          className={`exp-option ${selectedPriorities.includes("security") ? "selected" : ""}`}
          onClick={() => togglePriority("security")}
        >
          <h3>Seguridad</h3>
          <p>Prevenir vulnerabilidades y hackeos</p>
        </div>
        <div
          className={`exp-option ${selectedPriorities.includes("readability") ? "selected" : ""}`}
          onClick={() => togglePriority("readability")}
        >
          <h3>Legibilidad</h3>
          <p>Código limpio y fácil de leer para el equipo</p>
        </div>
        <div
          className={`exp-option ${selectedPriorities.includes("performance") ? "selected" : ""}`}
          onClick={() => togglePriority("performance")}
        >
          <h3>Rendimiento</h3>
          <p>Optimizar la velocidad de ejecución</p>
        </div>
      </div>

      {/* Checkboxes y Opciones Inferiores */}
      <div className="settings-footer">
        <div className="checkboxes-section">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={addComments}
              onChange={(e) => setAddComments(e.target.checked)}
            />
            <span>
              Añadir comentarios explicativos en el código generado por la IA
            </span>
          </label>
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={translateSpanish}
              onChange={(e) => setTranslateSpanish(e.target.checked)}
            />
            <span>Traducir la explicación a español</span>
          </label>
        </div>

        {/* Botón de guardar */}
        <button className="save-button" onClick={() => handleSaveSettings()}>
          Guardar preferencias
        </button>
      </div>
    </div>
  );
}
