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
            var customMsg = document.querySelector(".customMsg");
            customMsg.textContent = formElement.reportValidity();
            customMsg.style.display = 'block';
        }
    }

    if (isForm != undefined && isForm && formElement != null){
        formElement.reset();
    }
}


function orderSongs(songsMap){
    if (songsMap == undefined || songsMap.length == 0) return;
    var first;
    for (let i = 0; i < songsMap.length; i++){
        if (songsMap[i][0].idSongBefore == 0){
            first = songsMap[i];
            songsMap.splice(i, 1);
            break;
        }
    }
    var songsOrdered = new Array();
    songsOrdered.push(first);
    var lastId = first[0].id;

    while (songsMap.length != 0){
        for (let i = 0; i < songsMap.length; i++){
            if (songsMap[i][0].idSongBefore == lastId){
                songsOrdered.push(songsMap[i]);
                lastId = songsMap[i][0].id;
                songsMap.splice(i, 1);
                break;
            }
            if (i == songsMap.length - 1){
                setError("Error while sorting playlist songs");
                return;
            }
        }
    }
    return songsOrdered;
}



