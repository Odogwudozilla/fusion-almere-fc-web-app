import React from 'react';
import { useRecordContext, Show } from 'react-admin';
import { Card, CardContent, Typography } from '@mui/material';

const ProfilePage = props => (
    <Show {...props}>
        <ProfileDetails />
    </Show>
);

const ProfileDetails = () => {
    const record = useRecordContext();
    if (!record) return null;

    return (
        <Card>
            <CardContent>
                <Typography variant="h5">{record.name}</Typography>
                <Typography>Email: {record.email}</Typography>
                <Typography>Phone: {record.phone}</Typography>
            </CardContent>
        </Card>
    );
};

export default ProfilePage;
