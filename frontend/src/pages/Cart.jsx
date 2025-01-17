import CartPage from "../components/CartPage";
import CartPayment from "../components/CartPayment";

import { CartProvider } from "../context/CartContext";

const Cart = () => {
  return (
    <>
      <CartProvider>
        <CartPage />
        <CartPayment />
      </CartProvider>
    </>
  );
};

export default Cart;
