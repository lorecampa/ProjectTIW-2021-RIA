(function(){
    var homePageBtnContainer = document.getElementById("homePageBtnContainer");
    var logoutBtnContainer = document.getElementById("logoutBtnContainer");
    var sortingBox = document.getElementById("sortingBox");
    var tbody = sortingBox.querySelector("tbody");
    var errorMessageBox = document.getElementById("errorMessageBox");
    var idPlaylist, titlePlaylist;
    var startElement;

    //array with the songs loaded
    var songsOrdered;

    window.addEventListener("load", ()=>{
        errorMessageBox.style.display = 'none';
        tbody.innerHTML = "";

        const urlParams = new URLSearchParams(window.location.search);
        idPlaylist = urlParams.get('idPlaylist');
        titlePlaylist = urlParams.get('titlePlaylist');

        document.querySelector("h2").textContent = titlePlaylist;

        registerEvents();

        makeCall("GET", "GetSongsOfPlaylist?idPlaylist="+idPlaylist, null, function(req){
            if (req.readyState == 4){
                if(req.status == 200){
                    var playlistSongs = JSON.parse(req.responseText);
                    if (playlistSongs.length > 0){
                        songsOrdered = new Array();
                        //order songs and push each element in songs ordered
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
    })


    function registerEvents(){
        //save current order in database
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

        homePageBtnContainer.querySelector("input[type='button']").addEventListener("click", ()=>{
            window.location.href = "HomePage.html";
        });

        logoutBtnContainer.querySelector("input[type='button']").addEventListener("click", () =>{
            makeCall("GET", "Logout", null, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        sessionStorage.removeItem("user");
                        window.location.href = "LoginPage.html";  
                    }
                }
            })  
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


    function registerDragEvents(row){
        row.draggable = true;
        row.addEventListener("dragstart", dragStart); 
        row.addEventListener("dragover", dragOver);
        row.addEventListener("drop", drop); 
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
            //creation SongUpdated object in order to be sent to the servlet
            var song = new SongUpdated(array[i].querySelector("#idSong").textContent,array[i].querySelector("#idSongBefore").textContent);
            arrayToSend.push(song);
        }
        return arrayToSend;
    }

    function changeIdBefore(to, from){
        to.querySelector("#idSongBefore").textContent = from.querySelector("#idSong").textContent
    }
    
    //object with only the attribute we need to send
    function SongUpdated(idSong, idSongBefore) {
        this.idSong = idSong;
        this.idSongBefore = idSongBefore;
    }




    //DRAG EVENTS

    function dragStart(event) {
        startElement = event.target.closest("tr");
    }

    function dragOver(event) {
        // We need to use prevent default, otherwise the drop event is not called
        event.preventDefault(); 
    }

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
    }




})();