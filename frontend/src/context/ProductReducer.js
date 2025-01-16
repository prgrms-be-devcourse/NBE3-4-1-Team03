export const productReducer = (state, action) => {
  switch (action.type) {
    case "GET_PRODUCT":
      return {
        ...state,
        product_info: action.payload.data.product_info || [],
        currentPage: action.payload.data.currentPage || 1,
        totalPages: action.payload.data.totalPages || 0,
        totalElements: action.payload.data.totalElements || 0,
        hasNext: action.payload.data.hasNext || false,
        hasPrevious: action.payload.data.hasPrevious || false,
        last: action.payload.data.islast || false,
      };
  }
};
