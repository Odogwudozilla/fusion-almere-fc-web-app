import React from 'react';
import { Admin, Resource } from 'react-admin';
import dataProvider from './dataProvider';
import UserList from './UserList';
import ProfilePage from './ProfilePage';

const AdminPanel = () => (
    <Admin dataProvider={dataProvider}>
        <Resource name="users" list={UserList} />
        <Resource name="profiles" show={ProfilePage} />
    </Admin>
);

export default AdminPanel;
