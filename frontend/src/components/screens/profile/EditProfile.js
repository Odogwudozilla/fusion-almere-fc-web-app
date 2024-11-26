import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../styles/Profile.css';
import RequiredInput from "../../RequiredInput";

const EditProfile = () => {
  const { hashedId } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    profilePictureUrl: '',
    name: '',
    username: '',
    email: '',
    mobileNumber: '',
    whatsappNumber: '',
    postcode: '',
    address: '',
  });

  useEffect(() => {
    // Fetch user data
    const fetchUser = async () => {
      const response = await fetch(`/api/users/${hashedId}/profile`);
      const data = await response.json();
      setFormData(data);
    };

    fetchUser();
  }, [hashedId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await fetch(`/api/users/${hashedId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData),
    });
    navigate(`/users/${hashedId}/profile`);
  };

  return (
    <div className="edit-profile-container">
      {/* Headline and Back Link */}
      <div className="edit-profile-header">
        <h1>Edit your profile</h1>
        <a
          href="#"
          onClick={(e) => {
            e.preventDefault();
            navigate(`/users/${hashedId}/profile`);
          }}
        >
          Back to profile
        </a>
      </div>

      <form className="edit-profile-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Profile Picture URL</label>
          <RequiredInput
            type="text"
            name="profilePictureUrl"
            value={formData.profilePictureUrl || ''}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label>Username</label>
          <RequiredInput
            type="text"
            name="username"
            value={formData.username || ''}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Name</label>
          <RequiredInput
            type="text"
            name="name"
            value={formData.name || ''}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Email</label>
          <RequiredInput
            type="email"
            name="email"
            value={formData.email || ''}
            onChange={handleChange}
            readOnly
          />
        </div>
        <div className="form-group">
          <label>Mobile Number</label>
          <RequiredInput
            type="tel"
            name="mobileNumber"
            value={formData.mobileNumber || ''}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label>WhatsApp Number</label>
          <RequiredInput
            type="tel"
            name="whatsappNumber"
            value={formData.whatsappNumber || ''}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Postcode</label>
          <RequiredInput
            type="text"
            name="postcode"
            value={formData.postcode || ''}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label>Address</label>
          <RequiredInput
            type="text"
            name="address"
            value={formData.address || ''}
            onChange={handleChange}
          />
        </div>

        <button type="submit" className="save-button">
          Save Changes
        </button>
      </form>
    </div>
  );
};

export default EditProfile;
