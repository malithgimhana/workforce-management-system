import { useState, useEffect, useRef } from 'react'
import { useAuth } from '../context/AuthContext'
import { getCompanyJobs, postJob, deleteJob } from '../api/job.api'
import { getJobApplicants, approveWorker, rejectWorker } from '../api/application.api'
import { getBalance, getHistory, payCommission } from '../api/payment.api'
import { toast } from 'react-toastify'
import { useForm } from 'react-hook-form'
import api from '../api/axios'

const CATEGORIES = ['FACTORY', 'RESTAURANT', 'MARKETING', 'OTHER']
const GENDERS = ['ANY', 'MALE', 'FEMALE', 'OTHER']

function PostJobModal({ onClose, onSuccess }) {
  const { register, handleSubmit, formState: { errors, isSubmitting }, setValue, watch } = useForm()
  const [geocoding, setGeocoding] = useState(false)
  const [coordsPreview, setCoordsPreview] = useState(null)

  const watchedLocation = watch('factoryLocation')
  const watchedLat = watch('latitude')
  const watchedLng = watch('longitude')

  const handleFindLocation = async () => {
    const address = watchedLocation?.trim()
    if (!address) { toast.error('Enter a location address first'); return }
    setGeocoding(true)
    try {
      const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address + ', Sri Lanka')}&limit=1&addressdetails=1`
      const res = await fetch(url)
      if (!res.ok) { toast.error(`Map service error: ${res.status}. Try again or enter coordinates manually.`); return }
      const data = await res.json()
      if (!data.length) { toast.error('Address not found. Try a more specific address (e.g. include city/district).'); return }
      const { lat, lon, display_name } = data[0]
      setValue('latitude', parseFloat(parseFloat(lat).toFixed(6)))
      setValue('longitude', parseFloat(parseFloat(lon).toFixed(6)))
      setCoordsPreview({ lat, lon, label: display_name })
      toast.success('Location found!')
    } catch (err) {
      console.error('[FindOnMap]', err)
      toast.error('Could not reach map service. Check your internet connection or enter coordinates manually.')
    } finally {
      setGeocoding(false)
    }
  }

  const onSubmit = async (data) => {
    try {
      await postJob(data)
      toast.success('Job posted successfully!')
      onSuccess()
      onClose()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to post job')
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-bold">Post New Job</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Job Title *</label>
            <input {...register('title', { required: true })} className="input-field" placeholder="e.g. Factory Worker" />
            {errors.title && <p className="text-red-500 text-xs mt-1">Required</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea {...register('description')} rows={3} className="input-field resize-none" placeholder="Job description..." />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Daily Wage (LKR) *</label>
              <input {...register('dailyWage', { required: true, min: 0.01 })} type="number" step="0.01" className="input-field" placeholder="1500.00" />
              {errors.dailyWage && <p className="text-red-500 text-xs mt-1">Required</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Required Workers *</label>
              <input {...register('requiredWorkers', { required: true, min: 1 })} type="number" className="input-field" placeholder="10" />
              {errors.requiredWorkers && <p className="text-red-500 text-xs mt-1">Required</p>}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Shift Start Time *</label>
              <input {...register('shiftStartTime', { required: true })} type="time" className="input-field" />
              {errors.shiftStartTime && <p className="text-red-500 text-xs mt-1">Required</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Shift End Time *</label>
              <input {...register('shiftEndTime', { required: true })} type="time" className="input-field" />
              {errors.shiftEndTime && <p className="text-red-500 text-xs mt-1">Required</p>}
            </div>
          </div>

          {/* Location section */}
          <div className="bg-blue-50 border border-blue-100 rounded-xl p-4 space-y-3">
            <p className="text-sm font-semibold text-blue-800">Job Location</p>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Full Address *</label>
              <div className="flex gap-2">
                <input
                  {...register('factoryLocation', { required: true })}
                  className="input-field flex-1"
                  placeholder="e.g. 75 Braybrooke Place, Colombo 02"
                />
                <button
                  type="button"
                  onClick={handleFindLocation}
                  disabled={geocoding}
                  className="px-3 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg whitespace-nowrap disabled:opacity-60 transition-colors"
                >
                  {geocoding ? 'Finding...' : '📍 Find on Map'}
                </button>
              </div>
              {errors.factoryLocation && <p className="text-red-500 text-xs mt-1">Address is required</p>}
              <p className="text-xs text-gray-500 mt-1">
                Use the <strong>street address</strong> (not building name) for best results — e.g. "75 Braybrooke Place, Colombo" instead of "Hemas House".{' '}
                <a
                  href="https://www.google.com/maps"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-indigo-600 hover:underline"
                >
                  Look up on Google Maps →
                </a>
              </p>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Latitude</label>
                <input {...register('latitude')} type="number" step="any" className="input-field" placeholder="7.169811" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Longitude</label>
                <input {...register('longitude')} type="number" step="any" className="input-field" placeholder="79.891762" />
              </div>
            </div>

            {coordsPreview && (
              <div className="bg-green-50 border border-green-200 rounded-lg p-3">
                <p className="text-xs text-green-800 font-medium mb-1">Location found:</p>
                <p className="text-xs text-green-700 mb-2 line-clamp-2">{coordsPreview.label}</p>
                <a
                  href={`https://www.google.com/maps?q=${coordsPreview.lat},${coordsPreview.lon}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-xs text-indigo-600 hover:underline font-medium"
                >
                  ✓ Verify this pin on Google Maps →
                </a>
                <p className="text-xs text-gray-500 mt-1">If the pin is wrong, adjust the address and click "Find on Map" again.</p>
              </div>
            )}

            {!coordsPreview && watchedLat && watchedLng && (
              <a
                href={`https://www.google.com/maps?q=${watchedLat},${watchedLng}`}
                target="_blank"
                rel="noopener noreferrer"
                className="text-xs text-indigo-600 hover:underline block"
              >
                Preview current coordinates on Google Maps →
              </a>
            )}
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Category *</label>
              <select {...register('category', { required: true })} className="input-field">
                <option value="">Select</option>
                {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
              {errors.category && <p className="text-red-500 text-xs mt-1">Required</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Gender</label>
              <select {...register('gender')} className="input-field">
                {GENDERS.map(g => <option key={g} value={g}>{g}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Shift Date *</label>
              <input {...register('shiftDate', { required: true })} type="date" className="input-field"
                min={new Date().toISOString().split('T')[0]} />
              {errors.shiftDate && <p className="text-red-500 text-xs mt-1">Required</p>}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Min Age</label>
              <input {...register('minAge')} type="number" className="input-field" placeholder="18" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Max Age</label>
              <input {...register('maxAge')} type="number" className="input-field" placeholder="50" />
            </div>
          </div>

          <div className="flex gap-3 pt-2">
            <button type="button" onClick={onClose} className="flex-1 btn-secondary">Cancel</button>
            <button type="submit" disabled={isSubmitting} className="flex-1 btn-primary">
              {isSubmitting ? 'Posting...' : 'Post Job'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default function EmployerDashboard() {
  const { user, role } = useAuth()
  const [activeTab, setActiveTab] = useState('jobs')
  const [jobs, setJobs] = useState([])
  const [applicants, setApplicants] = useState({})
  const [balance, setBalance] = useState(null)
  const [payments, setPayments] = useState([])
  const [loading, setLoading] = useState(true)
  const [showPostModal, setShowPostModal] = useState(false)
  const [docStatus, setDocStatus] = useState(null)
  const [docUploading, setDocUploading] = useState(false)
  const brCertRef = useRef()

  const canManageJobs = ['IT_ADMIN', 'GM', 'COMPANY', 'HR_MANAGER', 'EMPLOYER'].includes(role)
  const canViewFinance = ['FINANCE', 'GM', 'COMPANY', 'EMPLOYER'].includes(role)
  const isFactoryManager = role === 'FACTORY_MANAGER'
  const isCompanyAdmin = ['COMPANY', 'EMPLOYER', 'GM', 'IT_ADMIN'].includes(role)

  useEffect(() => {
    fetchData()
    fetchDocStatus()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const jobsRes = await getCompanyJobs()
      setJobs(jobsRes.data.data || [])

      if (canViewFinance) {
        const balanceRes = await getBalance()
        setBalance(balanceRes.data.data)
        const paymentsRes = await getHistory()
        setPayments(paymentsRes.data.data || [])
      }
    } catch (err) {
      toast.error('Failed to load dashboard data')
    } finally {
      setLoading(false)
    }
  }

  const fetchDocStatus = async () => {
    try {
      const res = await api.get('/documents/company-status')
      setDocStatus(res.data.data)
    } catch {
      // ignore — company may not have uploaded yet
    }
  }

  const handleBrUpload = async () => {
    const file = brCertRef.current?.files?.[0]
    if (!file) { toast.error('Select a file first'); return }
    setDocUploading(true)
    try {
      const form = new FormData()
      form.append('brCert', file)
      await api.post('/documents/company/upload', form, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      toast.success('BR certificate uploaded! Awaiting admin review.')
      fetchDocStatus()
      brCertRef.current.value = ''
    } catch (err) {
      toast.error(err.response?.data?.message || 'Upload failed')
    } finally {
      setDocUploading(false)
    }
  }

  const fetchApplicants = async (jobId) => {
    try {
      const res = await getJobApplicants(jobId)
      setApplicants(prev => ({ ...prev, [jobId]: res.data.data || [] }))
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to load applicants')
    }
  }

  const handleApprove = async (appId, jobId) => {
    try {
      await approveWorker(appId)
      toast.success('Worker approved!')
      fetchApplicants(jobId)
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to approve')
    }
  }

  const handleReject = async (appId, jobId) => {
    try {
      await rejectWorker(appId)
      toast.success('Worker rejected')
      fetchApplicants(jobId)
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to reject')
    }
  }

  const handleDeleteJob = async (jobId) => {
    if (!confirm('Delete this job?')) return
    try {
      await deleteJob(jobId)
      toast.success('Job deleted')
      fetchData()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete job')
    }
  }

  const handlePay = async (paymentId) => {
    try {
      await payCommission({ paymentId })
      toast.success('Payment processed!')
      fetchData()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Payment failed')
    }
  }

  const tabs = [
    ...(canManageJobs ? ['jobs'] : []),
    ...(canManageJobs ? ['workers'] : []),
    ...(isFactoryManager ? ['attendance'] : []),
    ...(canViewFinance ? ['payments'] : []),
  ]

  if (tabs.length === 0) return (
    <div className="max-w-4xl mx-auto px-4 py-16 text-center">
      <p className="text-gray-500">No dashboard access for your role.</p>
    </div>
  )

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 rounded-2xl p-6 text-white mb-8">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 bg-white/20 rounded-full flex items-center justify-center text-2xl font-bold">
            {user?.fullName?.charAt(0)?.toUpperCase()}
          </div>
          <div>
            <h1 className="text-2xl font-bold">{user?.fullName}</h1>
            <p className="text-indigo-200">{role} — Employer Dashboard</p>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-6">
          <div className="bg-white/10 rounded-xl p-4 text-center">
            <p className="text-2xl font-bold">{jobs.filter(j => j.isActive).length}</p>
            <p className="text-xs text-indigo-200 mt-1">Active Jobs</p>
          </div>
          <div className="bg-white/10 rounded-xl p-4 text-center">
            <p className="text-2xl font-bold">{jobs.reduce((s, j) => s + (j.approvedWorkers || 0), 0)}</p>
            <p className="text-xs text-indigo-200 mt-1">Total Workers</p>
          </div>
          <div className="bg-white/10 rounded-xl p-4 text-center">
            <p className="text-2xl font-bold">{payments.filter(p => p.status === 'PENDING').length}</p>
            <p className="text-xs text-indigo-200 mt-1">Pending Payments</p>
          </div>
          <div className="bg-white/10 rounded-xl p-4 text-center">
            <p className="text-2xl font-bold">
              {balance ? `LKR ${Number(balance.totalCommission || 0).toLocaleString()}` : 'N/A'}
            </p>
            <p className="text-xs text-indigo-200 mt-1">Total Commission</p>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex space-x-1 bg-gray-100 rounded-xl p-1 mb-6">
        {tabs.map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`flex-1 py-2 px-4 rounded-lg text-sm font-medium transition-colors capitalize ${
              activeTab === tab ? 'bg-white shadow-sm text-indigo-600' : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto"></div>
        </div>
      ) : (
        <>
          {/* Jobs Tab */}
          {activeTab === 'jobs' && (
            <div>
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-lg font-bold text-gray-900">Company Jobs ({jobs.length})</h2>
                <button onClick={() => setShowPostModal(true)} className="btn-primary text-sm">
                  + Post New Job
                </button>
              </div>
              <div className="space-y-3">
                {jobs.map(job => (
                  <div key={job.jobId} className="card flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <h3 className="font-semibold text-gray-900">{job.title}</h3>
                        <span className={`badge ${job.isActive ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'}`}>
                          {job.isActive ? 'Active' : 'Closed'}
                        </span>
                      </div>
                      <div className="text-sm text-gray-500">
                        {job.shiftDate} | {job.shiftStartTime} - {job.shiftEndTime} | LKR {Number(job.dailyWage).toLocaleString()}
                      </div>
                      <div className="text-xs text-gray-400 mt-1">
                        {job.approvedWorkers}/{job.requiredWorkers} workers | {job.factoryLocation}
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <button
                        onClick={() => { fetchApplicants(job.jobId); setActiveTab('workers') }}
                        className="btn-secondary text-xs"
                      >
                        Applicants
                      </button>
                      <button
                        onClick={() => handleDeleteJob(job.jobId)}
                        className="btn-danger text-xs"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                ))}
                {jobs.length === 0 && (
                  <div className="card text-center py-12">
                    <p className="text-gray-500">No jobs posted yet.</p>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Workers Tab */}
          {activeTab === 'workers' && (
            <div>
              <h2 className="text-lg font-bold text-gray-900 mb-4">Workers by Job</h2>
              {jobs.map(job => (
                <div key={job.jobId} className="card mb-4">
                  <div className="flex justify-between items-center mb-4">
                    <h3 className="font-semibold text-gray-900">{job.title}</h3>
                    <button onClick={() => fetchApplicants(job.jobId)} className="btn-secondary text-xs">
                      Load Applicants
                    </button>
                  </div>
                  {applicants[job.jobId] && (
                    <div className="space-y-2">
                      {applicants[job.jobId].length === 0 ? (
                        <p className="text-sm text-gray-500">No applicants</p>
                      ) : (
                        applicants[job.jobId].map(app => (
                          <div key={app.applicationId} className="flex items-center justify-between bg-gray-50 rounded-lg p-3">
                            <div>
                              <p className="text-sm font-medium text-gray-900">{app.user?.fullName}</p>
                              <p className="text-xs text-gray-500">{app.user?.phone} | {app.user?.nic}</p>
                            </div>
                            <div className="flex items-center gap-2">
                              <span className={`badge text-xs ${
                                app.status === 'APPROVED' ? 'bg-green-100 text-green-700' :
                                app.status === 'REJECTED' ? 'bg-red-100 text-red-700' :
                                'bg-yellow-100 text-yellow-700'
                              }`}>{app.status}</span>
                              {app.status === 'PENDING' && (
                                <>
                                  <button onClick={() => handleApprove(app.applicationId, job.jobId)}
                                    className="text-xs bg-green-600 text-white px-2 py-1 rounded hover:bg-green-700">Approve</button>
                                  <button onClick={() => handleReject(app.applicationId, job.jobId)}
                                    className="text-xs bg-red-600 text-white px-2 py-1 rounded hover:bg-red-700">Reject</button>
                                </>
                              )}
                            </div>
                          </div>
                        ))
                      )}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}

          {/* Attendance Tab */}
          {activeTab === 'attendance' && (
            <div>
              <h2 className="text-lg font-bold text-gray-900 mb-4">Today's Attendance</h2>
              <div className="card text-center py-12">
                <p className="text-gray-500">Use the QR Scanner to track attendance</p>
              </div>
            </div>
          )}

          {/* Documents Tab */}
          {activeTab === 'documents' && (
            <div>
              <h2 className="text-lg font-bold text-gray-900 mb-6">Company Document Verification</h2>

              {/* Status banner */}
              {docStatus?.docStatus && (
                <div className={`rounded-xl p-4 mb-6 flex items-center gap-3 ${
                  docStatus.docStatus === 'APPROVED' ? 'bg-green-50 border border-green-200' :
                  docStatus.docStatus === 'REJECTED' ? 'bg-red-50 border border-red-200' :
                  'bg-yellow-50 border border-yellow-200'
                }`}>
                  <span className={`text-2xl ${
                    docStatus.docStatus === 'APPROVED' ? 'text-green-600' :
                    docStatus.docStatus === 'REJECTED' ? 'text-red-600' : 'text-yellow-600'
                  }`}>
                    {docStatus.docStatus === 'APPROVED' ? '✓' : docStatus.docStatus === 'REJECTED' ? '✗' : '⏳'}
                  </span>
                  <div>
                    <p className={`font-semibold ${
                      docStatus.docStatus === 'APPROVED' ? 'text-green-800' :
                      docStatus.docStatus === 'REJECTED' ? 'text-red-800' : 'text-yellow-800'
                    }`}>
                      {docStatus.docStatus === 'APPROVED' ? 'Documents Approved' :
                       docStatus.docStatus === 'REJECTED' ? 'Documents Rejected' : 'Pending Review'}
                    </p>
                    {docStatus.docStatus === 'REJECTED' && docStatus.docRejectReason && (
                      <p className="text-sm text-red-600 mt-0.5">Reason: {docStatus.docRejectReason}</p>
                    )}
                    {docStatus.docStatus === 'PENDING' && (
                      <p className="text-sm text-yellow-700 mt-0.5">Admin is reviewing your submitted BR certificate.</p>
                    )}
                  </div>
                </div>
              )}

              {/* Upload section */}
              <div className="card">
                <h3 className="font-semibold text-gray-900 mb-1">Upload BR Certificate</h3>
                <p className="text-sm text-gray-500 mb-4">
                  Upload your Business Registration certificate (PDF or image). This is required before you can post jobs.
                </p>
                <div className="flex items-center gap-3">
                  <input
                    ref={brCertRef}
                    type="file"
                    accept=".pdf,.jpg,.jpeg,.png"
                    className="flex-1 text-sm border border-gray-200 rounded-xl px-3 py-2 file:mr-3 file:py-1 file:px-3 file:rounded-lg file:border-0 file:text-sm file:font-medium file:bg-red-50 file:text-red-700 hover:file:bg-red-100"
                  />
                  <button
                    onClick={handleBrUpload}
                    disabled={docUploading}
                    className="btn-primary text-sm whitespace-nowrap"
                  >
                    {docUploading ? 'Uploading…' : 'Upload'}
                  </button>
                </div>
                {docStatus?.brCertPath && (
                  <p className="text-xs text-gray-400 mt-2">Last uploaded: {docStatus.brCertPath.split('/').pop()}</p>
                )}
              </div>
            </div>
          )}

          {/* Payments Tab */}
          {activeTab === 'payments' && (
            <div>
              {balance && (
                <div className="grid grid-cols-2 gap-4 mb-6">
                  <div className="card bg-gradient-to-r from-green-50 to-emerald-50 border-green-100">
                    <p className="text-sm text-gray-600">Account Balance</p>
                    <p className="text-2xl font-bold text-green-700">LKR {Number(balance.balance || 0).toLocaleString()}</p>
                  </div>
                  <div className="card bg-gradient-to-r from-indigo-50 to-blue-50 border-indigo-100">
                    <p className="text-sm text-gray-600">Total Commission</p>
                    <p className="text-2xl font-bold text-indigo-700">LKR {Number(balance.totalCommission || 0).toLocaleString()}</p>
                  </div>
                </div>
              )}

              <h2 className="text-lg font-bold text-gray-900 mb-4">Payment History</h2>
              <div className="space-y-3">
                {payments.map(p => (
                  <div key={p.paymentId} className="card flex items-center justify-between">
                    <div>
                      <p className="font-medium text-gray-900">{p.jobTitle}</p>
                      <p className="text-sm text-gray-500">
                        Wage: LKR {Number(p.workerWage).toLocaleString()} | Commission: LKR {Number(p.commissionAmount).toLocaleString()}
                      </p>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className={`badge ${p.status === 'PAID' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>
                        {p.status}
                      </span>
                      {p.status === 'PENDING' && (
                        <button onClick={() => handlePay(p.paymentId)} className="btn-primary text-xs">
                          Pay Now
                        </button>
                      )}
                    </div>
                  </div>
                ))}
                {payments.length === 0 && (
                  <div className="card text-center py-12">
                    <p className="text-gray-500">No payment records yet.</p>
                  </div>
                )}
              </div>
            </div>
          )}
        </>
      )}

      {showPostModal && (
        <PostJobModal onClose={() => setShowPostModal(false)} onSuccess={fetchData} />
      )}
    </div>
  )
}
