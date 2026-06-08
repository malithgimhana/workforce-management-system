import { Link } from 'react-router-dom'

export default function Unauthorized() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center max-w-md px-4">
        <p className="text-8xl font-bold text-yellow-500 mb-4">401</p>
        <h1 className="text-2xl font-bold text-gray-900 mb-2">Unauthorized</h1>
        <p className="text-gray-500 mb-8">
          You need to be logged in to access this page.
        </p>
        <div className="flex gap-3 justify-center">
          <Link to="/login" className="btn-primary">Login</Link>
          <Link to="/" className="btn-secondary">Back to Home</Link>
        </div>
      </div>
    </div>
  )
}
