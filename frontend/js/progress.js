requireAuth();
loadTheme();

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("name");
  window.location.href = "index.html";
}

async function loadProgressTasks() {
  const message = document.getElementById("progressMessage");
  const container = document.getElementById("progressTasks");
  try {
    const plan = await api("/plan/latest");
    container.innerHTML = "";
    if (!plan.tasks || plan.tasks.length === 0) {
      container.innerHTML = "<p class='muted'>No tasks in your latest plan.</p>";
      message.className = "muted";
      message.textContent = "";
      return;
    }

    plan.tasks.forEach((task) => {
      const block = document.createElement("div");
      block.className = `task ${task.status === "COMPLETED" ? "completed" : "pending"}`;
      block.innerHTML = `
        <strong>Day ${task.dayNumber}</strong>
        <p>${task.description}</p>
        <p class="muted">${task.revisionNote}</p>
        <button class="btn ${task.status === "COMPLETED" ? "" : "primary"}"
          onclick="toggleTask(${task.id}, '${task.status}')">
          Mark as ${task.status === "COMPLETED" ? "Incomplete" : "Complete"}
        </button>
      `;
      container.appendChild(block);
    });
    message.textContent = "";
  } catch (err) {
    message.className = "error";
    message.textContent = err.message;
  }
}

async function toggleTask(taskId, status) {
  const message = document.getElementById("progressMessage");
  try {
    await api("/progress", {
      method: "PATCH",
      body: JSON.stringify({
        taskId,
        completed: status !== "COMPLETED"
      })
    });
    message.className = "success";
    message.textContent = "Task status updated.";
    await loadProgressTasks();
  } catch (err) {
    message.className = "error";
    message.textContent = err.message;
  }
}

async function loadReminder() {
  try {
    const reminder = await api("/reminder");
    document.getElementById("frequency").value = reminder.frequencyPerWeek;
  } catch (err) {
    document.getElementById("progressMessage").textContent = err.message;
  }
}

async function saveReminder() {
  const message = document.getElementById("progressMessage");
  try {
    await api("/reminder", {
      method: "PUT",
      body: JSON.stringify({
        frequencyPerWeek: Number(document.getElementById("frequency").value)
      })
    });
    message.className = "success";
    message.textContent = "Reminder frequency updated.";
  } catch (err) {
    message.className = "error";
    message.textContent = err.message;
  }
}

async function autoAdjustReminder() {
  const message = document.getElementById("progressMessage");
  try {
    const reminder = await api("/reminder/auto-adjust", { method: "POST" });
    document.getElementById("frequency").value = reminder.frequencyPerWeek;
    message.className = "success";
    message.textContent = `Auto-adjusted reminder to ${reminder.frequencyPerWeek}/week.`;
  } catch (err) {
    message.className = "error";
    message.textContent = err.message;
  }
}

loadProgressTasks();
loadReminder();
