const changeToSignup=document.querySelector(".changeToSignup");
const changeToSignin=document.querySelector(".changeToSignin");
const signinSection=document.querySelector(".signinSection");
const signupSection=document.querySelector(".signupSection");
const signupNameBlock = document.querySelector(".signupName");
const signupEmailBlock = document.querySelector(".signupEmail");
const signupPasswordBlock = document.querySelector(".signupPassword");
const signinEmailBlock = document.querySelector(".signinEmail");
const signinPasswordBlock = document.querySelector(".signinPassword");
const sign = document.querySelector(".sign");
const introductionPage = document.querySelector(".introductionPage");
const login = document.querySelector(".login");
const changeToIntroductionButton = document.querySelector(".changeToIntroductionButton");
const signinEmail = document.querySelector(".signinEmail");
const signinPassword = document.querySelector(".signinPassword");

signinEmail.value = "test@test.com";
signinPassword.value = "test1234";

changeToIntroductionButton.addEventListener("click", () => {
    sign.style.display = "none";
    introductionPage.style.display = "block";
});

login.addEventListener("click", () => {
    introductionPage.style.display = "none";
    sign.style.display = "flex";
});

changeToSignup.addEventListener("click", () => {
    signinSection.style.display="none";
    signupSection.style.display="block";
    if(signupResult){
        signupResult.remove();  
    }
    signupNameBlock.value = "";
    signupEmailBlock.value = "";
    signupPasswordBlock.value = "";
})

changeToSignin.addEventListener("click", () => {
    signinSection.style.display="block";
    signupSection.style.display="none";
    if(signinResult){
        signinResult.remove();
    }
    signinEmailBlock.value = "";
    signinPasswordBlock.value = "";
})

// signup API
const signupButton = document.querySelector(".signupButton");
const signupResult = document.createElement("div");
signupResult.setAttribute("class","signupResult");
const emailRegex = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-.]+){1,}$/;
const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/;
signupButton.addEventListener("click", () => {
    const signupName = document.querySelector(".signupName").value;
    const signupEmail = document.querySelector(".signupEmail").value;
    const signupPassword = document.querySelector(".signupPassword").value;
    if(signupName == "" ||signupEmail == "" || signupPassword == ""){
        signupResult.setAttribute("style","color:#FF0000");        
        signupResult.textContent="⚠️ Enter your name, Email and password";    
    }else{
        const emailResult = signupEmail.match(emailRegex);
        const passwordResult = signupPassword.match(passwordRegex);
        if(emailResult == null){
            signupResult.setAttribute("style","color:#FF0000");        
            signupResult.textContent="⚠️ Invalid Email";
        }else if(passwordResult == null){
            signupResult.setAttribute("style","color:#FF0000");        
            signupResult.textContent="⚠️ Password need to be 6-20 chars, at least one letter and one number!";
        }else{
            const addMember = { 
                "name":signupName,
                "email":signupEmail,
                "password":signupPassword
            };  
            fetch("/api/user",{
                method:"POST",
                body:JSON.stringify(addMember),
                cache:"no-cache",
                headers:new Headers({
                    "content-type":"application/json"
                })
            }).then(function(response){
                return response.json();
            }).then(function(data){ 
                if(data.error == true){
                    signupResult.setAttribute("style","color:#FF0000");        
                    signupResult.textContent = data.message; 
                }  
                if(data.ok == true){
                    signupResult.setAttribute("style","color:#008000");           
                    signupResult.textContent = "Welcome ...";  
                    setTimeout(() => {
                        location.href="/";
                    },2000)
                }                     
            })
        }        
    }
    const changeToSignin = document.querySelector(".changeToSignin");
    signupSection.insertBefore(signupResult,changeToSignin);      
})

// signin API
const signinButton = document.querySelector(".signinButton");
const signinResult = document.createElement("div");
signinResult.setAttribute("class","signinResult");
signinButton.addEventListener("click", () => {
    const signinEmail = document.querySelector(".signinEmail").value;
    const signinPassword = document.querySelector(".signinPassword").value;
    if(signinEmail == ""|| signinPassword == ""){
        signinResult.setAttribute("style","color:#FF0000");        
        signinResult.textContent = "⚠️ Enter your Email and password";
        signinSection.insertBefore(signinResult,changeToSignup);  
    }else{
        const member = { 
            "email":signinEmail,
            "password":signinPassword
        }; 
        fetch("/api/user",{
            method:"PUT",
            body:JSON.stringify(member),
            cache:"no-cache",
            headers:new Headers({
                "content-type":"application/json"
            })
        }).then(function(response){
            console.log(response);
            if(response.status == 200){
                location.href="/";
            }
            return response.json();
        }).then(function(data){   
            if(data.error == true){
                signinResult.setAttribute("style","color:#FF0000");        
                signinResult.textContent = data.message;  
                signinSection.insertBefore(signinResult,changeToSignup);  
            }
            if(response.ok == true){
                location.href="/";
            }
        })
    }
})

// get signin status/information API
fetch("/api/user").then(function(response) {
    return response.json();
}).then(function(data) {
    if (data.data == null) {
        return;
    } else {
        location.href="/"
    }
});