import GitHubButton from "../buttons/GitHubButton";
import { PiCheckCircleFill, PiShieldCheckFill } from "react-icons/pi";
import { FaBrain } from "react-icons/fa";
import "../../styles/screens/Login.css";

function Login() {
  return (
    <>
      <div className="container">
        {/* Left side */}
        <div className="inner-container">
          <h1>Eleva la calidad de tu código sin frustraciones.</h1>
          <h2>
            Conecta tu cuenta y descubre cómo la IA convierte el análisis
            técnico de SonarQube en explicaciones claras y accionables.
          </h2>
          <div className="bulletIcons">
            <div className="bulletIcon">
              <PiCheckCircleFill color="#E27D60" size={32} />
              <p>Análisis estático instantáneo con un clic</p>
            </div>
            <div className="bulletIcon">
              <FaBrain color="#E27D60" size={32} />
              <p>Feedback interactivo adaptado a tu nivel técnico</p>
            </div>
            <div className="bulletIcon">
              <PiShieldCheckFill color="#E27D60" size={32} />
              <p>Seguridad total. No almacenamos tu código</p>
            </div>
          </div>
        </div>

        {/* Right side */}
        <div className="inner-container">
          <img src="/favicon.svg" alt="Logo" className="logo" />
          <h1>PAICASSO</h1>
          <h2>Tu asistente para un código de calidad</h2>
          <GitHubButton
            text="Iniciar sesión con GitHub"
            onClick={() => alert("Login con GitHub")}
          />
        </div>
      </div>
    </>
  );
}

export default Login;
