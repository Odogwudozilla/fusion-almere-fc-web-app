import React, { useState } from "react";
import "../styles/Form.css"; // Reusing the generic form styles

const RoleForm = ({ role, onClose, onSave, notify }) => {
    const [externalIdentifier, setExternalIdentifier] = useState(role ? role.externalIdentifier : "");
    const [name, setName] = useState(role ? role.name : "");
    const [description, setDescription] = useState(role ? role.description : "");

    const handleSubmit = (event) => {
        event.preventDefault();
        const method = role ? "PUT" : "POST";
        const url = role ? `/api/roles/${role.uuid}` : "/api/roles";

        fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ externalIdentifier, name, description }),
        })
            .then((response) => {
                if (response.ok) {
                    notify(role ? "Role updated successfully!" : "Role added successfully!", "success");
                    onSave();
                } else {
                    notify("Failed to save role. Please try again.", "error");
                }
            })
            .catch(() => notify("An unexpected error occurred.", "error"));
    };

    return (
        <form className="form-container" onSubmit={handleSubmit}>
            <h2 className="form-title">{role ? "Edit Role" : "Add Role"}</h2>
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
