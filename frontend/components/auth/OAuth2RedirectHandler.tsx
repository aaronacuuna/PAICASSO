import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Loading from "../animations/Loading";

interface OAuth2RedirectHandlerProps {
  setIsAuthenticated: (value: boolean) => void;
}

function OAuth2RedirectHandler({
  setIsAuthenticated,
}: OAuth2RedirectHandlerProps) {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get("token");
    const error = params.get("error");

    if (token) {
      localStorage.setItem("paicasso_token", token);
      setIsAuthenticated(true);
      navigate("/", { replace: true });
    } else if (error) {
      console.error("Error de login:", error);
      navigate("/login", { replace: true });
    }
  }, [location, navigate, setIsAuthenticated]);

  return (
    <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
      <Loading />
    </div>
  );
}

export default OAuth2RedirectHandler;
