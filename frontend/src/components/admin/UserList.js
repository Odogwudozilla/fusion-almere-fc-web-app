import React from 'react';
import {
    List,
    Datagrid,
    TextField,
    EmailField,
    BooleanField,
    useRecordContext,
    Button,
    useNotify,
    useDataProvider,
    Pagination,
} from 'react-admin';
import { Avatar as MuiAvatar, IconButton, Tooltip } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { useState } from 'react';

// Custom Avatar field
const AvatarField = () => {
    const record = useRecordContext();
    if (!record) return null;
    return (
        <MuiAvatar
            src={record.profilePictureUrl || '/default-avatar.png'}
            alt={record.name || 'User Avatar'}
        />
    );
};

// Conditional fields for Address and Postcode
const AddressFields = ({ showAddressDetails }) => {
    const record = useRecordContext();
    if (!record) return null;
    return (
        <>
            {showAddressDetails && <TextField source="postcode" label="Postcode" />}
            {showAddressDetails && <TextField source="address" label="Address" />}
        </>
    );
};

// Custom Action buttons
const ActionButtons = ({ record, refresh }) => {
    const notify = useNotify();
    const dataProvider = useDataProvider();

    // Always call useRecordContext() unconditionally
    const contextRecord = useRecordContext();

    // Use record prop if available, otherwise fallback to contextRecord
    const currentRecord = record || contextRecord;

    // If no record is found, return null or handle appropriately
    if (!currentRecord) {
        console.error('No record found!');
        return null;
    }

    const handleDelete = async () => {
        try {
            await dataProvider.delete('users', { id: currentRecord.externalIdentifier });
            notify('User deleted successfully!', { type: 'success' });
            refresh();
        } catch (error) {
            notify('Failed to delete user. Please try again.', { type: 'error' });
        }
    };

    return (
        <>
            <Tooltip title="Edit">
                <IconButton href={`#/users/${currentRecord.externalIdentifier}`} color="primary">
                    <EditIcon />
                </IconButton>
            </Tooltip>
            <Tooltip title="Delete">
                <IconButton onClick={handleDelete} color="error">
                    <DeleteIcon />
                </IconButton>
            </Tooltip>
        </>
    );
};



const UserList = (props) => {
    const [showAddressDetails, setShowAddressDetails] = useState(true);

    return (
        <List
            {...props}
            pagination={<Pagination rowsPerPageOptions={[10, 25, 50]} />}
            title="Users"
        >
            <div style={{ padding: '10px', textAlign: 'right' }}>
                <Button
                    label={showAddressDetails ? 'Hide Address Details' : 'Show Address Details'}
                    onClick={() => setShowAddressDetails(!showAddressDetails)}
                />
            </div>
            <Datagrid rowClick="edit">
                <AvatarField />
                <TextField source="username" label="Username" />
                <TextField source="name" label="Name" />
                <EmailField source="email" label="Email" />
                <TextField source="whatsappNumber" label="WhatsApp" />
                <AddressFields showAddressDetails={showAddressDetails} />
                <TextField source="membershipType" label="Membership Type" />
                <BooleanField source="status" label="Status" />
                <TextField source="assignedRoles" label="Roles" />
                <ActionButtons />
            </Datagrid>
        </List>
    );
};

export default UserList;
