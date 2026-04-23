import { useState, useEffect } from "react";
import "../../styles/screens/Configuration.css";
import { apiFetch } from "../../utils/apiFetch";
import Loading from "../animations/Loading";
import ToastMessage from "../animations/ToastMessage";

export default function Configuration() {
  const [selectedLevel, setSelectedLevel] = useState<string | null>(null);
  const [selectedPriorities, setSelectedPriorities] = useState<string[]>([]);
  const [addComments, setAddComments] = useState<boolean>(false);

  const [originalSelectedLevel, setOriginalSelectedLevel] = useState<
    string | null
  >(null);
  const [originalSelectedPriorities, setOriginalSelectedPriorities] = useState<
    string[]
  >([]);
  const [originalAddComments, setOriginalAddComments] =
    useState<boolean>(false);

  const [loading, setLoading] = useState<boolean>(true);
  const [isSaving, setIsSaving] = useState<boolean>(false);
  const [showToast, setShowToast] = useState<boolean>(false);

  useEffect(() => {
    const fetchConfig = async () => {
      try {
        setLoading(true);
        const data = await apiFetch("/api/configuracion");
        if (data) {
          setSelectedLevel(data.experienceLevel || null);
          setSelectedPriorities(data.priorities || []);
          setAddComments(data.addComments || false);
          setOriginalSelectedLevel(data.experienceLevel || null);
          setOriginalSelectedPriorities(data.priorities || []);
          setOriginalAddComments(data.addComments || false);
        }
      } catch (error) {
        console.error("Error al obtener la configuración:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchConfig();
  }, []);

  const togglePriority = (priority: string) => {
    if (selectedPriorities.includes(priority)) {
      setSelectedPriorities(selectedPriorities.filter((p) => p !== priority));
    } else {
      setSelectedPriorities([...selectedPriorities, priority]);
    }
  };

  const handleSaveSettings = async () => {
    const hasChanges =
      selectedLevel !== originalSelectedLevel ||
      addComments !== originalAddComments ||
      selectedPriorities.length !== originalSelectedPriorities.length ||
      selectedPriorities.some((p) => !originalSelectedPriorities.includes(p));

    if (!hasChanges) {
      setShowToast(false);
      return;
    }

    setIsSaving(true);
    try {
      const settings = {
        experienceLevel: selectedLevel,
        priorities: selectedPriorities,
        addComments,
      };

      await apiFetch("/api/configuracion", {
        method: "POST",
        body: JSON.stringify(settings),
      });

      setOriginalSelectedLevel(selectedLevel);
      setOriginalSelectedPriorities([...selectedPriorities]);
      setOriginalAddComments(addComments);

      setShowToast(true);
      setTimeout(() => setShowToast(false), 3000);
    } catch (error) {
      console.error("Error al guardar la configuración:", error);
      alert("Hubo un error al guardar tus preferencias.");
    } finally {
      setIsSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <Loading />
      </div>
    );
  }

  return (
    <div className="configuration-view">
      <div className="configuration-container">
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
        </div>

        <button
          className="save-button"
          onClick={handleSaveSettings}
          disabled={isSaving}
        >
          Guardar preferencias
        </button>
      </div>

      <ToastMessage
        message="Configuración guardada correctamente"
        isVisible={showToast}
      />
    </div>
  );
}
