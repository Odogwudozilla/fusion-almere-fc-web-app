import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import Home from './pages/Home';
import LoginRegister from './pages/LoginRegister'; // Login & Register combined page
import ContactUs from './pages/ContactUs'; // Contact Us page
import RoleManagement from './components/screens/role/RoleManagement';
import PermissionManagement from "./components/screens/permission/PermissionManagement";
import UserManagement from "./components/screens/user/UserManagement";
import './App.css';

function App() {
  return (
      <Router>
          <Layout>
              <Routes>
                  <Route path="/" element={<Home />} />
                  <Route path="/home" element={<Home />} />
                  <Route path="/login" element={<LoginRegister />} />
                  <Route path="/contact-us" element={<ContactUs />} />
                  <Route path="/admin/roles" element={<RoleManagement />} />
                  <Route path="/admin/permissions" element={<PermissionManagement />} />
                  <Route path="/admin/users" element={<UserManagement />} />
              </Routes>
          </Layout>
      </Router>
  );
}

export default App;
