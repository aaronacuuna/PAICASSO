import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  FaChevronLeft,
  FaChevronRight,
  FaList,
  FaCommentDots,
  FaPython,
  FaJava,
  FaHtml5,
  FaCss3Alt,
  FaJs,
  FaReact,
  FaFileCode,
} from "react-icons/fa";
import {
  SiTypescript,
  SiSharp,
  SiCplusplus,
  SiPhp,
  SiRuby,
  SiGo,
} from "react-icons/si";
import "../../styles/screens/Analysis.css";
import Chat from "../layout/Chat";
import Loading from "../animations/Loading";
import IssueCard from "../cards/IssueCard";
import { type ApiMetrica } from "../../types/Metrica";
import { apiFetch } from "../../utils/apiFetch";

export interface File {
  name: string;
  content: string;
}

export interface Issue {
  id: number;
  title: string;
  type: string;
  severity: "low" | "medium" | "high";
  file: File;
  line: number;
}

const getFileIcon = (filename?: string) => {
  if (!filename) return <FaFileCode size={16} />;
  const ext = filename.split(".").pop()?.toLowerCase();
  switch (ext) {
    case "py":
      return <FaPython size={16} />;
    case "java":
      return <FaJava size={16} />;
    case "js":
      return <FaJs size={16} />;
    case "jsx":
      return <FaReact size={16} />;
    case "ts":
      return <SiTypescript size={16} />;
    case "tsx":
      return <FaReact size={16} />;
    case "html":
      return <FaHtml5 size={16} />;
    case "css":
      return <FaCss3Alt size={16} />;
    case "cs":
      return <SiSharp size={16} />;
    case "cpp":
    case "cc":
    case "c":
    case "hpp":
    case "h":
      return <SiCplusplus size={16} />;
    case "php":
      return <SiPhp size={16} />;
    case "rb":
      return <SiRuby size={16} />;
    case "go":
      return <SiGo size={16} />;
    default:
      return <FaFileCode size={16} />;
  }
};

