import ProductList from "../components/ProductList";
import ProductSearch from "../components/ProductSearch";
import { ProductProvider } from "../context/ProductContext";

const Main = () => {
  return (
    <ProductProvider>
      <h1 className="text-center text-3xl mt-5 mb-2">Grids & Circle</h1>
      <ProductSearch />
      <ProductList />
    </ProductProvider>
  );
};

export default Main;
