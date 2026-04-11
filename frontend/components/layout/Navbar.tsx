import { Link } from "react-router-dom";
import "../../styles/layout/Navbar.css";

function Navbar() {
  const user = {
    name: "Aaron",
    photo: "/favicon.svg",
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <Link to="/" className="logo-link">
          <img src="/favicon.svg" alt="Logo PAICASSO" className="logo-img" />
          <span className="logo-text">PAICASSO</span>
        </Link>
      </div>

      <div className="navbar-right">
        <span className="greeting">Hola, {user.name}</span>
        <img src={user.photo} alt="Foto de perfil" className="profile-pic" />
      </div>
    </nav>
  );
}

export default Navbar;
