 
/*------  email request -------*/
 function passwordRequest(){
	if(passwordRequestValidate()){
		document.location = "otp.html";	
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
 