export default function Services() {
  const services = [
    {
      icon: '🔍',
      title: 'Job Search',
      description: 'Browse hundreds of daily job opportunities filtered by location, wage, and category across all Sri Lanka districts.',
    },
    {
      icon: '📲',
      title: 'Instant Apply',
      description: 'Apply for jobs with a single tap. Get notifications on approval status via SMS and app notifications.',
    },
    {
      icon: '📷',
      title: 'QR Attendance',
      description: 'Secure check-in and check-out system using QR codes. Factory managers scan your code — no manual records.',
    },
    {
      icon: '💰',
      title: 'Wage Transparency',
      description: 'Every job listing clearly shows the daily wage. FlexiWork charges a transparent 10% commission from employers only.',
    },
    {
      icon: '🏢',
      title: 'Employer Tools',
      description: 'Companies can post jobs, manage applications, approve workers, and track attendance all in one dashboard.',
    },
    {
      icon: '📊',
      title: 'Reports & Analytics',
      description: 'Download attendance and commission reports in CSV or PDF format for compliance and payroll.',
    },
  ]

  return (
    <div className="max-w-5xl mx-auto px-4 py-16">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">Our Services</h1>
        <p className="text-lg text-gray-600">Everything you need for flexible daily employment</p>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
        {services.map(s => (
          <div key={s.title} className="card hover:shadow-md transition-shadow">
            <div className="text-4xl mb-4">{s.icon}</div>
            <h3 className="text-lg font-bold text-gray-900 mb-2">{s.title}</h3>
            <p className="text-gray-600 text-sm">{s.description}</p>
          </div>
        ))}
      </div>

      <div className="bg-indigo-600 rounded-2xl p-8 text-white text-center">
        <h2 className="text-2xl font-bold mb-2">Ready to Get Started?</h2>
        <p className="text-indigo-200 mb-6">Join thousands of workers finding daily employment across Sri Lanka.</p>
        <div className="flex justify-center gap-4">
          <a href="/register" className="bg-white text-indigo-600 font-semibold px-6 py-3 rounded-xl hover:bg-indigo-50 transition-colors">
            Register as Worker
          </a>
          <a href="/register" className="bg-indigo-700 text-white font-semibold px-6 py-3 rounded-xl hover:bg-indigo-800 transition-colors">
            Register as Employer
          </a>
        </div>
      </div>
    </div>
  )
}
