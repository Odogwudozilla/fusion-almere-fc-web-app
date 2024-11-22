import React, { useState, useEffect } from "react";
import "../styles/Form.css";

const RoleForm = ({ role, onClose, onSave, notify }) => {
    const [externalIdentifier, setExternalIdentifier] = useState(role ? role.externalIdentifier : "");
    const [name, setName] = useState(role ? role.name : "");
    const [description, setDescription] = useState(role ? role.description : "");
    const [isSuperUser, setIsSuperUser] = useState(role ? role.isSuperUser : false); // Add isSuperUser state
    const [permissions, setPermissions] = useState([]);
    const [selectedPermissions, setSelectedPermissions] = useState(role ? role.assignedPermissions.map((perm) => perm.externalIdentifier) : []);
    const [errorMessages, setErrorMessages] = useState([]);

    useEffect(() => {
        fetch("/api/permissions")
            .then((response) => response.json())
            .then((data) => setPermissions(data))
            .catch((error) => console.error("Error fetching permissions:", error));
    }, []);

    const handleCheckboxChange = (permissionExternalIdentifier) => {
        setSelectedPermissions((prevSelectedPermissions) => {
            const isSelected = prevSelectedPermissions.includes(permissionExternalIdentifier);
            return isSelected
                ? prevSelectedPermissions.filter((extId) => extId !== permissionExternalIdentifier)
                : [...prevSelectedPermissions, permissionExternalIdentifier];
        });
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const method = role ? "PUT" : "POST";
        const url = role ? `/api/roles/${role.externalIdentifier}` : "/api/roles";

        const roleData = {
            externalIdentifier,
            name,
            description,
            isSuperUser, // Include isSuperUser in the payload
            assignedPermissions: permissions.filter(perm => selectedPermissions.includes(perm.externalIdentifier)),
        };

        fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(roleData),
        })
            .then((response) => {
                if (response.ok) {
                    notify(role ? "Role updated successfully!" : "Role added successfully!", "success");
                    onSave();
                } else {
                    return response.json().then((data) => {
                        setErrorMessages(data.errors || ["Failed to save role."]);
                        notify("Failed to save role. Please fix the errors and try again.", "error");
                    });
                }
            })
            .catch(() => notify("An unexpected error occurred.", "error"));
    };

    return (
        <form className="form-container" onSubmit={handleSubmit}>
            <h2 className="form-title">{role ? "Edit Role" : "Add Role"}</h2>

            {errorMessages.length > 0 && (
                <div className="error-messages">
                    {errorMessages.map((msg, index) => (
                        <p key={index} className="error-message">{msg}</p>
                    ))}
                </div>
            )}

            <div className="form-group">
                <label htmlFor="externalIdentifier">Code</label>
                <input
                    type="text"
                    id="externalIdentifier"
                    value={externalIdentifier}
                    onChange={(e) => setExternalIdentifier(e.target.value)}
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="name">Name</label>
                <input
                    type="text"
                    id="name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="description">Description</label>
                <input
                    type="text"
                    id="description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
            </div>

            <div className="form-group form-group-checkbox">
                <label htmlFor="isSuperUser">
                    <span>Is Super User</span>
                    <input
                        type="checkbox"
                        id="isSuperUser"
                        checked={isSuperUser}
                        onChange={(e) => setIsSuperUser(e.target.checked)}
                    />
                    
                </label>
            </div>
            <hr class="gradient-hr"/>

            {isSuperUser && (
                <div className="form-group permissions-group">
                    <label>Superuser Permissions</label>
                    <hr class="custom-hr"/>
                    <div className="permissions-list">
                        {permissions
                            .filter((permission) => permission.isForSuperUserOnly)
                            .map((permission) => (
                                <div key={permission.externalIdentifier} className="permission-item">
                                    <input
                                        type="checkbox"
                                        id={`permission-${permission.externalIdentifier}`}
                                        checked={selectedPermissions.includes(permission.externalIdentifier)}
                                        onChange={() => handleCheckboxChange(permission.externalIdentifier)}
                                    />
                                    <label htmlFor={`permission-${permission.externalIdentifier}`}>
                                        {permission.name}
                                    </label>
                                </div>
                            ))}
                    </div>
                </div>
            )}

            <div className="form-group permissions-group">
                <label>Regular Permissions</label>
                <hr class="custom-hr"/>
                <div className="permissions-list">
                    {permissions
                        .filter((permission) => !permission.isForSuperUserOnly)
                        .map((permission) => (
                            <div key={permission.externalIdentifier} className="permission-item">
                                <input
                                    type="checkbox"
                                    id={`permission-${permission.externalIdentifier}`}
                                    checked={selectedPermissions.includes(permission.externalIdentifier)}
                                    onChange={() => handleCheckboxChange(permission.externalIdentifier)}
                                />
                                <label htmlFor={`permission-${permission.externalIdentifier}`}>
                                    {permission.name}
                                </label>
                            </div>
                        ))}
                </div>
            </div>

            <div className="form-actions">
                <button type="submit" className="btn btn-primary">
                    Save
                </button>
                <button type="button" onClick={onClose} className="btn btn-secondary">
                    Cancel
                </button>
            </div>
        </form>
    );
};

export default RoleForm;
