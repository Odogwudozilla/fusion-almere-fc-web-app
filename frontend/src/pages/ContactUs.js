import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faEnvelope, faComment } from '@fortawesome/free-solid-svg-icons';

const ContactUs = () => {
    return (
        <div className="container">
            <h2 className="text-center mb-4">Contact Us</h2>
            <form>
                <div className="mb-3">
                    <label htmlFor="name" className="form-label">Your Name</label>
                    <div className="input-group">
                        <span className="input-group-text"><FontAwesomeIcon icon={faUser} /></span>
                        <input type="text" className="form-control" id="name" required />
                    </div>
                </div>

                <div className="mb-3">
                    <label htmlFor="email" className="form-label">Your Email</label>
                    <div className="input-group">
                        <span className="input-group-text"><FontAwesomeIcon icon={faEnvelope} /></span>
                        <input type="email" className="form-control" id="email" required />
                    </div>
                </div>

                <div className="mb-3">
                    <label htmlFor="message" className="form-label">Your Message</label>
                    <div className="input-group">
                        <span className="input-group-text"><FontAwesomeIcon icon={faComment} /></span>
                        <textarea className="form-control" id="message" rows="4" required></textarea>
                    </div>
                </div>

                <button type="submit" className="btn btn-primary w-100">Send Message</button>
            </form>
        </div>
    );
};

export default ContactUs;
