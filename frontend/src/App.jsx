import Layout from "./components/Layout";
import { Routes, Route } from "react-router-dom";
import Signup from "./pages/Signup";
import Main from "./pages/Main";
import Cart from "./pages/Cart";
import Login from "./pages/Login";
import Admin from "./pages/Admin";
import NotFound404 from "./pages/NotFound404";
import NewProduct from "./pages/NewProduct";
import Mypage from "./pages/Mypage";
import MyOrderList from "./pages/MyOrderList";

import { OrderProvider } from "./context/OrderContext";

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
                <Mypage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/mypage/orders"
            element={
              <ProtectedRoute>
                <OrderProvider>
                  <MyOrderList />
                </OrderProvider>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <ProtectedRoute>
                <Admin />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/newProduct"
            element={
              <ProtectedRoute>
                <NewProduct />
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
