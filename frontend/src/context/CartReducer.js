export const cartReducer = (state, action) => {
  switch (action.type) {
    case "SET_CART":
      return action.payload;
    case "ADD_ITEM": {
      const updatedCart = [...state, action.payload];
      return updatedCart;
    }
    case "REMOVE_ITEM": {
      const updatedCart = state.filter((item) => item.id !== action.payload);
      return updatedCart;
    }
    case "CLEAR_CART": {
      return [];
    }
    case "INCREASE_ITEM": {
      const updatedCart = state.map((item) =>
        item.id === action.payload
          ? {
              ...item,
              count: item.count + 1,
              totalprice: item.price * (item.count + 1),
            }
          : item
      );
      return updatedCart;
    }
    case "DECREASE_ITEM": {
      const updatedCart = state.map((item) =>
        item.id === action.payload && item.count > 1
          ? {
              ...item,
              count: item.count - 1,
              totalprice: item.price * (item.count - 1),
            }
          : item
      );
      return updatedCart;
    }
    default:
      return state;
  }
};
