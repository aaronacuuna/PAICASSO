import {type Metric} from "../../types/Metric";
import "../../styles/cards/MetricCard.css";

interface MetricCardProps {
  metric: Metric;
}

export default function MetricCard({ metric }: MetricCardProps) {
  return (
    <div className="metric-container">
      <h2>{metric.name}</h2>
      {metric.name === "Líneas" ? (
        <p>
          {metric.value >= 1000
            ? `${(metric.value / 1000).toFixed(1)}k`
            : metric.value}
        </p>
      ) : (
        <p>
          {metric.value}
          {metric.percentage ? "%" : ""}
          {metric.label ? ` (${metric.label})` : ""}
        </p>
      )}
    </div>
  );
}
