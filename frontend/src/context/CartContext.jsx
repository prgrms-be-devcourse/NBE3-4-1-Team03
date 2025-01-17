import {
  createContext,
  useReducer,
  useEffect,
  useContext,
  useRef,
} from "react";
import { cartReducer } from "./CartReducer";

import PropTypes from "prop-types";

// const initialCart = JSON.parse(localStorage.getItem("cart")) || [];

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, dispatch] = useReducer(cartReducer, []);
  const isInitialRender = useRef(true); // 첫 렌더링 여부를 체크하는 ref

  useEffect(() => {
    console.log("초기로딩");
    const storedCart = JSON.parse(localStorage.getItem("cart")) || [];
    console.log(storedCart);
    dispatch({ type: "SET_CART", payload: storedCart });
  }, []);

  useEffect(() => {
    // 첫 번째 렌더링이 아닐 때만 로컬스토리지에 저장
    if (isInitialRender.current) {
      isInitialRender.current = false; // 첫 렌더링이 끝났으므로 `true`에서 `false`로 변경
      return; // 첫 렌더링일 경우 로컬스토리지 업데이트하지 않음, 아직 state update 안된상태.
    }

    console.log("장바구니 내부 상태 변경!!");
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  return (
    <CartContext.Provider value={{ cart, dispatch }}>
      {children}
    </CartContext.Provider>
  );
};

CartProvider.propTypes = {
  children: PropTypes.node.isRequired,
};

export const useCartContext = () => useContext(CartContext);
