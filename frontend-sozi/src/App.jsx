import { useState, useEffect } from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './App.css'
import keycloakService from './services/keycloakService'
import OverviewTable from './components/table/table'
import Header from './components/header/header'
import LoaderCentered from './components/loader/Loader'
import Login from './components/login/login'

function App() {
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Initialize Keycloak when the app loads
    keycloakService.initKeycloak(
      // Success callback
      (keycloak) => {
        setAuthenticated(!!keycloak);
        setLoading(false);
      },
      // Error callback
      (error) => {
        console.error('Failed to initialize Keycloak:', error);
        setLoading(false);
      }
    );
  }, []);

  if (loading) {
    return <LoaderCentered />;
  }

  return (
    <BrowserRouter>
      {authenticated ? (
        <>
         <Header />
          <OverviewTable />
        </>
      ) : (
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/home" element={<OverviewTable />} />
        </Routes>
      )}
    </BrowserRouter>
  )
}

export default App
