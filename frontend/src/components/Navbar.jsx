import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import { useState } from "react";
import { useNavigate } from "react-router-dom";

import useApi from "../hooks/useApi";
import OkCancelModal from "./OkCancelModal"; // OkCancelModal 컴포넌트 가져오기

const Navbar = () => {
  const { isAuthenticated, logout } = useAuth();
  const { request } = useApi();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await request("/logout", "POST", {}, {}, true);
      logout();
      setIsModalOpen(false);
      navigate("/");
    } catch (error) {
      console.error("로그아웃 실패:", error);
    }
  };

  const handleCancel = () => {
    setIsModalOpen(false);
  };

  return (
    <header className="bg-light border-b border-gray-500 container mx-auto mt-2 mb-2 px-2 sm:px-6 lg:px-8">
      <nav className="relative flex items-center justify-between h-16">
        {/* 홈 화면 이동 */}
        <Link to="/" className="text-2xl font-bold text-gray-800">
          홈
        </Link>
        {/* 버튼 동적 리스트 */}
        <ul className="flex items-center space-x-4">
          {!isAuthenticated && (
            <>
              <li>
                <Link
                  to="/login"
                  className="text-gray-600 hover:text-gray-800 px-3 py-2 rounded-md text-sm font-medium"
                >
                  로그인
                </Link>
              </li>
              <li>
                <Link
                  to="/signup"
                  className="text-gray-600 hover:text-gray-800 px-3 py-2 rounded-md text-sm font-medium"
                >
                  회원가입
                </Link>
              </li>
            </>
          )}

          {isAuthenticated && (
            <>
              <li>
                <button
                  onClick={() => setIsModalOpen(true)} // 모달 열기
                  className="text-gray-600 hover:text-gray-800 px-3 py-2 rounded-md text-sm font-medium"
                >
                  로그아웃
                </button>
              </li>
              <li>
                <Link
                  to="/admin"
                  className="text-gray-600 hover:text-gray-800 px-3 py-2 rounded-md text-sm font-medium"
                >
                  관리자 페이지
                </Link>
              </li>
            </>
          )}
          <li>
            <Link
              to="/mypage"
              className="text-gray-600 hover:text-gray-800 px-3 py-2 rounded-md text-sm font-medium"
            >
              마이페이지
            </Link>
          </li>
          <li>
            <Link
              to="/cart"
              className="text-gray-600 hover:text-gray-800 px-3 py-2 rounded-md text-sm font-medium"
            >
              장바구니
            </Link>
          </li>
        </ul>
      </nav>

      {/* 로그아웃 확인 모달 */}
      <OkCancelModal
        isOpen={isModalOpen}
        message="로그아웃 하시겠습니까?"
        onConfirm={handleLogout} // 로그아웃 확정 시 실행
        onCancel={handleCancel} // 취소 시 실행
        okButtonMessage="확인"
        cancelButtonMessage="취소"
      />
    </header>
  );
};

export default Navbar;
