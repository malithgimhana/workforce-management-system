import { useAuth } from '../context/AuthContext'
import { apply } from '../api/application.api'
import { toast } from 'react-toastify'
import { useState } from 'react'

const categoryColors = {
  FACTORY: 'bg-blue-100 text-blue-700',
  RESTAURANT: 'bg-orange-100 text-orange-700',
  MARKETING: 'bg-purple-100 text-purple-700',
  OTHER: 'bg-gray-100 text-gray-700',
}

export default function JobCard({ job, distance }) {
  const { isAuthenticated, role } = useAuth()
  const [applying, setApplying] = useState(false)
  const [applied, setApplied] = useState(false)

  const handleApply = async () => {
    if (!isAuthenticated) {
      toast.info('Please login to apply')
      return
    }
    setApplying(true)
    try {
      await apply(job.jobId)
      setApplied(true)
      toast.success('Application submitted successfully!')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to apply')
    } finally {
      setApplying(false)
    }
  }

  return (
    <div className="card hover:shadow-md transition-shadow duration-200">
      <div className="flex justify-between items-start mb-3">
        <div className="flex-1">
          <div className="flex items-center gap-2 flex-wrap mb-1">
            <span className={`badge ${categoryColors[job.category] || 'bg-gray-100 text-gray-700'}`}>
              {job.category}
            </span>
            {distance != null && (
              <span className="badge bg-green-100 text-green-700">
                {distance.toFixed(1)} km away
              </span>
            )}
            {!job.isActive && (
              <span className="badge bg-red-100 text-red-700">Closed</span>
            )}
          </div>
          <h3 className="text-lg font-semibold text-gray-900">{job.title}</h3>
          <p className="text-sm text-indigo-600 font-medium">{job.company?.name}</p>
        </div>
        <div className="text-right">
          <p className="text-xl font-bold text-gray-900">LKR {Number(job.dailyWage).toLocaleString()}</p>
          <p className="text-xs text-gray-500">per day</p>
        </div>
      </div>

      {job.description && (
        <p className="text-sm text-gray-600 mb-3 line-clamp-2">{job.description}</p>
      )}

      <div className="grid grid-cols-2 gap-2 text-sm text-gray-600 mb-4">
        <div className="flex items-center gap-1">
          <svg className="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          {job.shiftStartTime} - {job.shiftEndTime}
        </div>
        <div className="flex items-center gap-1">
          <svg className="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          {job.shiftDate}
        </div>
        <div className="flex items-center gap-1">
          <svg className="w-4 h-4 text-gray-400 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
          </svg>
          {job.factoryLocation ? (
            <a
              href={
                job.latitude && job.longitude
                  ? `https://www.google.com/maps?q=${job.latitude},${job.longitude}`
                  : `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(job.factoryLocation)}`
              }
              target="_blank"
              rel="noopener noreferrer"
              className="truncate text-indigo-600 hover:text-indigo-800 hover:underline"
              title={job.latitude && job.longitude ? 'Open exact location in Google Maps' : 'Search location in Google Maps'}
            >
              {job.factoryLocation}
            </a>
          ) : (
            <span className="truncate text-gray-400">Location not set</span>
          )}
        </div>
        <div className="flex items-center gap-1">
          <svg className="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          {job.approvedWorkers}/{job.requiredWorkers} workers
        </div>
      </div>

      {(job.gender !== 'ANY' || job.minAge || job.maxAge) && (
        <div className="flex gap-2 mb-3 flex-wrap">
          {job.gender && job.gender !== 'ANY' && (
            <span className="text-xs text-gray-500 bg-gray-50 px-2 py-1 rounded">Gender: {job.gender}</span>
          )}
          {(job.minAge || job.maxAge) && (
            <span className="text-xs text-gray-500 bg-gray-50 px-2 py-1 rounded">
              Age: {job.minAge || '?'} - {job.maxAge || '?'}
            </span>
          )}
        </div>
      )}

      {isAuthenticated && role === 'WORKER' && job.isActive && (
        <button
          onClick={handleApply}
          disabled={applying || applied}
          className={`w-full py-2 rounded-lg text-sm font-medium transition-colors ${
            applied
              ? 'bg-green-100 text-green-700 cursor-default'
              : 'btn-primary'
          }`}
        >
          {applied ? 'Applied!' : applying ? 'Applying...' : 'Apply Now'}
        </button>
      )}
    </div>
  )
}
