import api from './axios'

export const getBalance = () => api.get('/payments/balance')
export const getHistory = () => api.get('/payments/history')
export const payCommission = (data) => api.post('/payments/pay', data)
export const getAllPayments = () => api.get('/payments/admin')
