import InputField from "../components/InputField";
import { useNavigate } from "react-router-dom";
import useApi from "../hooks/useApi";
import useFormValidation from "../hooks/useFormValidation";
import { useState } from "react";

const NewProduct = () => {
  const navigate = useNavigate();
  const { request, loading } = useApi();

  const validationRules = {
    name: [(value) => (!value ? "제품명을 입력해주세요." : "")],
    description: [(value) => (!value ? "제품 설명을 입력해주세요." : "")],
    price: [
      (value) => (!value ? "가격을 입력해주세요." : ""),
      (value) => (value < 100 ? "가격은 100원 이상이어야 합니다." : ""),
    ],
    amount: [
      (value) => (!value ? "판매 수량을 입력해주세요." : ""),
      (value) => (value < 0 ? "판매 수량은 0 이상이어야 합니다." : ""),
    ],
  };

  const { formData, errors, handleChange, validate } = useFormValidation(
    { name: "", description: "", price: "", amount: "" },
    validationRules
  );

  const [status, setStatus] = useState(true); // 기본값: 판매 가능

  const handleStatusChange = (e) => {
    const { id } = e.target;
    if (id === "statusAvailable") {
      setStatus(true);
    } else if (id === "statusUnavailable") {
      setStatus(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) return;

    const requestData = {
      name: formData.name,
      description: formData.description,
      price: formData.price,
      amount: formData.amount,
      status: status,
    };

    console.log(requestData);

    try {
      const response = await request(
        "/products",
        "POST",
        requestData,
        {},
        true
      );
      alert(response.message);
      navigate("/admin");
    } catch (err) {
      alert(err);
    }
  };

  return (
    <section className="container mx-auto mt-5 bg-gray-100">
      <div className="p-6 bg-white rounded-lg shadow-md">
        <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">
          상품 등록
        </h1>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <InputField
            id="name"
            name="name"
            label="제품명"
            value={formData.name}
            onChange={handleChange}
            error={errors.name}
            placeholder="제품명을 입력하세요."
          />
          <InputField
            id="description"
            name="description"
            label="제품 설명"
            value={formData.description}
            onChange={handleChange}
            error={errors.description}
            placeholder="제품 설명을 입력하세요"
          />
          <InputField
            id="price"
            name="price"
            label="제품 가격"
            value={formData.price}
            onChange={handleChange}
            error={errors.price}
            placeholder="가격을 입력하세요."
            type="number"
          />
          <InputField
            id="amount"
            name="amount"
            label="판매 수량"
            value={formData.amount}
            onChange={handleChange}
            error={errors.amount}
            placeholder="판매 수량을 적어주세요"
            type="number"
          />

          {/* 판매 상태 체크박스 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              판매 상태
            </label>
            <div className="flex items-center space-x-4">
              {" "}
              {/* `space-x-4`로 간격 조절 */}
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="statusAvailable"
                  name="status"
                  checked={status === true}
                  onChange={handleStatusChange}
                  className="h-4 w-4 text-blue-500 border-gray-300 rounded"
                />
                <span className="ml-2 text-gray-700">판매 가능</span>
              </div>
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="statusUnavailable"
                  name="status"
                  checked={status === false}
                  onChange={handleStatusChange}
                  className="h-4 w-4 text-red-500 border-gray-300 rounded"
                />
                <span className="ml-2 text-gray-700">판매 불가</span>
              </div>
            </div>
          </div>

          <div>
            <button
              type="submit"
              className="w-full px-4 py-2 text-white bg-blue-500 rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400"
              disabled={loading}
            >
              {loading ? "처리 중..." : "상품 등록"}
            </button>
          </div>
        </form>
      </div>
    </section>
  );
};

export default NewProduct;
