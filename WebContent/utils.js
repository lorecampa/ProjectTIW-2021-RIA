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

    if (formElement != null){
        formElement.reset();
    }
}



