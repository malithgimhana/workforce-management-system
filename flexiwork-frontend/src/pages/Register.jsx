import { useState, useRef } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import axios from 'axios'

const DISTRICTS = [
  'Colombo','Gampaha','Kalutara','Kandy','Matale','Nuwara Eliya','Galle','Matara',
  'Hambantota','Jaffna','Kilinochchi','Mannar','Mullaitivu','Vavuniya','Trincomalee',
  'Batticaloa','Ampara','Kegalle','Ratnapura','Anuradhapura','Polonnaruwa','Badulla',
  'Monaragala','Kurunegala','Puttalam',
]

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({
    firstName: '', lastName: '', phone: '', nic: '', email: '',
    gender: '', password: '', address: '', district: '',
  })
  const [errors, setErrors]     = useState({})
  const [submitting, setSubmitting] = useState(false)
  const [showPw, setShowPw]     = useState(false)

  // File states
  const [photoSrc,    setPhotoSrc]    = useState(null)
  const [nicFrontSrc, setNicFrontSrc] = useState(null)
  const [nicBackSrc,  setNicBackSrc]  = useState(null)
  const photoRef    = useRef()
  const nicFrontRef = useRef()
  const nicBackRef  = useRef()

  const set = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }))

  function validate() {
    const e = {}
    if (!form.firstName.trim()) e.firstName = 'Required'
    if (!form.lastName.trim())  e.lastName  = 'Required'
    if (!/^[0-9]{10}$/.test(form.phone)) e.phone = 'Must be 10 digits'
    if (!/^([0-9]{9}[VvXx]|[0-9]{12})$/.test(form.nic.trim())) e.nic = 'Invalid NIC format'
    if (!form.password || form.password.length < 8) e.password = 'Min 8 characters'
    if (!form.address.trim())   e.address   = 'Required'
    if (!form.district)         e.district  = 'Required'
    if (!form.gender)           e.gender    = 'Required'
    if (!nicFrontRef.current?.files?.[0]) e.nicFront = 'NIC front photo required'
    if (!nicBackRef.current?.files?.[0])  e.nicBack  = 'NIC back photo required'
    return e
  }

  async function handleSubmit(e) {
    e.preventDefault()
    const errs = validate()
    if (Object.keys(errs).length) { setErrors(errs); return }
    setErrors({})
    setSubmitting(true)
    try {
      const fd = new FormData()
      fd.append('firstName', form.firstName.trim())
      fd.append('lastName',  form.lastName.trim())
      fd.append('phone',     form.phone)
      fd.append('nic',       form.nic.trim())
      fd.append('gender',    form.gender.toUpperCase())
      fd.append('password',  form.password)
      fd.append('address',   form.address.trim())
      fd.append('district',  form.district)
      if (form.email.trim()) fd.append('email', form.email.trim())
      if (photoRef.current?.files?.[0])    fd.append('photo',    photoRef.current.files[0])
      if (nicFrontRef.current?.files?.[0]) fd.append('nicFront', nicFrontRef.current.files[0])
      if (nicBackRef.current?.files?.[0])  fd.append('nicBack',  nicBackRef.current.files[0])

      await axios.post('/api/auth/register', fd)
      toast.success('Account created! Your documents are pending review. Please sign in.')
      navigate('/login')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Registration failed')
    } finally {
      setSubmitting(false)
    }
  }

  function previewFile(file, setter) {
    if (!file) return
    setter(URL.createObjectURL(file))
  }

  return (
    <div style={{ minHeight: '100vh', background: '#F6F6F4', padding: '44px 20px 64px', position: 'relative', overflowX: 'hidden', fontFamily: "'Plus Jakarta Sans', system-ui, sans-serif" }}>
      <div style={{ position: 'fixed', inset: 0, zIndex: -1, overflow: 'hidden', pointerEvents: 'none' }}>
        <div style={{ position: 'absolute', inset: 0, background: 'radial-gradient(680px 460px at 88% -6%, rgba(235,23,0,0.2) 0%, transparent 62%), radial-gradient(620px 520px at 4% 108%, rgba(235,23,0,0.14) 0%, transparent 60%)' }} />
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(rgba(235,23,0,0.14) 1px, transparent 1.6px)', backgroundSize: '26px 26px' }} />
        <FloatingCards />
      </div>

      <div style={{ width: '100%', maxWidth: 580, margin: '0 auto', background: '#fff', border: '1px solid #ECECEC', borderRadius: 20, boxShadow: '0 18px 50px rgba(26,26,26,.13)', padding: '40px 36px 36px' }}>
        {/* Brand */}
        <div style={{ textAlign: 'center', marginBottom: 28 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10, marginBottom: 16 }}>
            <div style={{ width: 38, height: 38, background: '#EB1700', borderRadius: 10, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontWeight: 800, fontSize: 15 }}>FW</div>
            <span style={{ fontWeight: 800, fontSize: 18, color: '#1A1A1A' }}>FlexiWork</span>
          </div>
          <h1 style={{ fontSize: 24, fontWeight: 800, color: '#1A1A1A', marginBottom: 6 }}>Create your account</h1>
          <p style={{ color: '#767676', fontSize: 14.5 }}>Join thousands earning daily across Sri Lanka.</p>
        </div>

        {/* Profile Photo */}
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: 24 }}>
          <button type="button" onClick={() => photoRef.current.click()} style={{ width: 84, height: 84, borderRadius: '50%', background: photoSrc ? 'transparent' : '#FFF0EE', border: '2.5px dashed #EB1700', cursor: 'pointer', overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            {photoSrc ? <img src={photoSrc} alt="Profile" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
              : <svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="#EB1700" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="8.5" r="3.6"/><path d="M4.5 20a7.5 7.5 0 0 1 15 0"/></svg>}
          </button>
          <input ref={photoRef} type="file" accept="image/*" style={{ display: 'none' }} onChange={e => previewFile(e.target.files[0], setPhotoSrc)} />
          <span style={{ marginTop: 8, fontSize: 12.5, color: '#767676' }}>{photoSrc ? 'Photo added — tap to change' : 'Add a profile photo (optional)'}</span>
        </div>

        <form onSubmit={handleSubmit} noValidate>
          {/* Name row */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14, marginBottom: 14 }}>
            <Field label="First name" error={errors.firstName}>
              <input type="text" placeholder="First name" value={form.firstName} onChange={set('firstName')} style={inputStyle(errors.firstName)} />
            </Field>
            <Field label="Last name" error={errors.lastName}>
              <input type="text" placeholder="Last name" value={form.lastName} onChange={set('lastName')} style={inputStyle(errors.lastName)} />
            </Field>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14, marginBottom: 14 }}>
            <Field label="Mobile phone" error={errors.phone}>
              <input type="tel" placeholder="07X XXX XXXX" value={form.phone} onChange={set('phone')} style={inputStyle(errors.phone)} />
            </Field>
            <Field label="Email (optional)">
              <input type="email" placeholder="your@email.com" value={form.email} onChange={set('email')} style={inputStyle()} />
            </Field>
          </div>

          <div style={{ marginBottom: 14 }}>
            <Field label="NIC number" error={errors.nic} hint="Old format: 9 digits + V/X  |  New format: 12 digits">
              <input type="text" placeholder="NIC number" value={form.nic} onChange={set('nic')} maxLength={12} style={inputStyle(errors.nic)} />
            </Field>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14, marginBottom: 14 }}>
            <Field label="Gender" error={errors.gender}>
              <select value={form.gender} onChange={set('gender')} style={{ ...inputStyle(errors.gender), appearance: 'none' }}>
                <option value="">Select gender</option>
                <option value="male">Male</option>
                <option value="female">Female</option>
                <option value="other">Other</option>
              </select>
            </Field>
            <Field label="District" error={errors.district}>
              <select value={form.district} onChange={set('district')} style={{ ...inputStyle(errors.district), appearance: 'none' }}>
                <option value="">Select district</option>
                {DISTRICTS.map(d => <option key={d} value={d}>{d}</option>)}
              </select>
            </Field>
          </div>

          <div style={{ marginBottom: 14 }}>
            <Field label="Address" error={errors.address}>
              <textarea placeholder="House no, street, city" value={form.address} onChange={set('address')} rows={2} style={{ ...inputStyle(errors.address), resize: 'none' }} />
            </Field>
          </div>

          <div style={{ marginBottom: 20 }}>
            <Field label="Password" error={errors.password}>
              <div style={{ position: 'relative' }}>
                <input type={showPw ? 'text' : 'password'} placeholder="Min 8 characters" value={form.password} onChange={set('password')} style={{ ...inputStyle(errors.password), paddingRight: 44 }} />
                <button type="button" onClick={() => setShowPw(v => !v)} style={{ position: 'absolute', right: 12, top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', color: '#767676' }}>
                  {showPw
                    ? <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.9"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                    : <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.9"><path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>}
                </button>
              </div>
            </Field>
          </div>

          {/* NIC Photos */}
          <div style={{ marginBottom: 20 }}>
            <p style={{ fontSize: 13, fontWeight: 700, color: '#3D3D3D', marginBottom: 10 }}>NIC Photos <span style={{ color: '#EB1700' }}>*</span></p>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
              <Field label="Front of NIC" error={errors.nicFront}>
                <PhotoUploadBox
                  label="Front side"
                  preview={nicFrontSrc}
                  inputRef={nicFrontRef}
                  hasError={!!errors.nicFront}
                  onChange={e => previewFile(e.target.files[0], setNicFrontSrc)}
                />
              </Field>
              <Field label="Back of NIC" error={errors.nicBack}>
                <PhotoUploadBox
                  label="Back side"
                  preview={nicBackSrc}
                  inputRef={nicBackRef}
                  hasError={!!errors.nicBack}
                  onChange={e => previewFile(e.target.files[0], setNicBackSrc)}
                />
              </Field>
            </div>
            <p style={{ fontSize: 12, color: '#9A9A9A', marginTop: 6 }}>Clear photos of both sides of your NIC. Admin will verify before you can apply for jobs.</p>
          </div>

          <button type="submit" disabled={submitting} style={{ width: '100%', background: submitting ? '#f5a09a' : '#EB1700', color: '#fff', border: 'none', borderRadius: 13, padding: '14px 0', fontSize: 15.5, fontWeight: 700, cursor: submitting ? 'not-allowed' : 'pointer', marginBottom: 12 }}>
            {submitting ? 'Creating account…' : 'Create Account & Submit Documents'}
          </button>

          <Link to="/login" style={{ display: 'block', textDecoration: 'none' }}>
            <div style={{ width: '100%', background: '#F6F6F4', color: '#3D3D3D', border: '1px solid #ECECEC', borderRadius: 13, padding: '13px 0', fontSize: 14.5, fontWeight: 700, textAlign: 'center' }}>
              I already have an account
            </div>
          </Link>

          <p style={{ marginTop: 18, fontSize: 12.5, color: '#9A9A9A', textAlign: 'center' }}>
            By creating an account you agree to FlexiWork's{' '}
            <a href="#" style={{ color: '#EB1700', fontWeight: 700, textDecoration: 'none' }}>Terms</a> &amp;{' '}
            <a href="#" style={{ color: '#EB1700', fontWeight: 700, textDecoration: 'none' }}>Privacy Policy</a>.
          </p>
        </form>
      </div>
    </div>
  )
}

