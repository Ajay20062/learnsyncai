requireAuth();
loadTheme();

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("name");
  window.location.href = "index.html";
}

function renderPlan(plan) {
  const container = document.getElementById("planTasks");
  container.innerHTML = "";
  if (!plan.tasks || plan.tasks.length === 0) {
    container.innerHTML = "<p class='muted'>No tasks in this plan.</p>";
    return;
  }

  plan.tasks.forEach((task) => {
    const div = document.createElement("div");
    div.className = `task ${task.status === "COMPLETED" ? "completed" : "pending"}`;
    div.innerHTML = `<strong>Day ${task.dayNumber}</strong><p>${task.description}</p><p class="muted">${task.revisionNote}</p>`;
    container.appendChild(div);
  });
}

async function generatePlan() {
  const message = document.getElementById("planMessage");
  try {
    message.textContent = "Generating AI plan...";
    const plan = await api("/plan/generate", {
      method: "POST",
      body: JSON.stringify({
        goal: document.getElementById("goal").value,
        durationDays: Number(document.getElementById("duration").value),
        dailyHours: Number(document.getElementById("hours").value),
        skillLevel: document.getElementById("skill").value
      })
    });
    renderPlan(plan);
    message.className = "success";
    message.textContent = "Plan generated successfully.";
  } catch (err) {
    message.className = "error";
    message.textContent = err.message;
  }
}

async function loadLatestPlan() {
  const message = document.getElementById("planMessage");
  try {
    const plan = await api("/plan/latest");
    renderPlan(plan);
    message.className = "success";
    message.textContent = "Loaded latest plan.";
  } catch (err) {
    message.className = "error";
    message.textContent = err.message;
  }
}

async function loadAdaptive() {
  const message = document.getElementById("planMessage");
  try {
    const data = await api("/plan/adapt/weekly");
    message.className = "muted";
    message.textContent = `Completion: ${data.completionPercent.toFixed(1)}%. AI: ${data.recommendation}`;
  } catch (err) {
    message.className = "error";
    message.textContent = err.message;
  }
}

loadLatestPlan();
