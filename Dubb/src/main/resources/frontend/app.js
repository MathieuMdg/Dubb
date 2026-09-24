const API_BASE = "http://localhost:8080/api";

// ----- State -----
let currentUser = JSON.parse(localStorage.getItem("dubb_user") || "null");
let currentClip = null;
let currentAttempt = null;
let dubbableSegments = [];
let currentSegmentIndex = 0;

let mediaRecorder = null;
let recordedChunks = [];

// ----- Navigation -----
function showPage(pageId) {
    document.querySelectorAll(".page-view").forEach(p => p.classList.remove("active"));
    document.getElementById(pageId).classList.add("active");
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function stopAllVideos() {
    document.querySelectorAll("video").forEach(v => {
        v.pause();
        v.removeAttribute("src");
        v.load();
    });
}

function setLoggedInUI() {
    document.querySelectorAll(".nav-link, #logout-btn").forEach(el => el.classList.remove("hidden"));
}

function setLoggedOutUI() {
    document.querySelectorAll(".nav-link, #logout-btn").forEach(el => el.classList.add("hidden"));
}

// ----- Init -----
if (currentUser) {
    setLoggedInUI();
    loadClips().then(() => showPage("page-clips"));
} else {
    setLoggedOutUI();
    showPage("page-auth");
}

// ===================================================================
// AUTH
// ===================================================================

document.getElementById("tab-login").addEventListener("click", () => {
    document.getElementById("tab-login").classList.add("actif");
    document.getElementById("tab-register").classList.remove("actif");
    document.getElementById("login-form").classList.remove("hidden");
    document.getElementById("register-form").classList.add("hidden");
});

document.getElementById("tab-register").addEventListener("click", () => {
    document.getElementById("tab-register").classList.add("actif");
    document.getElementById("tab-login").classList.remove("actif");
    document.getElementById("register-form").classList.remove("hidden");
    document.getElementById("login-form").classList.add("hidden");
});

document.getElementById("login-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const username = document.getElementById("login-username").value.trim();
    const userMail = document.getElementById("login-mail").value.trim();
    const errorEl = document.getElementById("login-error");
    errorEl.classList.add("hidden");

    try {
        const response = await fetch(`${API_BASE}/users/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, userMail })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message);
        }

        currentUser = await response.json();
        localStorage.setItem("dubb_user", JSON.stringify(currentUser));
        setLoggedInUI();
        await loadClips();
        showPage("page-clips");

    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.classList.remove("hidden");
    }
});

document.getElementById("register-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const username = document.getElementById("register-username").value.trim();
    const userMail = document.getElementById("register-mail").value.trim();
    const errorEl = document.getElementById("register-error");
    errorEl.classList.add("hidden");

    try {
        const response = await fetch(`${API_BASE}/users`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, userMail })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message);
        }

        currentUser = await response.json();
        localStorage.setItem("dubb_user", JSON.stringify(currentUser));
        setLoggedInUI();
        await loadClips();
        showPage("page-clips");

    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.classList.remove("hidden");
    }
});

document.getElementById("logout-btn").addEventListener("click", () => {
    currentUser = null;
    localStorage.removeItem("dubb_user");
    stopAllVideos();
    setLoggedOutUI();
    showPage("page-auth");
});

document.getElementById("logo-link").addEventListener("click", (e) => {
    e.preventDefault();
    if (currentUser) showPage("page-clips");
});

document.querySelectorAll(".nav-link").forEach(link => {
    link.addEventListener("click", (e) => {
        e.preventDefault();
        const target = link.dataset.page;
        if (target === "page-history") loadHistory();
        showPage(target);
    });
});

// ===================================================================
// CLIP LIST
// ===================================================================

async function loadClips() {
    const response = await fetch(`${API_BASE}/clips`);
    const clips = await response.json();

    const grid = document.getElementById("clip-grid");
    grid.innerHTML = "";

    clips.forEach(clip => {
        const card = document.createElement("div");
        card.className = "carte";
        card.innerHTML = `
            <div class="carte-visuel">🎬</div>
            <div class="carte-corps">
                <h3>${clip.clipTitle}</h3>
                <p>${Math.round(clip.clipDuration)}s · ${clip.segments.filter(s => s.dubbable).length} segment(s)</p>
            </div>
        `;
        card.addEventListener("click", () => selectClip(clip));
        grid.appendChild(card);
    });
}

function selectClip(clip) {
    currentClip = clip;
    document.getElementById("preview-title").textContent = clip.clipTitle;

    const video = document.getElementById("original-video");
    video.src = `${API_BASE}/clips/${clip.clipId}/video`;

    showPage("page-preview");
}

document.getElementById("preview-back-btn").addEventListener("click", (e) => {
    e.preventDefault();
    stopAllVideos();
    showPage("page-clips");
});

// ===================================================================
// START ATTEMPT
// ===================================================================

document.getElementById("start-attempt-btn").addEventListener("click", async () => {
    const btn = document.getElementById("start-attempt-btn");
    btn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/attempts`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId: currentUser.userId, clipId: currentClip.clipId })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message);
        }

        currentAttempt = await response.json();
        dubbableSegments = currentClip.segments
            .filter(s => s.dubbable)
            .sort((a, b) => a.orderIndex - b.orderIndex);
        currentSegmentIndex = 0;

        buildProgressTrack();
        loadSegmentPage();
        showPage("page-record");

    } catch (err) {
        alert("Erreur : " + err.message);
    } finally {
        btn.disabled = false;
    }
});

