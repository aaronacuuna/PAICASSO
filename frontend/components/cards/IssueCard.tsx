import type { Issue } from "../screens/Analysis";
import "../../styles/cards/IssueCard.css";

interface IssueCardProps {
  issue: Issue;
  onClick: () => void;
  selected: boolean;
}

export default function IssueCard({
  issue,
  onClick,
  selected,
}: IssueCardProps) {
  return (
    <div
      key={issue.id}
      className={`issue-item ${selected ? "selected" : ""}`}
      onClick={onClick}
    >
      <h4>{issue.title}</h4>
      <p>
        {issue.file.name}:{issue.line}
      </p>
    </div>
  );
}
