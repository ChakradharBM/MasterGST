<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>

	<%@include file="/WEB-INF/views/includes/script.jsp" %>
	<script type="text/javascript"> 
	var userid= '<c:out value="${id}"/>';
	$(function () {
		$(".otp_form_input .form-control").keyup(function () {
			if (this.value.length == this.maxLength) {
				$(this).next('.form-control').focus();
			}
		});
	});
    function otptry() {
		$.ajax({
			type : "POST",
			url : "otptryagian",
			data:{
				'userid': userid
				},
			dataType : 'text',
			timeout : 100000,
			success : function(data) {
			$('#otp_Msg1').html('OTP has been sent to you on your mobile number and registered email');
			},
		});
        return false;
    }
	function move(txt) {
		   var value = txt.value;
           var length = txt.value.length

           if (length == 0) {
               $(txt).prev().focus();
               return false;
           }
           else if (length > 4) {
               $(txt).next().focus().val(value.substr(4, 1));
               $(txt).val(value.substr(0, 4));
               return false;
           }
       }
	function updateSpinner(btn) {
		var otp1 = $('#otp1').val();
		var otp2 = $('#otp2').val();
		var otp3 = $('#otp3').val();
		var otp4 = $('#otp4').val();
		if(otp1 && otp2 && otp3 && otp4){
			$('#otpready').val("yes");
			$('#otp_Msg').html('');
			$(btn).addClass('btn-loader');
		}else {
			$('#otp_Msg').html('Please Enter Otp');
			$('#otp_Msg').css('margin-bottom','0px')
		}
	}
	
	function submitotp(){
		return $('#otpready').val() == 'yes';
	}
	
	</script>
</head>
  
  <body>
    <!-- header page begin -->
    <%@include file="/WEB-INF/views/includes/login_header.jsp" %>
    <!-- header page end -->
	
    <div class="bodywrap">
      <div class="container">
        <div class="row">
          <!-- login form begin -->
          <div class="formboxwrap">
            <h3> Filing GST Made Simple, & Pay your Tax easily </h3>
            <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
			<div class="col-md-3 col-sm-12 m-auto"></div>
            <div class="col-md-6 col-sm-12 m-auto">
              <div class="formbox">
              <input type="hidden" name="otpready" id="otpready" value="no"/>
          	 <form:form method="POST" class="meterialform" name="otpform" action="otpsubmit" modelAttribute="otp" onsubmit="return submitotp()">
                  <div class="whitebg otp">
				  <c:choose>
					  <c:when test="${initialSignup eq 'no'}">
						<h2> You Haven't Verfied Your Mobile Number, Please Verify Mobile Number</h2>	
					  </c:when>
					  <c:otherwise>
						<h2> Verify Mobile Number</h2>
					  </c:otherwise>
					</c:choose>
                    <h6>OTP has been sent to you on your mobile number and also to registered email.
                      Please enter it below
                    </h6>
                   <!-- serverside error begin -->                    
                    <span class="errormsg" id="tryerror"  style="margin-top:-18px!important"><c:out value="${error}"/></span> 
					<span class="errormsg" id="otp_Msg" style="margin-top:0px!important;margin-bottom:0px;"></span> 
                    <!-- serverside error end -->    
					<div class="col-sm-12 otp_form_input">
					  <div class="group upload-btn">
                        <div class="group upload-btn"></div>
                        <input type="text" class="form-control" id="otp1" name="otp1" required="required" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" onKeyUp="javascript:return move(this)"  maxlength="1" tabindex="1" placeholder="0" />
                        <input type="text" class="form-control" id="otp2" name="otp2" required="required" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" onKeyUp="javascript:return move(this)"  maxlength="1" tabindex="2" placeholder="0"/>
                        <input type="text" class="form-control" id="otp3" name="otp3" required="required" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" onKeyUp="javascript:return move(this)"  maxlength="1" tabindex="3" placeholder="0"/>
                        <input type="text" class="form-control" id="otp4" name="otp4" required="required" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" onKeyUp="javascript:return move(this)"  maxlength="1" tabindex="4" placeholder="0"/>
                      </div>
					  
					 
                      <h6>Didn&acute;t  Receive OTP?&nbsp;<a href="#" id="link" onClick="otptry(); return false;">try again</a></h6>
					  <div class="" id="otp_Msg1" style="margin-top:0px!important;color:green;font-size: 12px; text-align: center;height:20px"></div>
                    </div>
                  </div>
                  <div class="bluebg">
				  <p>
				   <input type="hidden" name="hiddenid" value=<c:out value="${id}"/>>
				   <input type="hidden" name="source" value=<c:out value="${source}"/>>
				   <input type="hidden" name="subscrid" value="<c:out value="${subscrid}"/>">
				   <input type="submit" class="btn btn-lg btn-blue" onClick="updateSpinner(this)" value="Verify My Number"/>
                   </p>
                  </div>
				 
               </form:form>
              </div>
            </div>
            <div class="col-md-3 col-sm-12 m-auto"></div>
		  </div>
          <!-- login form end -->
        </div>
      </div>
    </div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
  </body>
  <script type="text/javascript">
  var tme = setInterval(getOtpStatus, 2000);
  function getOtpStatus(){
  $.ajax({
		url: '${contextPath}/otpstatus?userId='+userid, 
		method: 'GET',
		cache : false,
		success: function(result, textSt, xhr){
			if(result.status == 'success'){
				$('#otpready').val('yes');
				clearTimeout(tme);
			}else if(result.status == 'error'){
				$('#tryerror').html('Problem in generating OTP');
				clearTimeout(tme);
			}else if(result.status == 'init'){
				
			}
		},
		error: function(result){}
	});
  }
  
  </script>
</html> 
 