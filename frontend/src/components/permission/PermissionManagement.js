import React, { useState } from "react";
import PermissionList from "./PermissionList";
import PermissionForm from "./PermissionForm";
import Notification from "../Notification";

function PermissionManagement() {
    const [showForm, setShowForm] = useState(false);
    const [selectedPermission, setSelectedPermission] = useState(null);
    const [refreshKey, setRefreshKey] = useState(0); // Key for refreshing the list when adding or editing a permission
    const [notification, setNotification] = useState({ message: "", type: "" });

    const handleNotification = (message, type) => {
        setNotification({ message, type });
        setTimeout(() => setNotification({ message: "", type: "" }), 5000); // Auto-close after 5 seconds
    };

    const handleAddClick = () => {
        setSelectedPermission(null);
        setShowForm(true);
    };

    const handleEditClick = (permission) => {
        setSelectedPermission(permission);
        setShowForm(true);
    };

    const handleCloseForm = () => {
        setShowForm(false);
    };

    const handleSave = () => {
        setShowForm(false);
        setRefreshKey((prevKey) => prevKey + 1); // Refresh the list after saving
        handleNotification("Permission saved successfully!", "success");
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
                <PermissionForm
                    permission={selectedPermission}
                    onClose={handleCloseForm}
                    onSave={handleSave}
                    notify={handleNotification}
                />
            )}
            </div>
            <div className="management-screen-list-container">
                <button onClick={handleAddClick} className="btn btn-primary">
                    Add Permission
                </button>
                <PermissionList
                    key={refreshKey}
                    onEditClick={handleEditClick}
                    notify={handleNotification}
                />
            </div>
        </div>
    );
}

export default PermissionManagement;
