import { useState, useEffect } from "react";

const API_BASE_URL = `${import.meta.env.VITE_API_URL}${
  import.meta.env.VITE_API_URL_VERSION
}`;

export const useFetch = (endpoint, options) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  console.log(`요청주소 : ${API_BASE_URL}${endpoint}`);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        const result = await response.json();

        if (!response.ok) {
          setError(result.message || "Failed to fetch data");
          return;
        }
        setData(result);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [endpoint, options]);

  return { data, loading, error };
};
