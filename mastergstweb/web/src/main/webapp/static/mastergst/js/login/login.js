     /*---- login form begin -----*/
   $('#loginbtn').click(function(){
	 loginValidate();
   });
  
  function login(){
		if(loginValidate()){
		document.getElementById("loginform").submit();
	}
	}
   
   function loginValidate(){ 
		var phoneNumberRegex=/^\d{10}$/;
		var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			var email = $('#EmailId').val();
			var password = $('#Password').val();		
			var c = 0;  
			
		if(email==""){
			$('#EmailId_Msg').text("Please enter EmailID");
			c++;
		}else if(!emailRegex.test(email)){
			$('#EmailId_Msg').text("Please enter valid EmailID");
			c++;
		}else{
			$('#EmailId_Msg').text("");
		}
		if(password==""){
			$('#Password_Msg').text("Please enter Password"); 
			c++;
		}else{  
			$('#Password_Msg').val(""); 
		}
		return c==0;
  }  

/*------  password request -------*/
 function passwordRequest(){
	if(passwordRequestValidate()){
		document.getElementById("loginform").submit();
	}
   }
   
   function passwordRequestValidate(){
	var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		var emailid = $('#email').val();
		
		var c = 0;  
		
	if(emailid==""){
		$('#email_Msg').text("Please enter EmailID");
		c++;
	}else if(!emailRegex.test(emailid)){
		$('#email_Msg').text("Please enter valid EmailID");
		c++;
	}else{
		$('#email_Msg').text("");
	}
	
	return c==0;
   }
/*---- reset otp password --------*/
  $(".otp_form_input .form-control").keyup(function () {
      if (this.value.length == this.maxLength) {
        $(this).next('.form-control').focus();
      }
   });
  
   
   function validOtp(){
	if(otpValidate()){
	document.location = "reset-password.html";	
	}
   }
   
   function otpValidate(){
		var otp1 = $('#otp1').val();
		var otp2 = $('#otp2').val();
		var otp3 = $('#otp3').val();
		var otp4 = $('#otp4').val();
		var c = 0;  
		
	if(otp1=="" || otp2=="" || otp3=="" || otp4==""){
		$('#otp_Msg').text("Please enter otp");
		c++;
	}else{
		$('#otp_Msg').text("");
	}
	
	return c==0;
   }
   
/*---- reset password begin -----*/

function passwordChange(){
		if(passwordChangeValidation()){
			document.getElementById("resetpwdform").submit();
		}
}

function passwordChangeValidation(){
	var password = $('#password').val();
	var confirmPassword = $('#confirmPassword').val();
	var c = 0; 
	if(password==""){
		$('#password_Msg').text("Please enter Password");
		c++;
	}else{
		$('#password_Msg').text("");
	}
	if(confirmPassword==""){
		$('#confirmPassword_Msg').text("Please enter Password"); 
		c++;
	}else if(confirmPassword != password){
	$('#confirmPassword_Msg').text("Passord is Not matched"); 
		c++;
	}else{  
		$('#confirmPassword_Msg').val(""); 
	}
	return c==0;
}