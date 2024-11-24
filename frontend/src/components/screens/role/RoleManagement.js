import React, { useState } from "react";
import RoleList from "./RoleList";
import RoleForm from "./RoleForm";
import Notification from "../../Notification";

const RoleManagement = () => {
    const [showForm, setShowForm] = useState(false);
    const [selectedRole, setSelectedRole] = useState(null);
    const [refreshKey, setRefreshKey] = useState(0);
    const [notification, setNotification] = useState({ message: "", type: "" });

    const handleNotification = (message, type) => {
        setNotification({ message, type });
        setTimeout(() => setNotification({ message: "", type: "" }), 5000); // Auto-close after 5 seconds
    };

    const handleAddClick = () => {
        setSelectedRole(null);
        setShowForm(true);
    };

    const handleEditClick = (role) => {
        setSelectedRole(role);
        setShowForm(true);
    };

    const handleCloseForm = () => {
        setShowForm(false);
    };

    const handleSave = () => {
        setShowForm(false);
        setRefreshKey((prevKey) => prevKey + 1);
        handleNotification("Role saved successfully!", "success");
    };

    return (
        <div className="management-screen">
            <Notification
                message={notification.message}
                type={notification.type}
                onClose={() => setNotification({ message: "", type: "" })}
            />
             <div className="management-screen-form-container">
            {showForm && (
                <RoleForm
                    role={selectedRole}
                    onClose={handleCloseForm}
                    onSave={handleSave}
                    notify={handleNotification}
                />
            )}
            </div>
            <div className="management-screen-list-container">
                <button onClick={handleAddClick} className="btn btn-primary">
                    Add Role
                </button>
                <RoleList
                    key={refreshKey}
                    onEditClick={handleEditClick}
                    notify={handleNotification}
                />
            </div>
        </div>
    );
};

export default RoleManagement;
