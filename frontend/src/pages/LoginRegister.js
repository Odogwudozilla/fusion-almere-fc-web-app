import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faEnvelope, faKey } from '@fortawesome/free-solid-svg-icons';

const LoginRegister = () => {
    const [isLogin, setIsLogin] = useState(true);

    return (
        <div className="container">
            <h2 className="text-center mb-4">{isLogin ? "Login" : "Register"}</h2>

            {isLogin ? (
                <form>
                    <div className="mb-3">
                        <label htmlFor="username" className="form-label">Username</label>
                        <div className="input-group">
                            <span className="input-group-text"><FontAwesomeIcon icon={faUser} /></span>
                            <input type="text" className="form-control" id="username" required />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">Password</label>
                        <div className="input-group">
                            <span className="input-group-text"><FontAwesomeIcon icon={faKey} /></span>
                            <input type="password" className="form-control" id="password" required />
                        </div>
                    </div>

                    <button type="submit" className="btn btn-primary w-100">Login</button>

                    <p className="mt-3 text-center">
                        Don't have an account?{" "}
                        <a
                            href="#"
                            className="form-switch-link"
                            onClick={() => setIsLogin(false)}
                        >
                            Register
                        </a>
                    </p>
                </form>
            ) : (
                <form>
                    <div className="mb-3">
                        <label htmlFor="username" className="form-label">Username</label>
                        <div className="input-group">
                            <span className="input-group-text"><FontAwesomeIcon icon={faUser} /></span>
                            <input type="text" className="form-control" id="username" required />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label htmlFor="email" className="form-label">Email Address</label>
                        <div className="input-group">
                            <span className="input-group-text"><FontAwesomeIcon icon={faEnvelope} /></span>
                            <input type="email" className="form-control" id="email" required />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">Password</label>
                        <div className="input-group">
                            <span className="input-group-text"><FontAwesomeIcon icon={faKey} /></span>
                            <input type="password" className="form-control" id="password" required />
                        </div>
                    </div>

                    <button type="submit" className="btn btn-primary w-100">Register</button>

                    <p className="mt-3 text-center">
                        Already have an account?{" "}
                        <a
                            href="#"
                            className="form-switch-link"
                            onClick={() => setIsLogin(true)}
                        >
                            Login
                        </a>
                    </p>
                </form>
            )}
        </div>
    );
};

export default LoginRegister;
