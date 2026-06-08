import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'

export default function AdminPanel() {
  const [activeTab, setActiveTab] = useState('workers')
  const [users, setUsers]         = useState([])
  const [companies, setCompanies] = useState([])
  const [stats, setStats]         = useState(null)
  const [loading, setLoading]     = useState(true)
  const navigate  = useNavigate()
  const { logout, user } = useAuth()

  useEffect(() => { fetchAll() }, [])

  const fetchAll = async () => {
    setLoading(true)
    try {
      const [usersRes, companiesRes, dashRes] = await Promise.all([
        api.get('/admin/users'),
        api.get('/admin/companies'),
        api.get('/admin/dashboard'),
      ])
      setUsers(usersRes.data.data || [])
      setCompanies(companiesRes.data.data || [])
      setStats(dashRes.data.data)
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to load admin data')
    } finally {
      setLoading(false)
    }
  }

  const handleDeactivateUser = async (id) => {
    if (!confirm('Deactivate this user?')) return
    try {
      await api.delete(`/admin/users/${id}`)
      toast.success('User deactivated')
      setUsers(users.map(u => u.userId === id ? { ...u, isDeleted: true } : u))
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to deactivate user')
    }
  }

  const handleDeleteCompany = async (id) => {
    if (!confirm('Delete this company?')) return
    try {
      await api.delete(`/admin/companies/${id}`)
      toast.success('Company deleted')
      setCompanies(companies.filter(c => c.companyId !== id))
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to delete company')
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const statCards = [
    { label: 'Workers',      value: stats?.totalWorkers },
    { label: 'Companies',    value: stats?.totalCompanies },
    { label: 'Jobs',         value: stats?.totalJobs },
    { label: 'Applications', value: stats?.totalApplications },
    { label: 'Payments',     value: stats?.totalPayments },
  ]

  const TABS = [
    { key: 'workers',   label: 'Workers' },
    { key: 'companies', label: 'Companies' },
    { key: 'jobs',      label: 'All Jobs' },
    { key: 'payments',  label: 'Payments' },
  ]

  return (
    <div className="min-h-screen bg-white flex flex-col">

      {/* ── Top Navbar (sticky) ── */}
      <header className="sticky top-0 z-50 bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between shadow-sm">
        {/* Left: Logo */}
        <div className="flex items-center gap-2">
          <div className="w-9 h-9 rounded-xl bg-[#EB1700] flex items-center justify-center">
            <span className="text-white font-extrabold text-sm">FW</span>
          </div>
          <span className="font-bold text-gray-900 text-lg">FlexiWork</span>
          <span className="ml-1 bg-gray-900 text-white text-xs font-semibold px-2 py-0.5 rounded-full">Admin</span>
        </div>

        {/* Right: user + actions */}
        <div className="flex items-center gap-3">
          <span className="text-sm text-gray-600 font-medium">{user?.fullName || 'FlexiWork Admin'}</span>
          <button
            onClick={() => setActiveTab('workers')}
            className="flex items-center gap-1.5 bg-[#EB1700] hover:bg-[#c91400] text-white text-sm font-semibold px-4 py-2 rounded-xl transition-colors"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
            </svg>
            Dashboard
          </button>
          <button
            onClick={handleLogout}
            className="flex items-center gap-1.5 border border-gray-300 hover:border-gray-400 text-gray-700 text-sm font-medium px-4 py-2 rounded-xl transition-colors"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            Sign Out
          </button>
        </div>
      </header>

      {/* ── Dark Hero / Stats ── */}
      <section className="bg-[#1B2133] px-6 py-8">
        <h1 className="text-white text-2xl font-bold mb-6">System Dashboard</h1>
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
          {statCards.map(card => (
            <div key={card.label} className="bg-[#252B3B] rounded-2xl px-5 py-5 text-center">
              <p className="text-white text-3xl font-bold">{loading ? '—' : (card.value ?? 0)}</p>
              <p className="text-gray-400 text-sm mt-1">{card.label}</p>
            </div>
          ))}
        </div>
      </section>

      {/* ── Tabs + Content ── */}
      <div className="flex-1 bg-white px-6 py-6">

        {/* Tab Pills */}
        <div className="flex items-center gap-2 mb-6">
          {TABS.map(tab => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`px-5 py-2 rounded-full text-sm font-semibold transition-colors ${
                activeTab === tab.key
                  ? 'bg-[#EB1700] text-white'
                  : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {loading ? (
          <div className="flex items-center justify-center py-20">
            <div className="w-8 h-8 border-2 border-[#EB1700] border-t-transparent rounded-full animate-spin" />
          </div>
        ) : (
          <>
            {/* ── Workers Tab ── */}
            {activeTab === 'workers' && (
              <div>
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-lg font-bold text-gray-900">Registered Workers</h2>
                  <button
                    onClick={() => exportCSV(users, ['name','nic','phone','district','email'], 'workers')}
                    className="text-sm font-semibold text-[#EB1700] hover:underline"
                  >
                    Export CSV
                  </button>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="border-b border-gray-100">
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">Name</th>
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">NIC</th>
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">Phone</th>
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">District</th>
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">Status</th>
                        <th className="text-left py-3 text-gray-500 font-medium">Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {users.map(u => (
                        <tr key={u.userId} className="border-b border-gray-50 hover:bg-gray-50">
                          <td className="py-3 pr-6 font-medium text-gray-900">{u.name}</td>
                          <td className="py-3 pr-6 text-gray-600">{u.nic}</td>
                          <td className="py-3 pr-6 text-gray-600">{u.phone}</td>
                          <td className="py-3 pr-6 text-gray-600">{u.district || '—'}</td>
                          <td className="py-3 pr-6">
                            <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${
                              u.isDeleted ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-700'
                            }`}>
                              {u.isDeleted ? 'Inactive' : 'Active'}
                            </span>
                          </td>
                          <td className="py-3">
                            {!u.isDeleted && (
                              <button
                                onClick={() => handleDeactivateUser(u.userId)}
                                className="text-sm font-semibold text-[#EB1700] hover:underline"
                              >
                                Deactivate
                              </button>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* ── Companies Tab ── */}
            {activeTab === 'companies' && (
              <div>
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-lg font-bold text-gray-900">Registered Companies</h2>
                  <button
                    onClick={() => exportCSV(companies, ['name','brNumber','phone','email'], 'companies')}
                    className="text-sm font-semibold text-[#EB1700] hover:underline"
                  >
                    Export CSV
                  </button>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="border-b border-gray-100">
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">Company Name</th>
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">BR Number</th>
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">Phone</th>
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">Email</th>
                        <th className="text-left py-3 pr-6 text-gray-500 font-medium">Balance</th>
                        <th className="text-left py-3 text-gray-500 font-medium">Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {companies.map(c => (
                        <tr key={c.companyId} className="border-b border-gray-50 hover:bg-gray-50">
                          <td className="py-3 pr-6 font-medium text-gray-900">{c.name}</td>
                          <td className="py-3 pr-6 text-gray-600">{c.brNumber}</td>
                          <td className="py-3 pr-6 text-gray-600">{c.phone}</td>
                          <td className="py-3 pr-6 text-gray-600">{c.email}</td>
                          <td className="py-3 pr-6 text-gray-600">LKR {Number(c.balance || 0).toLocaleString()}</td>
                          <td className="py-3">
                            <button
                              onClick={() => handleDeleteCompany(c.companyId)}
                              className="text-sm font-semibold text-[#EB1700] hover:underline"
                            >
                              Delete
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* ── All Jobs Tab ── */}
            {activeTab === 'jobs' && (
              <div>
                <h2 className="text-lg font-bold text-gray-900 mb-4">All Jobs</h2>
                <p className="text-sm text-gray-500">Job listing coming soon — use the Reports tab to export job data.</p>
              </div>
            )}

            {/* ── Payments Tab ── */}
            {activeTab === 'payments' && (
              <div>
                <h2 className="text-lg font-bold text-gray-900 mb-6">Reports & Exports</h2>
                <div className="space-y-6">
                  <div className="border border-gray-200 rounded-2xl p-5">
                    <h3 className="font-semibold text-gray-900 mb-3">Attendance Reports</h3>
                    <div className="flex flex-wrap gap-3">
                      <ExportBtn type="attendance" format="csv" label="Download Attendance CSV" />
                      <ExportBtn type="attendance" format="pdf" label="Download Attendance PDF" />
                    </div>
                  </div>
                  <div className="border border-gray-200 rounded-2xl p-5">
                    <h3 className="font-semibold text-gray-900 mb-3">Commission Reports</h3>
                    <div className="flex flex-wrap gap-3">
                      <ExportBtn type="commission" format="csv" label="Download Commission CSV" />
                      <ExportBtn type="commission" format="pdf" label="Download Commission PDF" />
                    </div>
                  </div>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}

function ExportBtn({ type, format, label }) {
  const handleClick = async () => {
    try {
      const res = await api.get(`/reports/${type}?format=${format}`, { responseType: 'blob' })
      const url = URL.createObjectURL(new Blob([res.data]))
      const a = document.createElement('a')
      a.href = url
      a.download = `${type}-report.${format}`
      a.click()
      URL.revokeObjectURL(url)
    } catch {
      toast.error('Export failed')
    }
  }
  return (
    <button
      onClick={handleClick}
      className="flex items-center gap-2 border border-gray-300 hover:border-[#EB1700] hover:text-[#EB1700] text-gray-700 text-sm font-medium px-4 py-2 rounded-xl transition-colors"
    >
      {label}
    </button>
  )
}

function exportCSV(data, fields, filename) {
  if (!data.length) return
  const header = fields.join(',')
  const rows   = data.map(row => fields.map(f => `"${row[f] ?? ''}"`).join(','))
  const blob   = new Blob([[header, ...rows].join('\n')], { type: 'text/csv' })
  const url    = URL.createObjectURL(blob)
  const a      = document.createElement('a')
  a.href = url; a.download = `${filename}.csv`; a.click()
  URL.revokeObjectURL(url)
}
