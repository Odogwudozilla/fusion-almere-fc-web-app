// src/components/Notification.js
import React from "react";
import "./styles/Notification.css";

const Notification = ({ message, type, onClose }) => {
    if (!message) return null;

    return (
        <div className={`notification notification-${type}`}>
            <span>{message}</span>
            <button onClick={onClose} className="notification-close">X</button>
        </div>
    );
};

export default Notification;
