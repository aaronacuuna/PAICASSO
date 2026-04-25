import { useState, useRef, useEffect } from "react";
import { FaPlus, FaArrowRight } from "react-icons/fa";
import "../../styles/screens/Chat.css";
import LoadingChat from "../animations/LoadingChat";
import { TypewriterText } from "../animations/TypewriterText";
import type { Issue } from "../screens/Analysis";
import { apiFetch } from "../../utils/apiFetch";
import MarkdownRenderer from "./MarkdownRenderer";
import { predefinedPrompts } from "../../utils/prompts";
import Loading from "../animations/Loading";

interface Message {
  id: number;
  text: string;
  sender: "user" | "assistant";
  isNew?: boolean;
}

interface ApiMessage {
  id: number;
  contenido: string;
  remitente: string;
  timestamp: string;
}

interface ChatProps {
  repoId?: string;
  incomingMessage?: string | null;
  onIncomingMessageHandled?: () => void;
  selectedIssue?: Issue | null;
}

export default function Chat({
  repoId,
  incomingMessage,
  onIncomingMessageHandled,
  selectedIssue,
}: ChatProps) {
  const [prompt, setPrompt] = useState("");
  const [contextAdded, setContextAdded] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingMessages, setIsLoadingMessages] = useState(false);
  const [sesionId, setSesionId] = useState<number | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const lastHandledMsgRef = useRef<string | null>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isLoading]);

  useEffect(() => {
    if (repoId) {
      const loadMessages = async () => {
        try {
          setIsLoadingMessages(true);
          const res = await apiFetch(`/api/llm/sesion/${repoId}/mensajes`);
          if (res) {
            setSesionId(res.sesionId);
            setMessages(
              res.mensajes.map((m: ApiMessage) => ({
                id: m.id,
                text: m.contenido,
                sender: m.remitente === "usuario" ? "user" : "assistant",
                isNew: false,
              })),
            );
          }
        } catch (error) {
          console.error("Error al cargar mensajes del chat:", error);
        } finally {
          setIsLoadingMessages(false);
        }
      };
      loadMessages();
    }
  }, [repoId]);

  async function handleSendMessage(messageText?: string) {
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

    try {
      const payload = {
        repoId: repoId ? parseInt(repoId) : undefined,
        sesionId,
        mensaje: textToSend,
        componentKey:
          contextAdded && selectedIssue ? selectedIssue.file.name : null,
        lineaError: contextAdded && selectedIssue ? selectedIssue.line : null,
      };

      const data = await apiFetch("/api/llm/analizar", {
        method: "POST",
        body: JSON.stringify(payload),
      });

      if (data) {
        setSesionId(data.sesionId);
        const aiResponse: Message = {
          id: Date.now() + 1,
          text: data.respuesta,
          sender: "assistant",
          isNew: true,
        };
        setMessages((prev) => [...prev, aiResponse]);
      }
    } catch (error) {
      console.error("Error al enviar el mensaje:", error);
    } finally {
      setIsLoading(false);
    }
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
        {isLoadingMessages ? (
          <div className="loading-messages">
            <Loading />
          </div>
        ) : messages.length === 0 ? (
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
                  msg.isNew ? (
                    <TypewriterText text={msg.text} onTyping={scrollToBottom}>
                      {(text) => <MarkdownRenderer text={text} />}
                    </TypewriterText>
                  ) : (
                    <MarkdownRenderer text={msg.text} />
                  )
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
              {selectedIssue?.file.name.split("/").pop()}
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
