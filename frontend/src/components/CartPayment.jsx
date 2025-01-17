import { useNavigate } from "react-router-dom";
import { useCartContext } from "../context/CartContext";
import useApi from "../hooks/useApi";
import { useState } from "react";
import OkCancelModal from "./OkCancelModal";

const CartPayment = () => {
  const { cart, dispatch } = useCartContext();
  const navigate = useNavigate();
  const totalItems = cart.reduce((acc, item) => acc + item.count, 0);
  const totalAmount = cart.reduce((acc, item) => acc + item.totalprice, 0);
  const formattedAmount = new Intl.NumberFormat().format(totalAmount);
  const { request } = useApi();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [confirmMessage, setConfirmMessage] = useState("");

  const handleMakeOrder = async (e) => {
    e.preventDefault();

    if (totalAmount === 0) {
      alert("상품을 먼저 장바구니에 담아주세요!");
      navigate("/");
      return;
    }

    setConfirmMessage(`총금액은 ${totalAmount}원 입니다. 주문하시겠습니까?`);
    setIsModalOpen(true);
  };

  const handleConfirmOrder = async () => {
    const productData = {
      product_info: cart.map((item) => ({
        product_id: item.id,
        amount: item.count,
      })),
    };

    try {
      const response = await request("/orders", "POST", productData);
      alert(response.message);
      navigate("/mypage");
    } catch (err) {
      alert(err);
    }

    dispatch({ type: "CLEAR_CART" });
    setIsModalOpen(false);
  };

  const handleCancelOrder = () => {
    setIsModalOpen(false);
  };

  return (
    <section className="bg-light border-b border-gray-500 container mx-auto mt-4 mb-2">
      <h2 className="text-2xl font-semibold text-gray-900 mb-6">결제 정보</h2>

      <div className="mb-4">
        <p className="text-lg text-gray-800">
          전체 주문 수량:{" "}
          <span className="font-bold text-blue-600">{totalItems}개</span>
        </p>
        <p className="text-lg text-gray-800">
          총 결제 금액:{" "}
          <span className="font-bold text-red-600">{formattedAmount}원</span>
        </p>
      </div>

      <div className="mt-6 mb-4">
        <button
          className="py-3 px-6 bg-green-500 text-white rounded-lg hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-50"
          onClick={handleMakeOrder}
        >
          주문하기
        </button>
      </div>

      {/* OkCancelModal */}
      <OkCancelModal
        isOpen={isModalOpen}
        message={confirmMessage}
        onConfirm={handleConfirmOrder}
        onCancel={handleCancelOrder}
      />
    </section>
  );
};

export default CartPayment;
