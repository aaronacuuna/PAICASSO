import { useEffect, useState } from "react";
import { apiFetch } from "../../utils/apiFetch";
import Loading from "../animations/Loading";
import {type Repository, type ApiRepository} from "../../types/Repository";
import { FaGithub, FaJava } from "react-icons/fa";
import { FiX, FiPlus, FiCheck } from "react-icons/fi";
import { RiBookmarkLine } from "react-icons/ri";
import {
  SiJavascript,
  SiTypescript,
  SiPython,
  SiCplusplus,
  SiSharp,
  SiHtml5,
  SiCss,
  SiPhp,
  SiRuby,
} from "react-icons/si";
import "../../styles/layout/GithubReposModal.css";

interface GithubReposModalProps {
  isOpen: boolean;
  onClose: () => void;
  onRepoLinked: () => void;
}

function GithubReposModal({
  isOpen,
  onClose,
  onRepoLinked,
}: GithubReposModalProps) {
  const [githubRepos, setGithubRepos] = useState<Repository[] | null>(null);
  const [loading, setLoading] = useState(false);

  const getLanguageIcon = (lang: string | null) => {
    if (!lang) return null;
    const l = lang.toLowerCase();
    if (l.includes("javascript")) return <SiJavascript />;
    if (l.includes("typescript")) return <SiTypescript />;
    if (l.includes("python")) return <SiPython />;
    if (l.includes("java") && !l.includes("javascript")) return <FaJava />;
    if (l.includes("c++") || l.includes("cpp")) return <SiCplusplus />;
    if (l.includes("c#") || l.includes("csharp")) return <SiSharp />;
    if (l.includes("html")) return <SiHtml5 />;
    if (l.includes("css")) return <SiCss />;
    if (l.includes("php")) return <SiPhp />;
    if (l.includes("ruby")) return <SiRuby />;
    return null;
  };

  useEffect(() => {
    if (isOpen) {
      cargarReposDeGithub();
    } else {
      setGithubRepos(null);
    }
  }, [isOpen]);

  const cargarReposDeGithub = async () => {
    setLoading(true);
    try {
      const data = await apiFetch("/api/repositorios/github");
      const mappedRepos: Repository[] = data.map((repo: ApiRepository) => ({
        id: repo.id,
        name: repo.nombre,
        language: repo.lenguajePrincipal,
        url: repo.url,
        vinculado: repo.vinculado,
        lastAnalysisDate: null,
      }));

      setGithubRepos(mappedRepos);
    } catch (error) {
      console.error("Error al cargar repos de GitHub:", error);
      setGithubRepos([]);
    } finally {
      setLoading(false);
    }
  };

  const vincularRepo = async (repo: Repository) => {
    try {
      await apiFetch("/api/repositorios", {
        method: "POST",
        body: JSON.stringify({
          nombre: repo.name,
          url: repo.url,
          lenguajePrincipal: repo.language || "Desconocido",
        }),
      });
      onRepoLinked();
      onClose();
    } catch (error) {
      console.error("Error al vincular el repositorio:", error);
      alert("Hubo un error al vincular el repositorio. Inténtalo de nuevo.");
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div className="header-title">
            <FaGithub size={24} className="github-icon" />
            <h2>Tus repositorios</h2>
          </div>
          <button
            className="close-button"
            onClick={onClose}
            aria-label="Cerrar modal"
          >
            <FiX size={22} />
          </button>
        </div>

        <div className="modal-body">
          {loading || !githubRepos ? (
            <div className="loading-center">
              <Loading />
            </div>
          ) : githubRepos.length === 0 ? (
            <div className="empty-repos">
              <RiBookmarkLine size={48} className="empty-icon" />
              <p>No se encontraron repositorios en tu cuenta de GitHub.</p>
            </div>
          ) : (
            <ul className="github-repo-list">
              {githubRepos.map((repo) => (
                <li
                  key={repo.id || repo.url}
                  className={`github-repo-item ${repo.vinculado ? "vinculado" : ""}`}
                >
                  <div className="repo-info">
                    <div className="repo-name">
                      <span className="repo-icon">
                        <RiBookmarkLine />
                      </span>
                      <strong>{repo.name}</strong>
                    </div>
                    <span className="badge">
                      {getLanguageIcon(repo.language)}
                      {repo.language || "Desconocido"}
                    </span>
                  </div>

                  {repo.vinculado ? (
                    <span className="vinculado-text">
                      <FiCheck size={16} /> Vinculado
                    </span>
                  ) : (
                    <button
                      className="link-button"
                      onClick={() => vincularRepo(repo)}
                    >
                      <FiPlus size={16} /> Añadir
                    </button>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}

export default GithubReposModal;
