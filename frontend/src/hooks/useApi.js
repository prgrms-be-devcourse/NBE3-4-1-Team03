import { useState } from "react";
import axios from "axios";

const baseURL = `${import.meta.env.VITE_API_URL}${
  import.meta.env.VITE_API_URL_VERSION
}`;

const useApi = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const apiClient = axios.create({
    baseURL,
    headers: {
      "Content-Type": "application/json",
      Authorization: "",
    },
    withCredentials: true,
    validateStatus: (status) => {
      return status >= 200 && status < 500;
    },
  });

  const request = async (
    endpoint,
    method = "GET",
    data = {},
    params = {},
    includeAuth
  ) => {
    setLoading(true);
    setError(null);

    if (includeAuth) {
      const token = localStorage.getItem("Authorization") || "";
      if (token) {
        apiClient.defaults.headers["Authorization"] = token;
      }
    } else {
      delete apiClient.defaults.headers["Authorization"];
    }

    try {
      const response = await apiClient.request({
        url: endpoint,
        method,
        data,
        params,
      });

      const newAuthToken = response.headers["authorization"];
      console.log(`AuthToken : ${newAuthToken}`);
      if (newAuthToken) {
        localStorage.setItem("Authorization", newAuthToken);
      }

      if (!response.data.isSuccess) {
        throw new Error(response.data.message || "Unknown error");
      }

      return response.data;
    } catch (err) {
      setError(err.response?.data || "An error occurred");
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { request, loading, error };
};

export default useApi;
