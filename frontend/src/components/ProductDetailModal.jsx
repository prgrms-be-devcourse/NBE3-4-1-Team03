import PropTypes from "prop-types";
import useApi from "../hooks/useApi";
import Error from "./Error";
import Loading from "./Loading";
import { useState, useEffect } from "react";

const ProductDetailModal = ({ productId, onClose }) => {
  const { request } = useApi();
  const [productInfo, setProductInfo] = useState(null);
  const [error, setError] = useState(null);

  // 날짜 포맷 함수
  const formatDate = (date) => {
    return new Date(date).toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    });
  };

  useEffect(() => {
    const fetchProductDetails = async () => {
      try {
        const response = await request(`/products/${productId}`);
        console.log(response.data);
        setProductInfo(response.data);
        setError(null);
      } catch (err) {
        console.error(`요청 실패: ${err}`);
        setError(err);
      }
    };

    fetchProductDetails();
  }, [productId]);

  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center"
      onClick={onClose}
    >
      <div
        className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full"
        onClick={(e) => e.stopPropagation()} // 모달 내부 클릭 시 닫히지 않도록
      >
        {error ? (
          <Error errorMessage="상품 정보를 불러오는데 실패했습니다. 나중에 다시 시도해주세요." />
        ) : productInfo ? (
          // 데이터 로드 성공 시
          <div>
            <img
              alt={productInfo.name}
              src="https://i.imgur.com/HKOFQYa.jpeg"
              className="w-full h-48 object-cover rounded-lg"
            />
            <h1 className="text-xl font-semibold mt-4 text-gray-800">
              {productInfo.name}
            </h1>
            <p className="text-gray-600 mt-2">{productInfo.description}</p>
            <div className="mt-4">
              <p className="text-lg text-gray-800">
                <span className="font-bold">가격:</span> {productInfo.price}원
              </p>
              <p className="text-lg text-gray-800">
                <span className="font-bold">재고수량:</span>{" "}
                {productInfo.amount}
              </p>
              <p className="text-lg text-gray-800">
                <span className="font-bold">판매상태:</span>{" "}
                {productInfo.amount === 0
                  ? "재고 없음"
                  : productInfo.status
                  ? "판매"
                  : "판매 중지"}
              </p>
            </div>
            <hr className="my-4" />
            <div className="text-sm text-gray-600">
              <p>
                <span className="font-bold">등록일:</span>{" "}
                {formatDate(productInfo.created_date)}
              </p>
              <p>
                <span className="font-bold">수정일:</span>{" "}
                {formatDate(productInfo.modified_date)}
              </p>
            </div>
          </div>
        ) : (
          <Loading />
        )}
        <button
          onClick={onClose}
          className="mt-4 py-2 px-4 bg-red-600 text-white rounded-md hover:bg-red-700"
        >
          닫기
        </button>
      </div>
    </div>
  );
};

ProductDetailModal.propTypes = {
  productId: PropTypes.number.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default ProductDetailModal;
