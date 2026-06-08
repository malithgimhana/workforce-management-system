export default function About() {
  return (
    <div className="max-w-4xl mx-auto px-4 py-16">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">About FlexiWork</h1>
        <p className="text-lg text-gray-600">Connecting Sri Lankan workers with daily employment opportunities</p>
      </div>

      <div className="grid md:grid-cols-2 gap-8 mb-12">
        <div className="card">
          <div className="w-12 h-12 bg-indigo-100 rounded-xl flex items-center justify-center mb-4">
            <svg className="w-6 h-6 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-2">Our Mission</h3>
          <p className="text-gray-600">
            FlexiWork bridges the gap between daily workers and employers across Sri Lanka.
            We believe everyone deserves access to fair work opportunities and transparent payment systems.
          </p>
        </div>

        <div className="card">
          <div className="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center mb-4">
            <svg className="w-6 h-6 text-purple-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-2">Our Vision</h3>
          <p className="text-gray-600">
            To become Sri Lanka's most trusted flexible work platform, empowering over 1 million workers
            to access dignified daily employment by 2027.
          </p>
        </div>
      </div>

      <div className="card mb-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6 text-center">Why FlexiWork?</h2>
        <div className="grid md:grid-cols-3 gap-6">
          {[
            { title: 'Fast Application', desc: 'Apply for jobs in seconds with your profile. No lengthy forms.' },
            { title: 'QR Attendance', desc: 'Secure check-in/check-out using QR codes. No paper records.' },
            { title: 'Transparent Pay', desc: 'Know your daily wage upfront. No hidden fees or deductions.' },
          ].map(item => (
            <div key={item.title} className="text-center">
              <h4 className="font-semibold text-gray-900 mb-2">{item.title}</h4>
              <p className="text-sm text-gray-600">{item.desc}</p>
            </div>
          ))}
        </div>
      </div>

      <div className="card text-center">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">Our Team</h2>
        <p className="text-gray-600 mb-6">Built by a passionate team of developers committed to improving lives through technology.</p>
        <div className="flex justify-center gap-6 flex-wrap">
          {['Engineering', 'Design', 'Operations', 'Support'].map(dept => (
            <div key={dept} className="bg-indigo-50 text-indigo-700 px-4 py-2 rounded-lg font-medium text-sm">
              {dept}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
