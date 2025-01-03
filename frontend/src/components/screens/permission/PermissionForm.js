import React, { useState } from "react";
import "../../styles/Form.css"; 
import RequiredInput from "../../RequiredInput";

const PermissionForm = ({ permission, onClose, onSave, notify }) => {
    const [externalIdentifier, setExternalIdentifier] = useState(permission ? permission.externalIdentifier : "");
    const [name, setName] = useState(permission ? permission.name : "");
    const [description, setDescription] = useState(permission ? permission.description : "");
    const [isForSuperUserOnly, setIsForSuperUserOnly] = useState(permission ? permission.isForSuperUserOnly : false);

    const handleSubmit = (event) => {
        event.preventDefault();
        const method = permission ? "PUT" : "POST";
        const url = permission ? `/api/permissions/${permission.externalIdentifier}` : "/api/permissions";

        fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ externalIdentifier, name, description, isForSuperUserOnly }),
        })
            .then((response) => {
                if (response.ok) {
                    notify(permission ? "Permission updated successfully!" : "Permission added successfully!", "success");
                    onSave();
                } else {
                    notify("Failed to save Permission. Please try again.", "error");
                }
            })
            .catch(() => notify("An unexpected error occurred.", "error"));
    };

    return (
        <form className="form-container" onSubmit={handleSubmit}>
            <h2 className="form-title">{permission ? "Edit Permission" : "Add Permission"}</h2>
            <div className="form-group">
                <label htmlFor="externalIdentifier">Code</label>
                <RequiredInput
                    type="text"
                    id="externalIdentifier"
                    value={externalIdentifier}
                    onChange={(e) => setExternalIdentifier(e.target.value)}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="name">Name</label>
                <RequiredInput
                    type="text"
                    id="name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />
            </div>
            <div className="form-group">
                <label htmlFor="description">Description</label>
                <RequiredInput
                    type="text"
                    id="description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
            </div>
            <div className="form-group form-group-checkbox">
                <label htmlFor="isForSuperUserOnly">
                    <span>Superuser Only</span>
                    <RequiredInput
                        type="checkbox"
                        id="isForSuperUserOnly"
                        checked={isForSuperUserOnly}
                        onChange={(e) => setIsForSuperUserOnly(e.target.checked)}
                    />
                </label>
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

export default PermissionForm;
