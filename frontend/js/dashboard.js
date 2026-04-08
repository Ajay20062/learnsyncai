requireAuth();
loadTheme();
document.getElementById("name").textContent = localStorage.getItem("name") || "Student";

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("name");
  window.location.href = "index.html";
}

async function loadDashboard() {
  const data = await api("/progress/dashboard");
  document.getElementById("completion").textContent = `${data.completionPercent}%`;
  document.getElementById("streak").textContent = data.streakDays;
  document.getElementById("today").textContent = `${data.todayCompletedCount}/${data.todayTaskCount}`;
  document.getElementById("missed").textContent = data.missedTasks;

  const taskList = document.getElementById("taskList");
  taskList.innerHTML = "";
  if (data.todayTasks.length === 0) {
    taskList.innerHTML = "<p class='muted'>No tasks today.</p>";
  } else {
    data.todayTasks.forEach((task) => {
      const div = document.createElement("div");
      div.className = `task ${task.status === "COMPLETED" ? "completed" : "pending"}`;
      div.innerHTML = `<strong>Day ${task.dayNumber}</strong><p>${task.description}</p><p class="muted">${task.revisionNote}</p>`;
      taskList.appendChild(div);
    });
  }

  const ctx = document.getElementById("weeklyChart");
  new Chart(ctx, {
    type: "line",
    data: {
      labels: data.weeklyChart.map((p) => p.day),
      datasets: [{
        label: "Completed Tasks",
        data: data.weeklyChart.map((p) => p.completedTasks),
        borderColor: "#1363df",
        backgroundColor: "rgba(19,99,223,0.15)",
        tension: 0.35,
        fill: true
      }]
    },
    options: { responsive: true, plugins: { legend: { display: false } } }
  });
}

loadDashboard().catch((err) => {
  alert(err.message);
  logout();
});
