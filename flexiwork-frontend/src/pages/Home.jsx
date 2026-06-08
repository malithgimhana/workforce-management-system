import { useState, useEffect, useCallback } from 'react'
import JobCard from '../components/JobCard'
import Pagination from '../components/Pagination'
import { searchJobs } from '../api/job.api'
import { toast } from 'react-toastify'

const SRI_LANKA_DISTRICTS = [
  'Colombo', 'Gampaha', 'Kalutara', 'Kandy', 'Matale', 'Nuwara Eliya',
  'Galle', 'Matara', 'Hambantota', 'Jaffna', 'Kilinochchi', 'Mannar',
  'Mullaitivu', 'Vavuniya', 'Trincomalee', 'Batticaloa', 'Ampara',
  'Kegalle', 'Ratnapura', 'Anuradhapura', 'Polonnaruwa', 'Badulla',
  'Monaragala', 'Kurunegala', 'Puttalam',
]

const CATEGORIES = ['FACTORY', 'RESTAURANT', 'MARKETING', 'OTHER']

export default function Home() {
  const [jobs, setJobs] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const [filters, setFilters] = useState({
    district: '',
    minWage: '',
    maxWage: '',
    category: '',
  })
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  const fetchJobs = useCallback(async (currentPage = 0) => {
    setLoading(true)
    try {
      const params = {
        page: currentPage,
        size: 9,
        ...(filters.district && { district: filters.district }),
        ...(filters.minWage && { minWage: filters.minWage }),
        ...(filters.maxWage && { maxWage: filters.maxWage }),
        ...(filters.category && { category: filters.category }),
      }
      const res = await searchJobs(params)
      const data = res.data.data
      setJobs(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to load jobs')
    } finally {
      setLoading(false)
    }
  }, [filters])

  useEffect(() => {
    fetchJobs(page)
  }, [fetchJobs, page])

  const handleSearch = (e) => {
    e.preventDefault()
    setPage(0)
    fetchJobs(0)
  }

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }))
  }

  const clearFilters = () => {
    setFilters({ district: '', minWage: '', maxWage: '', category: '' })
    setPage(0)
  }

  return (
    <div>
      {/* Hero Section */}
      <section className="bg-gradient-to-br from-indigo-600 via-indigo-700 to-purple-700 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
          <div className="text-center max-w-3xl mx-auto">
            <h1 className="text-4xl md:text-5xl font-bold mb-4 leading-tight">
              Find Daily Work.<br />Get Paid Today.
            </h1>
            <p className="text-indigo-100 text-lg mb-8">
              Connect with companies across Sri Lanka offering flexible daily shifts.
              Apply in seconds, work tomorrow.
            </p>

            {/* Search Bar */}
            <form onSubmit={handleSearch} className="flex gap-3 max-w-2xl mx-auto">
              <div className="flex-1 relative">
                <svg className="w-5 h-5 absolute left-3 top-3 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
                <input
                  type="text"
                  placeholder="Search by location or job type..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 rounded-xl text-gray-900 focus:outline-none focus:ring-2 focus:ring-white"
                />
              </div>
              <button type="submit" className="bg-white text-indigo-600 font-semibold px-6 py-3 rounded-xl hover:bg-indigo-50 transition-colors">
                Search
              </button>
            </form>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-3 gap-8 mt-16 max-w-2xl mx-auto text-center">
            <div>
              <p className="text-3xl font-bold">500+</p>
              <p className="text-indigo-200 text-sm mt-1">Active Jobs</p>
            </div>
            <div>
              <p className="text-3xl font-bold">10K+</p>
              <p className="text-indigo-200 text-sm mt-1">Workers Placed</p>
            </div>
            <div>
              <p className="text-3xl font-bold">100+</p>
              <p className="text-indigo-200 text-sm mt-1">Companies</p>
            </div>
          </div>
        </div>
      </section>

      {/* Jobs Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Filters */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-4 mb-8">
          <div className="flex flex-wrap gap-4 items-end">
            <div className="flex-1 min-w-[150px]">
              <label className="block text-xs font-medium text-gray-600 mb-1">District</label>
              <select
                value={filters.district}
                onChange={(e) => handleFilterChange('district', e.target.value)}
                className="input-field text-sm"
              >
                <option value="">All Districts</option>
                {SRI_LANKA_DISTRICTS.map(d => (
                  <option key={d} value={d}>{d}</option>
                ))}
              </select>
            </div>

            <div className="flex-1 min-w-[120px]">
              <label className="block text-xs font-medium text-gray-600 mb-1">Min Wage (LKR)</label>
              <input
                type="number"
                placeholder="e.g. 1000"
                value={filters.minWage}
                onChange={(e) => handleFilterChange('minWage', e.target.value)}
                className="input-field text-sm"
              />
            </div>

            <div className="flex-1 min-w-[120px]">
              <label className="block text-xs font-medium text-gray-600 mb-1">Max Wage (LKR)</label>
              <input
                type="number"
                placeholder="e.g. 3000"
                value={filters.maxWage}
                onChange={(e) => handleFilterChange('maxWage', e.target.value)}
                className="input-field text-sm"
              />
            </div>

            <div className="flex-1 min-w-[140px]">
              <label className="block text-xs font-medium text-gray-600 mb-1">Category</label>
              <select
                value={filters.category}
                onChange={(e) => handleFilterChange('category', e.target.value)}
                className="input-field text-sm"
              >
                <option value="">All Categories</option>
                {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>

            <div className="flex gap-2">
              <button onClick={() => { setPage(0); fetchJobs(0) }} className="btn-primary text-sm">
                Filter
              </button>
              <button onClick={clearFilters} className="btn-secondary text-sm">
                Clear
              </button>
            </div>
          </div>
        </div>

        {/* Results Header */}
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-bold text-gray-900">
            {loading ? 'Loading...' : `${totalElements} Jobs Available`}
          </h2>
        </div>

        {/* Job Grid */}
        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {Array(6).fill(0).map((_, i) => (
              <div key={i} className="card animate-pulse">
                <div className="h-4 bg-gray-200 rounded w-1/3 mb-3"></div>
                <div className="h-6 bg-gray-200 rounded w-3/4 mb-2"></div>
                <div className="h-4 bg-gray-200 rounded w-1/2 mb-4"></div>
                <div className="space-y-2">
                  <div className="h-3 bg-gray-200 rounded"></div>
                  <div className="h-3 bg-gray-200 rounded w-5/6"></div>
                </div>
              </div>
            ))}
          </div>
        ) : jobs.length === 0 ? (
          <div className="text-center py-16">
            <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
            <p className="text-gray-500 text-lg">No jobs found</p>
            <p className="text-gray-400 text-sm mt-1">Try adjusting your filters</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {jobs.map(job => <JobCard key={job.jobId} job={job} />)}
          </div>
        )}

        <Pagination page={page} totalPages={totalPages} onPageChange={(p) => { setPage(p); fetchJobs(p) }} />
      </section>
    </div>
  )
}
