import type Metric from "../../types/Metric";
import type Repository from "../../types/Repository";
import MetricCard from "../cards/MetricCard";
import "../../styles/screens/Dashboard.css";
import { FaCog, FaLongArrowAltRight } from "react-icons/fa";
import { Link, useNavigate } from "react-router-dom";
import LoadingMetrics from "../animations/LoadingMetrics";

const mockMetrics: Metric[] = [
  { name: "Bugs", value: 13, label: "C", percentage: false, repositoryId: 1 },
  {
    name: "Vulnerabilidades",
    value: 0,
    label: "A",
    percentage: false,
    repositoryId: 1,
  },
  {
    name: "Code Smells",
    value: 13,
    label: "C",
    percentage: false,
    repositoryId: 1,
  },
  { name: "Líneas", value: 3434, percentage: false, repositoryId: 1 },
  { name: "Cobertura", value: 13.7, percentage: true, repositoryId: 1 },
  { name: "Duplicaciones", value: 29.2, percentage: true, repositoryId: 1 },
];

export default function Dashboard({ repo }: { repo: Repository }) {
  const metrics = mockMetrics.filter((m) => m.repositoryId === repo.id);
  const navigate = useNavigate();

  return (
    <div className="dashboard">
      <div className="header">
        <div className="left-header">
          <h1>{repo.name}</h1>
          <div className="labels-repo">
            <p>{repo.language}</p>
            <p>Último análisis: {repo.lastAnalysisDate || "Nunca"}</p>
          </div>
        </div>
        <div className="right-header">
          <button
            onClick={() => navigate(`/settings/${encodeURIComponent(repo.name)}`)}
            className="settings-button"
          >
            <FaCog />
            Ajustes
          </button>

          <button onClick={() => alert("Análisis estático")} className="button">
            Análisis estático
          </button>
        </div>
      </div>

      {metrics?.length > 0 ? (
        <div className="metrics-grid">
          {metrics.map((metric) => (
            <MetricCard key={metric.name} metric={metric} />
          ))}
          <div className="text-ai">
            <p>
              Explora estos problemas en detalle y resuélvelos con ayuda de tu
              asistente
            </p>
            <Link to="/analysis">
              Acceder al Análisis Inteligente <FaLongArrowAltRight />
            </Link>
          </div>
        </div>
      ) : (
        <LoadingMetrics />
      )}
    </div>
  );
}
