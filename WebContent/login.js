(function(){

    var errorMessage = document.getElementById("errorMessage");
    
    var loginBox = document.getElementById("loginBox");
    var loginButton = loginBox.querySelector("input[type='button']");

    var registrationBox = document.getElementById("registrationBox");
    var registrationButton = registrationBox.querySelector("input[type='button']");

    window.addEventListener("load", ()=>{
      if (sessionStorage.getItem("user") != null){
        window.location.href = "HomePage.html";
      }else{
        this.showLoginPage();
        this.registerEvent();
      }
    })

    function cBack(req){
        if (req.readyState == XMLHttpRequest.DONE){
            const msg = req.responseText;
            if (req.status == 200){
              sessionStorage.setItem('user', msg);
              window.location.href = "HomePage.html";
            }else{
              errorMessage.textContent = msg
            }
        }
    }

    this.registerEvent = function(){
      loginBox.querySelector("a").addEventListener('click', () =>{
        this.showRegistrationPage();
      });
  
      registrationBox.querySelector("a").addEventListener('click', () =>{
        this.showLoginPage();
      });
  
  
      loginButton.addEventListener('click', (e) => {
        makeCall("POST", 'CheckLogin', e.target.closest("form"), cBack);
      });
  
      registrationButton.addEventListener('click', (e) => {
        makeCall("POST", 'CreateUser', e.target.closest("form"), cBack);
      })

    }


    this.showLoginPage = function(){
      loginBox.style.display = 'block';
      registrationBox.style.display = 'none';
    }
    this.showRegistrationPage = function(){
      loginBox.style.display = 'none';
      registrationBox.style.display = 'block';

    }


})();