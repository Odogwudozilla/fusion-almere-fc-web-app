import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheckCircle, faCircle, faEdit, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import "../../styles/List.css";

const UserList = ({ onEditClick, notify }) => {
    const [users, setUsers] = useState([]);
    const [showAddressDetails, setShowAddressDetails] = useState(true); // Toggle state for address and postcode

    useEffect(() => {
        fetch("/api/users")
            .then((response) => response.json())
            .then((data) => setUsers(data))
            .catch((error) => console.error("Error fetching users:", error));
    }, []);

    const handleDelete = (externalIdentifier) => {
        fetch(`/api/users/${externalIdentifier}`, { method: "DELETE" })
            .then((response) => {
                if (response.ok) {
                    notify("User deleted successfully!", "success");
                    setUsers(users.filter((user) => user.externalIdentifier !== externalIdentifier));
                } else {
                    notify("Failed to delete user. Please try again.", "error");
                }
            })
            .catch(() => notify("An unexpected error occurred.", "error"));
    };

    return (
        <div className="list-container">
            <h2 className="list-title">Users</h2>
            <p>Total Users: {users.length}</p>

            <label className="custom-checkbox">
                <FontAwesomeIcon
                    icon={showAddressDetails ? faCheckCircle : faCircle}
                    onClick={() => setShowAddressDetails(!showAddressDetails)}
                    className="checkbox-icon"
                />
                Show Address Details (Postcode & Address)
            </label>

            <table className="list-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Username</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Mobile</th>
                        <th>WhatsApp</th>
                        {showAddressDetails && <th>Postcode</th>} {/* Conditionally show Postcode */}
                        {showAddressDetails && <th>Address</th>} {/* Conditionally show Address */}
                        <th>Membership Type</th>
                        <th>Status</th>
                        <th>Assigned roles</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map((user, index) => (
                        <tr key={user.externalIdentifier}>
                            <td>{index + 1}</td>
                            <td>{user.username}</td>
                            <td>{user.name}</td>
                            <td>{user.email}</td>
                            <td>{user.mobileNumber || "N/A"}</td>
                            <td>{user.whatsappNumber || "N/A"}</td>
                            {showAddressDetails && <td>{user.postcode || "N/A"}</td>} {/* Conditionally render Postcode */}
                            {showAddressDetails && <td>{user.address || "N/A"}</td>} {/* Conditionally render Address */}
                            <td>{user.membershipType}</td>
                            <td>{user.status}</td>
                            <td>
                                {user.assignedRoles && user.assignedRoles.length > 0 ? (
                                    <ul> Roles:
                                        {user.assignedRoles.map((role) => (
                                            <li key={role.externalIdentifier}>
                                                <details>
                                                    <summary>{role.name}</summary>
                                                    {role.assignedPermissions && role.assignedPermissions.length > 0 ? (
                                                        <ul>Permissions:
                                                            {role.assignedPermissions.map((permission) => (
                                                                <li key={permission.externalIdentifier}>{permission.name}</li>
                                                            ))}
                                                        </ul>
                                                    ) : (
                                                        <div>No Permissions</div>
                                                    )}
                                                </details>
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    "None"
                                )}
                            </td>
                            <td>
                                <button
                                    onClick={() => onEditClick(user)}
                                    className="btn btn-secondary"
                                    title="Edit"
                                >
                                    <FontAwesomeIcon icon={faEdit} />
                                </button>
                                <button
                                    onClick={() => handleDelete(user.externalIdentifier)}
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

export default UserList;
