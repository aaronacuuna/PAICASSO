import "../../styles/animations/Loading.css";

export default function Loading({ text }: { text?: string }) {
  return (
    <div className="loading">
      <div className="dots-container">
        <div className="dot"></div>
        <div className="dot"></div>
        <div className="dot"></div>
      </div>
      {text && <h3 className="loading-text">{text}</h3>}
    </div>
  );
}
