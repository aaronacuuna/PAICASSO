import "../../styles/cards/RepositoryCard.css";
import type Repository from "../../types/Repository";

interface RepositoryCardProps {
  repo: Repository;
  onClick: () => void;
  selected?: boolean;
}

export default function RepositoryCard({
  repo,
  onClick,
  selected,
}: RepositoryCardProps) {
  return (
    <div
      className={`repository-card ${selected ? "selected" : ""}`}
      onClick={onClick}
    >
      <h3>{repo.name}</h3>
    </div>
  );
}
