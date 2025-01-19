import Layout from "./components/Layout";
import { Routes, Route } from "react-router-dom";
import Signup from "./pages/Signup";
import Main from "./pages/Main";
import Cart from "./pages/Cart";
import Login from "./pages/Login";
import NotFound404 from "./pages/NotFound404";

import ProtectedRoute from "./pages/PrivateRoute";

function App() {
  return (
    <>
      <Layout>
        <Routes>
          <Route path="/" element={<Main />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          {/* Protected Routes */}
          <Route
            path="/mypage"
            element={
              <ProtectedRoute>
                <div>마이페이지 컴포넌트</div>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <ProtectedRoute>
                <div>관리자 컴포넌트</div>
              </ProtectedRoute>
            }
          />
          <Route
            path="/logout"
            element={
              <ProtectedRoute>
                <div>로그아웃 컴포넌트</div>
              </ProtectedRoute>
            }
          />
          <Route
            path="/cart"
            element={
              <ProtectedRoute>
                <Cart />
              </ProtectedRoute>
            }
          />
          <Route path="/*" element={<NotFound404 />} />
        </Routes>
      </Layout>
    </>
  );
}

export default App;
