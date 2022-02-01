	
	function googlemapsinitialize(){
		document.body.appendChild(scriptElement);
	}

	function profileEditDetails() {
		$('#profile_save_btn').css('display','block');
		$('#profile_edit_btn').css('display','none');
		//var name=$("#name span").text();
		var mnumber=$("#mobileNumber span").text();
		var address=$("#address span").text();
  
		//document.getElementById("FullName").innerHTML = "<span class='errormsg' id='FullName_Msg'></span><input type='text' class='form-control' id='userfullname' value=''/>";
		document.getElementById("MobileNumber").innerHTML = "<span class='errormsg' id='MobileNumber_Msg'></span><input type='text' class='form-control' id='usermobilenumber' maxlength='10' value=''/>";
		document.getElementById("Address").innerHTML = "<span class='errormsg' id='Address_Msg'></span><input type='text' class='form-control' id='useraddress' value=''/>";
  
		/*$('#userfullname').text(name);
		$('#userfullname').val(name);*/
   
		$('#useraddress').text(address);
		$('#useraddress').val(address);
   
		$('#usermobilenumber').text(mnumber);
		$('#usermobilenumber').val(mnumber);
		
		var input = document.getElementById('useraddress');
		var autocomplete = new google.maps.places.Autocomplete(input);
   
   }
   
    function profileEditValidate(){  
		//var fullName = $('#userfullname').val();
		var phoneNumberRegex=/^\d{10}$/;
		var phone = $('#usermobilenumber').val();
		var address = $('#useraddress').val(); 
		
		var c = 0;  
	
	/*if(fullName==""){
		$('#FullName_Msg').text("Please enter Full Name"); 
		c++;
	}else{  
		$('#FullName_Msg').text(""); 
	}*/
	 
	if(phone==""){
		$('#MobileNumber_Msg').text("Please enter Phone Number"); 
		c++;
	}else if(!phoneNumberRegex.test(phone)){
		$('#MobileNumber_Msg').text("Please enter valid Phone Number");
		c++;
	}else{  
		$('#MobileNumber_Msg').text(""); 
	}
	
	if(address==""){
		$('#Address_Msg').text("Please enter Address"); 
		c++;
	}else{  
		$('#Address_Msg').text(""); 
	}
	
	return c==0;
   }
   