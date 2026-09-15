fetch("/api/clips")
    .then(response => response.json())
    .then(clips => {

        console.log("We got here")

        const list = document.getElementById("cliplist");

        clips.forEach(clip => {

            const element = document.createElement("p");

            element.textContent = "'" + clip.clipTitle + "' from the movie '" + clip.movieTitle + "'";

            console.log(clip)
            list.appendChild(element);
        });
        console.log("and then we got there")
    });