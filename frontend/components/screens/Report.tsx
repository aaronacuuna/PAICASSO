import type { ApiReport } from "./Analysis";
import MarkdownRenderer from "../layout/MarkdownRenderer";
import "../../styles/screens/Chat.css";
import "../../styles/screens/Report.css";

interface ReportProps {
  report: ApiReport | null;
}

export default function Report({ report }: ReportProps) {
  if (!report) {
    return (
      <div className="report-screen">
        <h2>Informe de análisis</h2>
        <p>
          No se ha podido generar el informe. Por favor, inténtalo de nuevo más
          tarde.
        </p>
      </div>
    );
  }

  const fecha = new Date(report.fechaGeneracion).toLocaleString("es-ES", {
    day: "2-digit",
    month: "long",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

  return (
    <div className="report-screen">
      <h2>Informe de análisis</h2>
      <p className="report-date">Generado el {fecha}</p>

      <div className="report-section">
        <h3>Diagnóstico</h3>
        <MarkdownRenderer text={report.diagnostico} />
      </div>

      <div className="report-section">
        <h3>Propuesta de mejora</h3>
        <MarkdownRenderer text={report.propuesta} />
      </div>
    </div>
  );
}
