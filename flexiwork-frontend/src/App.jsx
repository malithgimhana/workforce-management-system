import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'

import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import WorkerDashboard from './pages/WorkerDashboard'
import EmployerDashboard from './pages/EmployerDashboard'
import AdminPanel from './pages/AdminPanel'
import QRScanner from './pages/QRScanner'
import About from './pages/About'
import Services from './pages/Services'
import Contact from './pages/Contact'
import NotFound from './pages/NotFound'
import Unauthorized from './pages/Unauthorized'
import Forbidden from './pages/Forbidden'

const EMPLOYER_ROLES = ['EMPLOYER', 'COMPANY', 'IT_ADMIN', 'GM', 'HR_MANAGER', 'FACTORY_MANAGER', 'FINANCE']

export default function App() {
  return (
    <AuthProvider>
      <div className="min-h-screen bg-gray-50">
        <Routes>
          {/* Auth pages — no navbar */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Admin — full-page layout, no navbar */}
          <Route path="/admin" element={
            <ProtectedRoute roles={['ADMIN']}>
              <AdminPanel />
            </ProtectedRoute>
          } />

          {/* Pages with Navbar */}
          <Route path="/*" element={
            <>
              <Navbar />
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/jobs" element={<Home />} />
                <Route path="/about" element={<About />} />
                <Route path="/services" element={<Services />} />
                <Route path="/contact" element={<Contact />} />
                <Route path="/unauthorized" element={<Unauthorized />} />
                <Route path="/forbidden" element={<Forbidden />} />

                <Route path="/worker/dashboard" element={
                  <ProtectedRoute roles={['WORKER']}>
                    <WorkerDashboard />
                  </ProtectedRoute>
                } />

                <Route path="/employer/dashboard" element={
                  <ProtectedRoute roles={EMPLOYER_ROLES}>
                    <EmployerDashboard />
                  </ProtectedRoute>
                } />

                <Route path="/qr-scanner" element={
                  <ProtectedRoute roles={['FACTORY_MANAGER', 'GM', 'IT_ADMIN']}>
                    <QRScanner />
                  </ProtectedRoute>
                } />

                <Route path="*" element={<NotFound />} />
              </Routes>
            </>
          } />
        </Routes>
      </div>
    </AuthProvider>
  )
}
