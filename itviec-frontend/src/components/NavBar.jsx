// Navbar.jsx
import React from 'react';
import {Link, useLocation} from 'react-router-dom';

import {
  AppBar,
  Box,
  Button,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
} from '@mui/material';

import {
  AccountCircle as AccountCircleIcon,
  Assignment as AssignmentIcon,
  CalendarToday as CalendarTodayIcon,
  Home as HomeIcon,
  Menu as MenuIcon,
  People as PeopleIcon,
  Work as WorkIcon,
} from '@mui/icons-material';

import Logout from './Logout';

const Navbar = ({ userInfo }) => {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [openSidebar, setOpenSidebar] = React.useState(false);
  const location = useLocation();

  const toggleSidebar = () => {
    setOpenSidebar(!openSidebar);
  };

  const handleMenuClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div>
      {/* Horizontal Navbar */}
      <AppBar position="fixed" sx={{ top: 0, left: 0, right: 0, zIndex: 1000 }}>
        <Toolbar>
          <IconButton edge="start" color="inherit" aria-label="menu" onClick={toggleSidebar}>
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" sx={{ flexGrow: 1, pl: 2 }}>
            IMS System
          </Typography>
          <Box display="flex" alignItems="center">
            <Button color="inherit" onClick={handleMenuClick}>
              <AccountCircleIcon sx={{ marginRight: 1 }} />
              {userInfo.name}
            </Button>
          </Box>
          <Box sx={{ ml: 2 }}>
            <Logout />
          </Box>
        </Toolbar>
      </AppBar>

      {/* Sidebar */}
      <Drawer
        anchor="left"
        open={openSidebar}
        onClose={toggleSidebar}
        sx={{
          width: 240,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: 240,
            boxSizing: 'border-box',
            marginTop: '64px', // Height of the AppBar
          },
        }}
      >
        <Box sx={{ overflow: 'auto' }}>
          <List>
            <ListItem 
              button 
              component={Link} 
              to="/test" 
              onClick={toggleSidebar}
              selected={isActive('/test')}
            >
              <ListItemIcon><HomeIcon /></ListItemIcon>
              <ListItemText primary="Homepage" />
            </ListItem>
            <ListItem 
              button 
              component={Link} 
              to="/candidates" 
              onClick={toggleSidebar}
              selected={isActive('/candidates')}
            >
              <ListItemIcon><PeopleIcon /></ListItemIcon>
              <ListItemText primary="Candidates" />
            </ListItem>
            <ListItem 
              button 
              component={Link} 
              to="/jobs" 
              onClick={toggleSidebar}
              selected={isActive('/jobs')}
            >
              <ListItemIcon><WorkIcon /></ListItemIcon>
              <ListItemText primary="Jobs" />
            </ListItem>
            <ListItem 
              button 
              component={Link} 
              to="/interviews" 
              onClick={toggleSidebar}
              selected={isActive('/interviews')}
            >
              <ListItemIcon><CalendarTodayIcon /></ListItemIcon>
              <ListItemText primary="Interviews" />
            </ListItem>
            {userInfo.role !== 'Interviewer' && (
              <ListItem 
                button 
                component={Link} 
                to="/offers" 
                onClick={toggleSidebar}
                selected={isActive('/offers')}
              >
                <ListItemIcon><AssignmentIcon /></ListItemIcon>
                <ListItemText primary="Offers" />
              </ListItem>
            )}
            {userInfo.role === 'Admin' && (
              <ListItem 
                button 
                component={Link} 
                to="/users" 
                onClick={toggleSidebar}
                selected={isActive('/users')}
              >
                <ListItemIcon><AccountCircleIcon /></ListItemIcon>
                <ListItemText primary="Users" />
              </ListItem>
            )}
          </List>
        </Box>
      </Drawer>
    </div>
  );
};

export default Navbar;
