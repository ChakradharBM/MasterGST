/*---- reset otp  --------*/
  $(".otp_form_input .form-control").keyup(function () {
      if (this.value.length == this.maxLength) {
        $(this).next('.form-control').focus();
      }
   });
  
   
   function validOtp(){
	if(otpValidate()){
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
   