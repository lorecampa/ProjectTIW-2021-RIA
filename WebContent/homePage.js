(function(){

    //objects
    var userInfo, userPlaylists, createPlaylist, createAlbum, createSong, currPlaylist;
    var homePageBtnContainer, logoutBtnContainer;
    //main controller
    var pageOrchestrator = new PageOrchestrator(); 


    window.addEventListener("load", () =>{
        pageOrchestrator.start();
    })
    
    function UserInfo(){
        var container = document.getElementById("personalMessage");
        var user = JSON.parse(sessionStorage.getItem("user"));

        this.show  = function(){
            container.querySelector("h2").textContent =  "Welcome " + user.username;
            container.style.display = 'block';

        }
        this.hide = function(){
            container.style.display = 'none';
        }
    }

    function Playlist(playlist){
        var playlistBox = document.getElementById("playlistBox");
        var addSongToPlaylistBox = document.getElementById("addSongToPlaylist");
        var prevBtn = document.getElementById("prevBtn");
        var nextBtn  = document.getElementById("nextBtn");
        playlistBox.querySelector("h3").innerHTML = "";

        var currSongs;
        var prevIdx = 0;
        var nextIdx = 5;
        var self = this;

        this.show = function(){
            this.registerEvents();
            playlistBox.style.display = 'block';
            prevBtn.style.display = 'bloxk';
            nextBtn.style.display = 'bloxk';
            //one time reset
            playlistBox.querySelector("h3").textContent = playlist.title;
            addSongToPlaylistBox.querySelector("select").innerHTML = "";

            makeCall("GET", "GetSongsOfPlaylist?idPlaylist="+playlist.id, null, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        currSongs = JSON.parse(req.responseText);
                        self.update();
                    }else{
                        handleCustomRedirect(req.status);
                    }
                }
            })

            makeCall("GET", "GetUserSongsNotInPlaylist?idPlaylist="+playlist.id, null, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        self.updateSongsToInsert(JSON.parse(req.responseText));
                    }else{
                        handleCustomRedirect(req.status);
                    }
                }
            })


        }

        this.update = function(){
            this.reset();
            var rowHead = playlistBox.querySelector("thead > tr");
            var rowBody = playlistBox.querySelector("tbody > tr");
            var size = currSongs.length;
            if (size == 0){
                //TODO insert message
                return;
            }else{
                this.controlButtonVisibility(size);
            }
            for (let i = prevIdx; i < nextIdx && i < size; i++){
                var song = currSongs[i];
                var title = document.createElement("td");
                var linkTitle = document.createElement("a");
                linkTitle.appendChild(document.createTextNode(song.titleSong));
                linkTitle.addEventListener("click", () =>{
                    console.log("TODO");
                    //TODO
                });
                linkTitle.href = "#";
                title.appendChild(linkTitle);
                rowHead.appendChild(title);

                var imageContainer = document.createElement("td");
                var image = document.createElement("img");
                image.src = "ShowFile/image_" + song.imageUrl;
                image.alt = song.imageUrl;
                image.setAttribute("width", "250px");
                image.setAttribute("height", "250px");
                imageContainer.appendChild(image);
                rowBody.appendChild(imageContainer);
            }
        }

        this.updateSongsToInsert = function(songs){
            if (songs.length > 0){
                addSongToPlaylistBox.style.display = "block";
            }else{
                addSongToPlaylistBox.style.display = "none";
                return;
            }
            var select = addSongToPlaylistBox.querySelector("select");
            songs.forEach(x => {
                var option = document.createElement("option");
                option.text = x.titleSong;
                option.value = x.idSong;
                select.appendChild(option);
            })

        }

        this.controlButtonVisibility = function(num){
            if (nextIdx >= num){
                nextBtn.style.display = 'none';
            }else{
                nextBtn.style.display = 'block';
            }

            if (prevIdx == 0){
                prevBtn.style.display = 'none';
            }else{
                prevBtn.style.display = 'block';
            }
        }

        this.nextSlide = function(){
            prevIdx += 5;
            nextIdx += 5;
            this.update();

        }
        this.prevSlide = function(){
            prevIdx -= 5;
            nextIdx -= 5;
            this.update();

        }

        this.registerEvents = function(){
            prevBtn.addEventListener("click", () =>{
                this.prevSlide();
            })
            nextBtn.addEventListener("click", () =>{
                this.nextSlide();
            })
        }

        this.hide = function(){
            playlistBox.style.display = 'none';
        }

        this.reset = function(){
            playlistBox.querySelector("thead > tr").innerHTML = "";
            playlistBox.querySelector("tbody > tr").innerHTML = "";

            //think if necessary
        }

    }




    function UserPlaylists(){
        var userPlaylistsBox = document.getElementById("userPlaylistsBox");
        var tableBody = userPlaylistsBox.querySelector("tbody");
        var self = this;

        this.show = function(){
            userPlaylistsBox.style.display = 'block';
            makeCall("GET", "GetUserPlaylist", null, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        self.update(JSON.parse(req.responseText));
                    }else{
                        handleCustomRedirect(req.status);
                    }
                }
            });
        }

        this.hide = function(){
            userPlaylistsBox.style.display = 'none';
        }

        this.update = function(playlists){
            this.reset();
            playlists.forEach(function(playlist){
                var row = document.createElement("tr");
                var nameCell = document.createElement("td");
                var linkName = document.createElement("a");
                nameCell.appendChild(linkName);
                linkName.appendChild(document.createTextNode(playlist.title));
                linkName.href = "#";
                linkName.addEventListener("click", () =>{
                    currPlaylist = new Playlist(playlist);
                    pageOrchestrator.showPlaylistPage();
                })
                
                row.appendChild(nameCell);

                var dateCell = document.createElement("td");
                dateCell.textContent = playlist.date;
                row.appendChild(dateCell);

                var reorderCell = document.createElement("td");
                var linkReorder = document.createElement("a");
                reorderCell.appendChild(linkReorder);
                var linkText = document.createTextNode("Reorder");
                linkReorder.appendChild(linkText);
                linkReorder.href = "ReorderPage.html?id=" + playlist.id;
                row.appendChild(reorderCell);

                tableBody.appendChild(row);
            });
        }

        this.reset = function(){
            tableBody.innerHTML = "";
        }

    }

    function CreatePlaylist(){
        var createPlaylistBox = document.getElementById("createPlaylistBox");
      
        this.show = function(){
            createPlaylistBox.style.display = 'block';
        }
        this.hide = function(){
            createPlaylistBox.style.display = 'none';
        }

    }

    function CreateAlbum(){
        var createAlbumBox = document.getElementById("createAlbumBox");
        this.show = function(){
            createAlbumBox.style.display = 'block';
        }

        this.hide = function(){
            createAlbumBox.style.display = 'none';
        }
        

    }

    function CreateSong(){
        var createSongBox = document.getElementById("createSongBox");
        
        this.show = function(){
            createSongBox.style.display = 'block';
        }

        this.hide = function(){
            createSongBox.style.display = 'none';
        }

        

    }







    function PageOrchestrator(){
        this.start = function(){
            userInfo = new UserInfo();
            userPlaylists = new UserPlaylists();
            createAlbum = new CreateAlbum();
            createPlaylist = new CreatePlaylist();
            createSong = new CreateSong();
            currPlaylist = new Playlist();

            //Button
            homePageBtnContainer = document.getElementById("homePageBtnContainer");
            homePageBtnContainer.querySelector("input[type='button']").addEventListener("click", ()=>{
                this.showHomePage();
            });

            logoutBtnContainer = document.getElementById("logoutBtnContainer");
            logoutBtnContainer.querySelector("input[type='button']").addEventListener("click", () =>{
                sessionStorage.removeItem("user");
                handleCustomRedirect(204);
            })

            this.showHomePage();
        }

        this.showHomePage = function(){
            this.reset();
            userInfo.show();
            userPlaylists.show();
            createAlbum.show();
            createPlaylist.show();
            createSong.show();
        }

        this.showPlaylistPage = function(){
            this.reset();
            homePageBtnContainer.style.display = 'block';
            currPlaylist.show();
        }

        this.reset = function(){
            homePageBtnContainer.style.display = 'none';
            currPlaylist.hide();
            userInfo.hide();
            userPlaylists.hide();
            createAlbum.hide();
            createPlaylist.hide();
            createSong.hide();
        }


    }
    
})();