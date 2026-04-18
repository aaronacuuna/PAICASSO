import "../../styles/animations/LoadingChat.css";

export default function LoadingChat() {
  return (
    <div className="message assistant loading-chat-wrapper">
      <div className="loading-chat-dots">
        <div className="dot"></div>
        <div className="dot"></div>
        <div className="dot"></div>
      </div>
    </div>
  );
}
