import api from './axios'

export const register = (data) => api.post('/auth/register', data)
export const companyRegister = (data) => api.post('/auth/company/register', data)
export const login = (data) => api.post('/auth/login', data)
export const sessionLogin = (data) => api.post('/session/login', data)
export const logout = () => api.post('/auth/logout')
