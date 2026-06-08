import api from './axios'

export const searchJobs = (params) => api.get('/jobs', { params })
export const getJob = (id) => api.get(`/jobs/${id}`)
export const postJob = (data) => api.post('/jobs', data)
export const updateJob = (id, data) => api.put(`/jobs/${id}`, data)
export const deleteJob = (id) => api.delete(`/jobs/${id}`)
export const getCompanyJobs = () => api.get('/jobs/company')
