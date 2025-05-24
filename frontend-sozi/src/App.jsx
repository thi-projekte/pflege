import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './App.css'
import { LoginForm } from './components/login-form'
import OverviewTable from './components/table/table'

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LoginForm className="w-full max-w-sm mx-auto mt-10" />} />
        <Route path="/home" element={<OverviewTable />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
