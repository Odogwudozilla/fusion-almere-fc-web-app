import React from 'react';
import { Routes, Route } from 'react-router-dom';
import ShowProfile from './ShowProfile';
import EditProfile from './EditProfile';

const ProfileManagement = () => {
  return (
    <Routes>
      <Route path="profile" element={<ShowProfile />} />
      <Route path="profile/edit" element={<EditProfile />} />
    </Routes>
  );
};

export default ProfileManagement;
