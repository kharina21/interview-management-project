import React, {useEffect, useState} from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes, useLocation} from 'react-router-dom';
import Login from './pages/Login';
import Test from './pages/Test';
import ForgotPassword from './pages/ForgotPassword';
import Logout from './components/Logout';
import axios from 'axios';
import ResetPassword from "./pages/ResetPassword.jsx";

const ProtectedRoute = ({ children }) => {
  const location = useLocation();
  const [isAuthenticated, setIsAuthenticated] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/check-auth', {
          withCredentials: true
        });
        setIsAuthenticated(response.data.authenticated);
      } catch (error) {
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/logout" element={<Logout />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        <Route
          path="/test" 
          element={
            <ProtectedRoute>
              <Test />
            </ProtectedRoute>
          } 
        />
        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;
