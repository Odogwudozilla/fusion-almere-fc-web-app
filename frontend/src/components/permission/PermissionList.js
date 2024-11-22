import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheckCircle, faCircle, faEdit, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import "../styles/List.css";

const PermissionList = ({ onEditClick, notify}) => {
    const [permissions, setPermissions] = useState([]);
    const [showDescription, setShowDescription] = useState(true);

    useEffect(() => {
        fetch("/api/permissions")
            .then((response) => response.json())
            .then((data) => setPermissions(data))
            .catch((error) => console.error("Error fetching permissions:", error));
    }, []);

    const handleDelete = (id) => {
        fetch(`/api/permissions/${id}`, { method: "DELETE" })
            .then((response) => {
                if (response.ok) {
                    notify("Permission deleted successfully!", "success");
                    setPermissions(permissions.filter((permission) => permission.uuid !== id));
                } else {
                    notify("Failed to delete permission. Please try again.", "error");
                }
            })
            .catch(() => notify("An unexpected error occurred.", "error"));
    };

    // Add a fallback for the onEditClick function if it is not provided
    const handleEdit = onEditClick || ((permission) => {
        console.log("No edit function provided. Permission:", permission);
    });

    return (
        <div className="list-container">
            <h2 className="list-title">Permissions</h2>
            <p>Total Permissions: {permissions.length}</p>

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
                        {showDescription && <th>Description</th>}
                        <th>Is for super user</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {permissions.map((permission, index) => (
                        <tr key={permission.uuid}>
                            <td>{index + 1}</td>
                            <td>{permission.name}</td>
                            <td>{permission.externalIdentifier}</td>
                            {showDescription && <td>{permission.description}</td>}
                            <td>{permission.isForSuperUserOnly === true ? "True" : "False"}</td>
                            <td>
                                <button
                                    onClick={() => handleEdit(permission)} // Call the fallback function or provided one
                                    className="btn btn-secondary"
                                    title="Edit"
                                >
                                    <FontAwesomeIcon icon={faEdit} /> 
                                </button>
                                <button
                                    onClick={() => handleDelete(permission.uuid)}
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

export default PermissionList;
