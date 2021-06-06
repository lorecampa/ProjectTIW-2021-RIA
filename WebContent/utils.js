

function makeCall(method, url, formElement, cback, isForm){
    var req = new XMLHttpRequest();

    req.onreadystatechange = function(){
        if (req.readyState == XMLHttpRequest.DONE){
            cback(req);
        }
    };

    req.open(method, url);
    if (formElement == null){
        req.send();
    }else if(isForm != undefined && !isForm){
        req.send(formElement);
    }else{
        if (formElement.checkValidity()){
            req.send(new FormData(formElement))
        }else{
            var customMsg = formElement.querySelector(".customMsg");
            customMsg.textContent = "Form inputs are not valid";
            customMsg.style.display = 'block';
        }
    }

    if (formElement != undefined && formElement != null && isForm == undefined){
        formElement.reset();
    }
    
}


function orderSongs(songsMap){
    if (songsMap == undefined || songsMap.length == 0) return;

    //find first element
    var first;
    for (let i = 0; i < songsMap.length; i++){
        if (songsMap[i][0].idSongBefore == 0){
            first = songsMap[i];
            songsMap.splice(i, 1);
            break;
        }
    }
    //create new array and append song in order
    var songsOrdered = new Array();
    songsOrdered.push(first);
    var lastId = first[0].id;

    while (songsMap.length != 0){
        for (let i = 0; i < songsMap.length; i++){
            if (songsMap[i][0].idSongBefore == lastId){
                //unshift (insert at the beginning of the array) because we want DESC album date order
                songsOrdered.unshift(songsMap[i]);
                lastId = songsMap[i][0].id;
                songsMap.splice(i, 1);
                break;
            }
            if (i == songsMap.length - 1){
                //problem because no song idBefore association is found
                return;
            }
        }
    }
    return songsOrdered;
}



