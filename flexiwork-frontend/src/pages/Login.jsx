import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import { login as apiLogin } from '../api/auth.api'
import { useAuth } from '../context/AuthContext'
import { toast } from 'react-toastify'

export default function Login() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm()
  const { login } = useAuth()
  const navigate = useNavigate()

  const onSubmit = async (data) => {
    try {
      const res = await apiLogin(data)
      const { token, ...userData } = res.data.data
      login(token, userData)
      toast.success('Welcome back, ' + userData.fullName + '!')

      const role = userData.role
      if (role === 'WORKER') navigate('/worker/dashboard')
      else if (role === 'ADMIN') navigate('/admin')
      else if (role === 'FACTORY_MANAGER') navigate('/qr-scanner')
      else navigate('/employer/dashboard')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Invalid credentials')
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-purple-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-indigo-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <span className="text-white font-bold text-2xl">FW</span>
          </div>
          <h1 className="text-2xl font-bold text-gray-900">Welcome back</h1>
          <p className="text-gray-500 mt-1">Sign in to your FlexiWork account</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Phone or Email
              </label>
              <input
                {...register('identifier', { required: 'Phone or email is required' })}
                type="text"
                placeholder="0771234567 or you@example.com"
                className="input-field"
              />
              {errors.identifier && (
                <p className="text-red-500 text-xs mt-1">{errors.identifier.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Password
              </label>
              <input
                {...register('password', { required: 'Password is required' })}
                type="password"
                placeholder="Enter your password"
                className="input-field"
              />
              {errors.password && (
                <p className="text-red-500 text-xs mt-1">{errors.password.message}</p>
              )}
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full btn-primary py-3 text-base disabled:opacity-60"
            >
              {isSubmitting ? 'Signing in...' : 'Sign In'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Don't have an account?{' '}
              <Link to="/register" className="text-indigo-600 font-medium hover:text-indigo-700">
                Register here
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
