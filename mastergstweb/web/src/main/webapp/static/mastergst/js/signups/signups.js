/*---- click on boxes get path of html -----*/ 

/*--- signup validation----*/
function enrollment(){
	if(signupValidate()){
		$('#enrollment').attr('href',"otp.html");
	}
   }   
   function signupValidate(){  
		var fullName = $('#fullNameId').val();
		var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		var email = $('#emailId').val();
		var password = $('#passwordId').val();
		var phoneNumberRegex=/^\d{10}$/;
		var phone = $('#mobileId').val();
		var address = $('#addressId').val();
		var city = $('#cityId').val();
		var state = $('#stateId').val();
		var noOfClients = $('#numberOfClientsId').val();
	 
		
		var c = 0;  
	
	if(fullName==""){
		$('#fullNameId_Msg').text("Please enter Full Name"); 
		c++;
	}else{  
		$('#fullNameId_Msg').text(""); 
	}
	 
	if(email==""){
		$('#emailId_Msg').text("Please enter EmailID");
		c++;
	}else if(!emailRegex.test(email)){
		$('#emailId_Msg').text("Please enter valid EmailID");
		c++;
	}else{
		$('#emailId_Msg').text("");
	}
	
		if(password==""){
		$('#passwordId_Msg').text("Please enter Password"); 
		c++;
	}else{  
		$('#passwordId_Msg').text(""); 
	}
	
		if(phone==""){
		$('#mobileId_Msg').text("Please enter Phone Number"); 
		c++;
	}else{  
		$('#mobileId_Msg').text(""); 
	}
	
	if(city==""){
		$('#cityId_Msg').text("Please enter City Name"); 
		c++;
	}else{  
		$('#cityId_Msg').text(""); 
	}
	if(address==""){
		$('#addressId_Msg').text("Please enter Address"); 
		c++;
	}else{  
		$('#addressId_Msg').text(""); 
	}
	if(state==""){
		$('#stateId_Msg').text("Please enter State Name"); 
		c++;
	}else{  
		$('#stateId_Msg').text(""); 
	}
	
	if(noOfClients==""){
		$('#numberOfClientsId_Msg').text("Please enter Number Of Clients"); 
		c++;
	}else{  
		$('#numberOfClientsId_Msg').text(""); 
	}
	
	return c==0;
   }
   