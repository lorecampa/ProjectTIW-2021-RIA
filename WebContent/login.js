(function(){

    var errorMessageBox = document.getElementById("errorMessageBox");
    
    var loginBox = document.getElementById("loginBox");
    var loginButton = loginBox.querySelector("input[type='button']");

    var registrationBox = document.getElementById("registrationBox");
    var registrationButton = registrationBox.querySelector("input[type='button']");

    window.addEventListener("load", ()=>{
      if (sessionStorage.getItem("user") != null){
        window.location.href = "HomePage.html";
      }
      
      this.showLoginPage();
      this.registerEvent();

    })

    this.buttonCback = function(req, container){
      if (req.readyState == XMLHttpRequest.DONE){
        const msg = req.responseText;
        if (req.status == 200){
          sessionStorage.setItem('user', msg);
          window.location.href = "HomePage.html";
        }else if (req.status == 400){
          this.showLocalError(container, msg);
        }else{
          this.showErrorPage(req.status, msg);
        }
      }
    }

    this.registerEvent = function(){
      errorMessageBox.querySelector("a").addEventListener("click", ()=>{
        this.showLoginPage();
      })

      loginBox.querySelector("a").addEventListener('click', () =>{
        this.showRegistrationPage();
      });
  
      registrationBox.querySelector("a").addEventListener('click', () =>{
        this.showLoginPage();
      });
      
  
      loginButton.addEventListener('click', (e) => {
        makeCall("POST", 'CheckLogin', e.target.closest("form"), function(req){
          buttonCback(req, loginBox);
        });
      });
  
      registrationButton.addEventListener('click', (e) => {
        makeCall("POST", 'CreateUser', e.target.closest("form"), function(req){
          buttonCback(req, registrationBox);
        });
      })

    }


    this.showLoginPage = function(){
      this.reset();
      loginBox.style.display = 'block';
    }
    this.showRegistrationPage = function(){
      this.reset();
      registrationBox.style.display = 'block';
    }
    this.showErrorPage = function(status, msg){
      this.reset();
      errorMessageBox.style.display = 'block';
      errorMessageBox.querySelector("h1").textContent = status;
      errorMessageBox.querySelector("p").textContent = msg;
    }

    this.showLocalError = function(container, msg){
      var customMsg = container.querySelector(".customMsg");
      customMsg.style.display = 'block';
      customMsg.textContent = msg;
    }

    this.reset = function(){
      Array.from(document.querySelectorAll(".customMsg")).forEach(x => x.style.display = 'none');
      errorMessageBox.style.display = 'none';
      loginBox.style.display = 'none';
      registrationBox.style.display = 'none';
    }


})();