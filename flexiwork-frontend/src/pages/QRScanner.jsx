import { useState, useEffect, useRef } from 'react'
import { Html5QrcodeScanner } from 'html5-qrcode'
import { scanQR } from '../api/qr.api'
import { toast } from 'react-toastify'

export default function QRScanner() {
  const [scanning, setScanning] = useState(false)
  const [result, setResult] = useState(null)
  const [manualToken, setManualToken] = useState('')
  const scannerRef = useRef(null)
  const scannerInstance = useRef(null)

  useEffect(() => {
    return () => {
      if (scannerInstance.current) {
        scannerInstance.current.clear().catch(console.error)
      }
    }
  }, [])

  const startScanner = () => {
    setScanning(true)
    setResult(null)
    setTimeout(() => {
      if (scannerRef.current) {
        scannerInstance.current = new Html5QrcodeScanner(
          'qr-reader',
          { fps: 10, qrbox: { width: 250, height: 250 } },
          false
        )
        scannerInstance.current.render(
          async (decodedText) => {
            await stopScanner()
            await handleScan(decodedText)
          },
          (error) => {
            // silent error
          }
        )
      }
    }, 100)
  }

  const stopScanner = async () => {
    if (scannerInstance.current) {
      try {
        await scannerInstance.current.clear()
      } catch {}
      scannerInstance.current = null
    }
    setScanning(false)
  }

  const handleScan = async (token) => {
    try {
      let qrToken = token
      // Try to parse JSON content
      try {
        const parsed = JSON.parse(token)
        qrToken = parsed.token || token
      } catch {}

      const res = await scanQR(qrToken)
      setResult(res.data.data)
      const action = res.data.data.action
      if (action === 'CHECK_IN') toast.success(`Check-in recorded for ${res.data.data.userName}`)
      else if (action === 'CHECK_OUT') toast.success(`Check-out recorded for ${res.data.data.userName}`)
      else toast.info(res.data.data.message)
    } catch (err) {
      toast.error(err.response?.data?.message || 'Scan failed')
      setResult({ error: err.response?.data?.message || 'Invalid QR code' })
    }
  }

  const handleManualScan = async () => {
    if (!manualToken.trim()) return
    await handleScan(manualToken.trim())
    setManualToken('')
  }

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      <div className="text-center mb-8">
        <h1 className="text-2xl font-bold text-gray-900">QR Scanner</h1>
        <p className="text-gray-500 mt-1">Scan worker QR codes for attendance</p>
      </div>

      {/* Camera Scanner */}
      <div className="card mb-6">
        <h2 className="font-semibold text-gray-900 mb-4">Camera Scan</h2>
        {!scanning ? (
          <button onClick={startScanner} className="w-full btn-primary py-4 flex items-center justify-center gap-2">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            Start Camera Scanner
          </button>
        ) : (
          <div>
            <div id="qr-reader" className="w-full"></div>
            <button onClick={stopScanner} className="w-full mt-4 btn-secondary">
              Stop Scanner
            </button>
          </div>
        )}
      </div>

      {/* Manual Token Input */}
      <div className="card mb-6">
        <h2 className="font-semibold text-gray-900 mb-4">Manual Token Entry</h2>
        <div className="flex gap-3">
          <input
            type="text"
            value={manualToken}
            onChange={(e) => setManualToken(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleManualScan()}
            placeholder="Enter QR token..."
            className="input-field flex-1"
          />
          <button onClick={handleManualScan} className="btn-primary">
            Scan
          </button>
        </div>
      </div>

      {/* Result */}
      {result && (
        <div className={`card ${result.error ? 'border-red-200 bg-red-50' : 'border-green-200 bg-green-50'}`}>
          {result.error ? (
            <div className="flex items-center gap-3 text-red-700">
              <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p className="font-medium">{result.error}</p>
            </div>
          ) : (
            <div>
              <div className="flex items-center gap-3 mb-4">
                <div className={`w-12 h-12 rounded-full flex items-center justify-center ${
                  result.action === 'CHECK_IN' ? 'bg-green-100' :
                  result.action === 'CHECK_OUT' ? 'bg-blue-100' : 'bg-gray-100'
                }`}>
                  <svg className={`w-6 h-6 ${
                    result.action === 'CHECK_IN' ? 'text-green-600' :
                    result.action === 'CHECK_OUT' ? 'text-blue-600' : 'text-gray-600'
                  }`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                </div>
                <div>
                  <p className="font-bold text-gray-900 text-lg">{result.message}</p>
                  <span className={`badge ${
                    result.action === 'CHECK_IN' ? 'bg-green-100 text-green-700' :
                    result.action === 'CHECK_OUT' ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-700'
                  }`}>{result.action}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3 text-sm">
                <div className="bg-white rounded-lg p-3">
                  <p className="text-gray-500 text-xs mb-1">Worker</p>
                  <p className="font-medium text-gray-900">{result.userName}</p>
                </div>
                <div className="bg-white rounded-lg p-3">
                  <p className="text-gray-500 text-xs mb-1">Job</p>
                  <p className="font-medium text-gray-900">{result.jobTitle}</p>
                </div>
                {result.checkInTime && (
                  <div className="bg-white rounded-lg p-3">
                    <p className="text-gray-500 text-xs mb-1">Check In</p>
                    <p className="font-medium text-gray-900">{new Date(result.checkInTime).toLocaleTimeString()}</p>
                  </div>
                )}
                {result.checkOutTime && (
                  <div className="bg-white rounded-lg p-3">
                    <p className="text-gray-500 text-xs mb-1">Check Out</p>
                    <p className="font-medium text-gray-900">{new Date(result.checkOutTime).toLocaleTimeString()}</p>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