// ===================================================================
// RECORDING
// ===================================================================

function buildProgressTrack() {
    const track = document.getElementById("progress-track");
    track.innerHTML = "";
    dubbableSegments.forEach(() => {
        const dot = document.createElement("div");
        dot.className = "progress-dot";
        track.appendChild(dot);
    });
}

function updateProgressTrack() {
    document.querySelectorAll(".progress-dot").forEach((dot, i) => {
        dot.classList.remove("done", "current");
        if (i < currentSegmentIndex) dot.classList.add("done");
        else if (i === currentSegmentIndex) dot.classList.add("current");
    });
}

function loadSegmentPage() {
    const segment = dubbableSegments[currentSegmentIndex];

    document.getElementById("segment-label").textContent = segment.label || `Segment ${currentSegmentIndex + 1}`;
    document.getElementById("segment-timerange").textContent =
        `${segment.startTime}s – ${segment.endTime}s · ${currentSegmentIndex + 1} sur ${dubbableSegments.length}`;

    updateProgressTrack();

    const video = document.getElementById("segment-video");
    video.src = `${API_BASE}/clips/${currentClip.clipId}/video`;

    document.getElementById("video-overlay").classList.add("visible");
    document.getElementById("rec-indicator").classList.add("hidden");
    document.getElementById("record-btn").disabled = false;
    document.getElementById("record-btn-label").textContent = "Enregistrer";
    document.getElementById("post-record-actions").classList.add("hidden");
    document.getElementById("recording-status").textContent = "";

    const isLast = currentSegmentIndex === dubbableSegments.length - 1;
    document.getElementById("next-segment-btn").classList.toggle("hidden", isLast);
    document.getElementById("finalize-btn").classList.toggle("hidden", !isLast);
}

document.getElementById("preview-segment-btn").addEventListener("click", () => {
    playSegmentRange(false);
});

function playSegmentRange(withRecording) {
    const segment = dubbableSegments[currentSegmentIndex];
    const video = document.getElementById("segment-video");

    document.getElementById("video-overlay").classList.remove("visible");
    video.currentTime = segment.startTime;
    video.play();

    const stopAtEnd = () => {
        if (video.currentTime >= segment.endTime) {
            video.pause();
            video.removeEventListener("timeupdate", stopAtEnd);
            if (!withRecording) document.getElementById("video-overlay").classList.add("visible");
        }
    };
    video.addEventListener("timeupdate", stopAtEnd);
}

document.getElementById("record-btn").addEventListener("click", async () => {
    const segment = dubbableSegments[currentSegmentIndex];
    const duration = segment.endTime - segment.startTime;

    try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        mediaRecorder = new MediaRecorder(stream);
        recordedChunks = [];

        mediaRecorder.ondataavailable = e => recordedChunks.push(e.data);
        mediaRecorder.onstop = async () => {
            stream.getTracks().forEach(t => t.stop());
            const blob = new Blob(recordedChunks, { type: "audio/webm" });
            await uploadRecording(blob);
        };

        playSegmentRange(true);
        mediaRecorder.start();

        document.getElementById("record-btn").disabled = true;
        document.getElementById("rec-indicator").classList.remove("hidden");
        document.getElementById("recording-status").textContent = "Enregistrement de ta voix…";
        document.getElementById("post-record-actions").classList.add("hidden");

        setTimeout(() => {
            if (mediaRecorder.state === "recording") mediaRecorder.stop();
        }, duration * 1000);

    } catch (err) {
        alert("Erreur d'accès au micro : " + err.message);
    }
});

