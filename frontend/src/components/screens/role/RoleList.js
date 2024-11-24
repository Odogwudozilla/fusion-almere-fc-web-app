import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheckCircle, faCircle, faEdit, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import "../../styles/List.css";

const RoleList = ({ onEditClick, notify }) => {
    const [roles, setRoles] = useState([]);
    const [showDescription, setShowDescription] = useState(true); // State for toggling description visibility

    useEffect(() => {
        fetch("/api/roles")
            .then((response) => response.json())
            .then((data) => setRoles(data))
            .catch((error) => console.error("Error fetching roles:", error));
    }, []);

    const handleDelete = (externalIdentifier) => {
        fetch(`/api/roles/${externalIdentifier}`, { method: "DELETE" })
            .then((response) => {
                if (response.ok) {
                    notify("Role deleted successfully!", "success");
                    setRoles(roles.filter((role) => role.externalIdentifier !== externalIdentifier));
                } else {
                    notify("Failed to delete role. Please try again.", "error");
                }
            })
            .catch(() => notify("An unexpected error occurred.", "error"));
    };

    return (
        <div className="list-container">
            <h2 className="list-title">Roles</h2>
            <p>Total Roles: {roles.length}</p>

            <label className="custom-checkbox">
                <FontAwesomeIcon
                    icon={showDescription ? faCheckCircle : faCircle}
                    onClick={() => setShowDescription(!showDescription)}
                    className="checkbox-icon"
                />
                Show Descriptions
            </label>

            <table className="list-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>External Identifier</th>
                        {showDescription && <th>Description</th>} {/* Conditionally render Description column */}
                        <th>Is for Super User</th>
                        <th>Assigned Permissions</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {roles.map((role, index) => (
                        <tr key={role.externalIdentifier}>
                            <td>{index + 1}</td>
                            <td>{role.name}</td>
                            <td>{role.externalIdentifier}</td>
                            {showDescription && <td>{role.description}</td>} {/* Conditionally render Description data */}
                            <td>{role.isSuperUser === true ? "True" : "False"}</td>
                            <td>
                                {role.assignedPermissions && role.assignedPermissions.length > 0
                                    ? role.assignedPermissions.map((permission) => (
                                        <div key={permission.externalIdentifier}>
                                            {permission.name}
                                        </div>
                                    ))
                                    : "None"}
                            </td>
                            <td>
                                <button
                                    onClick={() => onEditClick(role)}
                                    className="btn btn-secondary"
                                    title="Edit"
                                >
                                    <FontAwesomeIcon icon={faEdit} />
                                </button>
                                <button
                                    onClick={() => handleDelete(role.externalIdentifier)}
                                    className="btn btn-danger"
                                    title="Delete"
                                >
                                    <FontAwesomeIcon icon={faTrashAlt} />
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default RoleList;
