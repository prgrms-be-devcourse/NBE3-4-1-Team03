import { useNavigate } from "react-router-dom";

const Admin = () => {
  const navigate = useNavigate();

  const handleNewProduct = () => {
    navigate("/admin/newProduct");
  };

  return (
    <div className="flex flex-col items-center bg-gray-100 mt-5">
      <h1 className="text-2xl font-bold mb-8">관리자 페이지</h1>
      <div className="space-y-4 w-full px-4">
        {/* 상품 등록 버튼 */}
        <button
          onClick={handleNewProduct}
          className="w-full py-4 bg-gray-200 rounded-lg shadow-md hover:bg-gray-300"
        >
          상품 등록
        </button>
        {/* 상품 삭제 버튼 (비활성화) */}
        <button
          className="w-full py-4 bg-gray-200 rounded-lg shadow-md hover:bg-gray-300 cursor-not-allowed"
          disabled
        >
          상품 삭제
        </button>
        {/* 상품 수정 버튼 (비활성화) */}
        <button
          className="w-full py-4 bg-gray-200 rounded-lg shadow-md hover:bg-gray-300 cursor-not-allowed"
          disabled
        >
          상품 수정
        </button>
      </div>
    </div>
  );
};

export default Admin;
