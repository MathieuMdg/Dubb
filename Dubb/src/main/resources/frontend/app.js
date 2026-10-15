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

document.querySelectorAll(".nav-link").forEach(link => {
    link.addEventListener("click", (e) => {
        e.preventDefault();
        const target = link.dataset.page;
        stopEditorPlayback();
        if (target === "page-history") loadHistory();
        if (target === "page-editor") openEditor();
        showPage(target);
    });
});

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
    video.src = `${API_BASE}/clips/${clip.clipId}/clean-video`;

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

        // Always use the server's current segments, never the copy cached in the clip list
        const clipResponse = await fetch(`${API_BASE}/clips/${currentClip.clipId}`);
        if (!clipResponse.ok) throw new Error("Impossible de charger le clip");
        currentClip = await clipResponse.json();

        dubbableSegments = currentClip.segments
            .filter(s => s.dubbable)
            .sort((a, b) => a.orderIndex - b.orderIndex);

        if (dubbableSegments.length === 0) {
            throw new Error("Ce clip n'a aucun segment à doubler");
        }

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

function seekTo(video, time) {
    return new Promise(resolve => {
        if (Math.abs(video.currentTime - time) < 0.05 && video.readyState >= 3) {
            return resolve();
        }
        video.addEventListener("seeked", () => resolve(), { once: true });
        video.currentTime = time;
    });
}

