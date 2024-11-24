import React, { useState } from "react";
import UserList from "./UserList";
import UserForm from "./UserForm";
import Notification from "../../Notification";

const UserManagement = () => {
    const [showForm, setShowForm] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [refreshKey, setRefreshKey] = useState(0);
    const [notification, setNotification] = useState({ message: "", type: "" });

    const handleNotification = (message, type) => {
        setNotification({ message, type });
        setTimeout(() => setNotification({ message: "", type: "" }), 5000); // Auto-close after 5 seconds
    };

    const handleAddClick = () => {
        setSelectedUser(null);
        setShowForm(true);
    };

    const handleEditClick = (user) => {
        setSelectedUser(user);
        setShowForm(true);
    };

    const handleCloseForm = () => {
        setShowForm(false);
    };

    const handleSave = () => {
        setShowForm(false);
        setRefreshKey((prevKey) => prevKey + 1);
        handleNotification("User saved successfully!", "success");
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
                    <UserForm
                        user={selectedUser}
                        onClose={handleCloseForm}
                        onSave={handleSave}
                        notify={handleNotification}
                    />
                )}
            </div>
            <div className="management-screen-list-container">
                <button onClick={handleAddClick} className="btn btn-primary">
                    Add User
                </button>
                <UserList
                    key={refreshKey}
                    onEditClick={handleEditClick}
                    notify={handleNotification}
                />
            </div>
        </div>
    );
};

export default UserManagement;
