import UserEditForm from "../components/UserEditForm.jsx";
import {useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import axios from "../../axiosConfig.js";
import {
    Box, Button,
    Card,
    CardContent,
    Chip,
    CircularProgress,
    Dialog,
    DialogContent,
    DialogTitle,
    Typography
} from "@mui/material";

export default function UserDetails() {
    const { userId } = useParams();
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUserDetails = async () => {
            try {
                const response = await axios.get(`/api/users/${userId}`);
                setUser(response.data);
            } catch (error) {
                console.error('Error fetching user details:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchUserDetails();
    }, [userId]);

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <CircularProgress />
            </Box>
        );
    }

    if (!user) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <Typography variant="h6" color="error">
                    User not found.
                </Typography>
            </Box>
        );
    }

    return (
        <Dialog open={isOpen} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>User Details</DialogTitle>
            <DialogContent>
                <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                    <Card sx={{ width: 400, padding: 2 }}>
                        <CardContent>
                            <Typography variant="h5" gutterBottom>
                                User Details
                            </Typography>
                            <Typography variant="body1">
                                <strong>Username:</strong> {user.username}
                            </Typography>
                            <Typography variant="body1">
                                <strong>First Name:</strong> {user.firstname}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Last Name:</strong> {user.lastname}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Email:</strong> {user.email}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Gender:</strong> {user.gender}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Phone:</strong> {user.phone}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Address:</strong> {user.address}
                            </Typography>
                            <Typography variant="body1" gutterBottom component="div">
                                <strong>Roles:</strong>{' '}
                                {user.roles.map((role, index) => (
                                    <Chip key={index} label={role} sx={{ marginRight: 1 }} />
                                ))}
                            </Typography>
                            <UserEditForm user={user} />
                            <Box mt={2}>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={() => navigate('/admin/users')}
                                >
                                    Back to Users
                                </Button>
                            </Box>
                        </CardContent>
                    </Card>
                </Box>
            </DialogContent>
        </Dialog>
    );
}