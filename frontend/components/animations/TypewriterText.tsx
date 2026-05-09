import { useState, useEffect } from "react";

interface TypewriterTextProps {
  text: string;
  onTyping: () => void;
  children?: (typedText: string) => React.ReactNode;
}

export function TypewriterText({
  text,
  onTyping,
  children,
}: TypewriterTextProps) {
  const [displayedText, setDisplayedText] = useState("");

  useEffect(() => {
    setDisplayedText("");
    let i = 0;
    const timer = setInterval(() => {
      if (i < text.length) {
        setDisplayedText(text.slice(0, i + 1));
        i++;
        onTyping();
      } else {
        clearInterval(timer);
      }
    }, 3);
    return () => clearInterval(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [text]);

  return <>{children ? children(displayedText) : displayedText}</>;
}
