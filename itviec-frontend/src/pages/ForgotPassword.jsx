import React, {useState} from 'react';
import {Alert, Box, Button, Stack, TextField, Typography,} from '@mui/material';
import {useNavigate} from 'react-router-dom';
import axios from 'axios';


export default function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [status, setStatus] = useState({ type: '', message: '' });
  const navigate = useNavigate();

  const validateEmail = () => {
    if (!email) {
      setStatus({ type: 'error', message: 'ME002: Required field' });
      return false;
    }
    return true;
  };

  const handleApiResponse = (response) => {
    if (response.status === 200) {
      setStatus({
        type: 'success',
        message: "We've sent an email with the link to reset your password.",
      });
    } else if (response.status === 404) {
      setStatus({
        type: 'error',
        message: "The email address doesn't exist. Please try again.",
      });
    }
  };

  const handleSend = async () => {
    if (!validateEmail()) return;

    try {
      const response = await axios.post('/api/forgot-password', { email }, {
        headers: { 'Content-Type': 'application/json' },
      });
      handleApiResponse(response);
    } catch (error) {
      setStatus({
        type: 'error',
        message: 'Network error. Please check your connection and try again.',
      });
    }
  };

  const handleCancel = () => {
    navigate('/login');
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
          Forgot Password
        </Typography>

        {status.message && (
          <Alert severity={status.type}>{status.message}</Alert>
        )}

        <TextField
          fullWidth
          label="Email Address"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <Stack direction="row" spacing={2} justifyContent="flex-end">
          <Button variant="outlined" onClick={handleCancel}>
            Cancel
          </Button>
          <Button variant="contained" onClick={handleSend}>
            Send
          </Button>
        </Stack>
      </Stack>
    </Box>
    </Stack>
  );
}