export default function Analysis() {
  const { repoId } = useParams();

  const [isIssuesOpen, setIsIssuesOpen] = useState(true);
  const [isChatOpen, setIsChatOpen] = useState(true);
  const [isLoading, setIsLoading] = useState(true);

  const [issues, setIssues] = useState<Issue[] | null>(null);
  const [repoName, setRepoName] = useState("");
  const [selectedIssue, setSelectedIssue] = useState<Issue | null>(null);
  const [chatMessage, setChatMessage] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const repoData = await apiFetch(`/api/repositorios/${repoId}`);
        if (repoData) {
          setRepoName(repoData.nombre);
        }

        const issuesData = await apiFetch(`/api/incidencias/${repoId}`);
        if (issuesData) {
          const mappedIssues: Issue[] = issuesData.map(
            (metrica: ApiMetrica) => ({
              id: metrica.id,
              title: metrica.descripcion,
              type: metrica.tipo,
              severity: metrica.severidad.toLowerCase(),
              line: metrica.linea,
              file: {
                name: metrica.archivo,
                content: "// El código fuente no está cargado.",
              },
            }),
          );

          setIssues(mappedIssues);
        }
      } catch (error) {
        console.error("Error al cargar los datos:", error);
        setIssues([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [repoId]);

  const handleSelectIssue = async (issue: Issue) => {
    setIsLoading(true);
    try {
      const data = await apiFetch(`/api/incidencias/${issue.id}/codigo`);

      if (data) {
        setSelectedIssue({
          ...issue,
          file: { ...issue.file, content: data.codigo },
        });
      } else {
        throw new Error("No se pudo cargar el código");
      }
    } catch (error) {
      console.error("Error al obtener el código:", error);
      setSelectedIssue({
        ...issue,
        file: {
          ...issue.file,
          content: "// Error al conectar con SonarQube para leer el código.",
        },
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleExplain = () => {
    if (selectedIssue) {
      // TODO: Construir un mensaje más detallado usando selectedIssue.description, selectedIssue.file.content, etc.
      setChatMessage(
        `Explícame la incidencia "${selectedIssue.title}" en el archivo ${selectedIssue.file.name}, por favor.`,
      );
      setIsChatOpen(true);
    }
  };

  return (
    <div className="analysis">
      {/* Panel izquierdo: Issues */}
      <div
        className={`side-panel issues-panel ${isIssuesOpen ? "open" : "closed"}`}
      >
        <button
          className="toggle-btn"
          onClick={() => setIsIssuesOpen(!isIssuesOpen)}
          title={isIssuesOpen ? "Ocultar panel" : "Mostrar issues"}
        >
          {isIssuesOpen ? <FaChevronLeft /> : <FaList />}
        </button>
        {isIssuesOpen && (
          <div className="panel-content">
            <h3>Incidencias</h3>
            {issues === null ? (
              <div className="loading-center">
                <Loading text="Cargando incidencias..." />
              </div>
            ) : issues.length > 0 ? (
              <div className="issues-list">
                {issues.map((issue) => (
                  <IssueCard
                    key={issue.id}
                    issue={issue}
                    onClick={() => handleSelectIssue(issue)}
                    selected={selectedIssue?.id === issue.id}
                  />
                ))}
              </div>
            ) : (
              <p>No hay incidencias detectadas.</p>
            )}
          </div>
        )}
      </div>

      {/* Contenido Central */}
      <div className="analysis-container">
        <h1>
          Análisis Inteligente {">"}{" "}
          <span className="repo-name">{repoName}</span>
        </h1>

        <div className="content-wrapper">
          {isLoading ? (
            <div className="loading-center">
              <Loading />
            </div>
          ) : selectedIssue ? (
            <div className="issue-details">
              <div className="file-name">
                {getFileIcon(selectedIssue.file.name)}
                {selectedIssue.file.name}
              </div>
              <div className="file-content">
                <pre className="code-block">
                  <div className="code-block-inner">
                    {selectedIssue.file.content
                      .split("\n")
                      .map((line, index) => {
                        const currentLineNumber =
                          Math.max(1, selectedIssue.line - 10) + index;
                        return (
                          <div key={index} className="code-line-wrapper">
                            {currentLineNumber === selectedIssue.line && (
                              <div className="issue-inline-container">
                                <div className="issue-inline-text">
                                  <strong>{selectedIssue.title}</strong>
                                </div>
                                <button
                                  className="inline-explain-button"
                                  onClick={() => handleExplain()}
                                >
                                  <FaCommentDots size={16} />
                                  Explicar
                                </button>
                              </div>
                            )}
                            <div className="code-line">
                              <span className="line-number">
                                {currentLineNumber}
                              </span>
                              <span
                                className="line-text"
                                dangerouslySetInnerHTML={{
                                  __html: line || " ",
                                }}
                              />
                            </div>
                          </div>
                        );
                      })}
                  </div>
                </pre>
              </div>
            </div>
          ) : (
            <p>Selecciona una incidencia para ver detalles.</p>
          )}
        </div>
      </div>

      {/* Panel derecho: Chat */}
      <div
        className={`side-panel chat-panel ${isChatOpen ? "open" : "closed"}`}
      >
        <button
          className="toggle-btn"
          onClick={() => setIsChatOpen(!isChatOpen)}
          title={isChatOpen ? "Ocultar chat" : "Mostrar chat"}
        >
          {isChatOpen ? <FaChevronRight /> : <FaCommentDots />}
        </button>
        <div className={`chat-wrapper ${!isChatOpen ? "hidden" : ""}`}>
          <Chat
            incomingMessage={chatMessage}
            onIncomingMessageHandled={() => setChatMessage(null)}
            selectedIssue={selectedIssue}
          />
        </div>
      </div>
    </div>
  );
}
