export const OrderReducer = (state, action) => {
  switch (action.type) {
    case "GET_ORDER_LIST":
      return {
        ...state,
        data: action.payload.data,
        isSuccess: action.payload.isSuccess,
        message: action.payload.message,
      };
    default:
      return state;
  }
};
