import { useNavigate } from "react-router-dom";

const Mypage = () => {
  const navigate = useNavigate();

  const handleNewProduct = () => {
    navigate("/mypage/orders");
  };

  return (
    <div className="flex flex-col items-center bg-gray-100 mt-5">
      <h1 className="text-2xl font-bold mb-8">마이페이지</h1>
      <div className="space-y-4 w-full px-4">
        {/* 상품 등록 버튼 */}
        <button
          onClick={handleNewProduct}
          className="w-full py-4 bg-gray-200 rounded-lg shadow-md hover:bg-gray-300"
        >
          주문 목록 조회
        </button>
        <button
          className="w-full py-4 bg-gray-200 rounded-lg shadow-md hover:bg-gray-300 cursor-not-allowed"
          disabled
        >
          회원 정보 수정
        </button>
        <button
          className="w-full py-4 bg-gray-200 rounded-lg shadow-md hover:bg-gray-300 cursor-not-allowed"
          disabled
        >
          회원 탈퇴
        </button>
        <button
          className="w-full py-4 bg-gray-200 rounded-lg shadow-md hover:bg-gray-300 cursor-not-allowed"
          disabled
        >
          비밀번호 변경
        </button>
      </div>
    </div>
  );
};

export default Mypage;
