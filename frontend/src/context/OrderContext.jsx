import { createContext, useContext, useReducer, useEffect } from "react";
import { OrderReducer } from "./OrderReducer";
import useApi from "../hooks/useApi";
import { useNavigate } from "react-router-dom";

import PropTypes from "prop-types";

const OrderContext = createContext();

export const OrderProvider = ({ children }) => {
  const navigate = useNavigate();
  const [state, dispatch] = useReducer(OrderReducer);
  const { request } = useApi();

  const fetchOrders = async () => {
    try {
      const response = await request("/users/orders", "GET", {}, {}, true);
      dispatch({ type: "GET_ORDER_LIST", payload: response });
    } catch (err) {
      alert(err);
      navigate("/mypage");
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  return (
    <OrderContext.Provider value={{ state, dispatch }}>
      {children}
    </OrderContext.Provider>
  );
};

OrderProvider.propTypes = {
  children: PropTypes.node.isRequired,
};

export const useOrderContext = () => useContext(OrderContext);
