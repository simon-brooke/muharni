/**
 * muharni.js
 * 
 * Custom JavaScript functions, some dependent on JQuery, used in the `muharni` 
 * page.
 */

/**
 * An array for client side sound recordings made by the student. We don't 
 * strictly need one for each short and long character, but it will give a 
 * better experience for people who really get into it.
 * 
 * There are two tables each of 39 rows and 12 columns, but we're going to 
 * pack both tables into the same array. So the long sounds will be rows 1 
 * to 39 inclusive, and the short from 41 to 79 inclusive.
 */
const studentSounds = Array(80).fill(0).map(x => Array(13).fill(null));

/** 
 *  Creates a progressbar. Adapted from
 *  https://stackoverflow.com/questions/31109581/javascript-timer-progress-bar
 *
 *  @param id the id of the div we want to transform in a progressbar
 *  @param duration the duration of the timer example: '10s'
 *  @param callback, optional function which is called when the progressbar reaches 0.
 */
function createProgressbar(id, duration) {
    // We select the div that we want to turn into a progressbar
    try {
        const progressbar = document.getElementById(id);
        progressbar.className = 'progressbar';

        // We create the div that changes width to show progress
        let progressbarinner = progressbar.querySelector('.inner');

        if (progressbarinner == null) {
            progressbarinner = document.createElement('div');
            progressbarinner.className = 'inner';

            // Now we set the animation parameters
            progressbarinner.style.animationDuration = duration;

            // Append the progressbar to the main progressbardiv
            progressbar.appendChild(progressbarinner);
        }
        progressbarinner.addEventListener('animationend', () => {
            while (progressbar.hasChildNodes()) {
                progressbar.removeChild(progressbar.lastChild);
            }
        });

        // When everything is set up we start the animation
        progressbarinner.style.animationPlayState = 'running';
    } catch (e) {
        console.warn("Failed to create progress bar because " +
            e.message +
            ". This does not, cosmically speaking, matter.");
    }
}

function recordStudentSound(r, c) {
    console.info("Entered recordStudentSound for row " + r + ", column " + c);

    if (Number.isInteger(r) && Number.isInteger(c)) {
        $('#record-stop').css('color', 'green');

        try {
            createProgressbar('progress', '5s');

            navigator.mediaDevices.getUserMedia({ audio: true })
                .then(stream => {
                    const mediaRecorder = new MediaRecorder(stream);
                    const audioChunks = [];

                    mediaRecorder.start();
                    mediaRecorder.onerror = function (e) {
                        console.log("An error has occurred: " + e.message);
                    };
                    mediaRecorder.addEventListener("dataavailable", event => {
                        console.info("Audio recorded...")
                        audioChunks.push(event.data);
                    });

                    mediaRecorder.onstop = function (e) {
                        console.log("data available after MediaRecorder.stop() called.");

                        if (audioChunks.length > 0) {
                            studentSounds[r][c] = new Blob(audioChunks);

                            $('#play-student').prop('disabled', false);
                            $('#play-student').css('color', 'black');
                            $("#play-student").on("click", function (e) {
                                console.log("Playing student sound for row " + r + ", column " + c);
                                new Audio(URL.createObjectURL(studentSounds[r][c])).play();
                            });
                            console.log("Successfully recorded student sound for row " + r + ", column " + c);
                        } else {
                            console.warn("Failed to record student sound for row " + r + ", column " + c);
                            window.alert("No sound detected. Check your microphone?");
                        }
                        $('#record-stop').css('color', 'red');
                    };

                    setTimeout(() => {
                        mediaRecorder.requestData();
                        mediaRecorder.stop();

                    }, 5000);
                });
        } catch (error) {
            console.error(error);
            if (error instanceof TypeError) {
                window.alert("Sound recording is only possible on secure connections.");
            }
            else if (error instanceof DOMException) {
                window.alert("No microphone detected? " + error.message);
            }
        }
    }
}


$(document).ready(function () {
    $(".entry-text").on("click", function (e) {
        let cellId = e.currentTarget ? e.currentTarget.id : null;

        if (cellId) {
            let row = parseInt(cellId.slice(1, 3));
            let colChar = cellId.slice(3);

            if (colChar == colChar.toLowerCase()) {
                row += 40; /* we're wrapping lower case into the bottom half of the array */
            }

            let col = cellId.charCodeAt(3) - 65;
            if (col > 26) {
                col -= 32; /* lower case */
            }

            let studentAudio = studentSounds[row][col];

            $("#popup").css({
                'left': e.pageX,
                'top': e.pageY
            });

            $("#character").text(e.currentTarget.innerText.substring(0, 1));

            $("#play-tutor").off("click"); /* trying to remove any previous click handler */
            $("#play-tutor").on("click", function (e) {
                let audioUrl = "audio/" + cellId.slice(1) + ".mp3";
                console.log("Playing tutor audio " + audioUrl);
                new Audio(audioUrl).play();
            });

            $("#record-stop").off("click");
            $("#record-stop").on("click", function (e) {
                console.log("Recording student sound for row " + row + ", column " + col);
                recordStudentSound(row, col);
            });

            $("#play-student").off("click");
            $('#play-student').css('color', 'gray');
            if (studentAudio != null) {
                $('#play-student').css('color', 'black');
                $("#play-student").on("click", function (e) {
                    console.log("Playing student sound for row " + row + ", column " + col);
                    new Audio(URL.createObjectURL(studentAudio)).play();
                });
            }
            $("#play-student").prop("disabled", studentAudio == null);

            $("#popup").show();
        }
    });
})