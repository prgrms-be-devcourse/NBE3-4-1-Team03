import { useOrderContext } from "../context/OrderContext";

const MyOrderList = () => {
  const { state } = useOrderContext();

  const formatPrice = (price) => {
    return `${price.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",")}원`;
  };

  const handleCancelOrder = (orderNumber) => {
    alert(`주문 번호 ${orderNumber} 이(가) 취소되었습니다.`);
  };

  if (!state || !Array.isArray(state.data)) {
    return <p className="text-center text-gray-500">Loading...</p>;
  }

  return (
    <section className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-2xl font-bold text-gray-800 mb-6">주문 목록</h1>

        {state.data.length === 0 ? (
          <p className="text-center text-gray-500">No orders found.</p>
        ) : (
          <div className="space-y-6">
            {state.data.map((order, index) => (
              <article
                key={index}
                className="bg-white shadow-md rounded-lg border border-gray-200 overflow-hidden"
              >
                <header className="p-4 border-b border-gray-200 bg-gray-100">
                  <h2 className="text-lg font-semibold text-gray-700">
                    Order Number: {order.orderNumber}
                  </h2>
                </header>

                <div className="p-4">
                  <p className="text-gray-600 mb-2">
                    <strong>주문자:</strong> {order.name}
                  </p>
                  <p className="text-gray-600 mb-2">
                    <strong>총 주문 수량:</strong> {order.totalAmount}
                  </p>
                  <p className="text-gray-600 mb-2">
                    <strong>총 주문 가격:</strong>{" "}
                    {formatPrice(order.totalPrice)}
                  </p>
                  <p className="text-gray-600 mb-2">
                    <strong>배송지 주소:</strong> {order.orderAddress}
                  </p>
                  <p className="text-gray-600 mb-2">
                    <strong>주문 상태:</strong> {order.orderStatus}
                  </p>
                  <p className="text-gray-600 mb-4">
                    <strong>주문 생성일:</strong> {order.createdDate}
                  </p>

                  <h3 className="text-gray-600 mb-4">주문 제품 목록:</h3>
                  <ul className="space-y-4">
                    {order.orderList.map((item, idx) => (
                      <li
                        key={idx}
                        className="flex items-center gap-4 bg-gray-50 p-3 rounded-lg shadow-sm border border-gray-200"
                      >
                        <img
                          alt={item.name}
                          src="https://i.imgur.com/HKOFQYa.jpeg"
                          className="w-16 h-16 object-cover rounded-md"
                        />
                        <div className="text-sm flex-1">
                          <p className="font-medium text-gray-700">
                            {item.name}
                          </p>
                          <p className="text-gray-600">
                            {formatPrice(item.price)} x {item.amount} ={" "}
                            {formatPrice(item.totalPrice)}
                          </p>
                        </div>
                      </li>
                    ))}
                  </ul>
                  <button
                    onClick={() => handleCancelOrder(order.orderNumber)}
                    className="mt-2 px-4 py-2 bg-red-500 text-white text-sm font-semibold rounded-md shadow hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-400"
                  >
                    주문 취소
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </div>
    </section>
  );
};

export default MyOrderList;