async function uploadRecording(blob) {
    const segment = dubbableSegments[currentSegmentIndex];
    document.getElementById("rec-indicator").classList.add("hidden");
    document.getElementById("recording-status").textContent = "Envoi en cours…";

    const formData = new FormData();
    formData.append("audio", blob, "recording.webm");

    try {
        const response = await fetch(
            `${API_BASE}/attempts/${currentAttempt.attemptId}/segments/${segment.segmentId}`,
            { method: "POST", body: formData }
        );

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message);
        }

        document.getElementById("recording-status").textContent = "✓ Segment enregistré !";
        document.getElementById("record-btn").disabled = false;
        document.getElementById("post-record-actions").classList.remove("hidden");
        document.getElementById("video-overlay").classList.add("visible");

    } catch (err) {
        document.getElementById("recording-status").textContent = "Erreur : " + err.message;
        document.getElementById("record-btn").disabled = false;
    }
}

document.getElementById("rerecord-btn").addEventListener("click", () => {
    document.getElementById("post-record-actions").classList.add("hidden");
    document.getElementById("recording-status").textContent = "";
    document.getElementById("video-overlay").classList.add("visible");
});

document.getElementById("next-segment-btn").addEventListener("click", () => {
    currentSegmentIndex++;
    loadSegmentPage();
});

document.getElementById("finalize-btn").addEventListener("click", async () => {
    showPage("page-processing");

    try {
        const response = await fetch(`${API_BASE}/attempts/${currentAttempt.attemptId}/finalize`, {
            method: "POST"
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message);
        }

        const finalAttempt = await response.json();
        showFinalResult(finalAttempt);
        showPage("page-result");

    } catch (err) {
        alert("Erreur lors de la finalisation : " + err.message);
        showPage("page-record");
    }
});

function showFinalResult(attempt) {
    document.getElementById("result-label").textContent = attempt.label || currentClip.clipTitle;

    const video = document.getElementById("final-video");
    video.src = `${API_BASE}/attempts/${attempt.attemptId}/final-video`;

    const downloadBtn = document.getElementById("download-btn");
    downloadBtn.onclick = () => downloadVideo(attempt.attemptId, (attempt.label || "dub") + ".mp4");
}

// Vrai téléchargement (pas d'ouverture plein écran) : on récupère le fichier
// en mémoire puis on force le téléchargement via un lien temporaire.
async function downloadVideo(attemptId, filename) {
    try {
        const response = await fetch(`${API_BASE}/attempts/${attemptId}/final-video`);
        if (!response.ok) throw new Error("Impossible de récupérer la vidéo");

        const blob = await response.blob();
        const url = URL.createObjectURL(blob);

        const a = document.createElement("a");
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);

    } catch (err) {
        alert("Erreur de téléchargement : " + err.message);
    }
}

// ===================================================================
// RESTART
// ===================================================================

document.getElementById("restart-btn").addEventListener("click", async () => {
    stopAllVideos();
    currentClip = null;
    currentAttempt = null;
    dubbableSegments = [];
    currentSegmentIndex = 0;

    await loadClips();
    showPage("page-clips");
});

// ===================================================================
// HISTORY
// ===================================================================

async function loadHistory() {
    const response = await fetch(`${API_BASE}/attempts?userId=${currentUser.userId}`);
    const attempts = await response.json();

    const completed = attempts.filter(a => a.status === "COMPLETED");
    const list = document.getElementById("history-list");
    const emptyMsg = document.getElementById("history-empty");

    list.innerHTML = "";

    if (completed.length === 0) {
        emptyMsg.classList.remove("hidden");
        return;
    }
    emptyMsg.classList.add("hidden");

    completed.forEach(attempt => {
        const item = document.createElement("div");
        item.className = "historique-item";
        item.innerHTML = `
            <span class="historique-emoji">🎬</span>
            <div class="historique-texte">
                <strong>${attempt.label || attempt.clipTitle}</strong>
                <small>${new Date(attempt.completedAt).toLocaleString("fr-FR")}</small>
            </div>
            <div class="historique-actions">
                <button class="bouton bouton-clair" data-action="play">▶ Écouter</button>
                <button class="bouton" data-action="download">⬇ Télécharger</button>
            </div>
        `;

        item.querySelector('[data-action="play"]').addEventListener("click", () => {
            showFinalResult(attempt);
            showPage("page-result");
        });

        item.querySelector('[data-action="download"]').addEventListener("click", () => {
            downloadVideo(attempt.attemptId, (attempt.label || "dub") + ".mp4");
        });

        list.appendChild(item);
    });
}