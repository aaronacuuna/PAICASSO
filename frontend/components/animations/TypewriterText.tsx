import { useState, useEffect } from "react";

interface TypewriterTextProps {
  text: string;
  onTyping: () => void;
}

export function TypewriterText({ text, onTyping }: TypewriterTextProps) {
  const [displayedText, setDisplayedText] = useState("");

  useEffect(() => {
    let i = 0;
    const timer = setInterval(() => {
      if (i < text.length) {
        setDisplayedText(text.slice(0, i + 1));
        i++;
        onTyping();
      } else {
        clearInterval(timer);
      }
    }, 10);
    return () => clearInterval(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [text]);

  return <>{displayedText}</>;
}
