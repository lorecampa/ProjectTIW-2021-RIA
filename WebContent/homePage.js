(function(){

    //components
    var userInfo, errorMessage, userPlaylists, createAlbum, createSong, currPlaylist, currPlayer;
    var homePageBtnContainer, logoutBtnContainer;
    var creationContainer, playlistBox, playerBox;

    

    //main controller
    var pageOrchestrator = new PageOrchestrator(); 


    window.addEventListener("load", () =>{
        //istanciate all components
        pageOrchestrator.start();
        
        pageOrchestrator.registerEvents();
        pageOrchestrator.showHomePage();
    })

    function ErrorMessage(){
        var container = document.getElementById("errorMessageBox");
        var status = container.querySelector("h1");
        var message = container.querySelector("p");
        
        this.show = function(){
            container.style.display = 'block';
        }
        this.hide = function(){
            container.style.display = 'none';
        }
        this.set = function(stat, msg){
            status.textContent = stat;
            message.textContent = msg;
        }
    }
    
    function UserInfo(){
        var container = document.getElementById("personalMessage");
        var user = JSON.parse(sessionStorage.getItem("user"));

        this.show  = function(){
            if(user != null){
                container.querySelector("h2").textContent =  "Welcome " + user.username;
            }else{
                container.querySelector("h2").textContent =  "User not found";
            }
            container.style.display = 'block';

        }
        this.hide = function(){
            container.style.display = 'none';
        }
    }


    function Player(songsMap){
        var song = songsMap[0];
        var album = songsMap[1];
        var linkBack = playerBox.querySelector("a");
        
        this.show = function(){
            playerBox.querySelector("#songTitlePlayer").textContent = song.title;
            playerBox.querySelector("#albumTitlePlayer").textContent = album.title;
            playerBox.querySelector("#albumInterpreterPlayer").textContent = album.interpreter;
            playerBox.querySelector("#albumYearPlayer").textContent = album.year;
            playerBox.querySelector("#albumGenrePlayer").textContent = album.albumGenre;
            var dynamicElementBox = playerBox.querySelector("div");
            dynamicElementBox.innerHTML = "";

            //image song creation
            var image = document.createElement("img");
            image.src = "ShowFile/image_" + album.imageUrl;
            image.alt = album.imageUrl;
            dynamicElementBox.appendChild(image);

            //audio song creation
            var audio = document.createElement("audio");
            audio.controls = true;
            var sourceAudio = document.createElement("source");
            sourceAudio.type = "audio/ogg";
            sourceAudio.src = "ShowFile/audio_"+song.songUrl;
            audio.appendChild(sourceAudio);
            dynamicElementBox.appendChild(audio);
            
            playerBox.style.display = 'block';

        }

        this.registerEvents = function(){
            linkBack.addEventListener("click", () =>{
                pageOrchestrator.showPlaylistPage();
            });
        }

    }


    function Playlist(playlist){        
        var customMsgPlaylist = document.getElementById("playlistName");
        var addSongToPlaylistBox = document.getElementById("addSongToPlaylist");
        var customMsg = addSongToPlaylistBox.querySelector("p");
        var prevBtn = document.getElementById("prevBtn");
        var nextBtn  = document.getElementById("nextBtn");
        var addSongBtn = addSongToPlaylistBox.querySelector("input[type='button']");

        var playlistSongs;
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
                        var songs = JSON.parse(req.responseText);
                        if (songs.length != undefined){
                            customMsgPlaylist.textContent = playlist.title;
                            self.update(orderSongs(JSON.parse(req.responseText)));
                        }else{
                            customMsgPlaylist.textContent = "No songs in playlist";
                        }
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
                            var select = addSongToPlaylistBox.querySelector("select");
                            songs.forEach(x => {
                                var option = document.createElement("option");
                                option.text = x.title;
                                option.value = x.id;
                                select.appendChild(option);
                            });
                        }else{
                            addSongToPlaylistBox.style.display = "none";
                        }                        
                    }else{
                        pageOrchestrator.showErrorPage(req.status, req.responseText);
                    }
                }
            })


        }

        this.clickSongCback = function(song){
            return function(){
                if (currPlayer == undefined){
                    currPlayer = new Player(song);
                    currPlayer.registerEvents();
                }else{
                    currPlayer = new Player(song);
                }
                pageOrchestrator.showPlayerPage();
            }
        }


        this.update = function(songsMap){
            var table = playlistBox.querySelector("table");
            var rowHead = table.querySelector("thead > tr");
            var rowBody = table.querySelector("tbody > tr");
            Array.from(document.querySelectorAll("tr")).forEach(x => x.innerHTML = "");

            if (songsMap != undefined){
                songsMap.forEach(x => playlistSongs.push(x));
            }
            
            var currSize = playlistSongs.length;
            
            if (nextIdx >= currSize){
                nextBtn.style.display = 'none';
            }else{
                nextBtn.style.display = 'block';
            }

            if (prevIdx == 0){
                prevBtn.style.display = 'none';
            }else{
                prevBtn.style.display = 'block';
            }
            for (let i = prevIdx; i < currSize && i < nextIdx; i++){
                var song = playlistSongs[i][0];
                var album = playlistSongs[i][1];
                
                //title song creation
                var title = document.createElement("td");
                var linkTitle = document.createElement("a");
                linkTitle.appendChild(document.createTextNode(song.title));
                linkTitle.addEventListener("click", this.clickSongCback(playlistSongs[i]));
                linkTitle.href = "#";
                title.appendChild(linkTitle);
                rowHead.appendChild(title);

                //image song creation
                var imageContainer = document.createElement("td");
                var image = document.createElement("img");
                image.addEventListener("click", this.clickSongCback(playlistSongs[i]));
                image.src = "ShowFile/image_" + album.imageUrl;
                image.alt = album.imageUrl;
                imageContainer.appendChild(image);
                rowBody.appendChild(imageContainer);
            }
        }

        this.updateSongsToInsert = function(){
            var option = addSongToPlaylistBox.querySelector("option:checked");
            option.parentNode.removeChild(option);
            if (addSongToPlaylistBox.querySelector("select").length == 0){
                addSongToPlaylistBox.style.display = 'none';
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
            var prevBtnClone = prevBtn.cloneNode(true);
            prevBtn.parentNode.replaceChild(prevBtnClone, prevBtn);
            prevBtn = prevBtnClone;
            prevBtn.addEventListener("click", () =>{
                this.prevSlide();
            })

            var nextBtnClone = nextBtn.cloneNode(true);
            nextBtn.parentNode.replaceChild(nextBtnClone, nextBtn);
            nextBtn = nextBtnClone;
            nextBtn.addEventListener("click", () =>{
                this.nextSlide();
            })


            var addSongBtnClone = addSongBtn.cloneNode(true);
            addSongBtn.parentNode.replaceChild(addSongBtnClone, addSongBtn);
            addSongBtn = addSongBtnClone;
            addSongBtn.addEventListener("click", (e)=>{
                makeCall("POST", "AddSongToPlaylist?idPlaylist=" + playlist.id, e.target.closest("form"), function(req){
                    if (req.readyState == 4){
                        if(req.status == 200){
                            self.show();                            
                        }else if (req.status == 400){
                            pageOrchestrator.showLocalMessage(customMsg, req.responseText);
                        }else{
                            pageOrchestrator.showErrorPage(req.status, req.responseText);
                        }
                    }
                });
            });
        }


        this.reset = function(){
            prevBtn.style.display = 'none';
            nextBtn.style.display = 'none';
            playlistSongs = new Array();
            addSongToPlaylistBox.querySelector("select").innerHTML = "";
            playlistBox.querySelector("p").innerHTML = "";
            Array.from(playlistBox.querySelectorAll("tr")).forEach(x => x.innerHTML="");
        }

    }




    function UserPlaylists(){
        var userPlaylistsBox = document.getElementById("userPlaylistBox");
        var table = userPlaylistsBox.querySelector("table");
        var tableBody = table.querySelector("tbody");
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
                        var response = JSON.parse(req.responseText);
                        if (response.length == 0){
                            userPlaylistsBox.querySelector("p").textContent = "No playlists yet";
                            table.style.display = 'none';
                        }else{
                            self.update(response);
                        }
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
            table.style.display = 'block';
            userPlaylistsBox.querySelector("p").innerHTML = "";

            playlists.forEach(function(playlist){
                //title creation
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

                //date creation
                var dateCell = document.createElement("td");
                dateCell.textContent = playlist.date;
                row.appendChild(dateCell);

                //reorder link creation
                var reorderCell = document.createElement("td");
                var linkReorder = document.createElement("a");
                reorderCell.appendChild(linkReorder);
                var linkText = document.createTextNode("Reorder");
                linkReorder.appendChild(linkText);
                linkReorder.href = "ReorderPage.html?idPlaylist=" + playlist.id +"&titlePlaylist=" + playlist.title;
                row.appendChild(reorderCell);
                
                tableBody.insertBefore(row, tableBody.firstChild);
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
                            pageOrchestrator.showLocalMessage(customMsg, "Playlist " + array[0].title + " inserted correctly");
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
        var customMsg = createAlbumBox.querySelector(".customMsg");
        

        this.show = function(){
            customMsg.style.display = 'none';
            createAlbumBox.style.display = 'block';
        }

        this.hide = function(){
            createAlbumBox.style.display = 'none';
        }
        this.registerEvents = function(){
            createAlbumBox.querySelector("input[type = 'button'").addEventListener("click", (e) => {

                //makes last genre selection default
                var select = createAlbumBox.querySelector("select");
                var optionChecked = select.querySelector("option:checked");
                Array.from(select.querySelectorAll("option")).forEach(x => x.removeAttribute("selected"));
                optionChecked.setAttribute("selected", "selected");

                makeCall("POST", "CreateAlbum", e.target.closest("form"), function(req){
                    if (req.readyState == 4){
                        if(req.status == 200){
                            var album = JSON.parse(req.responseText);
                            createSong.updateAlbumList(album);
                            pageOrchestrator.showLocalMessage(customMsg, "Album " + album.title + " inserted correctly");
                        }else if (req.status == 400){
                            pageOrchestrator.showLocalMessage(customMsg, req.responseText);
                        }else{
                            pageOrchestrator.showErrorPage(req.status, req.responseText);
                        }
                    }
                })
                
            })
        }
        

    }

    function CreateSong(){
        var createSongBox = document.getElementById("createSongBox");
        var customMsg = createSongBox.querySelector(".customMsg");
        var self = this;
        
        this.updateAlbumList = function(album){
            var select = createSongBox.querySelector("select");
            select.style.display = 'block';
            var newOption = document.createElement("option");
            newOption.text = album.title;
            newOption.value = album.id;
            select.appendChild(newOption);
            
        }

        this.show = function(){
            customMsg.style.display = "none";
            createSongBox.style.display = 'block';

            makeCall("GET", "GetUserAlbums", null, function(req){
                if (req.readyState == 4){
                    if(req.status == 200){
                        createSongBox.querySelector("select").innerHTML = "";
                        var albums = JSON.parse(req.responseText);
                        albums.forEach(x =>{
                            self.updateAlbumList(x)
                        });
                        if (albums.length > 0){
                            document.getElementById("noAlbumLabel").style.display = 'none';
                        }else{
                            createSongBox.querySelector("select").style.display = 'none';
                        }
                        
                    }else if (req.status == 400){
                        pageOrchestrator.showLocalMessage(customMsg, req.responseText);
                    }else{
                        pageOrchestrator.showErrorPage(req.status, req.responseText);
                    }
                }
            })
        }

        this.hide = function(){
            createSongBox.style.display = 'none';
        }

        this.registerEvents = function(){
            createSongBox.querySelector("input[type = 'button'").addEventListener("click", (e) => {
                //makes last album selection default
                var select = createSongBox.querySelector("select");
                var optionChecked = select.querySelector("option:checked");
                Array.from(select.querySelectorAll("option")).forEach(x => x.removeAttribute("selected"));
                optionChecked.setAttribute("selected", "selected");

                makeCall("POST", "CreateSong", e.target.closest("form"), function(req){
                    if (req.readyState == 4){
                        if(req.status == 200){
                            var song = req.responseText;
                            pageOrchestrator.showLocalMessage(customMsg, "Song " + song.title + " inserted correctly");
                        }else if (req.status == 400){
                            pageOrchestrator.showLocalMessage(customMsg, req.responseText);
                        }else{
                            pageOrchestrator.showErrorPage(req.status, req.responseText);
                        }
                    }
                }) 
            })
        }

    }







    function PageOrchestrator(){
        
        this.start = function(){
            homePageBtnContainer = document.getElementById("homePageBtnContainer");
            logoutBtnContainer = document.getElementById("logoutBtnContainer");
            creationContainer = document.getElementById("creationContainer");
            playlistBox = document.getElementById("playlistBox");
            playerBox = document.getElementById("playerBox");

            userInfo = new UserInfo();

            errorMessage = new ErrorMessage();

            userPlaylists = new UserPlaylists();

            createAlbum = new CreateAlbum();

            createSong = new CreateSong();
        }

        this.showHomePage = function(){
            this.reset();
            creationContainer.style.display ='block';
            userInfo.show();
            userPlaylists.show();
            createAlbum.show();
            createSong.show();
        }

        this.showPlaylistPage = function(){
            this.reset();
            homePageBtnContainer.style.display = 'block';
            if(currPlaylist!= null) currPlaylist.show();
        }
        this.showPlayerPage = function(){
            this.reset();
            homePageBtnContainer.style.display = 'block';
            if (currPlayer!=null) currPlayer.show();
        }

        this.showErrorPage = function(status, msg){
            this.reset();
            homePageBtnContainer.style.display = 'block';
            errorMessage.set(status, msg);
            errorMessage.show();
        }

        this.showLocalMessage = function(container, msg){
            Array.from(document.querySelectorAll(".customMsg")).forEach(x => x.style.display = 'none');
            container.style.color = "red";
            container.textContent = msg;
            container.style.display = 'block';
        }

        //hides all components
        this.reset = function(){
            homePageBtnContainer.style.display = 'none';
            creationContainer.style.display = 'none';
            userInfo.hide();
            errorMessage.hide();
            userPlaylists.hide();
            createAlbum.hide();
            createSong.hide();
            playlistBox.style.display = 'none';
            playerBox.style.display = 'none';
        }

        this.registerEvents = function(){
            homePageBtnContainer.querySelector("input[type='button']").addEventListener("click", ()=>{
                this.showHomePage();
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


            createSong.registerEvents();
            createAlbum.registerEvents();
            userPlaylists.registerEvents();

        }


    }
    
})();