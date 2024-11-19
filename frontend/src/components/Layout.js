import React from 'react';
import { Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css'; // Import Bootstrap CSS
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHome, faSignInAlt, faUserPlus, faEnvelope } from '@fortawesome/free-solid-svg-icons';
import './Layout.css'; // Import custom CSS for styling

function Layout({ children }) {
    return (
        <div>
            {/* Header with Navbar */}
            <header>
                <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
                    <div className="container">
                        {/* Brand */}
                        <Link className="navbar-brand" to="/">
                            <FontAwesomeIcon icon={faHome} /> Fusion Almere FC
                        </Link>

                        {/* Toggler for mobile view */}
                        <button
                            className="navbar-toggler"
                            type="button"
                            data-bs-toggle="collapse"
                            data-bs-target="#navbarNav"
                            aria-controls="navbarNav"
                            aria-expanded="false"
                            aria-label="Toggle navigation"
                        >
                            <span className="navbar-toggler-icon"></span>
                        </button>

                        {/* Navbar Links */}
                        <div className="collapse navbar-collapse" id="navbarNav">
                            <ul className="navbar-nav ms-auto">
                                <li className="nav-item">
                                    <Link className="nav-link" to="/login">
                                        <FontAwesomeIcon icon={faUserPlus} /> Login/Register
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/contact-us">
                                        <FontAwesomeIcon icon={faEnvelope} /> Contact Us
                                    </Link>
                                </li>
                            </ul>
                        </div>
                    </div>
                </nav>
            </header>

            {/* Main Content */}
            <main className="maincontainer my-5">{children}</main>

            {/* Footer */}
            <footer className="bg-dark text-white text-center py-4">
                <div className="container">
                    <p>© {new Date().getFullYear()} Fusion Almere FC</p>
                    <p>
                        Follow us on:
                        <a href="#" className="text-white mx-2"><FontAwesomeIcon icon={['fab', 'facebook']} /></a>
                        <a href="#" className="text-white mx-2"><FontAwesomeIcon icon={['fab', 'twitter']} /></a>
                        <a href="#" className="text-white mx-2"><FontAwesomeIcon icon={['fab', 'instagram']} /></a>
                    </p>
                </div>
            </footer>
        </div>
    );
}

export default Layout;
