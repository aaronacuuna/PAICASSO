import Login from "../components/screens/Login";
import Home from "../components/screens/Home";

function App() {
  const isAuthenticated = false;
  return <>{isAuthenticated ? <Home /> : <Login />}</>;
}

export default App;
