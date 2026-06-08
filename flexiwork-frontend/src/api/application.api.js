import api from './axios'

export const apply = (jobId) => api.post(`/applications/${jobId}`)
export const withdraw = (id) => api.delete(`/applications/${id}`)
export const getJobApplicants = (jobId) => api.get(`/applications/job/${jobId}`)
export const approveWorker = (id) => api.put(`/applications/${id}/approve`)
export const rejectWorker = (id) => api.put(`/applications/${id}/reject`)
export const getMyApplications = () => api.get('/applications/my')
