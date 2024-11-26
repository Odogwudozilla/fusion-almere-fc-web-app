import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../styles/Profile.css';

const ShowProfile = () => {
  const { hashedId } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Fetch user data
    const fetchUser = async () => {
      const response = await fetch(`/api/users/${hashedId}/profile`); 
      const data = await response.json();
      setUser(data);
    };

    fetchUser();
  }, [hashedId]);

  if (!user) return <div>Loading...</div>;

  const {
    profilePictureUrl,
    name,
    username,
    email,
    mobileNumber,
    whatsappNumber,
    postcode,
    address,
    membershipType,
    activatedAt,
    status,
  } = user;

  const formattedDate = new Date(activatedAt).toISOString().split('T')[0];

  return (
    <div className="profile-container">
      <div className="profile-header">
        <img
          src={profilePictureUrl || '/default-avatar.png'}
          alt={`${name}'s profile`}
          className="profile-picture"
        />
        <h1>{name || 'Not provided'}</h1>
        <p className="username">@{username || 'Not provided'}</p>
      </div>

      <div className="profile-details">
        <p><strong>Email:</strong> {email || 'Not provided'}</p>
        <p><strong>Mobile Number:</strong> {mobileNumber || 'Not provided'}</p>
        <p><strong>WhatsApp Number:</strong> {whatsappNumber || 'Not provided'}</p>
        <p><strong>Postcode:</strong> {postcode || 'Not provided'}</p>
        <p><strong>Address:</strong> {address || 'Not provided'}</p>
        <p><strong>Membership Type:</strong> {membershipType || 'Not provided'}</p>
        <p><strong>Date Joined:</strong> {formattedDate || 'Not provided'}</p>
        <p><strong>Status:</strong> {status || 'Not provided'}</p>
      </div>

      <button
        className="edit-button"
        onClick={() => navigate(`/users/${hashedId}/profile/edit`)}
      >
        <i className="fa fa-edit"></i> Edit Profile
      </button>
    </div>
  );
};

export default ShowProfile;
