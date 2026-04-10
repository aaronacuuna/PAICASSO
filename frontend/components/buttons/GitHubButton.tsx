import "../../styles/buttons/GitHubButton.css";

interface GitHubButtonProps {
  text: string;
  onClick?: () => void;
}

export default function GitHubButton({ text, onClick }: GitHubButtonProps) {
  return (
    <button className="github-button" onClick={onClick}>
      <div>
        <svg role="presentation" aria-hidden="true">
          <use href="/icons.svg#github-icon"></use>
        </svg>
        {text}
      </div>
    </button>
  );
}
