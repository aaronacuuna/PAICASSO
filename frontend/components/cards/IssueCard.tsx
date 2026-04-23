import type { Issue } from "../screens/Analysis";
import "../../styles/cards/IssueCard.css";
import { FiShield, FiInfo } from "react-icons/fi";
import { BiBug } from "react-icons/bi";

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
  const getIcon = (type: string) => {
    switch (type.toUpperCase()) {
      case "BUG":
        return <BiBug className="issue-card-icon" size={20} />;
      case "VULNERABILITY":
        return <FiShield className="issue-card-icon" size={20} />;
      case "CODE_SMELL":
        return <FiInfo className="issue-card-icon" size={20} />;
      default:
        return <FiInfo className="issue-card-icon" size={20} />;
    }
  };

  const fileName = issue.file.name.split("/").pop() || issue.file.name;

  return (
    <div
      className={`issue-item ${selected ? "selected" : ""}`}
      onClick={onClick}
    >
      <div className="issue-item-left">
        {getIcon(issue.type)}
        <div className="issue-item-content">
          <h4 className="issue-title" title={issue.title}>
            {issue.title}
          </h4>
          <div className="issue-meta-row">
            <span className="issue-meta" title={fileName}>
              {fileName}
            </span>
            <span className="issue-line">{issue.line}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