function PhotoUploadBox({ label, preview, inputRef, hasError, onChange }) {
  return (
    <div
      onClick={() => inputRef.current.click()}
      style={{ border: `2px dashed ${hasError ? '#EB1700' : preview ? '#EB1700' : '#E2E2E2'}`, borderRadius: 13, padding: '12px', cursor: 'pointer', textAlign: 'center', background: preview ? '#FFF0EE' : '#FAFAFA', minHeight: 100, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}
    >
      {preview
        ? <img src={preview} alt={label} style={{ width: '100%', height: 80, objectFit: 'cover', borderRadius: 8 }} />
        : <>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#EB1700" strokeWidth="1.8"><rect x="3" y="3" width="18" height="18" rx="3"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
            <span style={{ fontSize: 11.5, color: '#9A9A9A', marginTop: 6 }}>{label}</span>
          </>}
      <input ref={inputRef} type="file" accept="image/*" style={{ display: 'none' }} onChange={onChange} />
    </div>
  )
}

function Field({ label, error, hint, children }) {
  return (
    <div>
      <label style={{ display: 'block', fontSize: 13, fontWeight: 600, color: '#3D3D3D', marginBottom: 6 }}>{label}</label>
      {children}
      {hint && !error && <p style={{ fontSize: 12, color: '#9A9A9A', marginTop: 5 }}>{hint}</p>}
      {error && <p style={{ fontSize: 12, color: '#EB1700', marginTop: 5 }}>{error}</p>}
    </div>
  )
}

function inputStyle(hasError) {
  return { width: '100%', border: `1.5px solid ${hasError ? '#EB1700' : '#E2E2E2'}`, borderRadius: 13, padding: '11px 14px', fontSize: 14.5, color: '#1A1A1A', outline: 'none', background: '#fff', fontFamily: 'inherit', boxSizing: 'border-box' }
}

const CARDS = [
  { tag: 'Factory', tagColor: '#C8120A', tagBg: '#fce8e6', wage: '3,000', title: 'Factory Worker', company: 'ABC Garments Ltd', style: { top: '8%', left: '6%', transform: 'rotate(-7deg)' } },
  { tag: 'Restaurant', tagColor: '#B7791F', tagBg: '#FFF4E5', wage: '1,600', title: 'Barista', company: 'Bean & Co Cafe', style: { top: '60%', left: '3%', transform: 'rotate(5deg)' } },
  { tag: 'Retail', tagColor: '#1F8A5B', tagBg: '#E8F6EF', wage: '1,800', title: 'Warehouse Packer', company: 'SwiftMart Logistics', style: { top: '14%', right: '5%', transform: 'rotate(6deg)' } },
  { tag: 'Restaurant', tagColor: '#B7791F', tagBg: '#FFF4E5', wage: '1,300', title: 'Restaurant Waiter', company: 'Kandy Restaurants', style: { top: '66%', right: '6%', transform: 'rotate(-5deg)' } },
]

function FloatingCards() {
  return (
    <>
      {CARDS.map((c, i) => (
        <div key={i} style={{ position: 'absolute', width: 230, background: '#fff', border: '1px solid #ECECEC', borderRadius: 16, padding: '15px 17px', boxShadow: '0 16px 40px rgba(26,26,26,.07)', opacity: 0.55, ...c.style }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: 10, fontWeight: 800, textTransform: 'uppercase', padding: '3px 8px', borderRadius: 6, background: c.tagBg, color: c.tagColor }}>{c.tag}</span>
            <span style={{ fontSize: 14, fontWeight: 800 }}><span style={{ color: '#EB1700' }}>LKR</span> {c.wage}</span>
          </div>
          <div style={{ fontSize: 14.5, fontWeight: 700, marginTop: 9, color: '#1A1A1A' }}>{c.title}</div>
          <div style={{ fontSize: 11.5, fontWeight: 700, color: '#EB1700', marginTop: 2 }}>{c.company}</div>
        </div>
      ))}
    </>
  )
}
