// Auth helpers — shared across all pages

export function getToken() { return localStorage.getItem('fw_token') }
export function getUser() {
  try { return JSON.parse(localStorage.getItem('fw_user')) } catch { return null }
}
export function getRole() { return localStorage.getItem('fw_role') }
export function isAuthenticated() { return !!getToken() }

export function saveAuth(token, userData) {
  localStorage.setItem('fw_token', token)
  localStorage.setItem('fw_user', JSON.stringify(userData))
  localStorage.setItem('fw_role', userData.role)
}

export function clearAuth() {
  localStorage.removeItem('fw_token')
  localStorage.removeItem('fw_user')
  localStorage.removeItem('fw_role')
}

export function requireAuth(allowedRoles) {
  if (!isAuthenticated()) { window.location.href = '/login.html'; return false }
  if (allowedRoles && !allowedRoles.includes(getRole())) {
    window.location.href = '/forbidden.html'; return false
  }
  return true
}

export function getDashboardPath(role) {
  if (role === 'WORKER') return '/worker-dashboard.html'
  if (role === 'ADMIN') return '/admin.html'
  if (role === 'FACTORY_MANAGER') return '/qr-scanner.html'
  return '/employer-dashboard.html'
}

// Fetch wrapper — attaches JWT, handles 401/403
export async function api(path, options = {}) {
  const token = getToken()
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) }
  if (token) headers['Authorization'] = `Bearer ${token}`

  const res = await fetch('/api' + path, { ...options, headers })

  if (res.status === 401) {
    clearAuth()
    if (!window.location.pathname.endsWith('login.html')) window.location.href = '/login.html'
    throw new Error('Unauthorized')
  }
  if (res.status === 403) {
    window.location.href = '/forbidden.html'
    throw new Error('Forbidden')
  }

  const contentType = res.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    const data = await res.json()
    if (!res.ok) throw { response: { data, status: res.status } }
    return data
  }
  // blob response (file downloads)
  if (!res.ok) throw new Error('Request failed')
  return res
}

// Toast notifications
export function toast(message, type = 'info') {
  let container = document.getElementById('toast-container')
  if (!container) {
    container = document.createElement('div')
    container.id = 'toast-container'
    document.body.appendChild(container)
  }
  const el = document.createElement('div')
  el.className = `toast toast-${type}`
  el.textContent = message
  container.appendChild(el)
  setTimeout(() => el.remove(), 3500)
}
export const toastSuccess = (m) => toast(m, 'success')
export const toastError = (m) => toast(m, 'error')
export const toastInfo = (m) => toast(m, 'info')

// Render shared navbar into #navbar element
export function renderNavbar() {
  const user = getUser()
  const role = getRole()
  const auth = isAuthenticated()

  const dashPath = auth ? getDashboardPath(role) : '#'
  const rightHtml = auth
    ? `<div class="relative" id="user-menu-wrap">
        <button onclick="toggleUserMenu()" class="flex items-center gap-2 bg-gray-50 rounded-lg px-3 py-2 hover:bg-gray-100 transition-colors cursor-pointer border-0">
          <div class="w-8 h-8 bg-indigo-100 rounded-full flex items-center justify-center">
            <span class="text-indigo-600 font-semibold text-sm">${user?.name?.charAt(0)?.toUpperCase() || 'U'}</span>
          </div>
          <span class="text-gray-700 font-medium text-sm">${user?.name || ''}</span>
          <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
        </button>
        <div id="user-menu" class="hidden absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-100 py-1 z-50">
          <div class="px-4 py-2 border-b border-gray-50">
            <p class="text-xs text-gray-500">Signed in as</p>
            <p class="text-sm font-medium text-gray-800">${user?.name || ''}</p>
            <span class="badge bg-indigo-100 text-indigo-600 mt-1">${role || ''}</span>
          </div>
          <a href="${dashPath}" class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">Dashboard</a>
          <button onclick="handleLogout()" class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 border-0 bg-transparent cursor-pointer">Logout</button>
        </div>
      </div>`
    : `<a href="/login.html" class="text-gray-600 hover:text-indigo-600 font-medium">Login</a>
       <a href="/register.html" class="btn-primary text-sm ml-2">Register</a>`

  const el = document.getElementById('navbar')
  if (!el) return
  el.innerHTML = `
    <nav class="bg-white shadow-sm border-b border-gray-100 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <a href="/index.html" class="flex items-center gap-2">
            <div class="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-sm">FW</span>
            </div>
            <span class="text-xl font-bold text-gray-900">FlexiWork</span>
          </a>
          <div class="hidden md:flex items-center gap-8">
            <a href="/index.html" class="text-gray-600 hover:text-indigo-600 font-medium">Home</a>
            <a href="/about.html" class="text-gray-600 hover:text-indigo-600 font-medium">About</a>
            <a href="/services.html" class="text-gray-600 hover:text-indigo-600 font-medium">Services</a>
            <a href="/contact.html" class="text-gray-600 hover:text-indigo-600 font-medium">Contact</a>
          </div>
          <div class="hidden md:flex items-center">${rightHtml}</div>
          <button class="md:hidden text-gray-600" onclick="toggleMobileMenu()">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/></svg>
          </button>
        </div>
      </div>
      <div id="mobile-menu" class="hidden md:hidden border-t border-gray-100 bg-white px-4 py-3 space-y-2">
        <a href="/index.html" class="block py-2 text-gray-700 hover:text-indigo-600">Home</a>
        <a href="/about.html" class="block py-2 text-gray-700 hover:text-indigo-600">About</a>
        <a href="/services.html" class="block py-2 text-gray-700 hover:text-indigo-600">Services</a>
        <a href="/contact.html" class="block py-2 text-gray-700 hover:text-indigo-600">Contact</a>
        ${auth
          ? `<a href="${dashPath}" class="block py-2 text-indigo-600 font-medium">Dashboard</a>
             <button onclick="handleLogout()" class="block py-2 text-red-600 font-medium border-0 bg-transparent cursor-pointer">Logout</button>`
          : `<a href="/login.html" class="block py-2 text-gray-700">Login</a>
             <a href="/register.html" class="block py-2 text-indigo-600 font-medium">Register</a>`}
      </div>
    </nav>`

  window.toggleUserMenu = () => document.getElementById('user-menu')?.classList.toggle('hidden')
  window.toggleMobileMenu = () => document.getElementById('mobile-menu')?.classList.toggle('hidden')
  window.handleLogout = async () => {
    try { await api('/auth/logout', { method: 'POST' }) } catch {}
    clearAuth()
    window.location.href = '/index.html'
  }
  document.addEventListener('click', (e) => {
    const wrap = document.getElementById('user-menu-wrap')
    if (wrap && !wrap.contains(e.target)) document.getElementById('user-menu')?.classList.add('hidden')
  })
}
