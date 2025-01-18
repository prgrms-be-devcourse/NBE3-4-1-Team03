import { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // 초기 로컬스토리지 확인
    const token = localStorage.getItem("Authorization");
    setIsAuthenticated(!!token);

    // storage 이벤트 핸들러
    const handleStorageChange = () => {
      const updatedToken = localStorage.getItem("Authorization");
      setIsAuthenticated(!!updatedToken);
    };

    // storage 이벤트 리스너 추가
    window.addEventListener("storage", handleStorageChange);

    return () => {
      window.removeEventListener("storage", handleStorageChange); // 리스너 정리
    };
  }, []);

  const login = () => {
    setIsAuthenticated(true);
  };

  const logout = () => {
    localStorage.removeItem("Authorization");
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
