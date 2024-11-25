import React, { useState, useEffect } from "react";
import "../../styles/Form.css";
import RequiredInput from "../../RequiredInput";

const UserForm = ({ user, onClose, onSave, notify }) => {
    const [externalIdentifier, setExternalIdentifier] = useState(user ? user.externalIdentifier : "");
    const [name, setName] = useState(user ? user.name : "");
    const [username, setUsername] = useState(user ? user.username : "");
    const [email, setEmail] = useState(user ? user.email : "");
    const [mobileNumber, setMobileNumber] = useState(user ? user.mobileNumber : "");
    const [whatsappNumber, setWhatsappNumber] = useState(user ? user.whatsappNumber : "");
    const [postcode, setPostcode] = useState(user ? user.postcode : "");
    const [address, setAddress] = useState(user ? user.address : "");
    const [membershipType, setMembershipType] = useState(user ? user.membershipType : "");
    const [status, setStatus] = useState(user ? user.status : "");
    const [roles, setRoles] = useState([]);
    const [selectedRoles, setSelectedRoles] = useState(user ? user.assignedRoles.map((role) => role.externalIdentifier) : []);
    const [errorMessages, setErrorMessages] = useState([]);

    useEffect(() => {
        fetch("/api/roles")
            .then((response) => response.json())
            .then((data) => setRoles(data))
            .catch((error) => console.error("Error fetching roles:", error));
    }, []);

    const handleCheckboxChange = (roleExternalIdentifier) => {
        setSelectedRoles((prevSelectedRoles) => {
            const isSelected = prevSelectedRoles.includes(roleExternalIdentifier);
            return isSelected
                ? prevSelectedRoles.filter((extId) => extId !== roleExternalIdentifier)
                : [...prevSelectedRoles, roleExternalIdentifier];
        });
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const method = user ? "PUT" : "POST";
        const url = user ? `/api/users/${user.externalIdentifier}` : "/api/users";

        const userData = {
            externalIdentifier,
            name,
            username,
            email,
            mobileNumber,
            whatsappNumber,
            postcode,
            address,
            membershipType,
            status,
            assignedRoles: roles.filter((role) => selectedRoles.includes(role.externalIdentifier)),
        };

        fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData),
        })
            .then((response) => {
                if (response.ok) {
                    notify(user ? "User updated successfully!" : "User added successfully!", "success");
                    onSave();
                } else {
                    return response.json().then((data) => {
                        setErrorMessages(data.errors || ["Failed to save user."]);
                        notify("Failed to save user. Please fix the errors and try again.", "error");
                    });
                }
            })
            .catch(() => notify("An unexpected error occurred.", "error"));
    };

    return (
        <form className="form-container" onSubmit={handleSubmit}>
            <h2 className="form-title">{user ? "Edit User" : "Add User"}</h2>

            {errorMessages.length > 0 && (
                <div className="error-messages">
                    {errorMessages.map((msg, index) => (
                        <p key={index} className="error-message">{msg}</p>
                    ))}
                </div>
            )}

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
                <label htmlFor="username">Username</label>
                <RequiredInput
                    type="text"
                    id="username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="email">Email</label>
                <RequiredInput
                    type="email"
                    id="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="mobileNumber">Mobile Number</label>
                <RequiredInput
                    type="tel"
                    id="mobileNumber"
                    value={mobileNumber}
                    onChange={(e) => setMobileNumber(e.target.value)}
                />
            </div>

            <div className="form-group">
                <label htmlFor="whatsappNumber">WhatsApp Number</label>
                <RequiredInput
                    type="tel"
                    id="whatsappNumber"
                    value={whatsappNumber}
                    onChange={(e) => setWhatsappNumber(e.target.value)}
                />
            </div>

            <div className="form-group">
                <label htmlFor="postcode">Postcode</label>
                <RequiredInput
                    type="text"
                    id="postcode"
                    value={postcode}
                    onChange={(e) => setPostcode(e.target.value)}
                />
            </div>

            <div className="form-group">
                <label htmlFor="address">Address</label>
                <RequiredInput
                    type="text"
                    id="address"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                />
            </div>

            <div className="form-group">
                <label htmlFor="membershipType">Membership Type</label>
                <RequiredInput
                    type="text"
                    id="membershipType"
                    value={membershipType}
                    onChange={(e) => setMembershipType(e.target.value)}
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="status">Status</label>
                <RequiredInput
                    type="text"
                    id="status"
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                    required
                />
            </div>

            <div className="form-group roles-group">
                <label>Assigned Roles</label>
                <hr class="custom-hr"/>
                <div className="roles-list">
                    {roles.map((role) => (
                        <div key={role.externalIdentifier} className="role-item">
                            <RequiredInput
                                type="checkbox"
                                id={`role-${role.externalIdentifier}`}
                                checked={selectedRoles.includes(role.externalIdentifier)}
                                onChange={() => handleCheckboxChange(role.externalIdentifier)}
                            />
                            <label htmlFor={`role-${role.externalIdentifier}`}>{role.name}</label>
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

export default UserForm;
