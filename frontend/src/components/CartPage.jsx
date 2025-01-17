import { useCartContext } from "../context/CartContext";

export default function CartPage() {
  const { cart, dispatch } = useCartContext();

  const removeFromCart = (itemId) => {
    dispatch({ type: "REMOVE_ITEM", payload: itemId });
  };

  const clearCart = () => {
    dispatch({ type: "CLEAR_CART" });
  };

  const increaseQuantity = (itemId) => {
    dispatch({ type: "INCREASE_ITEM", payload: itemId });
  };

  const decreaseQuantity = (itemId) => {
    dispatch({ type: "DECREASE_ITEM", payload: itemId });
  };

  return (
    <section className="bg-light border-b border-gray-500 container mx-auto mt-4 mb-2">
      <ul role="list" className="divide-y divide-gray-100">
        {cart.length === 0 ? (
          <li className="py-5 text-center text-gray-500">
            장바구니에 아이템이 없습니다.
          </li>
        ) : (
          cart.map((item) => (
            <li key={item.id} className="flex justify-between gap-x-6 py-5">
              <div className="flex min-w-0 gap-x-4">
                <img
                  alt={item.name}
                  src="https://i.imgur.com/HKOFQYa.jpeg"
                  className="w-12 h-12 flex-none rounded-full bg-gray-50"
                />
                <div className="min-w-0 flex-auto">
                  <p className="text-sm font-semibold text-gray-900">
                    {item.name}
                  </p>
                  <p className="mt-1 text-xs text-gray-500">{item.price}원</p>
                </div>
              </div>
              <div className="hidden shrink-0 sm:flex sm:flex-col sm:items-end">
                <div className="flex items-center gap-x-2 mb-1">
                  <button
                    className="px-2 py-1 text-xs text-white bg-gray-500 rounded"
                    onClick={() => decreaseQuantity(item.id)}
                  >
                    -
                  </button>
                  <span className="text-sm text-gray-900">{item.count}</span>
                  <button
                    className="px-2 py-1 text-xs text-white bg-gray-500 rounded"
                    onClick={() => increaseQuantity(item.id)}
                  >
                    +
                  </button>
                </div>
                <p className="text-sm text-gray-900">총 {item.totalprice}원</p>
                <button
                  className="mt-1 text-xs text-red-500"
                  onClick={() => removeFromCart(item.id)}
                >
                  제거
                </button>
              </div>
            </li>
          ))
        )}
      </ul>

      <div className="mt-4 mb-4 flex justify-end">
        {cart.length > 0 && (
          <button
            className="py-2 px-4 bg-red-500 text-white rounded-md"
            onClick={clearCart}
          >
            장바구니 비우기
          </button>
        )}
      </div>
    </section>
  );
}
