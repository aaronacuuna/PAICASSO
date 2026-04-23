import { type Metric } from "../../types/Metric";
import { type ApiAnalisis } from "../../types/Analisis";
import { type Repository } from "../../types/Repository";
import MetricCard from "../cards/MetricCard";
import "../../styles/screens/Dashboard.css";
import { FaLongArrowAltRight } from "react-icons/fa";
import { FiCpu } from "react-icons/fi";
import { Link } from "react-router-dom";
import Loading from "../animations/Loading";
import { apiFetch } from "../../utils/apiFetch";
import { useEffect, useState, useCallback } from "react";

export default function Dashboard({ repo }: { repo: Repository }) {
  const [metrics, setMetrics] = useState<Metric[] | null>(null);

  const [loading, setLoading] = useState(true);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [hasError, setHasError] = useState(false);

  const [lastAnalysisDate, setLastAnalysisDate] = useState<string | null>(null);

  const fetchLastAnalysis = useCallback(
    async (isInitialLoad = false) => {
      if (isInitialLoad) setLoading(true);

      try {
        const data: ApiAnalisis = await apiFetch(
          `/api/repositorios/${repo.id}/analisis/ultimo`,
        );

        if (data && data.id) {
          setLastAnalysisDate(new Date(data.fechaEjecucion).toLocaleString());

          if (data.estado === "EN_PROGRESO") {
            setIsAnalyzing(true);
            setHasError(false);
          } else if (data.estado === "ERROR") {
            setIsAnalyzing(false);
            setHasError(true);
            setMetrics([]);
          } else if (data.estado === "COMPLETADO") {
            setIsAnalyzing(false);
            setHasError(false);
            setMetrics([
              {
                name: "Bugs",
                value: data.bugs || 0,
                percentage: false,
                repositoryId: repo.id,
              },
              {
                name: "Vulnerabilidades",
                value: data.vulnerabilidades || 0,
                percentage: false,
                repositoryId: repo.id,
              },
              {
                name: "Code Smells",
                value: data.codeSmells || 0,
                percentage: false,
                repositoryId: repo.id,
              },
              {
                name: "Líneas",
                value: data.lineasCodigo || 0,
                percentage: false,
                repositoryId: repo.id,
              },
              {
                name: "Cobertura",
                value: data.cobertura || 0,
                percentage: true,
                repositoryId: repo.id,
              },
              {
                name: "Duplicaciones",
                value: data.duplicaciones || 0,
                percentage: true,
                repositoryId: repo.id,
              },
            ]);
          }
        } else {
          setMetrics([]);
          setLastAnalysisDate("Nunca");
          setIsAnalyzing(false);
          setHasError(false);
        }
      } catch (error) {
        console.error("Error al cargar el último análisis:", error);
        setMetrics([]);
        setIsAnalyzing(false);
      } finally {
        if (isInitialLoad) setLoading(false);
      }
    },
    [repo.id],
  );

  useEffect(() => {
    fetchLastAnalysis(true);
  }, [fetchLastAnalysis]);

  useEffect(() => {
    let intervalId: number;

    if (isAnalyzing) {
      intervalId = window.setInterval(() => {
        fetchLastAnalysis(false);
      }, 3000);
    }

    return () => {
      if (intervalId) window.clearInterval(intervalId);
    };
  }, [isAnalyzing, fetchLastAnalysis]);

  const handleAnalyze = async () => {
    try {
      setIsAnalyzing(true);
      setHasError(false);
      setMetrics(null);

      await apiFetch(`/api/repositorios/${repo.id}/analizar`, {
        method: "POST",
      });
    } catch (error) {
      console.error("Error al iniciar el análisis:", error);
      setIsAnalyzing(false);
      alert("Hubo un problema de conexión al solicitar el análisis.");
    }
  };

  return (
    <div className="dashboard">
      <div className="header">
        <div className="left-header">
          <h1>{repo.name}</h1>
          <div className="labels-repo">
            <p>{repo.language}</p>
            {lastAnalysisDate !== null && lastAnalysisDate !== "Nunca" && (
              <p>Último análisis: {lastAnalysisDate}</p>
            )}
          </div>
        </div>
        <div className="right-header">
          <button
            onClick={handleAnalyze}
            className="button"
            disabled={isAnalyzing || loading}
          >
            <FiCpu className="activity-icon" />
            {isAnalyzing ? "Analizando..." : "Análisis estático"}
          </button>
        </div>
      </div>

      {loading ? (
        <Loading />
      ) : isAnalyzing ? (
        <Loading text="Analizando código en SonarQube. Esto puede tardar unos segundos..." />
      ) : hasError ? (
        <div
          className="empty-state-metrics"
          style={{ color: "var(--accent)" }}
        >
          <p>
            <strong>El análisis estático ha fallado</strong>
          </p>
          <p>
            Es posible no se tenga permisos para acceder al repositorio o que SonarQube no esté disponible
          </p>
        </div>
      ) : metrics && metrics.length > 0 ? (
        <div className="metrics-grid">
          {metrics.map((metric) => (
            <MetricCard key={metric.name} metric={metric} />
          ))}
          <div className="text-ai">
            <p>
              Explora estos problemas en detalle y resuélvelos con ayuda de tu
              asistente
            </p>
            <Link to={`/analysis/${repo.id}`}>
              Acceder al Análisis Inteligente <FaLongArrowAltRight />
            </Link>
          </div>
        </div>
      ) : (
        <div className="empty-state-metrics">
          <p style={{ marginBottom: "10px" }}>
            Aún no se ha ejecutado ningún análisis para este proyecto
          </p>
          <p>
            Pulsa el botón de <b>Análisis estático</b> para extraer las métricas
            de SonarQube por primera vez
          </p>
        </div>
      )}
    </div>
  );
}
