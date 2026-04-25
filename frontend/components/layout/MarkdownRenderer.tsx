import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter";
import { vscDarkPlus } from "react-syntax-highlighter/dist/esm/styles/prism";

const remarkTightLists = () => (tree: any) => {
  const visit = (node: any) => {
    if (node.type === "list") {
      node.spread = false;
      node.children?.forEach((child: any) => {
        child.spread = false;
      });
    }
    node.children?.forEach(visit);
  };
  visit(tree);
};

interface MarkdownRendererProps {
  text: string;
}

export default function MarkdownRenderer({ text }: MarkdownRendererProps) {
  return (
    <div className="markdown-body">
      <ReactMarkdown
        remarkPlugins={[remarkGfm, remarkTightLists]}
        components={{
          li({ children }) {
            const content = Array.isArray(children)
              ? children.map((child: any) => {
                  if (child?.props?.node?.tagName === "p")
                    return child.props.children;
                  return child;
                })
              : children;
            return <li>{content}</li>;
          },
          ol({ children }) {
            return (
              <ol
                style={{
                  margin: "0",
                  paddingLeft: "1.4em",
                  paddingTop: "0",
                  paddingBottom: "0",
                }}
              >
                {children}
              </ol>
            );
          },
          ul({ children }) {
            return (
              <ul
                style={{
                  margin: "0",
                  paddingLeft: "1.4em",
                  paddingTop: "0",
                  paddingBottom: "0",
                }}
              >
                {children}
              </ul>
            );
          },
          p({ children }) {
            return (
              <span style={{ display: "block", marginTop: "0.3em" }}>
                {children}
              </span>
            );
          },
          code({ inline, className, children, ...props }: any) {
            const match = /language-(\w+)/.exec(className || "");
            const codeText = String(children).replace(/\n$/, "");
            if (!inline && match) {
              return (
                <div className="custom-code-block">
                  <div className="custom-code-header">
                    <span className="language-badge">{match[1]}</span>
                    <button
                      className="copy-btn"
                      onClick={() => navigator.clipboard.writeText(codeText)}
                    >
                      Copiar
                    </button>
                  </div>
                  <SyntaxHighlighter
                    {...props}
                    style={vscDarkPlus as any}
                    language={match[1]}
                    PreTag="div"
                    wrapLongLines={true}
                    customStyle={{
                      margin: 0,
                      padding: "12px",
                      borderRadius: "0 0 8px 8px",
                      backgroundColor: "#1e1e1e",
                      fontSize: "13px",
                      whiteSpace: "pre-wrap",
                      wordBreak: "break-word",
                    }}
                  >
                    {codeText}
                  </SyntaxHighlighter>
                </div>
              );
            }
            return (
              <code {...props} className={`inline-code ${className || ""}`}>
                {children}
              </code>
            );
          },
        }}
      >
        {text}
      </ReactMarkdown>
    </div>
  );
}
