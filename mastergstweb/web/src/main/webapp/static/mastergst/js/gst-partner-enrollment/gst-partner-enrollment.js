/*-----google map --*/
/*function initialize() {
	var input = document.getElementById('addressId');
	var autocomplete = new google.maps.places.Autocomplete(input);
}

google.maps.event.addDomListener(window, 'load', initialize);*/

/*--- signup validation----*/
function enrollment(){
	if(signupValidate()){
		$('#enrollment').attr('href',"otp.html");
	}
   }
   
   function signupValidate(){  
		var fullName = $('#fullNameId_Msg').val();
		var emailRegex = /^[A-Za-z0-9._]*\@[A-Za-z]*\.[A-Za-z]{2,5}$/;
		var email = $('#emailId').val();
		var password = $('#passwordId').val();
		var phoneNumberRegex=/^\d{10}$/;
		var phone = $('#mobileId').val();
		var address = $('#addressId').val();
		var city = $('#cityId').val();
		var state = $('#stateId').val();
		 
		
		var c = 0;  
	
	if(fullName==""){
		$('#fullNameId_Msg').text("Please enter Full Name"); 
		c++;
	}else{  
		$('#fullNameId_Msg').val(""); 
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
		$('#passwordId_Msg').val(""); 
	}
	
		if(phone==""){
		$('#mobileId_Msg').text("Please enter Phone Number"); 
		c++;
	}else{  
		$('#mobileId_Msg').val(""); 
	}
	
	if(city==""){
		$('#cityId_Msg').text("Please enter City Name"); 
		c++;
	}else{  
		$('#cityId_Msg').val(""); 
	}
	if(address==""){
		$('#addressId_Msg').text("Please enter Address"); 
		c++;
	}else{  
		$('#addressId_Msg').val(""); 
	}
	if(state==""){
		$('#stateId_Msg').text("Please enter State Name"); 
		c++;
	}else{  
		$('#stateId_Msg').val(""); 
	}
	return c==0;
   }
   