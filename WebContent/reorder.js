(function(){

    var sortingBox;
    var tbody;
    var errorMessageBox;
    var idPlaylist;
    var titlePlaylist;
    var songsOrdered;

    window.addEventListener("load", ()=>{
        sortingBox = document.getElementById("sortingBox");
        tbody = sortingBox.querySelector("tbody");

        errorMessageBox = document.getElementById("errorMessageBox");
        errorMessageBox.style.display = 'none';

        tbody.innerHTML = "";
        const urlParams = new URLSearchParams(window.location.search);
        idPlaylist = urlParams.get('idPlaylist');
        titlePlaylist = urlParams.get('titlePlaylist');
        document.querySelector("h2").textContent = titlePlaylist;

        makeCall("GET", "GetSongsOfPlaylist?idPlaylist="+idPlaylist, null, function(req){
            if (req.readyState == 4){
                if(req.status == 200){
                    var playlistSongs = JSON.parse(req.responseText);
                    if (playlistSongs.length > 0){
                        songsOrdered = new Array();
                        orderSongs(playlistSongs).forEach(x => songsOrdered.push(x[0]));
                        loadSongs();
                    }else{
                        setNormalMessage("There are not songs in playlist");
                    }
                }else{
                    setError(req.status, req.responseText);
                }
            }
        });

        document.getElementById("saveSorting").addEventListener("click", ()=>{
            var arrayToSend = saveOrder();
            makeCall("POST", "SaveOrder?idPlaylist="+idPlaylist, JSON.stringify(arrayToSend), function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        //order saved
                        var msgContainer = sortingBox.querySelector("p");
                        msgContainer.textContent = "Order saved correctly";
                        msgContainer.style.display = 'block';

                    }else{
                        setError(req.status, req.responseText);
                    }
                }
            }, false);
        });
    })

    function registerDragEvents(row){
        row.draggable = true;
        row.addEventListener("dragstart", dragStart); //save dragged element reference
        row.addEventListener("dragover", dragOver); // change color of reference element to red
        row.addEventListener("dragleave", dragLeave); // change color of reference element to black
        row.addEventListener("drop", drop); 
    }


    function loadSongs(){
        songsOrdered.forEach(x => {
            var row = document.createElement("tr");

            row.setAttribute("class", "draggable");
            var titleCell = document.createElement("td");
            titleCell.textContent = x.title;

            var dateAddingCell = document.createElement("td");
            dateAddingCell.textContent = x.dateAdding;
            

            var hiddenIdBeforeCell = document.createElement("td");
            hiddenIdBeforeCell.id = "idSongBefore";
            hiddenIdBeforeCell.hidden = true;
            hiddenIdBeforeCell.textContent = x.idSongBefore;

            var hiddenIdCell = document.createElement("td");
            hiddenIdCell.id = "idSong";
            hiddenIdCell.hidden = true;
            hiddenIdCell.textContent = x.id;

            row.appendChild(titleCell);
            row.appendChild(dateAddingCell)
            row.appendChild(hiddenIdBeforeCell);
            row.appendChild(hiddenIdCell);
            registerDragEvents(row);


            tbody.appendChild(row);
        })
    }


    function setError(status, msg){
        sortingBox.style.display = 'none';
        errorMessageBox.style.display = 'block';
        errorMessageBox.querySelector("h1").style.display = 'block';
        errorMessageBox.querySelector("h1").textContent = status;
        errorMessageBox.querySelector("p").textContent = msg;
    }

    function setNormalMessage(msg){
        sortingBox.style.display = 'none';
        errorMessageBox.style.display = 'block';
        errorMessageBox.querySelector("h1").style.display = 'none';
        errorMessageBox.querySelector("p").textContent = msg;

    }


    function saveOrder(){
        var array = Array.from(document.querySelectorAll('tbody > tr'));
        var arrayToSend = new Array();
        for (var i = 0; i < array.length; i++){
            if (i != array.length - 1){
                changeIdBefore(array[i], array[i+1]);
            }else{
                array[i].querySelector("#idSongBefore").textContent = "0";
            }
            var song = new SongUpdated(array[i].querySelector("#idSong").textContent,array[i].querySelector("#idSongBefore").textContent);
            arrayToSend.push(song);
        }
        return arrayToSend;
    }

    function SongUpdated(idSong, idSongBefore) {
        this.idSong = idSong;
        this.idSongBefore = idSongBefore;
    }




    //DRAG EVENTS



    var startElement;

    /* 
        This fuction puts all row to "notselected" class, 
        then we use CSS to put "notselected" in black and "selected" in red
    */
    function unselectRows(rowsArray) {
        rowsArray.forEach(x => x.className = "notselected");
    }



    function dragStart(event) {
        startElement = event.target.closest("tr");
    }


    function dragOver(event) {
        // We need to use prevent default, otherwise the drop event is not called
        event.preventDefault(); 

        // We need to select the row that triggered this event to marked as "selected" so it's clear for the user
        var dest = event.target.closest("tr");

        // Mark  the current element as "selected", then with CSS we will put it in red
        dest.className = "selected";
    }

    /*
        The dragleave event is fired when a dragged 
        element leaves a valid drop target.
        https://developer.mozilla.org/en-US/docs/Web/API/Document/dragleave_event
    */
    function dragLeave(event) {
        // We need to select the row that triggered this event to marked as "notselected" so it's clear for the user 
        var dest = event.target.closest("tr");

        // Mark  the current element as "notselected", then with CSS we will put it in black
        dest.className = "notselected";
    }

    /*
        The drop event is fired when an element or text selection is dropped on a valid drop target.
        https://developer.mozilla.org/en-US/docs/Web/API/Document/drop_event
    */
    function drop(event) {
        
        // Obtain the row on which we're dropping the dragged element
        var dest = event.target.closest("tr");

        // Obtain the index of the row in the table to use it as reference 
        // for changing the dragged element possition
        var table = dest.closest('table'); 
        var rowsArray = Array.from(table.querySelectorAll('tbody > tr'));

        var indexDest = rowsArray.indexOf(dest);
        var indexStart = rowsArray.indexOf(startElement);

        // Move the dragged element to the new position
        if (indexStart < indexDest){
            // If we're moving down, then we insert the element after our reference (indexDest)
            startElement.parentElement.insertBefore(startElement, rowsArray[indexDest + 1]);
        }else{
            // If we're moving up, then we insert the element before our reference (indexDest)
            startElement.parentElement.insertBefore(startElement, rowsArray[indexDest]);
        }            
        // Mark all rows in "not selected" class to reset previous dragOver
        unselectRows(rowsArray);
    }


    

    function changeIdBefore(to, from){
        to.querySelector("#idSongBefore").textContent = from.querySelector("#idSong").textContent
    }
    



})();