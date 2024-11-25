import React from 'react';
import './styles/RequiredInput.css'; // For optional styling

function RequiredInput({ label, id, required, ...props }) {
    return (
        <div className="required-input-wrapper">
            <label htmlFor={id}>{label}</label>
            <div className="required-input">
                <input id={id} {...props} required={required} />
                {required && <span className="required-asterisk">*</span>}
            </div>
        </div>
    );
}

export default RequiredInput;

