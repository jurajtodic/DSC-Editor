import React from 'react';
import ReactDOM from 'react-dom/client';
import './style/index.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import App from './App';
import NavBar from './components/NavBar';
import DscEditor from './components/DscEditor';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <NavBar/>
      <Routes>
        <Route path='/' element={<App/>}></Route>
        <Route path='DscEdit/:id' element={<DscEditor/>}></Route>
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);
