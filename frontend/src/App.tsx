import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "../components/screens/Login";
import Home from "../components/screens/Home";
import Configuration from "../components/screens/Configuration";
import Navbar from "../components/layout/Navbar";

function App() {
  const isAuthenticated = true;

  return (
    <BrowserRouter>
      {isAuthenticated && <Navbar />}

      <Routes>
        <Route
          path="/login"
          element={!isAuthenticated ? <Login /> : <Navigate to="/" />}
        />
        <Route
          path="/settings/:repoName"
          element={isAuthenticated ? <Configuration /> : <Navigate to="/login" />}
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
