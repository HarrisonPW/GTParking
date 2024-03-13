// src/components/Splash.js

import React from 'react';
import { useNavigate } from 'react-router-dom';
import car from './car.svg';
import './Splash.css'; // Make sure to create Splash.css in the same directory

const Splash = () => {
    const navigate = useNavigate();
    
    

  // After 3 seconds, navigate to the home screen
  React.useEffect(() => {
    const timer = setTimeout(() => {
        navigate('/home');
    }, 3000);
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="splash-container">
      <img src={car} className="splash-logo" alt="car logo" />
      <h1 className="splash-title">park <span className="color-different">W</span> me</h1>
    </div>
  );
};

export default Splash;
