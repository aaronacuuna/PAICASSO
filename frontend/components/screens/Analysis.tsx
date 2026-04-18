import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  FaChevronLeft,
  FaChevronRight,
  FaList,
  FaCommentDots,
} from "react-icons/fa";
import "../../styles/screens/Analysis.css";
import Chat from "../layout/Chat";
import { mockIssues } from "./mock";
import Loading from "../animations/Loading";
import IssueCard from "../cards/IssueCard";

export interface File {
  name: string;
  content: string;
}

export interface Issue {
  id: number;
  title: string;
  description: string;
  severity: "low" | "medium" | "high";
  file: File;
  line: number;
}

export default function Analysis() {
  const { repoName } = useParams();
  const [isIssuesOpen, setIsIssuesOpen] = useState(true);
  const [isChatOpen, setIsChatOpen] = useState(true);
  const [issues, setIssues] = useState<Issue[] | null>(null);
  const [selectedIssue, setSelectedIssue] = useState<Issue | null>(null);
  const [chatMessage, setChatMessage] = useState<string | null>(null);

  useEffect(() => {
    setTimeout(() => {
      setIssues(mockIssues);
    }, 400);
  }, [repoName]);

  const handleExplain = () => {
    if (selectedIssue) {
      // TODO: Construir un mensaje más detallado usando selectedIssue.description, selectedIssue.file.content, etc.
      setChatMessage(`Explícame la incidencia "${selectedIssue.title}" en el archivo ${selectedIssue.file.name}, por favor.`);
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
              <Loading text="Cargando incidencias..." />
            ) : issues.length > 0 ? (
              <div className="issues-list">
                {issues.map((issue) => (
                  <IssueCard
                    issue={issue}
                    onClick={() => setSelectedIssue(issue)}
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

        <div>
          {selectedIssue ? (
            <div className="issue-details">
              <div className="file-name">
                {selectedIssue.file.name}
              </div>
              <div className="file-content">
                <pre className="code-block">
                  {selectedIssue.file.content.split("\n").map((line, index) => (
                    <div key={index}>
                      {index + 1 === selectedIssue.line && (
                        <>
                          <div className="issue-inline-container">
                            <div>
                              <strong>{selectedIssue.title}.</strong>{" "}
                              {selectedIssue.description}
                            </div>
                            <button className="inline-explain-button" onClick={() => handleExplain()}>
                              Explicar
                            </button>
                          </div>
                        </>
                      )}
                      <div className="code-line">
                        <span className="line-number">{index + 1}</span>
                        <span className="line-text">{line || " "}</span>
                      </div>
                    </div>
                  ))}
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
          <Chat incomingMessage={chatMessage} onIncomingMessageHandled={() => setChatMessage(null)} selectedIssue={selectedIssue} />
        </div>
      </div>
    </div>
  );
}
