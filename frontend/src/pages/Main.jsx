import ProductList from "../components/ProductList";
import ProductSearch from "../components/ProductSearch";

const Main = () => {
  return (
    <section>
      <h1 className="text-center text-3xl mt2 mb-2">Grids & Circle</h1>
      <ProductSearch />
      <ProductList />
    </section>
  );
};

export default Main;
