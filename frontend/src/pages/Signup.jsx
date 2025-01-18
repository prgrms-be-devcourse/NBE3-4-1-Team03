import { useNavigate } from "react-router-dom";

import useApi from "../hooks/useApi";
import useFormValidation from "../hooks/useFormValidation";

import InputField from "../components/InputField";

const Signup = () => {
  const navigate = useNavigate();
  const { request, loading } = useApi();

  const validationRules = {
    name: [
      (value) => !value && "사용자 이름을 입력해주세요.",
      (value) =>
        value.length < 1 ||
        (value.length > 10 &&
          "사용자 이름은 1자리 이상, 10자리 이하여야 합니다."),
    ],
    email: [
      (value) => !value && "이메일을 입력해주세요.",
      (value) => !/\S+@\S+\.\S+/.test(value) && "유효한 이메일을 입력해주세요.",
    ],
    password: [
      (value) => !value && "비밀번호를 입력해주세요.",
      (value) => value.length < 8 && "비밀번호는 8자리 이상이어야 합니다.",
      (value) =>
        !/[!@#$%^&*(),.?":{}|<>]/.test(value) &&
        "비밀번호는 특수문자를 포함해야 합니다.",
    ],
    confirmPassword: [
      (value, formData) =>
        value !== formData.password &&
        "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
    ],
    address: [(value) => !value && "주소를 입력해주세요."],
    detailAddress: [(value) => !value && "상세 주소를 입력해주세요."],
    phone: [
      (value) => !value && "전화번호를 입력해주세요.",
      (value) =>
        !/^\d{10,11}$/.test(value) &&
        "전화번호는 숫자만 입력해야 하며, 10자리 또는 11자리여야 합니다.",
    ],
  };

  const { formData, errors, handleChange, validate } = useFormValidation(
    {
      name: "",
      email: "",
      password: "",
      confirmPassword: "",
      address: "",
      detailAddress: "",
      phone: "",
    },
    validationRules
  );

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) return;

    const requestData = {
      email: formData.email,
      password: formData.password,
      name: formData.name,
      address: formData.address,
      detailAddress: formData.detailAddress,
      phone: formData.phone,
    };

    console.log(requestData);

    try {
      const response = await request("/signup", "POST", formData, {}, false);
      alert(response.message);
      navigate("/login");
    } catch (err) {
      alert(err);
    }
  };

  return (
    <section className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="flex-grow max-w-lg p-6 bg-white rounded-lg shadow-md">
        <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">
          회원가입
        </h1>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <InputField
            id="name"
            name="name"
            label="사용자 이름"
            value={formData.name}
            onChange={handleChange}
            error={errors.name}
            placeholder="사용자 이름을 입력하세요"
          />
          <InputField
            id="email"
            name="email"
            label="이메일"
            value={formData.email}
            onChange={handleChange}
            error={errors.email}
            placeholder="이메일을 입력하세요"
          />
          <InputField
            id="password"
            name="password"
            label="비밀번호"
            type="password"
            value={formData.password}
            onChange={handleChange}
            error={errors.password}
            placeholder="비밀번호를 입력하세요"
          />
          <InputField
            id="confirmPassword"
            name="confirmPassword"
            label="비밀번호 확인"
            type="password"
            value={formData.confirmPassword}
            onChange={handleChange}
            error={errors.confirmPassword}
            placeholder="비밀번호를 다시 입력하세요"
          />
          <InputField
            id="address"
            name="address"
            label="주소"
            value={formData.address}
            onChange={handleChange}
            error={errors.address}
            placeholder="주소를 입력하세요"
          />
          <InputField
            id="detailAddress"
            name="detailAddress"
            label="상세 주소"
            value={formData.detailAddress}
            onChange={handleChange}
            error={errors.detailAddress}
            placeholder="상세 주소를 입력하세요"
          />
          <InputField
            id="phone"
            name="phone"
            label="전화번호"
            value={formData.phone}
            onChange={handleChange}
            error={errors.phone}
            placeholder="전화번호를 입력하세요"
          />
          <div>
            <button
              type="submit"
              className="w-full px-4 py-2 text-white bg-blue-500 rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400"
              disabled={loading}
            >
              {loading ? "처리 중..." : "회원가입"}
            </button>
          </div>
        </form>
      </div>
    </section>
  );
};

export default Signup;