async function playSegmentRange(withRecording) {
    const segment = dubbableSegments[currentSegmentIndex];
    const video = document.getElementById("segment-video");

    document.getElementById("video-overlay").classList.remove("visible");

    await seekTo(video, segment.startTime);
    await video.play(); // resolves only once playback has really started

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
        const stream = await navigator.mediaDevices.getUserMedia({
            audio: {
                echoCancellation: false,
                noiseSuppression: false,
                autoGainControl: false
            }
        });
        mediaRecorder = new MediaRecorder(stream);
        recordedChunks = [];
        mediaRecorder.ondataavailable = e => recordedChunks.push(e.data);
        mediaRecorder.onstop = async () => {
            stream.getTracks().forEach(t => t.stop());
            const blob = new Blob(recordedChunks, { type: "audio/webm" });
            await uploadRecording(blob);
        };

        await playSegmentRange(true);
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
    const progressResponse = await fetch(`${API_BASE}/attempts/${currentAttempt.attemptId}/progress`);
    const progress = await progressResponse.json();
    if (!progress.complete) {
        alert(`Il reste ${progress.missingSegmentIds.length} segment(s) à enregistrer. Recharge la page et recommence la tentative.`);
        return;
    }

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

// ===================================================================
// SEGMENT EDITOR
// ===================================================================

const MIN_GAP = 0.2; // minimum segment length in seconds

const editor = {
    clipId: null,
    duration: 0,
    segments: [],
    editingId: null,
    rafId: null
};

const edVideo = document.getElementById("editor-video");
const edStart = document.getElementById("range-start");
const edEnd = document.getElementById("range-end");

function fmt(t) { return Number(t).toFixed(1) + "s"; }

async function apiError(response) {
    try { return (await response.json()).message; }
    catch (e) { return "Erreur " + response.status; }
}

async function openEditor() {
    const select = document.getElementById("editor-clip-select");
    const response = await fetch(`${API_BASE}/clips`);
    const clips = await response.json();

    select.innerHTML = "";
    clips.forEach(c => {
        const opt = document.createElement("option");
        opt.value = c.clipId;
        opt.textContent = c.clipTitle;
        select.appendChild(opt);
    });

    if (clips.length > 0) await selectEditorClip(clips[0].clipId);
}

document.getElementById("editor-clip-select").addEventListener("change", (e) => {
    selectEditorClip(Number(e.target.value));
});

async function selectEditorClip(clipId) {
    stopEditorPlayback();
    editor.clipId = clipId;
    cancelEdit();

    // Guide video: it contains the original voices, which is what we want to hear here
    edVideo.src = `${API_BASE}/clips/${clipId}/video`;
    await new Promise(resolve => edVideo.addEventListener("loadedmetadata", resolve, { once: true }));

    editor.duration = edVideo.duration;
    edStart.max = edEnd.max = editor.duration;
    edStart.value = 0;
    edEnd.value = Math.min(editor.duration, 5);

    await refreshSegments();
    updateRangeUI();
}

// ----- Range slider -----

function updateRangeUI() {
    const start = Number(edStart.value);
    const end = Number(edEnd.value);
    const total = editor.duration || 1;

    const fill = document.getElementById("range-fill");
    fill.style.left = (start / total * 100) + "%";
    fill.style.width = ((end - start) / total * 100) + "%";

    document.getElementById("readout-start").textContent = fmt(start);
    document.getElementById("readout-end").textContent = fmt(end);
    document.getElementById("readout-duration").textContent = fmt(end - start);

    // keep the start thumb reachable when both thumbs are near the right edge
    edStart.style.zIndex = start > total * 0.9 ? 3 : 1;
}

edStart.addEventListener("input", () => {
    stopEditorPlayback();
    if (Number(edStart.value) > Number(edEnd.value) - MIN_GAP) {
        edStart.value = Number(edEnd.value) - MIN_GAP;
    }
    edVideo.currentTime = Number(edStart.value);   // scrub: show the frame under the handle
    updateRangeUI();
});

edEnd.addEventListener("input", () => {
    stopEditorPlayback();
    if (Number(edEnd.value) < Number(edStart.value) + MIN_GAP) {
        edEnd.value = Number(edStart.value) + MIN_GAP;
    }
    edVideo.currentTime = Number(edEnd.value);
    updateRangeUI();
});

// Releasing a handle plays the selected interval
edStart.addEventListener("change", playInterval);
edEnd.addEventListener("change", playInterval);
document.getElementById("editor-play-btn").addEventListener("click", playInterval);

// ----- Preview playback -----

function stopEditorPlayback() {
    if (editor.rafId) cancelAnimationFrame(editor.rafId);
    editor.rafId = null;
    edVideo.pause();
}

async function playInterval() {
    stopEditorPlayback();
    const start = Number(edStart.value);
    const end = Number(edEnd.value);

    edVideo.currentTime = start;
    try { await edVideo.play(); } catch (e) { return; }

    const tick = () => {
        if (edVideo.currentTime >= end) {
            if (document.getElementById("editor-loop").checked) {
                edVideo.currentTime = start;
            } else {
                edVideo.pause();
                editor.rafId = null;
                return;
            }
        }
        editor.rafId = requestAnimationFrame(tick);
    };
    editor.rafId = requestAnimationFrame(tick);
}

// ----- Existing segments -----

async function refreshSegments() {
    const response = await fetch(`${API_BASE}/clips/${editor.clipId}/segments`);
    editor.segments = (await response.json()).sort((a, b) => a.startTime - b.startTime);
    renderBands();
    renderSegmentList();
}

function renderBands() {
    const bands = document.getElementById("range-bands");
    bands.innerHTML = "";
    const total = editor.duration || 1;

    editor.segments.forEach(s => {
        const band = document.createElement("div");
        band.className = "range-band " + (s.dubbable ? "dub" : "keep")
            + (s.segmentId === editor.editingId ? " editing" : "");
        band.style.left = (s.startTime / total * 100) + "%";
        band.style.width = ((s.endTime - s.startTime) / total * 100) + "%";
        bands.appendChild(band);
    });
}

function renderSegmentList() {
    const list = document.getElementById("segment-list");
    const empty = document.getElementById("segment-empty");
    list.innerHTML = "";
    empty.classList.toggle("hidden", editor.segments.length > 0);

    editor.segments.forEach(s => {
        const item = document.createElement("div");
        item.className = "segment-item" + (s.dubbable ? " dub" : "");
        item.innerHTML = `
            <div class="segment-text">
                <strong>${s.label || "Segment " + s.orderIndex}</strong>
                <small>${fmt(s.startTime)} → ${fmt(s.endTime)} · ${s.dubbable ? "à doubler" : "gardé tel quel"}</small>
            </div>
            <button class="bouton bouton-clair" data-a="listen">▶</button>
            <button class="bouton bouton-clair" data-a="edit">Modifier</button>
            <button class="bouton bouton-clair" data-a="delete">Supprimer</button>
        `;
        item.querySelector('[data-a="listen"]').onclick = () => {
            edStart.value = s.startTime; edEnd.value = s.endTime;
            updateRangeUI(); playInterval();
        };
        item.querySelector('[data-a="edit"]').onclick = () => startEdit(s);
        item.querySelector('[data-a="delete"]').onclick = () => removeSegment(s);
        list.appendChild(item);
    });
}

// ----- Create / update / delete -----

function startEdit(s) {
    editor.editingId = s.segmentId;
    edStart.value = s.startTime;
    edEnd.value = s.endTime;
    document.getElementById("segment-label").value = s.label || "";
    document.getElementById("segment-dubbable").checked = s.dubbable;
    document.getElementById("segment-save-btn").textContent = "Enregistrer la modification";
    document.getElementById("segment-cancel-btn").classList.remove("hidden");
    updateRangeUI();
    renderBands();
    playInterval();
}

function cancelEdit() {
    editor.editingId = null;
    document.getElementById("segment-label").value = "";
    document.getElementById("segment-dubbable").checked = true;
    document.getElementById("segment-save-btn").textContent = "Ajouter le segment";
    document.getElementById("segment-cancel-btn").classList.add("hidden");
    document.getElementById("segment-error").classList.add("hidden");
    renderBands();
}

document.getElementById("segment-cancel-btn").addEventListener("click", cancelEdit);

document.getElementById("segment-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const errorEl = document.getElementById("segment-error");
    errorEl.classList.add("hidden");
    stopEditorPlayback();

    const start = Number(edStart.value);
    const end = Number(edEnd.value);
    const body = {
        startTime: start,
        endTime: end,
        // temporary order: the real order is recomputed by normalizeOrder() below
        orderIndex: editor.segments.length + 1,
        dubbable: document.getElementById("segment-dubbable").checked,
        label: document.getElementById("segment-label").value.trim()
    };

    const isEdit = editor.editingId !== null;
    const url = `${API_BASE}/clips/${editor.clipId}/segments` + (isEdit ? `/${editor.editingId}` : "");

    try {
        const response = await fetch(url, {
            method: isEdit ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });
        if (!response.ok) throw new Error(await apiError(response));

        cancelEdit();
        await refreshSegments();
        await normalizeOrder();
    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.classList.remove("hidden");
    }
});

async function removeSegment(s) {
    if (!confirm(`Supprimer « ${s.label || "ce segment"} » ?\nLes enregistrements déjà faits sur ce segment seront aussi supprimés.`)) return;
    stopEditorPlayback();

    const response = await fetch(`${API_BASE}/clips/${editor.clipId}/segments/${s.segmentId}`, { method: "DELETE" });
    if (!response.ok) { alert(await apiError(response)); return; }

    if (editor.editingId === s.segmentId) cancelEdit();
    await refreshSegments();
    await normalizeOrder();
}

// The final-audio rendering walks segments by orderIndex and assumes chronological order,
// so orderIndex must always follow the start time (1, 2, 3...).
async function normalizeOrder() {
    let changed = false;
    for (let i = 0; i < editor.segments.length; i++) {
        const s = editor.segments[i];
        if (s.orderIndex === i + 1) continue;
        await fetch(`${API_BASE}/clips/${editor.clipId}/segments/${s.segmentId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                startTime: s.startTime, endTime: s.endTime,
                orderIndex: i + 1, dubbable: s.dubbable, label: s.label
            })
        });
        changed = true;
    }
    if (changed) await refreshSegments();
}