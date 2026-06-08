import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor: attach JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('fw_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor: handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('fw_token')
      localStorage.removeItem('fw_user')
      localStorage.removeItem('fw_role')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    } else if (error.response?.status === 403) {
      if (window.location.pathname !== '/forbidden') {
        window.location.href = '/forbidden'
      }
    }
    return Promise.reject(error)
  }
)

export default api
