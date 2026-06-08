import api from './axios'

export const generateQR = (jobId) => api.get(`/qr/generate/${jobId}`)
export const scanQR = (qrToken) => api.post('/qr/scan', { qrToken })
