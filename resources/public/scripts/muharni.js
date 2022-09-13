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



function recordStudentSound() {
    if (currentCell) {
        $('#record-student').css('background-color', 'green');
        navigator.mediaDevices.getUserMedia({ audio: true })
            .then(stream => {
                const mediaRecorder = new MediaRecorder(stream);
                mediaRecorder.start();

                const audioChunks = [];

                mediaRecorder.addEventListener("dataavailable", event => {
                    audioChunks.push(event.data);
                });

                setTimeout(() => {
                    mediaRecorder.stop();
                    if (audioChunks.length > 0) {
                        studentSounds[currentCell.row][currentCell.col] = new Blob(audioChunks);
                        $('#play-student').prop('disabled', false);
                    }
                    $('#record-student').css('background-color', 'red');

                }, 3000);
            });
    }
}

/**
 * If a student sound has been recorded for the current cell, play it.
 */
function playStudentSound() {
    if (currentCell) {
        if (studentSounds[currentCell.row][currentCell.col] != null) {
            new Audio(URL.createObjectURL(studentSounds[currentCell.row][currentCell.col])).play();
        }
    }
}


$(document).ready(function() {
    $(".entry").on("click", function(e) {
        let cellId = e.currentTarget ? e.currentTarget.id : null;

        if (cellId)
        {
            $("#popup").css({
                'left': e.pageX,
                'top': e.pageY
            });

            $("#character").text(e.currentTarget.innerText);

            $("#play-tutor").off("click"); /* trying to remove any previous click handler */
            $("#play-tutor").on("click", function(e){
                let audioUrl = "audio/" + cellId.slice(1) + ".mp3";
                new Audio( audioUrl).play();
            });
        
            $("#popup").show();
        }
    });
})