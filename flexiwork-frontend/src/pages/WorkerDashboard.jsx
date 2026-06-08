import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { getMyApplications } from '../api/application.api'
import { generateQR } from '../api/qr.api'
import { toast } from 'react-toastify'
import QRCode from 'react-qr-code'

const statusColors = {
  PENDING: 'bg-yellow-100 text-yellow-700',
  APPROVED: 'bg-green-100 text-green-700',
  REJECTED: 'bg-red-100 text-red-700',
  WITHDRAWN: 'bg-gray-100 text-gray-600',
}

export default function WorkerDashboard() {
  const { user } = useAuth()
  const [activeTab, setActiveTab] = useState('applications')
  const [applications, setApplications] = useState([])
  const [qrCodes, setQrCodes] = useState({})
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchApplications()
  }, [])

  const fetchApplications = async () => {
    try {
      const res = await getMyApplications()
      setApplications(res.data.data || [])
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to load applications')
    } finally {
      setLoading(false)
    }
  }

  const handleGenerateQR = async (jobId) => {
    try {
      const res = await generateQR(jobId)
      setQrCodes(prev => ({ ...prev, [jobId]: res.data.data.qrCode }))
      toast.success('QR code generated!')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to generate QR')
    }
  }

  const handleDownloadQR = (jobId, jobTitle) => {
    const dataUrl = qrCodes[jobId]
    const link = document.createElement('a')
    link.href = dataUrl
    link.download = `qr-${jobTitle?.replace(/\s+/g, '-') || jobId}.png`
    link.click()
  }

  const handleShareWhatsApp = async (jobId, jobTitle, shiftDate) => {
    const dataUrl = qrCodes[jobId]
    if (navigator.share) {
      try {
        const res = await fetch(dataUrl)
        const blob = await res.blob()
        const file = new File([blob], `qr-${jobId}.png`, { type: 'image/png' })
        await navigator.share({ title: `QR Code - ${jobTitle}`, files: [file] })
        return
      } catch {
        // fall through to WhatsApp link
      }
    }
    const text = encodeURIComponent(`My QR Code for job: ${jobTitle} on ${shiftDate}`)
    window.open(`https://wa.me/?text=${text}`, '_blank')
  }

  const approvedApps = applications.filter(a => a.status === 'APPROVED')
  const completedApps = applications.filter(a => a.status === 'APPROVED')

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 rounded-2xl p-6 text-white mb-8">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 bg-white/20 rounded-full flex items-center justify-center text-2xl font-bold">
            {user?.name?.charAt(0)?.toUpperCase()}
          </div>
          <div>
            <h1 className="text-2xl font-bold">Welcome, {user?.name}!</h1>
            <p className="text-indigo-200">Worker Dashboard</p>
          </div>
        </div>

        <div className="grid grid-cols-3 gap-4 mt-6">
          <div className="bg-white/10 rounded-xl p-4 text-center">
            <p className="text-2xl font-bold">{applications.length}</p>
            <p className="text-sm text-indigo-200 mt-1">Total Applied</p>
          </div>
          <div className="bg-white/10 rounded-xl p-4 text-center">
            <p className="text-2xl font-bold">{approvedApps.length}</p>
            <p className="text-sm text-indigo-200 mt-1">Approved</p>
          </div>
          <div className="bg-white/10 rounded-xl p-4 text-center">
            <p className="text-2xl font-bold">{applications.filter(a => a.status === 'PENDING').length}</p>
            <p className="text-sm text-indigo-200 mt-1">Pending</p>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex space-x-1 bg-gray-100 rounded-xl p-1 mb-6">
        {['applications', 'qrcodes', 'earnings'].map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`flex-1 py-2 px-4 rounded-lg text-sm font-medium transition-colors capitalize ${
              activeTab === tab ? 'bg-white shadow-sm text-indigo-600' : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            {tab === 'qrcodes' ? 'QR Codes' : tab.charAt(0).toUpperCase() + tab.slice(1)}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto"></div>
        </div>
      ) : (
        <>
          {/* Applications Tab */}
          {activeTab === 'applications' && (
            <div className="space-y-3">
              {applications.length === 0 ? (
                <div className="card text-center py-12">
                  <p className="text-gray-500">No applications yet. Browse jobs to get started!</p>
                </div>
              ) : (
                applications.map(app => (
                  <div key={app.applicationId} className="card flex items-center justify-between">
                    <div className="flex-1">
                      <h3 className="font-semibold text-gray-900">{app.job?.title}</h3>
                      <p className="text-sm text-indigo-600">{app.job?.company?.name}</p>
                      <div className="flex items-center gap-3 mt-1 text-sm text-gray-500 flex-wrap">
                        <span>{app.job?.shiftDate}</span>
                        <span>LKR {Number(app.job?.dailyWage || 0).toLocaleString()}</span>
                        {app.job?.factoryLocation && (
                          <a
                            href={
                              app.job.latitude && app.job.longitude
                                ? `https://www.google.com/maps?q=${app.job.latitude},${app.job.longitude}`
                                : `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(app.job.factoryLocation)}`
                            }
                            target="_blank"
                            rel="noopener noreferrer"
                            className="flex items-center gap-1 text-indigo-600 hover:text-indigo-800 hover:underline"
                            title="Open in Google Maps"
                          >
                            <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                            </svg>
                            {app.job.factoryLocation}
                          </a>
                        )}
                      </div>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className={`badge ${statusColors[app.status]}`}>{app.status}</span>
                    </div>
                  </div>
                ))
              )}
            </div>
          )}

          {/* QR Codes Tab */}
          {activeTab === 'qrcodes' && (
            <div className="space-y-4">
              {approvedApps.length === 0 ? (
                <div className="card text-center py-12">
                  <p className="text-gray-500">No approved applications yet.</p>
                </div>
              ) : (
                approvedApps.map(app => (
                  <div key={app.applicationId} className="card">
                    <div className="flex items-start justify-between mb-4">
                      <div>
                        <h3 className="font-semibold text-gray-900">{app.job?.title}</h3>
                        <p className="text-sm text-indigo-600">{app.job?.company?.name}</p>
                        <p className="text-sm text-gray-500 mt-1">{app.job?.shiftDate} | {app.job?.shiftStartTime} - {app.job?.shiftEndTime}</p>
                        <p className="text-sm font-medium text-gray-700">LKR {Number(app.job?.dailyWage || 0).toLocaleString()}/day</p>
                        {app.job?.factoryLocation && (
                          <a
                            href={
                              app.job.latitude && app.job.longitude
                                ? `https://www.google.com/maps?q=${app.job.latitude},${app.job.longitude}`
                                : `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(app.job.factoryLocation)}`
                            }
                            target="_blank"
                            rel="noopener noreferrer"
                            className="flex items-center gap-1 text-sm text-indigo-600 hover:text-indigo-800 hover:underline mt-1"
                            title="Open in Google Maps"
                          >
                            <svg className="w-3 h-3 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                            </svg>
                            {app.job.factoryLocation}
                          </a>
                        )}

                      </div>
                      <span className="badge bg-green-100 text-green-700">APPROVED</span>
                    </div>

                    {qrCodes[app.job?.jobId] ? (
                      <div className="flex flex-col items-center p-4 bg-gray-50 rounded-xl">
                        <img
                          src={qrCodes[app.job?.jobId]}
                          alt="QR Code"
                          className="w-48 h-48"
                        />
                        <p className="text-xs text-gray-500 mt-2">Show this QR code at the factory</p>
                        <div className="flex gap-3 mt-3 w-full">
                          <button
                            onClick={() => handleDownloadQR(app.job?.jobId, app.job?.title)}
                            className="flex-1 flex items-center justify-center gap-2 py-2 px-4 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg transition-colors"
                          >
                            <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 20 20" fill="currentColor">
                              <path fillRule="evenodd" d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm3.293-7.707a1 1 0 011.414 0L9 10.586V3a1 1 0 112 0v7.586l1.293-1.293a1 1 0 111.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" clipRule="evenodd" />
                            </svg>
                            Download QR
                          </button>
                          <button
                            onClick={() => handleShareWhatsApp(app.job?.jobId, app.job?.title, app.job?.shiftDate)}
                            className="flex-1 flex items-center justify-center gap-2 py-2 px-4 bg-green-500 hover:bg-green-600 text-white text-sm font-medium rounded-lg transition-colors"
                          >
                            <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
                              <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
                            </svg>
                            Share to WhatsApp
                          </button>
                        </div>
                      </div>
                    ) : (
                      <button
                        onClick={() => handleGenerateQR(app.job?.jobId)}
                        className="w-full btn-primary"
                      >
                        Generate QR Code
                      </button>
                    )}
                  </div>
                ))
              )}
            </div>
          )}

          {/* Earnings Tab */}
          {activeTab === 'earnings' && (
            <div>
              {completedApps.length === 0 ? (
                <div className="card text-center py-12">
                  <p className="text-gray-500">No completed shifts yet.</p>
                </div>
              ) : (
                <>
                  <div className="card mb-4 bg-gradient-to-r from-green-50 to-emerald-50 border-green-100">
                    <p className="text-sm text-gray-600">Estimated Total Earnings</p>
                    <p className="text-3xl font-bold text-green-700 mt-1">
                      LKR {completedApps.reduce((sum, a) => sum + Number(a.job?.dailyWage || 0), 0).toLocaleString()}
                    </p>
                  </div>
                  <div className="space-y-3">
                    {completedApps.map(app => (
                      <div key={app.applicationId} className="card flex items-center justify-between">
                        <div>
                          <h3 className="font-medium text-gray-900">{app.job?.title}</h3>
                          <p className="text-sm text-gray-500">{app.job?.shiftDate}</p>
                        </div>
                        <p className="font-bold text-gray-900">LKR {Number(app.job?.dailyWage || 0).toLocaleString()}</p>
                      </div>
                    ))}
                  </div>
                </>
              )}
            </div>
          )}
        </>
      )}
    </div>
  )
}
