(function(){

    var tbody;

    window.addEventListener("load", ()=>{
        tbody = document.querySelector("tbody");
        tbody.innerHTML = "";
        const urlParams = new URLSearchParams(window.location.search);
        const idPlaylist = urlParams.get('idPlaylist');
        makeCall("GET", "GetSongsOfPlaylist?idPlaylist="+idPlaylist, null, function(req){
            if (req.readyState == 4){
                if(req.status == 200){
                    loadSongs(JSON.parse(req.responseText));
                }else{
                    handleCustomRedirect(req.status);
                }
            }
        });
    })


    function loadSongs(songs){
        songs.forEach(x => {
            var row = document.createElement("tr");

            var cell = document.createElement("td");
            cell.textContent = x.titleSong;
            var hiddenIdCell = document.createElement("td");
            hiddenIdCell.hidden = true;
            hiddenIdCell.textContent = x.idSong;

            row.appendChild(cell);
            row.appendChild(hiddenIdCell);

            tbody.appendChild(row);
        })
    }
    



})();