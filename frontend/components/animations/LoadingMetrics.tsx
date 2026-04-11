import "../../styles/animations/LoadingMetrics.css";

export default function LoadingMetrics() {
  return (
    <div className="loading-metrics">
      <div className="dots-container">
        <div className="dot"></div>
        <div className="dot"></div>
        <div className="dot"></div>
      </div>
      <h3>Cargando métricas</h3>
    </div>
  );
}
