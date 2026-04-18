import { useState, useRef, useEffect } from "react";
import { FaPlus, FaArrowRight } from "react-icons/fa";
import "../../styles/screens/Chat.css";
import LoadingChat from "../animations/LoadingChat";
import { TypewriterText } from "../animations/TypewriterText";
import type { Issue } from "../screens/Analysis";

interface Message {
  id: number;
  text: string;
  sender: "user" | "assistant";
}

interface ChatProps {
  incomingMessage?: string | null;
  onIncomingMessageHandled?: () => void;
  selectedIssue?: Issue | null;
}

interface PredefinedPrompt {
  title: string;
  prompt: string;
}

export default function Chat({
  incomingMessage,
  onIncomingMessageHandled,
  selectedIssue,
}: ChatProps) {
  const [prompt, setPrompt] = useState("");
  const [contextAdded, setContextAdded] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const lastHandledMsgRef = useRef<string | null>(null);
  const predefinedPrompts: PredefinedPrompt[] = [
    {
      title: "Analizar seguridad del código",
      prompt:
        "¿Puedes revisar este código y decirme si encuentras vulnerabilidades de seguridad?",
    },
    {
      title: "Optimizar para mayor rendimiento",
      prompt:
        "¿Puedes revisar este código y sugerirme optimizaciones para mejorar su rendimiento?",
    },
    {
      title: "Mejorar legibilidad",
      prompt:
        "¿Puedes revisar este código y sugerirme cambios para mejorar su legibilidad y mantenibilidad?",
    },
    {
      title: "Crear tests unitarios",
      prompt:
        "¿Puedes revisar este código y sugerirme casos de prueba unitarios que debería tener para asegurar su correcto funcionamiento?",
    },
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isLoading]);

  function handleSendMessage(messageText?: string) {
    if (isLoading) return;

    const textToSend = messageText || prompt.trim();
    if (!textToSend) return;

    const newUserMsg: Message = {
      id: Date.now(),
      text: textToSend,
      sender: "user",
    };

    setMessages((prev) => [...prev, newUserMsg]);
    setPrompt("");
    setIsLoading(true);

    setTimeout(() => {
      const aiResponse: Message = {
        id: Date.now() + 1,
        text: "He revisado el contexto que me comentas. Esta es una respuesta mockeada de la IA respecto a tu consulta.",
        sender: "assistant",
      };
      setMessages((prev) => [...prev, aiResponse]);
      setIsLoading(false);
    }, 1000);
  }

  useEffect(() => {
    if (incomingMessage && incomingMessage !== lastHandledMsgRef.current) {
      lastHandledMsgRef.current = incomingMessage;
      if (!isLoading) {
        handleSendMessage(incomingMessage);
      }
      onIncomingMessageHandled?.();
    } else if (!incomingMessage) {
      lastHandledMsgRef.current = null;
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [incomingMessage, onIncomingMessageHandled, isLoading]);

  return (
    <div className="panel-content chat-content">
      <div className="chat-messages">
        {messages.length === 0 ? (
          <>
            <h3>Asistente Inteligente</h3>
            <p>
              Tu asistente de código está configurado correctamente. ¿En qué
              puedo ayudarte hoy?
            </p>
            <div className="prompts-container">
              {predefinedPrompts.map((p) => (
                <button
                  key={p.title}
                  className="predefined-prompt"
                  onClick={() => {
                    if (!isLoading) {
                      setPrompt(p.prompt);
                      handleSendMessage(p.prompt);
                    }
                  }}
                >
                  {p.title}
                </button>
              ))}
            </div>
          </>
        ) : (
          <div className="messages-list">
            {messages.map((msg) => (
              <div key={msg.id} className={`message ${msg.sender}`}>
                {msg.sender === "assistant" ? (
                  <TypewriterText text={msg.text} onTyping={scrollToBottom} />
                ) : (
                  msg.text
                )}
              </div>
            ))}
            {isLoading && <LoadingChat />}
            <div ref={messagesEndRef} />
          </div>
        )}
      </div>

      <div className="chat-input-area">
        {selectedIssue && (
          <div className="chat-context">
            <button
              className="add-context-btn"
              onClick={() => setContextAdded(!contextAdded)}
            >
              <FaPlus /> Añadir
            </button>
            <span className={`context-label ${contextAdded ? "added" : ""}`}>
              {selectedIssue?.file.name}
            </span>
          </div>
        )}

        <div className="chat-input-container">
          <textarea
            className="chat-input"
            value={prompt}
            rows={1}
            onChange={(e) => {
              setPrompt(e.target.value);
              e.target.style.height = "auto";
              e.target.style.height = `${e.target.scrollHeight}px`;
            }}
            onKeyDown={(e) => {
              if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                if (prompt.trim() && !isLoading) {
                  handleSendMessage();
                  setPrompt("");
                  e.currentTarget.style.height = "auto";
                }
              }
            }}
            placeholder="Pide una explicación..."
          />
          <button
            className="send-btn"
            onClick={() => {
              if (prompt.trim() && !isLoading) {
                handleSendMessage();
                setPrompt("");
              }
            }}
          >
            <FaArrowRight />
          </button>
        </div>
      </div>
    </div>
  );
}
