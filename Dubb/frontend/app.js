const API_BASE = "http://localhost:8080/api";

let currentUser = null;
let currentClip = null;
let currentAttempt = null;
let dubbableSegments = [];
let currentSegmentIndex = 0;

let mediaRecorder = null;
let recordedChunks = [];

// ----- Step 1: create user -----

document.getElementById("create-user-btn").addEventListener("click", async () => {
    const username = document.getElementById("username-input").value;
    const userMail = document.getElementById("usermail-input").value;

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
        document.getElementById("user-status").textContent = `User created: ${currentUser.username} (id: ${currentUser.userId})`;

        loadClips();
        document.getElementById("clip-list-section").classList.remove("hidden");

    } catch (err) {
        document.getElementById("user-status").textContent = "Error: " + err.message;
    }
});

// ----- Step 2: list and choose a clip -----

async function loadClips() {
    const response = await fetch(`${API_BASE}/clips`);
    const clips = await response.json();

    const listDiv = document.getElementById("clip-list");
    listDiv.innerHTML = "";

    clips.forEach(clip => {
        const div = document.createElement("div");
        div.className = "clip-item";
        div.textContent = `${clip.clipTitle} (${clip.clipDuration}s)`;
        div.addEventListener("click", () => selectClip(clip));
        listDiv.appendChild(div);
    });
}

function selectClip(clip) {
    currentClip = clip;

    const video = document.getElementById("original-video");
    video.src = `${API_BASE}/clips/${clip.clipId}/video`;

    document.getElementById("original-video-section").classList.remove("hidden");
}

// ----- Step 3: start attempt -----

document.getElementById("start-attempt-btn").addEventListener("click", async () => {
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

        dubbableSegments = currentClip.segments.filter(s => s.dubbable).sort((a, b) => a.orderIndex - b.orderIndex);
        currentSegmentIndex = 0;

        document.getElementById("recording-section").classList.remove("hidden");
        document.getElementById("finalize-section").classList.remove("hidden");

        showCurrentSegment();

    } catch (err) {
        alert("Error starting attempt: " + err.message);
    }
});

// ----- Step 4: record each dubbable segment -----

function showCurrentSegment() {
    if (currentSegmentIndex >= dubbableSegments.length) {
        document.getElementById("segment-info").textContent = "All segments recorded!";
        document.getElementById("record-btn").disabled = true;
        updateProgress();
        return;
    }

    const segment = dubbableSegments[currentSegmentIndex];
    document.getElementById("segment-info").textContent =
        `Segment ${currentSegmentIndex + 1} / ${dubbableSegments.length} (${segment.startTime}s - ${segment.endTime}s)`;

    const video = document.getElementById("segment-video");
    video.src = currentClip.clipId
        ? `${API_BASE}/clips/${currentClip.clipId}/video`
        : "";

    // Play only this segment's time range
    video.currentTime = segment.startTime;
    video.play();

    const stopAtEnd = () => {
        if (video.currentTime >= segment.endTime) {
            video.pause();
            video.removeEventListener("timeupdate", stopAtEnd);
        }
    };
    video.addEventListener("timeupdate", stopAtEnd);
}

document.getElementById("record-btn").addEventListener("click", async () => {
    try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        mediaRecorder = new MediaRecorder(stream);
        recordedChunks = [];

        mediaRecorder.ondataavailable = e => recordedChunks.push(e.data);

        mediaRecorder.onstop = async () => {
            const blob = new Blob(recordedChunks, { type: "audio/webm" });
            await uploadRecording(blob);
        };

        mediaRecorder.start();
        document.getElementById("recording-status").textContent = "Recording...";
        document.getElementById("record-btn").disabled = true;
        document.getElementById("stop-btn").disabled = false;

        const segment = dubbableSegments[currentSegmentIndex];
        const duration = segment.endTime - segment.startTime;

        // Auto-stop after the segment's exact duration
        setTimeout(() => {
            if (mediaRecorder.state === "recording") {
                mediaRecorder.stop();
            }
        }, duration * 1000);

    } catch (err) {
        alert("Microphone access error: " + err.message);
    }
});

document.getElementById("stop-btn").addEventListener("click", () => {
    if (mediaRecorder && mediaRecorder.state === "recording") {
        mediaRecorder.stop();
    }
});

async function uploadRecording(blob) {
    const segment = dubbableSegments[currentSegmentIndex];

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

        document.getElementById("recording-status").textContent = "Segment recorded!";
        document.getElementById("record-btn").disabled = false;
        document.getElementById("stop-btn").disabled = true;

        currentSegmentIndex++;
        showCurrentSegment();
        updateProgress();

    } catch (err) {
        document.getElementById("recording-status").textContent = "Error: " + err.message;
        document.getElementById("record-btn").disabled = false;
        document.getElementById("stop-btn").disabled = true;
    }
}

// ----- Step 5: progress and finalize -----

async function updateProgress() {
    const response = await fetch(`${API_BASE}/attempts/${currentAttempt.attemptId}/progress`);
    const progress = await response.json();

    document.getElementById("progress-info").textContent =
        `${progress.recordedSegments} / ${progress.totalDubbableSegments} segments recorded`;

    document.getElementById("finalize-btn").disabled = !progress.complete;
}

document.getElementById("finalize-btn").addEventListener("click", async () => {
    document.getElementById("finalize-btn").disabled = true;
    document.getElementById("finalize-btn").textContent = "Processing...";

    try {
        const response = await fetch(`${API_BASE}/attempts/${currentAttempt.attemptId}/finalize`, {
            method: "POST"
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message);
        }

        const finalAttempt = await response.json();

        const video = document.getElementById("final-video");
        video.src = `${API_BASE}/attempts/${finalAttempt.attemptId}/final-video`;

        document.getElementById("result-section").classList.remove("hidden");

    } catch (err) {
        alert("Error finalizing: " + err.message);
        document.getElementById("finalize-btn").disabled = false;
        document.getElementById("finalize-btn").textContent = "Finalize";
    }
});