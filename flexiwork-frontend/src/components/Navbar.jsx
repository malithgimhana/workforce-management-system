import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { logout as apiLogout } from '../api/auth.api'

export default function Navbar() {
  const { isAuthenticated, user, role, logout } = useAuth()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)
  const [userMenuOpen, setUserMenuOpen] = useState(false)

  const handleLogout = async () => {
    try {
      await apiLogout()
    } catch {}
    logout()
    navigate('/')
    setUserMenuOpen(false)
  }

  const getDashboardPath = () => {
    if (role === 'WORKER') return '/worker/dashboard'
    if (role === 'ADMIN') return '/admin'
    if (role === 'FACTORY_MANAGER') return '/qr-scanner'
    return '/employer/dashboard'
  }

  return (
    <nav className="bg-white shadow-sm border-b border-gray-100 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">FW</span>
            </div>
            <span className="text-xl font-bold text-gray-900">FlexiWork</span>
          </Link>

          {/* Desktop Nav Links */}
          <div className="hidden md:flex items-center space-x-8">
            <Link to="/" className="text-gray-600 hover:text-indigo-600 transition-colors font-medium">Home</Link>
            <Link to="/about" className="text-gray-600 hover:text-indigo-600 transition-colors font-medium">About</Link>
            <Link to="/services" className="text-gray-600 hover:text-indigo-600 transition-colors font-medium">Services</Link>
            <Link to="/contact" className="text-gray-600 hover:text-indigo-600 transition-colors font-medium">Contact</Link>
          </div>

          {/* Right Side */}
          <div className="hidden md:flex items-center space-x-4">
            {!isAuthenticated ? (
              <>
                <Link to="/login" className="text-gray-600 hover:text-indigo-600 font-medium transition-colors">
                  Login
                </Link>
                <Link to="/register" className="btn-primary text-sm">
                  Register
                </Link>
              </>
            ) : (
              <div className="relative">
                <button
                  onClick={() => setUserMenuOpen(!userMenuOpen)}
                  className="flex items-center space-x-2 bg-gray-50 rounded-lg px-3 py-2 hover:bg-gray-100 transition-colors"
                >
                  <div className="w-8 h-8 bg-indigo-100 rounded-full flex items-center justify-center">
                    <span className="text-indigo-600 font-semibold text-sm">
                      {user?.name?.charAt(0)?.toUpperCase() || 'U'}
                    </span>
                  </div>
                  <span className="text-gray-700 font-medium text-sm">{user?.name}</span>
                  <svg className="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>

                {userMenuOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-100 py-1 z-50">
                    <div className="px-4 py-2 border-b border-gray-50">
                      <p className="text-xs text-gray-500">Signed in as</p>
                      <p className="text-sm font-medium text-gray-800 truncate">{user?.name}</p>
                      <span className="badge bg-indigo-100 text-indigo-600 mt-1">{role}</span>
                    </div>
                    <Link
                      to={getDashboardPath()}
                      className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setUserMenuOpen(false)}
                    >
                      Dashboard
                    </Link>
                    <button
                      onClick={handleLogout}
                      className="w-full text-left flex items-center px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                    >
                      Logout
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Mobile Hamburger */}
          <button
            className="md:hidden text-gray-600 hover:text-gray-900"
            onClick={() => setMenuOpen(!menuOpen)}
          >
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              {menuOpen
                ? <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                : <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              }
            </svg>
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {menuOpen && (
        <div className="md:hidden border-t border-gray-100 bg-white">
          <div className="px-4 py-3 space-y-2">
            <Link to="/" className="block py-2 text-gray-700 hover:text-indigo-600" onClick={() => setMenuOpen(false)}>Home</Link>
            <Link to="/about" className="block py-2 text-gray-700 hover:text-indigo-600" onClick={() => setMenuOpen(false)}>About</Link>
            <Link to="/services" className="block py-2 text-gray-700 hover:text-indigo-600" onClick={() => setMenuOpen(false)}>Services</Link>
            <Link to="/contact" className="block py-2 text-gray-700 hover:text-indigo-600" onClick={() => setMenuOpen(false)}>Contact</Link>
            {!isAuthenticated ? (
              <div className="flex space-x-3 pt-2">
                <Link to="/login" className="btn-secondary flex-1 text-center text-sm" onClick={() => setMenuOpen(false)}>Login</Link>
                <Link to="/register" className="btn-primary flex-1 text-center text-sm" onClick={() => setMenuOpen(false)}>Register</Link>
              </div>
            ) : (
              <div className="pt-2 border-t border-gray-100">
                <Link to={getDashboardPath()} className="block py-2 text-indigo-600 font-medium" onClick={() => setMenuOpen(false)}>Dashboard</Link>
                <button onClick={handleLogout} className="block py-2 text-red-600 font-medium">Logout</button>
              </div>
            )}
          </div>
        </div>
      )}
    </nav>
  )
}
