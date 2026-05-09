import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter";
import { oneLight } from "react-syntax-highlighter/dist/esm/styles/prism";

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
          pre({ children }: any) {
            const codeChild = Array.isArray(children) ? children[0] : children;
            const className: string = codeChild?.props?.className || "";
            const match = /language-(\w+)/.exec(className);
            const rawChildren = codeChild?.props?.children;
            const rawText = Array.isArray(rawChildren)
              ? rawChildren.join("")
              : String(rawChildren ?? "");
            const dedent = (src: string) => {
              const lines = src.replace(/\n$/, "").split("\n");
              const indents = lines
                .filter((l) => l.trim().length > 0)
                .map((l) => l.match(/^[ \t]*/)?.[0].length ?? 0);
              const min = indents.length ? Math.min(...indents) : 0;
              return min > 0
                ? lines.map((l) => l.slice(min)).join("\n")
                : lines.join("\n");
            };
            const codeText = dedent(rawText);

            if (!match) {
              return <pre>{children}</pre>;
            }

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
                  style={oneLight as any}
                  language={match[1]}
                  PreTag="pre"
                  customStyle={{
                    margin: 0,
                    padding: "10px 6px 10px 8px",
                    borderRadius: "0 0 8px 8px",
                    background: "var(--code-bg)",
                    fontSize: "13px",
                    fontFamily: "var(--mono, monospace)",
                    whiteSpace: "pre",
                    overflowX: "auto",
                  }}
                  codeTagProps={{
                    style: {
                      fontFamily: "var(--mono, monospace)",
                      whiteSpace: "pre",
                      background: "transparent",
                    },
                  }}
                >
                  {codeText}
                </SyntaxHighlighter>
              </div>
            );
          },
          code({ className, children }: any) {
            return (
              <code className={`inline-code ${className || ""}`}>
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
