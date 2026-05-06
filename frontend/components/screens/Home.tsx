import { useNavigate, useLocation } from "react-router-dom";
import { FaLongArrowAltRight } from "react-icons/fa";
import "../../styles/screens/Home.css";
import RepositoryCard from "../cards/RepositoryCard";
import GitHubButton from "../buttons/GitHubButton";
import Dashboard from "./Dashboard";
import { type Repository, type ApiRepository } from "../../types/Repository";
import { useEffect, useState } from "react";
import Loading from "../animations/Loading";
import { apiFetch } from "../../utils/apiFetch";
import GithubReposModal from "../layout/GithubReposModal";

function Home() {
  const [repositories, setRepositories] = useState<Repository[] | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const pathParts = location.pathname.split("/");
  const isDashboard = pathParts[1] === "dashboard";
  const repoId =
    isDashboard && pathParts[2] ? parseInt(pathParts[2], 10) : null;
  const selectedRepo =
    repositories?.find((r: Repository) => r.id === repoId) || null;

  const handleSelectRepo = (repo: Repository) => {
    navigate(`/dashboard/${repo.id}`);
  };

  const handleDeleteRepo = async (repoId: number) => {
    if (!repositories) return;

    const prevRepositories = [...repositories];
    setRepositories(repositories.filter((repo) => repo.id !== repoId));

    if (selectedRepo?.id === repoId) {
      navigate("/");
    }

    try {
      await apiFetch(`/api/repositorios/${repoId}`, {
        method: "DELETE",
      });
    } catch (error) {
      console.error("Error al desvincular el repositorio:", error);
      setRepositories(prevRepositories);
      alert("Hubo un error al desvincular el repositorio.");
    }
  };

  const fetchRepositories = async () => {
    try {
      setRepositories(null);
      const data = await apiFetch("/api/repositorios");
      const mappedRepos: Repository[] = data.map((repo: ApiRepository) => ({
        id: repo.id,
        name: repo.nombre,
        language: repo.lenguajePrincipal,
        url: repo.url,
        vinculado: repo.vinculado,
        lastAnalysisDate: null,
      }));
      setRepositories(mappedRepos);
    } catch (error) {
      console.error("Error al cargar repositorios:", error);
      setRepositories([]);
    }
  };

  useEffect(() => {
    const loadRepositories = async () => {
      try {
        setRepositories(null);
        const data = await apiFetch("/api/repositorios");
        const mappedRepos: Repository[] = data.map((repo: ApiRepository) => ({
          id: repo.id,
          name: repo.nombre,
          language: repo.lenguajePrincipal,
          url: repo.url,
          vinculado: repo.vinculado,
          lastAnalysisDate: null,
        }));
        setRepositories(mappedRepos);
      } catch (error) {
        console.error("Error al cargar repositorios:", error);
        setRepositories([]);
      }
    };
    loadRepositories();
  }, []);

  return (
    <div className="home">
      <GithubReposModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onRepoLinked={fetchRepositories}
      />

      {/* Left panel */}
      {repositories ? (
        repositories.length > 0 ? (
          <div className="left-panel">
            <h2>Tus repositorios</h2>
            <ul>
              {repositories.map((repo: Repository) => (
                <RepositoryCard
                  key={repo.id}
                  repo={repo}
                  onClick={() => handleSelectRepo(repo)}
                  selected={selectedRepo?.id === repo.id}
                  onDelete={handleDeleteRepo}
                />
              ))}
            </ul>
          </div>
        ) : (
          <div className="left-panel empty">
            <p>Aún no has vinculado ningún proyecto a tu cuenta</p>
            <GitHubButton
              text="Vincular repositorio"
              onClick={() => setIsModalOpen(true)}
            />
          </div>
        )
      ) : (
        <div className="left-panel loading">
          <Loading />
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
              onClick={() => setIsModalOpen(true)}
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
