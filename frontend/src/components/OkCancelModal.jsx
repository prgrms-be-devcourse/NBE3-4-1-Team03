import PropTypes from "prop-types";

const OkCancelModal = ({
  isOpen,
  message,
  onConfirm,
  onCancel,
  okButtonMessage = "확인",
  cancelButtonMessage = "취소",
}) => {
  if (!isOpen) return null; // 모달이 열려있지 않으면 아무 것도 렌더링하지 않음

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded-lg shadow-lg">
        <p>{message}</p>
        <div className="mt-4">
          <button
            className="py-2 px-4 bg-blue-500 text-white rounded mr-2"
            onClick={onConfirm}
          >
            {okButtonMessage}
          </button>
          <button
            className="py-2 px-4 bg-red-500 text-white rounded"
            onClick={onCancel}
          >
            {cancelButtonMessage}
          </button>
        </div>
      </div>
    </div>
  );
};

OkCancelModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  message: PropTypes.string.isRequired,
  onConfirm: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
  okButtonMessage: PropTypes.string,
  cancelButtonMessage: PropTypes.string,
};

export default OkCancelModal;
