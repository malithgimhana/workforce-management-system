import { useState } from 'react'
import api from '../api/axios'
import { toast } from 'react-toastify'

export default function ExportButton({ type, format, label }) {
  const [loading, setLoading] = useState(false)

  const handleExport = async () => {
    setLoading(true)
    try {
      const response = await api.get(`/reports/export/${format}`, {
        params: { type },
        responseType: 'blob',
      })
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `${type}_report.${format}`)
      document.body.appendChild(link)
      link.click()
      link.remove()
      toast.success(`${label} downloaded successfully`)
    } catch {
      toast.error(`Failed to download ${label}`)
    } finally {
      setLoading(false)
    }
  }

  return (
    <button
      onClick={handleExport}
      disabled={loading}
      className="flex items-center gap-2 btn-secondary text-sm disabled:opacity-50"
    >
      <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
      </svg>
      {loading ? 'Downloading...' : label}
    </button>
  )
}
