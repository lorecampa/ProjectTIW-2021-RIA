(function(){
  
    
    var pageOrchestrator = new PageOrchestrator(); 

    var loginBox, registrationBox, errorMessageBox;
    var loginButton, registrationButton;

    var login, registration, errorMessage;
    

    window.addEventListener("load", ()=>{
      if (sessionStorage.getItem("user") != null){
        window.location.href = "HomePage.html";
      }
      pageOrchestrator.start();
      pageOrchestrator.registerEvent();
      pageOrchestrator.showLoginPage();
      

    })

    
    //error message
    function ErrorMessage(){
      errorMessageBox = document.getElementById("errorMessageBox");
      var status = errorMessageBox.querySelector("h1");
      var message = errorMessageBox.querySelector("p");

      this.show = function(){
        errorMessageBox.style.display = 'block';
      }

      this.set = function(stat, msg){
        status.textContent = stat;
        message.textContent = msg;
      }

      this.hide = function(){
        errorMessageBox.style.display = 'none';
      }

      this.registerEvent = function(){
        errorMessageBox.querySelector("a").addEventListener("click", ()=>{
          pageOrchestrator.showLoginPage();
        })
      }

    }


    //login
    function Login(){
      loginBox = document.getElementById("loginBox");
      loginButton = loginBox.querySelector("input[type='button']");

      this.show = function(){
        loginBox.style.display = 'block';
      }

      this.hide = function(){
        loginBox.style.display = 'none';
        loginBox.querySelector(".customMsg").style.display = 'none';
      }

      this.registerEvent = function(){
        loginButton.addEventListener('click', (e) => {
          makeCall("POST", 'CheckLogin', e.target.closest("form"), function(req){
            buttonCback(req, loginBox);
          });
        });

        loginBox.querySelector("a").addEventListener('click', () =>{
          pageOrchestrator.showRegistrationPage();
        });
      }

    }


    

    //registration
    function Registration(){
      registrationBox = document.getElementById("registrationBox");
      registrationButton = registrationBox.querySelector("input[type='button']");

      this.show = function(){
        registrationBox.style.display = 'block';
      }

      this.hide = function(){
        registrationBox.style.display = 'none';
        registrationBox.querySelector(".customMsg").style.display = 'none';
      }

      this.registerEvent = function(){
        registrationButton.addEventListener('click', (e) => {
          makeCall("POST", 'CreateUser', e.target.closest("form"), function(req){
            buttonCback(req, registrationBox);
          });
        })

        registrationBox.querySelector("a").addEventListener('click', () =>{
          pageOrchestrator.showLoginPage();
        });

      }

    }

    //same cback event for login and registration submit button
    this.buttonCback = function(req, container){
      if (req.readyState == XMLHttpRequest.DONE){
        const msg = req.responseText;
        if (req.status == 200){
          sessionStorage.setItem('user', msg);
          window.location.href = "HomePage.html";
        }else if (req.status == 400){
          pageOrchestrator.showLocalError(container, msg);
        }else{
          pageOrchestrator.showErrorPage(req.status, msg);
        }
      }
    }





    //page controller
    function PageOrchestrator(){
      
      this.start = function(){

        login = new Login();
        registration = new Registration();
        errorMessage = new ErrorMessage();
        

      }

      this.showLoginPage = function(){
        this.reset();
        login.show();
      }

      this.showRegistrationPage = function(){
        this.reset();
        registration.show();
      }

      this.showErrorPage = function(status, msg){
        this.reset();
        errorMessage.set(status, msg);
        errorMessage.show();
      }

      this.showLocalError = function(container, msg){
        var customMsg = container.querySelector(".customMsg");
        customMsg.style.display = 'block';
        customMsg.textContent = msg;
      }

      this.reset = function(){
        login.hide();
        registration.hide();
        errorMessage.hide();
      }

      this.registerEvent = function(){
        login.registerEvent();
        registration.registerEvent();
        errorMessage.registerEvent();
      }

    }


})();