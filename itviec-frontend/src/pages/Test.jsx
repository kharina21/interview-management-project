import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import Navbar from '../components/NavBar';
import axios from 'axios';

// Configure axios defaults
axios.defaults.withCredentials = true;
axios.defaults.baseURL = 'http://localhost:8080';


export default function Test() {
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get('/api/check-auth');
        console.log('Auth response:', response.data);

        if (response.data.authenticated) {
          // If we have user data, use it
          if (response.data.user) {
            // Get the roles from the response
            const roles = response.data.user.roles || [];
            // Convert the roles array to a Set and get the first role
            const roleSet = new Set(roles);
            const role = roleSet.size > 0 ? Array.from(roleSet)[0] : 'USER';
            
            setUserInfo({
              name: `${response.data.user.firstname} ${response.data.user.lastname}`,
              currentPage: 'Test',
              role: role
            });
          } else {
            // If we don't have user data but are authenticated, set default values
            setUserInfo({
              name: 'User',
              currentPage: 'Test',
              role: 'USER'
            });
          }
        } else {
          console.log('Not authenticated, redirecting to login');
          navigate('/login', { replace: true });
        }
      } catch (error) {
        console.error('Error fetching user info:', error);
        navigate('/login', { replace: true });
      } finally {
        setLoading(false);
      }
    };

    fetchUserInfo();
  }, [navigate]);

  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh' 
      }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (!userInfo) {
    return null;
  }

  return (
    <>
      <Navbar userInfo={userInfo} />
      <div className="container mt-5">
        <div className="row justify-content-center">
          <div className="col-md-8">
            <div className="card shadow">
              <div className="card-body text-center">
                <h1 className="display-4 mb-4">Welcome to Interview Management System</h1>
                <p className="lead">Hello, {userInfo.name}!</p>
                <p className="text-muted">You are logged in as {userInfo.role}</p>
                <div className="mt-4">
                  <p>This is your dashboard where you can manage interviews and candidates.</p>
                  <p>Use the navigation bar above to access different features of the system.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
