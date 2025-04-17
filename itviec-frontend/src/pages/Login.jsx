// Login.jsx
import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import axios from 'axios';
import {Alert, Box, Button, Checkbox, FormControlLabel, Link, Stack, TextField, Typography} from '@mui/material';

// Configure axios defaults
axios.defaults.withCredentials = true;
axios.defaults.baseURL = 'http://localhost:8080';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState('');
  const [isCheckingAuth, setIsCheckingAuth] = useState(true);
  const navigate = useNavigate();

  // Check for auto-login on component mount
  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await axios.get('/api/check-auth');
        if (response.data.authenticated) {
          navigate('/test', { replace: true });
        }
      } catch (error) {
        console.error('Auth check failed:', error);
      } finally {
        setIsCheckingAuth(false);
      }
    };
    checkAuth();
  }, [navigate]);

  const handleLogin = async () => {
    if (!username || !password) {
      setError('Required field');
      return;
    }

    try {
      const response = await axios.post('/api/login', {
        username,
        password
      });

      if (response.data.authenticated) {
        setError('');
        navigate('/test', { replace: true });
      } else {
        setError('Invalid username/password. Please try again');
      }
    } catch (error) {
      if (error.response && error.response.status === 400) {
        setError('Invalid username/password. Please try again');
      } else {
        setError('An error occurred. Please try again later');
      }
    }
  };

  if (isCheckingAuth) {
    return (
      <Stack justifyContent="center" alignItems="center" height="100vh">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </Stack>
    );
  }

  return (
    <Stack justifyContent="center" alignItems="center" height="100vh" width="100vw">
      <Box
        mx="auto"
        mt={8}
        p={4}
        boxShadow={3}
        borderRadius={2}
        bgcolor="background.paper"
      >
        <Stack spacing={2} alignItems="center">
          <Typography variant="h5" fontWeight="bold">
            IMS Recruitment
          </Typography>

          {error && <Alert severity="error">{error}</Alert>}

          <TextField
            fullWidth
            label="User Name"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            error={!!error}
          />
          <TextField
            fullWidth
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={!!error}
          />
          <FormControlLabel
            control={
              <Checkbox
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
              />
            }
            label="Remember me"
          />
          <Button
            variant="contained"
            color="primary"
            fullWidth
            onClick={handleLogin}
          >
            Login
          </Button>
          <Link
            component="button"
            variant="body2"
            onClick={() => navigate('/forgot-password')}
          >
            Forgot password?
          </Link>
        </Stack>
      </Box>
    </Stack>
  );
}