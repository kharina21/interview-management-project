// Logout.jsx
import React, {useState} from 'react';
import {Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography} from '@mui/material';
import {useNavigate} from 'react-router-dom';
import {ExitToApp as ExitToAppIcon} from '@mui/icons-material';
import axios from 'axios';

// Configure axios defaults
axios.defaults.withCredentials = true;
axios.defaults.baseURL = 'http://localhost:8080';


export default function Logout() {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleConfirm = async () => {
    try {
      const response = await axios.post('/api/logout');
      if (response.data.authenticated) {
        // Clear any client-side auth state
        setOpen(false);
        navigate('/login', { replace: true });
      } else {
        console.error('Logout failed:', response.data.message);
      }
    } catch (error) {
      console.error('Logout error:', error);
      // Still navigate to login even if the server request fails
      setOpen(false);
      navigate('/login', { replace: true });
    }
  };

  return (
    <>
      {/* Log out button */}
      <Button
        color="inherit"
        startIcon={<ExitToAppIcon />}
        onClick={handleOpen}
      >
        Logout
      </Button>

      {/* Confirmation dialog */}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Log out</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to log out?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} variant="outlined">
            Cancel
          </Button>
          <Button onClick={handleConfirm} variant="contained" color="error">
            OK
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
