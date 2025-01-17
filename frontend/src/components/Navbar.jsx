import { Link } from "react-router-dom";

// TODO: 현재 로그인 상태에 맞춰서 동적으로, 리스트 버튼들 변화하도록 설정 필요.

const Navbar = () => {
  return (
    <header className="bg-light border-b border-gray-500 container mx-auto mt-2 mb-2 px-2 sm:px-6 lg:px-8">
      <nav className="relative flex items-center justify-between h-16">
        {/* 홈 화면 이동 */}
        <Link to="/" className="text-2xl font-bold text-gray-800">
          홈
        </Link>
        {/* 버튼 동적 리스트 */}
        <ul className="flex items-center space-x-4">
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
              to="/logout"
              className="text-gray-600 hover:text-gray-800 px-3 py-2 rounded-md text-sm font-medium"
            >
              로그아웃
            </Link>
          </li>
          <li>
            <Link
              to="/admin"
              className="text-gray-600 hover:text-gray-800 px-3 py-2 rounded-md text-sm font-medium"
            >
              상품 등록
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
    </header>
  );
};

export default Navbar;
