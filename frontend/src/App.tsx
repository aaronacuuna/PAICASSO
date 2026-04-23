import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "../components/screens/Login";
import Home from "../components/screens/Home";
import Configuration from "../components/screens/Configuration";
import Navbar from "../components/layout/Navbar";
import Analysis from "../components/screens/Analysis";
import { useState } from "react";
import OAuth2RedirectHandler from "../components/auth/OAuth2RedirectHandler";

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(
    !!localStorage.getItem("paicasso_token"),
  );

  return (
    <BrowserRouter>
      {isAuthenticated && <Navbar setIsAuthenticated={setIsAuthenticated} />}

      <Routes>
        <Route 
          path="/oauth2/redirect" 
          element={<OAuth2RedirectHandler setIsAuthenticated={setIsAuthenticated} />} 
        />

        <Route
          path="/login"
          element={!isAuthenticated ? <Login /> : <Navigate to="/" />}
        />
        <Route
          path="/settings"
          element={
            isAuthenticated ? <Configuration /> : <Navigate to="/login" />
          }
        />
        <Route
          path="/analysis/:repoId"
          element={isAuthenticated ? <Analysis /> : <Navigate to="/login" />}
        />
        <Route
          path="/*"
          element={isAuthenticated ? <Home /> : <Navigate to="/login" />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
