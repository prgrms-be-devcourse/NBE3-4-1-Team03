import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const PrivateRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    alert("로그인이 필요합니다.");
    return <Navigate to="/login" />;
  }

  return children;
};

export default PrivateRoute;
