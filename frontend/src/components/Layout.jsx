import PropTypes from "prop-types";

import Navbar from "./Navbar";
import Footer from "./Footer";

const Layout = ({ children }) => {
  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <main className="flex-grow">{children}</main>
      <Footer />
    </div>
  );
};

//유효성 검사
Layout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default Layout;
