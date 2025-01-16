import { useState } from "react";

const useFormValidation = (initialFormData, validationRules) => {
  const [formData, setFormData] = useState(initialFormData);
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData({
      ...formData,
      [name]: value,
    });

    if (value.trim() !== "") {
      setErrors((prevErrors) => ({
        ...prevErrors,
        [name]: "",
      }));
    }
  };

  const validate = () => {
    const newErrors = {};

    Object.keys(validationRules).forEach((field) => {
      const value = formData[field];
      const fieldValidators = validationRules[field];

      for (let i = 0; i < fieldValidators.length; i++) {
        const errorMessage = fieldValidators[i](value, formData);
        if (errorMessage) {
          newErrors[field] = errorMessage;
          break;
        }
      }
    });

    setErrors(newErrors);

    return newErrors;
  };

  return { formData, errors, handleChange, validate };
};

export default useFormValidation;
