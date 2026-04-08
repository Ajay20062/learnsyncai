const API_BASE = resolveApiBase();

function resolveApiBase() {
  const fromWindow = window.LEARNSYNCAI_API_BASE;
  const fromStorage = localStorage.getItem("learnsync.apiBase");

  if (fromWindow) {
    return normalizeApiBase(fromWindow);
  }
  if (fromStorage) {
    return normalizeApiBase(fromStorage);
  }

  if (window.location.protocol === "http:" || window.location.protocol === "https:") {
    return `${window.location.protocol}//${window.location.hostname}:8080/api`;
  }

  return "http://localhost:8080/api";
}

function normalizeApiBase(value) {
  const trimmed = String(value).trim().replace(/\/+$/, "");
  return trimmed.endsWith("/api") ? trimmed : `${trimmed}/api`;
}

function getToken() {
  return localStorage.getItem("token");
}

async function api(path, options = {}) {
  const headers = { ...(options.headers || {}) };
  const hasBody = options.body !== undefined && options.body !== null;
  if (hasBody && !(options.body instanceof FormData)) {
    headers["Content-Type"] = headers["Content-Type"] || "application/json";
  }

  const token = getToken();
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  } catch (error) {
    throw new Error(
      `Cannot connect to backend at ${API_BASE}. Start backend on port 8080 and open frontend via http://localhost:5500 (not file://).`
    );
  }
  if (!response.ok) {
    const errorMessage = await parseApiError(response);
    if (response.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("name");
    }
    throw new Error(errorMessage);
  }
  if (response.status === 204) return null;
  return response.json();
}

async function parseApiError(response) {
  const fallbackMessage = `Request failed (${response.status})`;
  const payload = await response.json().catch(() => null);
  if (!payload) {
    return fallbackMessage;
  }

  if (payload.error && typeof payload.error === "string") {
    if (payload.details && typeof payload.details === "object") {
      const detailLines = Object.entries(payload.details).map(([field, message]) => `${field}: ${message}`);
      return `${payload.error} - ${detailLines.join(", ")}`;
    }
    return payload.error;
  }

  return fallbackMessage;
}

function toggleDarkMode() {
  document.body.classList.toggle("dark");
  localStorage.setItem("theme", document.body.classList.contains("dark") ? "dark" : "light");
}

function loadTheme() {
  if (localStorage.getItem("theme") === "dark") document.body.classList.add("dark");
}

function requireAuth() {
  if (!getToken()) {
    localStorage.removeItem("name");
    window.location.href = "index.html";
  }
}
