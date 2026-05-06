import { useState } from "react";
import "../../styles/cards/RepositoryCard.css";
import { type Repository } from "../../types/Repository";
import { RiBookmarkLine } from "react-icons/ri";
import { FiTrash2, FiX } from "react-icons/fi";

interface RepositoryCardProps {
  repo: Repository;
  onClick: () => void;
  onDelete: (repoId: number) => void;
  selected?: boolean;
}

export default function RepositoryCard({
  repo,
  onClick,
  onDelete,
  selected,
}: RepositoryCardProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleDeleteClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsModalOpen(true);
  };

  const handleConfirm = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete(repo.id);
    setIsModalOpen(false);
  };

  const handleCancel = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsModalOpen(false);
  };

  return (
    <>
      <div
        className={`repository-card ${selected ? "selected" : ""}`}
        onClick={onClick}
      >
        <div className="card-content">
          <div className="card-left">
            <RiBookmarkLine className="card-icon" size={20} />
            <h3>{repo.name}</h3>
          </div>
          <button
            className="delete-button"
            onClick={handleDeleteClick}
            aria-label="Desvincular repositorio"
          >
            <FiTrash2 size={18} />
          </button>
        </div>
      </div>

      {isModalOpen && (
        <div className="unlink-modal-overlay" onClick={handleCancel}>
          <div
            className="unlink-modal-content"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="unlink-modal-header">
              <h2>Desvincular repositorio</h2>
              <button className="close-btn" onClick={handleCancel}>
                <FiX size={22} />
              </button>
            </div>
            <div className="unlink-modal-body">
              <p>
                ¿Estás seguro de que deseas desvincular{" "}
                <strong>{repo.name}</strong>? Perderás el acceso al análisis y
                métricas de este proyecto desde PAICASSO.
              </p>
            </div>
            <div className="unlink-modal-footer">
              <button className="cancel-btn" onClick={handleCancel}>
                Cancelar
              </button>
              <button className="confirm-btn" onClick={handleConfirm}>
                Desvincular
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
