import Layout from "./components/Layout";
import { Routes, Route } from "react-router-dom";

import Signup from "./pages/Signup";

function App() {
  return (
    <>
      <Layout>
        <Routes>
          <Route path="/" element={<div>메인페이지 컴포넌트</div>} />
          <Route path="/login" element={<div>로그인 페이지 컴포넌트</div>} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/mypage" element={<div>마이페이지 컴포넌트</div>} />
          <Route path="/admin" element={<div>상품 등록 컴포넌트</div>} />
          <Route path="/logout" element={<div>로그아웃 컴포넌트</div>} />
        </Routes>
      </Layout>
    </>
  );
}

export default App;
