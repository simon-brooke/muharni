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



function recordStudentSound(r, c) {
    console.info("Entered recordStudentSound for row " + r + ", column " + c);

    if (Number.isInteger(r) && Number.isInteger(c)) {
        $('#record-student').css('color', 'green');
        try {
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
                        studentSounds[r][c] = new Blob(audioChunks);
                        $('#play-student').prop('disabled', false);
                        console.log("Successfully recorded student sound for row " + r + ", column " + c);
                    } else {
                        console.warn("Failed to record student sound for row " + r + ", column " + c);
                        window.alert("No sound detected. Check your microphone?");
                    }
                    $('#record-student').css('color', 'red');

                }, 3000);
            });
        } catch (error) {
            console.error( error);
            window.alert("Sound recording is only possible on secure connections.");
        }
    }
}

$(document).ready(function() {
    $(".entry-text").on("click", function(e) {
        let cellId = e.currentTarget ? e.currentTarget.id : null;

        if (cellId)
        {
            let row = parseInt(cellId.slice(1, 3));
            let colChar = cellId.slice( 3);

            if (colChar == colChar.toLowerCase()) {
                row += 40; /* we're wrapping lower case into the bottom half of the array */
            }

            let col = cellId.charCodeAt(3) - 65;
            if (col > 26 ) { 
                col -= 32; /* lower case */
            }

            let studentAudio = studentSounds[row][col];

            $("#popup").css({
                'left': e.pageX,
                'top': e.pageY
            });

            $("#character").text(e.currentTarget.innerText.substring(0, 1));

            $("#play-tutor").off("click"); /* trying to remove any previous click handler */
            $("#play-tutor").on("click", function(e){
                let audioUrl = "audio/" + cellId.slice(1) + ".mp3";
                console.log("Playing tutor audio " + audioUrl );
                new Audio( audioUrl).play();
            });

            $("#record-stop").off("click");
            $("#record-stop").on("click", function(e) {
                console.log( "Recording student sound for row " + row + ", column " + col);
                recordStudentSound(row, col);
            });

            $("#play-student").off("click");
            if (studentAudio != null) {
                $("#play-student").on( "click", function(e) {
                    console.log( "Playing student sound for row " + row + ", column " + col);
                    new Audio(URL.createObjectURL(studentAudio)).play();
                });
            } 
            $("#play-student").prop("disabled", studentAudio == null);
        
            $("#popup").show();
        }
    });
})