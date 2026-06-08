import { Link } from 'react-router-dom'

export default function Forbidden() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center max-w-md px-4">
        <p className="text-8xl font-bold text-red-500 mb-4">403</p>
        <h1 className="text-2xl font-bold text-gray-900 mb-2">Access Forbidden</h1>
        <p className="text-gray-500 mb-8">
          You don't have permission to access this page.
          Please contact your administrator if you think this is a mistake.
        </p>
        <div className="flex gap-3 justify-center">
          <Link to="/" className="btn-primary">Back to Home</Link>
          <Link to="/login" className="btn-secondary">Switch Account</Link>
        </div>
      </div>
    </div>
  )
}
