import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(null)
  const [role, setRole] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const storedToken = localStorage.getItem('fw_token')
    const storedUser = localStorage.getItem('fw_user')
    const storedRole = localStorage.getItem('fw_role')
    if (storedToken && storedUser) {
      setToken(storedToken)
      setUser(JSON.parse(storedUser))
      setRole(storedRole)
    }
    setLoading(false)
  }, [])

  const login = (tokenValue, userData) => {
    localStorage.setItem('fw_token', tokenValue)
    localStorage.setItem('fw_user', JSON.stringify(userData))
    localStorage.setItem('fw_role', userData.role)
    setToken(tokenValue)
    setUser(userData)
    setRole(userData.role)
  }

  const logout = () => {
    localStorage.removeItem('fw_token')
    localStorage.removeItem('fw_user')
    localStorage.removeItem('fw_role')
    setToken(null)
    setUser(null)
    setRole(null)
  }

  const isAuthenticated = !!user

  return (
    <AuthContext.Provider value={{ user, token, role, login, logout, isAuthenticated, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}

export default AuthContext
