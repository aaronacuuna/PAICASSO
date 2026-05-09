import { FaStethoscope, FaLightbulb, FaRegClock } from "react-icons/fa";
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
        <div className="report-header">
          <h2>Informe de análisis</h2>
        </div>
        <div className="report-empty">
          <p>
            No se ha podido generar el informe. Por favor, inténtalo de nuevo
            más tarde.
          </p>
        </div>
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
      <div className="report-header">
        <h2>Informe de análisis</h2>
        <span className="report-date">
          <FaRegClock size={12} />
          Generado el {fecha}
        </span>
      </div>

      <div className="report-section">
        <div className="report-section-header">
          <span className="report-section-icon">
            <FaStethoscope size={14} />
          </span>
          <h3>Diagnóstico</h3>
        </div>
        <div className="report-section-body">
          <MarkdownRenderer text={report.diagnostico} />
        </div>
      </div>

      <div className="report-section">
        <div className="report-section-header">
          <span className="report-section-icon">
            <FaLightbulb size={14} />
          </span>
          <h3>Propuesta de mejora</h3>
        </div>
        <div className="report-section-body">
          <MarkdownRenderer text={report.propuesta} />
        </div>
      </div>
    </div>
  );
}
