import { productSortOption, productDirection } from "../utils";
import { useProductContext } from "../context/ProductContext";

const ProductSearch = () => {
  const { setPage, setSort, setDirection } = useProductContext();
  const handleSearch = (e) => {
    e.preventDefault();
    const sort = e.target.sort.value;
    const direction = e.target.direction.value;

    setPage("");
    setSort(sort);
    setDirection(direction);
  };

  return (
    <>
      <section className="container mx-auto mt-4 mb-2">
        <form className="flex items-center space-x-2" onSubmit={handleSearch}>
          <select name="sort" className="border border-gray-300 rounded-md p-2">
            {productSortOption.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
          <select
            name="direction"
            className="border border-gray-300 rounded-md p-2"
          >
            {productDirection.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
          <input
            type="text"
            name="keyword"
            placeholder="아직 검색은 지원되지 않아요 (┬┬﹏┬┬)"
            className="border border-gray-300 rounded-md p-2 flex-1"
            disabled
          />
          <button
            type="submit"
            className="bg-blue-500 text-white px-4 py-2 rounded-md"
          >
            찾기
          </button>
        </form>
      </section>
    </>
  );
};

export default ProductSearch;
