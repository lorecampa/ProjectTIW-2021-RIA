function makeCall(method, url, formElement, cback){
    var req = new XMLHttpRequest();

    req.onreadystatechange = function(){
        if (req.readyState == XMLHttpRequest.DONE){
            cback(req);
        }
    };

    req.open(method, url);
    if (formElement == null){
        req.send();
    }else{
        if (formElement.checkValidity()){
            req.send(new FormData(formElement))
        }else{
            var customMsg = document.querySelector(".customMsg");
            customMsg.textContent = formElement.reportValidity();
            customMsg.style.display = 'block';
        }
    }

    if (formElement != null && !(formElement instanceof FormData)){
        formElement.reset();
    }
}



function handleCustomRedirect(type){
    switch(type){
        case 204:
            window.location.href = "LoginPage.html?type="+type;
        case 401:
            window.location.href = "LoginPage.html?type="+type;
            break;
        case 500:
            window.location.href = "ErrorPage.html?type="+type;
            break;
        default:
            window.location.href = "ErrorPage.html?type="+type;
            break;
    }
}



function getRedirectMessage(){
    const urlParams = new URLSearchParams(window.location.search);
    const numMessage = parseInt(urlParams.get('type'));
    if (isNaN(numMessage)){
        return null;
    }
    switch(numMessage){
        case 204:
            return "Logout succesfully";
        case 400:
            return "Error 400";
        case 401:
            return "Session's over, login again!";          
        case 500:
            return "Internal Server error";
        default:
            return "We encountered some problems";                     
    }
}

