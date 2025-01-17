import { useState } from "react";
import { useProductContext } from "../context/ProductContext";
import ProductDetailModal from "./ProductDetailModal";
import Error from "./Error";
import Loading from "./Loading";

const ProductList = () => {
  const { state, loading, error, setPage } = useProductContext();
  const { product_info, currentPage, totalPages, hasNext, hasPrevious } = state; // 페이지네이션 및 데이터 필요한거 뽑아쓰기.

  const [quantities, setQuantities] = useState({});
  const [modalProductId, setModalProductId] = useState(null); // 모달 상태 관리

  const handleQuantityChange = (productId, change) => {
    setQuantities((prev) => ({
      ...prev,
      [productId]: Math.max(1, (prev[productId] || 1) + change),
    }));
  };

  const handleAddToCart = (product) => {
    const { product_id, product_name, product_price } = product;
    const count = quantities[product_id] || 1; // 기본값은 1

    const newItem = {
      id: product_id,
      name: product_name,
      price: product_price,
      totalprice: product_price * count,
      count: count,
    };

    const existingCart = JSON.parse(localStorage.getItem("cart")) || [];
    const itemIndex = existingCart.findIndex((item) => item.id === product_id);

    if (itemIndex > -1) {
      existingCart[itemIndex].count += count;
      existingCart[itemIndex].totalprice =
        existingCart[itemIndex].price * existingCart[itemIndex].count;
    } else {
      existingCart.push(newItem);
    }

    alert("장바구니에 담겼습니다!");
    localStorage.setItem("cart", JSON.stringify(existingCart));
  };

  const openModal = (productId) => {
    setModalProductId(productId); // 모달 열림 상태와 ID 설정
  };

  const closeModal = () => {
    setModalProductId(null); // 모달 닫기
  };

  if (loading) return <Loading />;
  if (error) return <Error errorMessage={error} />;

  return (
    <section className="container mx-auto mt-4 mb-2">
      <h2 className="sr-only">Products</h2>

      <div className="grid grid-cols-1 gap-x-6 gap-y-10 sm:grid-cols-2 lg:grid-cols-5 xl:grid-cols-5 xl:gap-x-8">
        {product_info.map((product) => (
          <div
            key={product.product_id}
            className="group relative border border-gray-300 rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-200 p-1"
          >
            {/* 이미지 위에 텍스트 표시 */}
            {product.product_status === false && (
              <div className="absolute top-2 left-2 text-red-600 font-bold text-lg bg-white px-2 py-1 rounded-lg z-10">
                판매 중지
              </div>
            )}
            {product.product_amount === 0 && (
              <div className="absolute top-2 left-2 text-red-600 font-bold text-lg bg-white px-2 py-1 rounded-lg z-10">
                재고 없음
              </div>
            )}

            <img
              src="https://i.imgur.com/HKOFQYa.jpeg"
              alt={product.product_name}
              className="aspect-square w-full rounded-lg bg-gray-200 object-cover group-hover:opacity-75 xl:aspect-[7/8]"
              onClick={() => openModal(product.product_id)} // 상품 클릭 시 모달 열기
            />
            <h3 className="mt-4 text-sm text-gray-700">
              {product.product_name}
            </h3>
            <p className="mt-1 text-lg font-medium text-gray-900">
              {product.product_price}원
            </p>

            {/* 수량 선택 버튼 */}
            <div className="mt-2 flex items-center">
              <button
                onClick={() => handleQuantityChange(product.product_id, -1)}
                className="px-2 py-1 bg-gray-200 rounded-md"
              >
                -
              </button>
              <span className="mx-2">
                {quantities[product.product_id] || 1}
              </span>
              <button
                onClick={() => handleQuantityChange(product.product_id, 1)}
                className="px-2 py-1 bg-gray-200 rounded-md"
              >
                +
              </button>
            </div>

            {/* 장바구니 담기 버튼 */}
            <button
              onClick={() => handleAddToCart(product)}
              disabled={
                product.product_status === false || product.product_amount === 0
              }
              className={`mt-4 py-2 px-6 rounded-md ${
                product.product_status === false || product.product_amount === 0
                  ? "bg-gray-400 text-gray-600 cursor-not-allowed"
                  : "bg-blue-600 text-white hover:bg-blue-700"
              }`}
            >
              장바구니 담기
            </button>
          </div>
        ))}
      </div>

      {/* 페이지네이션 */}
      <nav className="mt-4">
        <ul className="flex justify-center space-x-2">
          {hasPrevious && (
            <li className="page-item">
              <button
                onClick={() => setPage(currentPage - 1)}
                className="page-link px-4 py-2 bg-gray-800 text-white rounded-lg"
              >
                이전
              </button>
            </li>
          )}

          {/* 페이지 번호 목록 */}
          {[...Array(totalPages)].map((_, index) => {
            const page = index + 1; // 페이지 번호는 1부터 시작
            if (page >= currentPage - 5 && page <= currentPage + 5) {
              return (
                <li
                  key={page}
                  className={`page-item ${
                    page === currentPage
                      ? "bg-blue-600 text-white"
                      : "bg-gray-200"
                  } px-3 py-2 rounded-lg`}
                >
                  <button onClick={() => setPage(page)} className="page-link">
                    {page}
                  </button>
                </li>
              );
            }
            return null;
          })}

          {/* 다음 버튼 */}
          {hasNext && (
            <li className="page-item">
              <button
                onClick={() => setPage(currentPage + 1)}
                className="page-link px-4 py-2 bg-gray-800 text-white rounded-lg"
              >
                다음
              </button>
            </li>
          )}
        </ul>
      </nav>

      {/* 모달 */}
      {modalProductId && (
        <ProductDetailModal productId={modalProductId} onClose={closeModal} />
      )}
    </section>
  );
};

export default ProductList;
