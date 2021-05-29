(function(){

    var errorMessage = document.getElementById("errorMessage");
    var loginDiv = document.getElementById("loginDiv");
    var loginButton = document.getElementById("loginButton");
    var registrationDiv = document.getElementById("registrationDiv");
    registrationDiv.hidden = true;
    var registrationButton = document.getElementById("registerButton");

    window.addEventListener("load", ()=>{
      if (sessionStorage.getItem("user") != null){
        window.location.href = "HomePage.html";
      }
    })

    function cBack(req){
        if (req.readyState == XMLHttpRequest.DONE){
            const msg = req.responseText;
            switch (req.status) {
              case 200:
            	  sessionStorage.setItem('user', msg);
                window.location.href = "HomePage.html";
                break;
              case 400:
              case 401:
              case 500:      
                errorMessage.textContent = msg;
                break;
            }
        }
    }


    document.getElementById("goToRegister").addEventListener('click', () =>{
      loginDiv.style.display = 'none';
      registrationDiv.style.display = 'block';
    });

    document.getElementById("goToLogin").addEventListener('click', () =>{
      loginDiv.style.display = 'block';
      registrationDiv.style.display = 'hidden';
    });


    loginButton.addEventListener('click', (e) => {
      var form = e.target.closest("form");
      if (form.checkValidity()){
        makeCall("POST", 'CheckLogin', form, cBack);
      }else{
        form.reportValidity();
      }
    });

    registrationButton.addEventListener('click', (e) => {
      var form = e.target.closest("form");
      if (form.checkValidity()){
        makeCall("POST", 'CreateUser', form, cBack);
      }else{
        form.reportValidity();
      }
    })

})();