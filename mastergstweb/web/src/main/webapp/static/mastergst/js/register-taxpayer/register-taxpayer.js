   function registertp(){
	if(regValidate()){
		$('#registertp').attr('href',"otp.html");
	}
   }
   
   function regValidate(){  
		var firstName = $('#firstNameId').val();
		var lastName = $('#lastNameId').val();
		var panTin = $('#panTinId').val();
		var userName = $('#userNameId').val();
		var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		var email = $('#emailId').val();
		var password = $('#passwordId').val();
		var confirmPassword = $('#confirmPasswordId').val();
		var phoneNumberRegex=/^\d{10}$/;
		var phone = $('#mobileId').val();
		var address = $('#addressId').val();
		var city = $('#cityId').val();
		var state = $('#stateId').val();
		var pincode = $('#pincodeId').val();
		var c = 0;  
	
	if(firstName==""){
		$('#firstNameId_Msg').text("Please enter First Name"); 
		c++;
	}else{  
		$('#firstNameId_Msg').val(""); 
	}
	 
	 if(lastName==""){
		$('#lastNameId_Msg').text("Please enter Last Name"); 
		c++;
	}else{  
		$('#lastNameId_Msg').val(""); 
	}
	
	 if(panTin==""){
		$('#panTinId_Msg').text("Please enter PAN / TIN Number"); 
		c++;
	}else{  
		$('#panTinId_Msg').val(""); 
	}
	
	 if(userName==""){
		$('#userNameId_Msg').text("Please enter User Name"); 
		c++;
	}else{  
		$('#userNameId_Msg').val(""); 
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
	if(confirmPassword==""){
		$('#confirmPasswordId_Msg').text("Please enter Password"); 
		c++;
	}else if(confirmPassword != password){
	$('#confirmPasswordId_Msg').text("Passord is Not matched"); 
		c++;
	}else{  
		$('#confirmPasswordId_Msg').val(""); 
	}
	
	if(phone==""){
		$('#mobileId_Msg').text("Please enter Phone Number"); 
		c++;
	}else{  
		$('#mobileId_Msg').val(""); 
	}
	
   	if(address==""){
		$('#addressId_Msg').text("Please enter Address"); 
		c++;
	}else{  
		$('#addressId_Msg').val(""); 
	}
	
	if(city==""){
		$('#cityId_Msg').text("Please enter City Name"); 
		c++;
	}else{  
		$('#cityId_Msg').val(""); 
	}

	if(state==""){
		$('#stateId_Msg').text("Please enter State Name"); 
		c++;
	}else{  
		$('#stateId_Msg').val(""); 
	}
	
		if(pincode==""){
		$('#pincodeId_Msg').text("Please enter Pincode Number"); 
		c++;
	}else{  
		$('#pincodeId_Msg').val(""); 
	}
	
	return c==0;
   }
   