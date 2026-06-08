import { useState } from 'react'
import { toast } from 'react-toastify'

export default function Contact() {
  const [formData, setFormData] = useState({ name: '', email: '', subject: '', message: '' })
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = (e) => {
    e.preventDefault()
    setSubmitting(true)
    setTimeout(() => {
      toast.success('Message sent! We will get back to you within 24 hours.')
      setFormData({ name: '', email: '', subject: '', message: '' })
      setSubmitting(false)
    }, 1000)
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-16">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">Contact Us</h1>
        <p className="text-lg text-gray-600">We'd love to hear from you. Send us a message!</p>
      </div>

      <div className="grid md:grid-cols-2 gap-8">
        {/* Contact Info */}
        <div className="space-y-6">
          <div className="card">
            <h3 className="font-bold text-gray-900 mb-4">Get in Touch</h3>
            {[
              { label: 'Email', value: 'support@flexiwork.lk', icon: '📧' },
              { label: 'Phone', value: '+94 11 234 5678', icon: '📞' },
              { label: 'Address', value: 'No 10, Galle Road, Colombo 03, Sri Lanka', icon: '📍' },
              { label: 'Hours', value: 'Mon–Sat, 8AM–6PM (IST)', icon: '🕐' },
            ].map(item => (
              <div key={item.label} className="flex items-start gap-3 mb-4 last:mb-0">
                <span className="text-xl">{item.icon}</span>
                <div>
                  <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">{item.label}</p>
                  <p className="text-gray-800">{item.value}</p>
                </div>
              </div>
            ))}
          </div>

          <div className="card">
            <h3 className="font-bold text-gray-900 mb-3">Follow Us</h3>
            <div className="flex gap-3">
              {['Facebook', 'Twitter', 'LinkedIn', 'Instagram'].map(s => (
                <button key={s} className="flex-1 text-xs bg-gray-100 text-gray-700 py-2 px-1 rounded-lg hover:bg-indigo-100 hover:text-indigo-700 transition-colors">
                  {s}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Contact Form */}
        <div className="card">
          <h3 className="font-bold text-gray-900 mb-6">Send a Message</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Your Name</label>
              <input
                type="text"
                required
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="input-field"
                placeholder="Kamal Perera"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
              <input
                type="email"
                required
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="input-field"
                placeholder="kamal@example.com"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Subject</label>
              <input
                type="text"
                required
                value={formData.subject}
                onChange={(e) => setFormData({ ...formData, subject: e.target.value })}
                className="input-field"
                placeholder="How can we help?"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Message</label>
              <textarea
                required
                rows={5}
                value={formData.message}
                onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                className="input-field resize-none"
                placeholder="Tell us more..."
              />
            </div>
            <button
              type="submit"
              disabled={submitting}
              className="w-full btn-primary py-3 disabled:opacity-60"
            >
              {submitting ? 'Sending...' : 'Send Message'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}
