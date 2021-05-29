(function(){

    //objects
    var userInfo, errorMessage, userPlaylists, createAlbum, createSong, currPlaylist;
    var homePageBtnContainer, logoutBtnContainer;
    //main controller
    var pageOrchestrator = new PageOrchestrator(); 


    window.addEventListener("load", () =>{
        pageOrchestrator.start();
    })

    function ErrorMessage(){
        var container = document.getElementById("errorMessageBox");
        var message = container.querySelector("h1");
        
        this.show = function(){
            container.style.display = 'block';
        }
        this.hide = function(){
            container.style.display = 'none';
        }
        this.set = function(msg){
            message.textContent = msg;
        }
    }
    
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
        var customMsg = addSongToPlaylistBox.querySelector("p");
        var prevBtn = document.getElementById("prevBtn");
        var nextBtn  = document.getElementById("nextBtn");
        var prevIdx = 0;
        var nextIdx = 5;
        var self = this;

        this.show = function(){
            this.reset();
            playlistBox.style.display = 'block';
            prevBtn.style.display = 'bloxk';
            nextBtn.style.display = 'bloxk';
            playlistBox.querySelector("p").textContent = playlist.title;

            makeCall("GET", "GetSongsOfPlaylist?idPlaylist="+playlist.id, null, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        self.update(JSON.parse(req.responseText));
                    }else{
                        pageOrchestrator.showErrorPage(req.status, req.responseText);
                    }
                }
            })

            makeCall("GET", "GetUserSongsNotInPlaylist?idPlaylist="+playlist.id, null, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        var songs = JSON.parse(req.responseText);
                        if (songs.length > 0){
                            addSongToPlaylistBox.style.display = "block";
                        }else{
                            addSongToPlaylistBox.style.display = "none";
                            return;
                        }
                        var select = addSongToPlaylistBox.querySelector("select");
                        songs.forEach(x => {
                            var option = document.createElement("option");
                            option.text = x.title;
                            option.value = x.id;
                            select.appendChild(option);
                        });
                    }else{
                        pageOrchestrator.showErrorPage(req.status, req.responseText);
                    }
                }
            })


        }

        this.update = function(songsMap){
            var rowHead = playlistBox.querySelector("thead > tr");
            var rowBody = playlistBox.querySelector("tbody > tr");
            var size = songsMap.length;
            if (size == 0){
                playlistBox.querySelector("table").style.display = 'none';
                playlistBox.querySelector("p").textContent = "No songs in playlist";
                return;
            }else{
                this.controlButtonVisibility(size);
            }

            for (let i = prevIdx; i < nextIdx && i < size; i++){
                var song = songsMap[i][0];
                var album = songsMap[i][1];

                var title = document.createElement("td");
                var linkTitle = document.createElement("a");
                linkTitle.appendChild(document.createTextNode(song.title));
                linkTitle.addEventListener("click", () =>{
                    console.log("TODO");
                    //TODO player page
                });
                linkTitle.href = "#";
                title.appendChild(linkTitle);
                rowHead.appendChild(title);

                var imageContainer = document.createElement("td");
                var image = document.createElement("img");
                image.src = "ShowFile/image_" + album.imageUrl;
                image.alt = album.imageUrl;
                image.setAttribute("width", "250px");
                image.setAttribute("height", "250px");
                imageContainer.appendChild(image);
                rowBody.appendChild(imageContainer);
            }
        }

        this.updateSongsToInsert = function(){
            var option = addSongToPlaylistBox.querySelector("option:checked")
            option.parentNode.removeChild(option);
            if (addSongToPlaylistBox.querySelector("select").length == 0){
                addSongToPlaylistBox.style.display = 'none';
            }

        }

        this.controlButtonVisibility = function(num){
            var tbody = playlistBox.querySelector("tbody");
            var size = num + tbody.querySelectorAll("td").length;
            if (nextIdx >= size){
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
            addSongToPlaylistBox.querySelector("input[type='button']").addEventListener("click", (e)=>{
                makeCall("POST", "AddSongToPlaylist?idPlaylist=" + playlist.id, e.target.closest("form"), function(req){
                    if (req.readyState == 4){
                        if(req.status == 200){
                            self.update(JSON.parse(req.responseText));
                            self.updateSongsToInsert();                            
                        }else if (req.status == 400){
                            pageOrchestrator.showLocalMessage(customMsg, req.responseText);
                        }else{
                            pageOrchestrator.showErrorPage(req.status, req.responseText);
                        }
                    }
                });
            });
        }

        this.hide = function(){
            playlistBox.style.display = 'none';
        }

        this.reset = function(){
            addSongToPlaylistBox.querySelector("select").innerHTML = "";
            playlistBox.querySelector("p").innerHTML = "";
            playlistBox.querySelector("thead > tr").innerHTML = "";
            playlistBox.querySelector("tbody > tr").innerHTML = "";

        }

    }




    function UserPlaylists(){
        var userPlaylistsBox = document.getElementById("userPlaylistsBox");
        var tableBody = userPlaylistsBox.querySelector("tbody");
        var createPlaylistBox = document.getElementById("createPlaylistBox");
        var customMsg = createPlaylistBox.querySelector("p");
        var self = this;


        this.show = function(){
            this.reset();
            userPlaylistsBox.style.display = 'block';
            createPlaylistBox.style.display = 'block';
            makeCall("GET", "GetUserPlaylist", null, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        self.update(JSON.parse(req.responseText));
                    }else{
                        pageOrchestrator.showErrorPage(req.status, req.responseText);
                    }
                }
            });
        }

        this.hide = function(){
            userPlaylistsBox.style.display = 'none';
            createPlaylistBox.style.display = 'none';

        }

        this.update = function(playlists){
            playlists.forEach(function(playlist){
                var row = document.createElement("tr");
                var nameCell = document.createElement("td");
                var linkName = document.createElement("a");
                nameCell.appendChild(linkName);
                linkName.appendChild(document.createTextNode(playlist.title));
                linkName.href = "#";
                linkName.addEventListener("click", () =>{
                    currPlaylist = new Playlist(playlist);
                    currPlaylist.registerEvents();
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
                linkReorder.href = "ReorderPage.html?idPlaylist=" + playlist.id;
                row.appendChild(reorderCell);

                tableBody.appendChild(row);
            });
        }
        this.registerEvents = function(){
            createPlaylistBox.querySelector("input[type = 'button'").addEventListener("click", (e)=>{
                makeCall("POST", "CreatePlaylist", e.target.closest("form"), function(req){
                    if (req.readyState == 4){
                        if(req.status == 200){
                            var array = new Array();
                            array.push(JSON.parse(req.responseText));
                            self.update(array);
                            pageOrchestrator.showLocalMessage(customMsg, "Playlist" + array[0].title + "inserted correctly");
                        }else if (req.status == 400){
                            pageOrchestrator.showLocalMessage(customMsg, req.responseText);
                        }else{
                            pageOrchestrator.showErrorPage(req.status, req.responseText);
                        }
                    }
                })
            });
        }

        this.reset = function(){
            tableBody.innerHTML = "";
        }

    }

    function CreateAlbum(){
        var createAlbumBox = document.getElementById("createAlbumBox");
        var customMsg = document.querySelector(".customMsg");
        createAlbumBox.querySelector("input[type = 'button'").addEventListener("click", (e) => {
            makeCall("POST", "CreateAlbum", e.target.closest("form"), function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        var album = JSON.parse(req.responseText);
                        createSong.updateAlbumList(album);
                        pageOrchestrator.showLocalMessage(customMsg, "Album inserted correctly");
                    }else if (req.status == 400){
                        pageOrchestrator.showLocalMessage(customMsg, req.responseText);
                    }else{
                        pageOrchestrator.showErrorPage(req.status, req.responseText);
                    }
                }
            })
            
        })

        this.show = function(){
            customMsg.style.display = 'none';
            createAlbumBox.style.display = 'block';
        }

        this.hide = function(){
            createAlbumBox.style.display = 'none';
        }
        

    }

    function CreateSong(){
        var createSongBox = document.getElementById("createSongBox");
        var customMsg = createSongBox.querySelector(".customMsg");
        var self = this;
        createSongBox.querySelector("input[type = 'button'").addEventListener("click", (e) => {
            var form = e.target.closest("form");
            makeCall("POST", "CreateSong", form, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        pageOrchestrator.showLocalMessage(customMsg, "Song inserted correctly");
                    }else if (req.status == 400){
                        pageOrchestrator.showLocalMessage(customMsg, req.responseText);
                    }else{
                        pageOrchestrator.showErrorPage(req.status, req.responseText);
                    }
                }
            })
            
        })

        this.updateAlbumList = function(album){
            var select = document.querySelector("select");
            var newOption = document.createElement("option");
            newOption.text = album.title;
            newOption.value = album.id;
            select.appendChild(newOption);
        }

        this.show = function(){
            makeCall("GET", "GetUserAlbums", null, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        createSongBox.querySelector("select").innerHTML = "";
                        var albums = JSON.parse(req.responseText);
                        albums.forEach(x =>{
                            self.updateAlbumList(x)
                        });
                    }else if (req.status == 400){
                        pageOrchestrator.showLocalMessage(customMsg, req.responseText);
                    }else{
                        pageOrchestrator.showErrorPage(req.status, req.responseText);
                    }
                }
            })
            customMsg.style.display = "none";
            createSongBox.style.display = 'block';
        }

        this.hide = function(){
            createSongBox.style.display = 'none';
        }

    }







    function PageOrchestrator(){
        this.start = function(){
            
            userInfo = new UserInfo();

            errorMessage = new ErrorMessage();

            userPlaylists = new UserPlaylists();
            userPlaylists.registerEvents();

            createAlbum = new CreateAlbum();

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
                window.location.href = "LoginPage.html";
            })

            this.showHomePage();
        }

        this.showHomePage = function(){
            this.reset();
            userInfo.show();
            userPlaylists.show();
            createAlbum.show();
            createSong.show();
        }

        this.showPlaylistPage = function(){
            this.reset();
            homePageBtnContainer.style.display = 'block';
            currPlaylist.show();
        }

        this.showErrorPage = function(status, msg){
            this.reset();
            homePageBtnContainer.style.display = 'block';
            errorMessage.set(msg);
            errorMessage.show();
        }

        this.showLocalMessage = function(container, msg){
            container.textContent = msg;
            container.style.display = 'block';
        }

        this.reset = function(){
            homePageBtnContainer.style.display = 'none';
            currPlaylist.hide();
            userInfo.hide();
            errorMessage.hide();
            userPlaylists.hide();
            createAlbum.hide();
            createSong.hide();
        }


    }
    
})();