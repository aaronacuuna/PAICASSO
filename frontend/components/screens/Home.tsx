import { useNavigate, useLocation } from "react-router-dom";
import { FaLongArrowAltRight } from "react-icons/fa";
import "../../styles/screens/Home.css";
import RepositoryCard from "../cards/RepositoryCard";
import GitHubButton from "../buttons/GitHubButton";
import Dashboard from "./Dashboard";
import type Repository from "../../types/Repository";

function Home() {
  const repositories: Repository[] = [
    { id: 1, name: "proyecto-A", language: "Java 17", lastAnalysisDate: "2024-06-01" },
    { id: 2, name: "proyecto-B", language: "Python", lastAnalysisDate: null },
    { id: 3, name: "proyecto-C", language: "JavaScript", lastAnalysisDate: null },
    { id: 4, name: "proyecto-D", language: "TypeScript", lastAnalysisDate: null },
    { id: 5, name: "proyecto-E", language: "C++", lastAnalysisDate: null },
  ];

  const navigate = useNavigate();
  const location = useLocation();
  const pathParts = location.pathname.split("/");
  const isDashboard = pathParts[1] === "dashboard";
  const repoName = isDashboard && pathParts[2] ? decodeURIComponent(pathParts[2]) : null;
  const selectedRepo = repositories.find((r) => r.name === repoName) || null;

  const handleSelectRepo = (repo: Repository) => {
    navigate(`/dashboard/${encodeURIComponent(repo.name)}`);
  };

  return (
    <div className="home">
      {/* Left panel */}
      {repositories.length > 0 ? (
        <div className="left-panel">
          <h2>Tus repositorios</h2>
          <ul>
            {repositories.map((repo) => (
              <RepositoryCard
                key={repo.id}
                repo={repo}
                onClick={() => handleSelectRepo(repo)}
                selected={selectedRepo?.id === repo.id}
              />
            ))}
          </ul>
        </div>
      ) : (
        <div className="left-panel empty">
          <p>Aún no has vinculado ningún proyecto a tu cuenta</p>
          <GitHubButton
            text="Vincular repositorio"
            onClick={() => alert("Vincular repositorio")}
          />
        </div>
      )}

      {/* Main content */}
      {selectedRepo ? (
        <Dashboard repo={selectedRepo} />
      ) : (
        <div className="main-content">
          <h1>¡Te damos la bienvenida a PAICASSO!</h1>
          <p className="subtitle">
            Comienza a mejorar la calidad de tu código hoy mismo
          </p>
          <div className="connect-container">
            <h2>Conecta tu repositorio</h2>
            <p className="subtitle">
              PAICASSO se integrará con tu código para extraer métricas de
              SonarQube y ofrecerte asistencia guiada por IA
            </p>
            <button
              className="connect-button"
              onClick={() => alert("Vincular repositorio")}
            >
              Añadir repositorio
            </button>
          </div>

          <div>
            <h2>¿Cómo funciona?</h2>
            <div className="explain">
              <p>1. Conecta el código</p>
              <FaLongArrowAltRight className="arrow-icon" />
              <p>2. Analiza métricas</p>
              <FaLongArrowAltRight className="arrow-icon" />
              <p>3. Resuelve con IA</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Home;
