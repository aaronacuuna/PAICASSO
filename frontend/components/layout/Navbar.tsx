import { Link, useNavigate } from "react-router-dom";
import { useState, useRef, useEffect } from "react";
import { useJwtToken } from "../../hooks/useJwtToken";
import { FiSettings, FiLogOut } from "react-icons/fi";
import "../../styles/layout/Navbar.css";

interface NavbarProps {
  setIsAuthenticated: (value: boolean) => void;
}

function Navbar({ setIsAuthenticated }: NavbarProps) {
  const user = useJwtToken();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("paicasso_token");
    setIsAuthenticated(false);
  };

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-left">
          <Link to="/" className="logo-link">
            <img src="/paicasso.png" alt="Logo PAICASSO" className="logo-img" />
            <span className="logo-text">
              P<span className="logo-highlight">AI</span>CASSO
            </span>
          </Link>
        </div>

        <div className="navbar-right">
          {user && (
            <div className="user-menu-container" ref={dropdownRef}>
              <button
                className="user-info"
                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
              >
                <img
                  src={user.fotoPerfil}
                  alt={`Foto`}
                  className="profile-pic"
                />
                <span className="greeting">{user.nombre}</span>
                <svg
                  className={`chevron ${isDropdownOpen ? "open" : ""}`}
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M6 9l6 6 6-6" />
                </svg>
              </button>

              {isDropdownOpen && (
                <div className="dropdown-menu">
                  <button
                    onClick={() => {
                      setIsDropdownOpen(false);
                      navigate("/settings");
                    }}
                    className="dropdown-item"
                  >
                    <FiSettings size={16} />
                    Configuración
                  </button>
                  <button onClick={handleLogout} className="dropdown-item">
                    <FiLogOut size={16} />
                    Cerrar sesión
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
