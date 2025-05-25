import { useState, useEffect } from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './App.css'
import keycloakService from './services/keycloakService'
import OverviewTable from './components/table/table'
import { Button } from './components/ui/button' // Import Button component

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
    return <div>Loading...</div>;
  }

  return (
    <BrowserRouter>
      {authenticated ? (
        <div>
          {/* Add logout button in the top right corner */}
          <div className="flex justify-end p-4">
            <button 
              className="px-4 py-2 bg-red-500 text-white rounded"
              onClick={() => keycloakService.logout()}
            >
              Logout
            </button>
          </div>
          
          {/* Show the table component */}
          <OverviewTable />
        </div>
      ) : (
        <Routes>
          <Route path="/" element={
            <div className="flex flex-col items-center justify-center min-h-screen">
              <h1 className="text-2xl mb-6">Welcome to Pflege Application</h1>
              <Button 
                className="w-full max-w-sm" 
                onClick={() => keycloakService.login()}
              >
                Login with Keycloak
              </Button>
            </div>
          } />
          <Route path="/home" element={<OverviewTable />} />
        </Routes>
      )}
    </BrowserRouter>
  )
}

export default App
