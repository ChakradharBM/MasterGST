<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
  <head>
	<%@include file="/WEB-INF/views/includes/script.jsp" %>
	<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
	<style>
	.resetpass .with-errors li{margin-top:15px!important}
	</style>
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
			<div class="col-md-4 col-sm-12 m-auto"></div>
            <div class="col-md-4 col-sm-12 m-auto">
               <div class="formbox login-reset">
			   <form:form method="POST" data-toggle="validator" id="loginform" class="meterialform resetpass" name="resetpwdform" action="resetsubmit">
                  <!--<form class="meterialform" id="loginform" name="resetpwdform"  action="resetsubmit" method="post" role="form">-->
                     <div class="whitebg">
                        <h2> Reset Password</h2>
                        <p class="f-12-gy">Please enter your new password to login your account.</p>
						<!-- serverside error begin -->                    
							<div class="errormsg"><c:out value="${error}"/></div> 
						<!-- serverside error end -->    
						
                       
						<div class="labletxt astrich">New Password</div>
                        <div class="form-group"> 
                        <span class="errormsg" id="password_Msg"></span>
                           <input type="password" id="password" name="password" data-minlength="6" required="required" data-error="Please enter more than 6 characters" />
                           <label for="input" class="control-label"></label><div class="help-block with-errors"></div> <i class="bar"></i> 
                        </div>
                        <div class="labletxt astrich"> Confirm New Password</div>
                        <div class="form-group"> 
                        <span class="errormsg" id="confirmPassword_Msg"></span>
                           <input type="password" id="confirmPassword" name="confirmpassword" data-minlength="6" required="required" data-error="Please enter more than 6 characters" />
                           <label for="input" class="control-label"></label><div class="help-block with-errors"></div> <i class="bar"></i> 
                        </div>
                     </div>
                     <div class="bluebg text-center pb-2">
					<input type="hidden" name="hiddenid" value=<c:out value="${id}"/>>
                       <input type="submit" class="btn btn-blue col-12" onClick="passwordChange()" name="enrollbtn" id="enrollbtn" value="Update Password" style="display:block">
					
                     </div>
                  <!--</form>-->
				  </form:form>
               </div>
      			
            </div>
		    <div class="col-md-4 col-sm-12 m-auto"></div>
		 </div>
   		<!-- login form end -->
        </div>
      </div>
    </div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
  </body>
</html> 