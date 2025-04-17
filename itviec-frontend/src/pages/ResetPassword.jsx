import React, { useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Alert, Box, Button, Stack, TextField, Typography } from '@mui/material';
import axios from 'axios';

export default function ResetPassword() {
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [status, setStatus] = useState({ type: '', message: '' });
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const handleSubmit = async () => {
        const token = searchParams.get('token');
        if (!token) {
            setStatus({ type: 'error', message: 'Invalid or missing token.' });
            return;
        }

        if (password !== confirmPassword) {
            setStatus({ type: 'error', message: 'Passwords do not match.' });
            return;
        }

        try {
            const response = await axios.post('/api/reset-password', {
                token,
                password,
                confirmPassword,
            });

            if (response.status === 200) {
                setStatus({ type: 'success', message: 'Password reset successfully.' });
                setTimeout(() => navigate('/login'), 3000);
            }
        } catch (error) {
            setStatus({
                type: 'error',
                message: error.response?.data || 'An error occurred. Please try again.',
            });
        }
    };

    return (
        <Stack justifyContent="center" alignItems="center" height="100vh" width="100vw">
            <Box
                maxWidth={400}
                mx="auto"
                mt={8}
                p={4}
                boxShadow={3}
                borderRadius={2}
                bgcolor="background.paper"
            >
                <Stack spacing={2}>
                    <Typography variant="h5" fontWeight="bold" textAlign="center">
                        Reset Password
                    </Typography>

                    {status.message && <Alert severity={status.type}>{status.message}</Alert>}

                    <TextField
                        fullWidth
                        label="New Password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <TextField
                        fullWidth
                        label="Confirm Password"
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />

                    <Button variant="contained" onClick={handleSubmit}>
                        Reset Password
                    </Button>
                </Stack>
            </Box>
        </Stack>
    );
}