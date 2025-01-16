import {
  createContext,
  useContext,
  useReducer,
  useEffect,
  useState,
} from "react";
import { productReducer } from "./ProductReducer";
import { useFetch } from "../hooks/useFetch";

import PropTypes from "prop-types";

const initialState = {
  currentPage: 1,
  totalPages: 0,
  totalElements: 0,
  pageSize: 10,
  hasNext: false,
  hasPrevious: true,
  isLast: true,
  product_info: [
    {
      product_id: 0,
      product_name: null,
      product_price: 0,
      product_amount: 0,
      product_status: false,
    },
  ],
};

const ProductContext = createContext();

export const ProductProvider = ({ children }) => {
  const [state, dispatch] = useReducer(productReducer, initialState);
  const [filterParams, setFilterParams] = useState({
    Page: "",
    Sort: "",
    Direction: "",
  });

  const queryString = new URLSearchParams(
    Object.fromEntries(
      Object.entries(filterParams).filter(([_, v]) => v !== "")
    )
  ).toString();
  const finalQueryString = queryString ? `?${queryString}` : "";
  const { data, loading, error } = useFetch(`/products${finalQueryString}`);

  useEffect(() => {
    if (data) {
      dispatch({ type: "GET_PRODUCT", payload: data });
    }
  }, [data]);

  const setPage = (page) => {
    setFilterParams((prev) => ({ ...prev, page }));
  };

  const setSort = (sort) => {
    setFilterParams((prev) => ({ ...prev, sort }));
  };

  const setDirection = (direction) => {
    setFilterParams((prev) => ({ ...prev, direction }));
  };

  return (
    <ProductContext.Provider
      value={{
        state,
        dispatch,
        loading,
        error,
        setPage,
        setSort,
        setDirection,
      }}
    >
      {children}
    </ProductContext.Provider>
  );
};

ProductProvider.propTypes = {
  children: PropTypes.node.isRequired,
};

export const useProductContext = () => useContext(ProductContext);
