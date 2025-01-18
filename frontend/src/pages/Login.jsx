import { useState } from "react";
import { useNavigate } from "react-router-dom";

import useApi from "../hooks/useApi";
import { useAuth } from "../context/AuthContext";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { request, loading } = useApi();
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await request(
        "/login",
        "POST",
        { email, password },
        {},
        false
      );
      console.log(response);
      alert(response.message);
      login();
      navigate("/mypage");
    } catch (err) {
      console.error("로그인 실패:", err);
      alert(err);
    }
  };

  return (
    <main className="flex flex-col justify-center px-6 py-12 lg:px-8">
      {/* 로고와 제목 */}
      <header className="text-center">
        <img
          alt="Company Logo - Login"
          src="https://tailwindui.com/plus/img/logos/mark.svg?color=indigo&shade=600"
          className="mx-auto h-10 w-auto"
        />
        <h2 className="mt-6 text-2xl font-bold tracking-tight text-gray-900">
          로 그 인
        </h2>
      </header>

      {/* 로그인 폼 */}
      <form
        onSubmit={handleSubmit}
        className="mt-10 mx-auto w-full max-w-sm space-y-6"
      >
        {/* 이메일 입력 */}
        <label className="block">
          <span className="block text-sm font-medium text-gray-900">
            Email address
          </span>
          <input
            id="email"
            name="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            autoComplete="email"
            className="mt-2 block w-full rounded-md bg-white px-3 py-2 text-gray-900 outline outline-1 outline-gray-300 placeholder:text-gray-400 focus:outline-indigo-600"
          />
        </label>

        {/* 비밀번호 입력 */}
        <label className="block">
          <span className="flex justify-between text-sm font-medium text-gray-900">
            Password
            <a
              href="#"
              className="font-semibold text-indigo-600 hover:text-indigo-500"
            >
              비밀번호 분실 하셨나요?
            </a>
          </span>
          <input
            id="password"
            name="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            autoComplete="current-password"
            className="mt-2 block w-full rounded-md bg-white px-3 py-2 text-gray-900 outline outline-1 outline-gray-300 placeholder:text-gray-400 focus:outline-indigo-600"
          />
        </label>

        {/* 로그인 버튼 */}
        <button
          type="submit"
          className="w-full rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus:outline-indigo-600"
          disabled={loading}
        >
          {loading ? "처리 중..." : "로 그 인"}
        </button>
      </form>
    </main>
  );
};

export default Login;
