import { useProductContext } from "../context/ProductContext";
import Error from "./Error";
import Loading from "./Loading";

const ProductList = () => {
  const { state, loading, error, setPage } = useProductContext();

  const { product_info, currentPage, totalPages, hasNext, hasPrevious } = state; //페이지네이션 및 데이터 필요한거 뽑아쓰기.

  if (loading) return <Loading />;
  if (error) return <Error errorMessage={error} />;

  return (
    <section className="container mx-auto mt-4 mb-2">
      <h2 className="sr-only">Products</h2>

      <div className="grid grid-cols-1 gap-x-6 gap-y-10 sm:grid-cols-2 lg:grid-cols-5 xl:grid-cols-5 xl:gap-x-8">
        {product_info.map((product) => (
          // todo: 상세 화면 어떻게 보여줄 것인지 생각 필요. 별도의 모달 창이 좋아보이긴함.
          <a key={product.product_id} href={product.href} className="group">
            <img
              src="https://i.imgur.com/HKOFQYa.jpeg"
              className="aspect-square w-full rounded-lg bg-gray-200 object-cover group-hover:opacity-75 xl:aspect-[7/8]"
            />
            <h3 className="mt-4 text-sm text-gray-700">
              {product.product_name}
            </h3>
            <p className="mt-1 text-lg font-medium text-gray-900">
              {product.product_price}원
            </p>
          </a>
        ))}
      </div>

      {/* 페이지네이션 */}
      <nav className="mt-4">
        <ul className="flex justify-center space-x-2">
          {hasPrevious && (
            <li className="page-item">
              <button
                onClick={() => setPage(currentPage - 1)}
                className="page-link px-4 py-2 bg-gray-800 text-white rounded-lg"
              >
                이전
              </button>
            </li>
          )}

          {/* 페이지 번호 목록 */}
          {[...Array(totalPages)].map((_, index) => {
            const page = index + 1; // 페이지 번호는 1부터 시작
            // 현재 페이지 기준으로 -5부터 +5까지만 보여줌
            if (page >= currentPage - 5 && page <= currentPage + 5) {
              return (
                <li
                  key={page}
                  className={`page-item ${
                    page === currentPage
                      ? "bg-blue-600 text-white"
                      : "bg-gray-200"
                  } px-3 py-2 rounded-lg`}
                >
                  <button onClick={() => setPage(page)} className="page-link">
                    {page}
                  </button>
                </li>
              );
            }
            return null; // 조건에 맞지 않으면 렌더링하지 않음
          })}

          {/* 다음 버튼 */}
          {hasNext && (
            <li className="page-item">
              <button
                onClick={() => setPage(currentPage + 1)}
                className="page-link px-4 py-2 bg-gray-800 text-white rounded-lg"
              >
                다음
              </button>
            </li>
          )}
        </ul>
      </nav>
    </section>
  );
};

export default ProductList;
