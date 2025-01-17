import PropTypes from "prop-types";

const Error = ({ errorMessage }) => (
  <div className="flex flex-col items-center justify-center h-full w-full text-center">
    <div className="text-4xl">⚠️</div>
    <div className="text-lg mt-2">
      {errorMessage || "Error! Not Found Page!"}
    </div>
  </div>
);

Error.propTypes = {
  errorMessage: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.oneOf([null]),
  ]),
};

export default Error;
