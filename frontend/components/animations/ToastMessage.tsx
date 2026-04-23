import { FiCheckCircle } from "react-icons/fi";
import "../../styles/animations/ToastAnimation.css";

interface ToastMessageProps {
  message: string;
  isVisible: boolean;
}

export default function ToastMessage({ message, isVisible }: ToastMessageProps) {
  if (!isVisible) return null;

  return (
    <div className="toast-container">
      <FiCheckCircle className="toast-icon" size={20} />
      <span className="toast-text">{message}</span>
    </div>
  );
}

