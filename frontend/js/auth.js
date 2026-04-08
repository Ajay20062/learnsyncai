loadTheme();
if (getToken()) {
  window.location.href = "dashboard.html";
}
checkBackend();

async function checkBackend() {
  const statusEl = document.getElementById("backendStatus");
  if (!statusEl) return;

  try {
    await fetch(`${API_BASE}/auth/login`, {
      method: "OPTIONS"
    });
    statusEl.className = "success";
    statusEl.textContent = `Backend reachable at ${API_BASE}`;
  } catch {
    statusEl.className = "warning";
    statusEl.textContent = "Backend is not reachable. Start backend on port 8080, then retry.";
  }
}

function switchTab(tab) {
  document.getElementById("loginForm").style.display = tab === "login" ? "block" : "none";
  document.getElementById("signupForm").style.display = tab === "signup" ? "block" : "none";
  document.getElementById("authMessage").textContent = "";
}

document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const message = document.getElementById("authMessage");
  message.className = "error";
  try {
    const data = await api("/auth/login", {
      method: "POST",
      body: JSON.stringify({
        email: document.getElementById("loginEmail").value,
        password: document.getElementById("loginPassword").value
      })
    });
    localStorage.setItem("token", data.token);
    localStorage.setItem("name", data.name);
    window.location.href = "dashboard.html";
  } catch (err) {
    message.textContent = err.message;
  }
});

document.getElementById("signupForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const message = document.getElementById("authMessage");
  message.className = "error";
  try {
    const data = await api("/auth/signup", {
      method: "POST",
      body: JSON.stringify({
        name: document.getElementById("signupName").value,
        email: document.getElementById("signupEmail").value,
        password: document.getElementById("signupPassword").value
      })
    });
    localStorage.setItem("token", data.token);
    localStorage.setItem("name", data.name);
    window.location.href = "dashboard.html";
  } catch (err) {
    message.textContent = err.message;
  }
});
