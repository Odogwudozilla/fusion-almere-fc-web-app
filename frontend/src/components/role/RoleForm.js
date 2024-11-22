import React, { useState, useEffect } from "react";
import "../styles/Form.css"; // Reusing the generic form styles

const RoleForm = ({ role, onClose, onSave, notify }) => {
    const [externalIdentifier, setExternalIdentifier] = useState(role ? role.externalIdentifier : "");
    const [name, setName] = useState(role ? role.name : "");
    const [description, setDescription] = useState(role ? role.description : "");
    const [permissions, setPermissions] = useState([]);
    const [selectedPermissions, setSelectedPermissions] = useState(role ? role.assignedPermissions.map((perm) => perm.externalIdentifier) : []); // Changed to use externalIdentifier
    const [errorMessages, setErrorMessages] = useState([]);

    // Fetching permissions and updating selectedPermissions if a role is passed
    useEffect(() => {
        fetch("/api/permissions")
            .then((response) => response.json())
            .then((data) => {
                setPermissions(data);

                // If a role exists, set the selectedPermissions based on the role's permissions
                if (role && role.assignedPermissions) {
                    const permissionIds = role.assignedPermissions.map((perm) => perm.externalIdentifier); // Ensure we're using externalIdentifier
                    setSelectedPermissions(permissionIds);
                    console.log("Initial selectedPermissions:", permissionIds); // Debug
                }
            })
            .catch((error) => console.error("Error fetching permissions:", error));
    }, [role]);

    // Handle checkbox state change
    const handleCheckboxChange = (permissionExternalIdentifier) => {
        setSelectedPermissions((prevSelectedPermissions) => {
            const isSelected = prevSelectedPermissions.includes(permissionExternalIdentifier);
            const updatedSelection = isSelected
                ? prevSelectedPermissions.filter((extId) => extId !== permissionExternalIdentifier) // Unselect
                : [...prevSelectedPermissions, permissionExternalIdentifier]; // Select
            console.log("Updated selectedPermissions:", updatedSelection); // Debug
            return updatedSelection;
        });
    };

    // Handle form submission
    const handleSubmit = (event) => {
        event.preventDefault();
        const method = role ? "PUT" : "POST";
        const url = role ? `/api/roles/${role.externalIdentifier}` : "/api/roles";

        // Prepare role object for submission
        const roleData = {
            externalIdentifier,
            name,
            description,
            assignedPermissions: permissions.filter(perm => selectedPermissions.includes(perm.externalIdentifier)), // Filter by externalIdentifier
        };
        console.log("Role Data for submission:", roleData); // Debug

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

            {/* Display error messages */}
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

            {/* Permissions section with checkboxes */}
            <div className="form-group permissions-group">
                <label>Permissions</label>
                <hr class="gradient-hr"/>
                {/* Superuser-only permissions */}
                <div className="permissions-group">
                    <h4 className="permissions-group-title">Superuser Only</h4>
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
                                        className="checkbox"
                                    />
                                    <label htmlFor={`permission-${permission.externalIdentifier}`} className="permission-label">
                                        {permission.name}
                                    </label>
                                </div>
                            ))}
                    </div>
                </div>

                {/* Regular permissions */}
                <div className="permissions-group">
                    <h4 className="permissions-group-title">Regular Permissions</h4>
                    <hr class="custom-hr"></hr>
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
                                        className="checkbox"
                                    />
                                    <label htmlFor={`permission-${permission.externalIdentifier}`} className="permission-label">
                                        {permission.name}
                                    </label>
                                </div>
                            ))}
                    </div>
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
