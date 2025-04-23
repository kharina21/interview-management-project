import {
    Card,
    CardContent,
    Typography,
    CircularProgress,
    Button,
    Box,
    Chip,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
} from '@mui/material';

import { useState, useEffect } from 'react';
export default function EditUser({user}) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editedUser, setEditedUser] = useState({});

    const handleOpenEditModal = () => {
        setEditedUser(user);
        setIsEditModalOpen(true);
    };

    const handleCloseEditModal = () => {
        setIsEditModalOpen(false);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setEditedUser({ ...editedUser, [name]: value });
    };

    const handleSaveUser = async () => {
        try {
            await axios.put(`/api/users/${user.id}`, editedUser);
            setUser(editedUser);
            setIsEditModalOpen(false);
        } catch (error) {
            console.error('Error saving user:', error);
        }
    };

    return (
        <>
        <Box mt={2} display="flex" justifyContent="space-between">
            <Button variant="contained" color="primary" onClick={() => navigate('/admin/users')}>
                Back to Users
            </Button>
            <Button variant="contained" color="secondary" onClick={handleOpenEditModal}>
                Edit User
            </Button>
        </Box>

    <Dialog open={isEditModalOpen} onClose={handleCloseEditModal} fullWidth maxWidth="sm">
        <DialogTitle>Edit User</DialogTitle>
        <DialogContent>
            <TextField
                label="First Name"
                name="firstname"
                value={editedUser.firstname || ''}
                onChange={handleInputChange}
                fullWidth
                margin="normal"
            />
            <TextField
                label="Last Name"
                name="lastname"
                value={editedUser.lastname || ''}
                onChange={handleInputChange}
                fullWidth
                margin="normal"
            />
            <TextField
                label="Email"
                name="email"
                value={editedUser.email || ''}
                onChange={handleInputChange}
                fullWidth
                margin="normal"
            />
        </DialogContent>
        <DialogActions>
            <Button onClick={handleCloseEditModal} color="primary">
                Cancel
            </Button>
            <Button onClick={handleSaveUser} color="secondary">
                Save
            </Button>
        </DialogActions>
    </Dialog>
        </>
    );
}