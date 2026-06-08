// Shared API utilities

function getCsrfToken() {
  const match = document.cookie.match(/(^|;)\s*XSRF-TOKEN\s*=\s*([^;]+)/);
  return match ? decodeURIComponent(match[2]) : '';
}

async function apiFetch(url, options = {}) {
  const method = (options.method || 'GET').toUpperCase();
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = localStorage.getItem('fw_token');
  if (token) headers['Authorization'] = 'Bearer ' + token;

  let res;
  try {
    res = await fetch(url, { ...options, headers, credentials: 'include' });
  } catch {
    showToast('Network error — check your connection.', 'error');
    throw new Error('Network error');
  }

  if (res.status === 401) {
    const hadToken = !!token; // only redirect if we had a session that expired
    localStorage.removeItem('fw_user');
    localStorage.removeItem('fw_token');
    localStorage.removeItem('fw_role');
    if (hadToken && !window.location.pathname.endsWith('login.html')) {
      showToast('Session expired. Redirecting to login…', 'error');
      setTimeout(() => { window.location.href = '/login.html'; }, 1500);
    }
    return res;
  }

  if (res.status === 403) {
    showToast('Access denied. You don\'t have permission for this.', 'error');
    return res;
  }

  return res;
}

// Extract a readable error message from an API response (handles 400, 404, 500, etc.)
async function apiErrorMessage(res, fallback = 'Something went wrong. Please try again.') {
  try {
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) {
      const body = await res.json();
      if (res.status === 404) return body.message || 'Not found.';
      if (res.status === 400) return body.message || body.error || 'Invalid request. Please check your input.';
      return body.message || body.error || fallback;
    }
  } catch {}
  if (res.status === 404) return 'The requested resource was not found.';
  if (res.status === 400) return 'Invalid request. Please check your input.';
  if (res.status === 500) return 'Server error. Please try again later.';
  return fallback;
}

async function getMe() {
  const res = await apiFetch('/api/auth/me');
  if (!res.ok) return null;
  const json = await res.json();
  return json.data;
}

function redirectIfNotLoggedIn() {
  getMe().then(user => { if (!user) window.location.href = '/login.html'; });
}

function requireRole(role) {
  getMe().then(user => {
    if (!user) { window.location.href = '/login.html'; return; }
    if (user.role !== role) { window.location.href = '/index.html'; }
  });
}

async function logout() {
  await apiFetch('/api/auth/logout', { method: 'POST' });
  localStorage.removeItem('fw_token');
  localStorage.removeItem('fw_user');
  localStorage.removeItem('fw_role');
  window.location.href = '/login.html';
}

function getDashLink(role) {
  if (role === 'WORKER') return '/worker-dashboard.html';
  if (role === 'ADMIN') return '/admin.html';
  if (role === 'SECURITY') return '/security-scan.html';
  return '/employer-dashboard.html';
}

function renderAuthArea(user, areaId = 'auth-area') {
  const area = document.getElementById(areaId);
  if (!area) return;
  if (user) {
    const dashLink = getDashLink(user.role);
    area.innerHTML = `
      <span class="text-sm font-semibold text-gray-700 hidden sm:block">${user.firstName} ${user.lastName}</span>
      <a href="${dashLink}" class="flex items-center gap-2 border border-gray-200 hover:border-primary hover:text-primary text-gray-700 pl-2 pr-4 py-1.5 rounded-full text-sm font-semibold transition">
        <span class="w-7 h-7 rounded-full bg-primary flex items-center justify-center shrink-0">
          <svg class="w-3.5 h-3.5" fill="none" stroke="white" stroke-width="2" viewBox="0 0 24 24">
            <rect x="3" y="3" width="7" height="7" rx="1" stroke="white" stroke-width="2"/>
            <rect x="14" y="3" width="7" height="7" rx="1" stroke="white" stroke-width="2"/>
            <rect x="3" y="14" width="7" height="7" rx="1" stroke="white" stroke-width="2"/>
            <rect x="14" y="14" width="7" height="7" rx="1" stroke="white" stroke-width="2"/>
          </svg>
        </span>
        Dashboard
      </a>
      <button onclick="logout()" class="flex items-center gap-1.5 border border-gray-200 hover:border-gray-400 text-gray-700 px-4 py-2 rounded-full text-sm font-medium transition">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
        </svg>
        Sign Out
      </button>`;
  } else {
    area.innerHTML = `
      <a href="/login.html" class="flex items-center gap-1.5 border border-gray-200 hover:border-gray-400 text-gray-700 px-4 py-2 rounded-full text-sm font-medium transition">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"/></svg>
        Log in
      </a>
      <a href="/register.html" class="flex items-center gap-1.5 bg-primary hover:bg-red-700 text-white px-5 py-2 rounded-full text-sm font-semibold transition">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"/></svg>
        Register
      </a>`;
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatTime(timeStr) {
  if (!timeStr) return '';
  const [h, m] = timeStr.split(':');
  const hour = parseInt(h);
  return `${hour % 12 || 12}:${m} ${hour < 12 ? 'AM' : 'PM'}`;
}

function showToast(msg, type = 'success') {
  const toast = document.createElement('div');
  toast.className = `fixed bottom-6 right-6 px-5 py-3 rounded-xl shadow-lg text-white text-sm font-medium z-50 transition-all
    ${type === 'success' ? 'bg-green-600' : 'bg-red-600'}`;
  toast.textContent = msg;
  document.body.appendChild(toast);
  setTimeout(() => toast.remove(), 3500);
}
