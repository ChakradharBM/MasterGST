<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | India's Best GST Software | Signup</title>
<%@include file="/WEB-INF/views/includes/script.jsp" %>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/signups/google-address.js" type="text/javascript"></script>
<!-- Hotjar Tracking Code for https://app.mastergst.com/ -->
<script>
    (function(h,o,t,j,a,r){
        h.hj=h.hj||function(){(h.hj.q=h.hj.q||[]).push(arguments)};
        h._hjSettings={hjid:2765016,hjsv:6};
        a=o.getElementsByTagName('head')[0];
        r=o.createElement('script');r.async=1;
        r.src=t+h._hjSettings.hjid+j+h._hjSettings.hjsv;
        a.appendChild(r);
    })(window,document,'https://static.hotjar.com/c/hotjar-','.js?sv=');
</script>
<c:set var="typeheader" value="${typeheader}" />
<style>
#enrollbtn :hover {color:#8ee3fe}
body{padding-bottom: 3rem!important;;min-height:90%;}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/includes/login_header.jsp" %>
<c:set var="userCenter" value="<%=MasterGSTConstants.SUVIDHA_CENTERS%>"/>
<div class="bodywrap">
  <div class="container">
    <div class="formboxwrap" >
      
      <div class="row">
        <div class="col-md-5 col-sm-12 push-1">
          <!-- enrollment form begin -->
          <div class="formbox">
            <form:form method="POST" data-toggle="validator" class="meterialform" name="signupform" action="createuser" modelAttribute="user">
              <div class="whitebg">
                <h2>${typeheader}</h2>
                <!-- serverside error begin -->
                <div class="errormsg">
                  <c:out value="${error}"/>
                </div>
                <!-- serverside error end -->
                <c:choose>
                  <c:when test="${not empty nametext}">
                    <div class="labletxt astrich">
                      <c:out value="${nametext}"/>
                    </div>
                    <div class="form-group"> <span class="errormsg" id="fullNameId_Msg"></span>
                      <input type="text" id="businessNameId"  name="fullname" data-minlength="3" data-error="Please enter more than 3 characters" required="required" aria-describedby="businessName" placeholder="Jane Smith" value='<c:out value="${user.fullname}"/>' onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" >
                      
                      <label for="input" class="control-label"></label>
                      <div class="help-block with-errors"></div>
                      <i class="bar"></i> </div>
                  </c:when>
                  <c:otherwise>
                    <div class="labletxt astrich">Full Name</div>
                    <div class="form-group"> <span class="errormsg" id="firstNameId_Msg"></span>
                      <input type="text" id="firstNameId"  name="fullname" data-minlength="3" data-error="Please enter more than 3 characters" required="required" aria-describedby="firstName" placeholder="Jane Smith" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value=<c:out value="${user.fullname}"/> >
                      
                      <label for="input" class="control-label"></label>
                      <div class="help-block with-errors"></div>
                      <i class="bar"></i> </div>
                  </c:otherwise>
                </c:choose>
                <div class="labletxt astrich">Email id</div>
                <div class="form-group"> <span class="errormsg" id="emailId_Msg"></span>
                  <input type='email' required='required' id='emailId' name='email' pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' data-error="Please enter valid email" aria-describedby="email" placeholder="Jane Smith@mycompany.com" value=<c:out value="${user.email}"/>>
                  
                  <label for="input" class="control-label"></label>
                  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
                <div class="labletxt astrich">Password</div>
                <div class="form-group"> <span class="errormsg" id="passwordId_Msg"></span>
                  <input type="password" required="required" id="passwordId" name="password" data-minlength="6" data-error="Please enter more than 6 characters" aria-describedby="password" placeholder="12345" value=<c:out value="${user.password}"/>>
                  
                  <label for="input" class="control-label"></label>
                  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
                <div class="labletxt astrich">Mobile Number</div>
                <div class="form-group"> <span class="errormsg" id="mobileId_Msg"></span>
                  <!-- <p class="indiamobilecode">+91</p>  -->
                  <input type="Tel" required="required" name="mobilenumber" id="phone" pattern="[0-9]+" data-minlength="10" maxlength="10" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57))" data-error="Please enter valid mobile number" id="mobileId" placeholder="9000000001" value=<c:out value="${user.mobilenumber}"/>>
  					<script>
    				$("#phone").intlTelInput({"initialCountry":"in"});
  					</script>
                  <label for="input" class="control-label"></label>
                  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
                <c:if test="${registrationnumber=='yes'}">
                  <div class="labletxt">Membership Number</div>
                  <div class="form-group"> <span class="errormsg" id="registrationNumberId_Msg"></span>
                    <input type="text" name="registrationnumber" id="registrationNumberId" placeholder="125435879" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=<c:out value="${user.registrationnumber}"/>>
                    
                    <label for="input" class="control-label"></label>
                    <i class="bar"></i> </div>
                </c:if>
                <div class="labletxt astrich">Address</div>
                <div class="form-group"> <span class="errormsg" id="addressId_Msg"></span>
                  <input type="text" required="required" name="address" id="addressId" data-error="Please enter the valid address" class="mapicon" placeholder="2-5-67, Madhapur" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode >= 44 && event.charCode <= 47) || (event.charCode == 158) || (event.charCode == 32))" value=<c:out value="${user.address}"/> >
                  
                  <label for="input" class="control-label"></label>
				  <div class="help-block with-errors"></div>
                  <i class="bar"></i> </div>
				  <c:if test="${'ASP/Developer Enrollment' eq typeheader}">
				  <div class="row">
				  <div class="labletxt col-md-12" style="padding-right:0px!important">Signing up for : </div>
					<div class="form-group-inline col-md-12 mb-3">
                    <div class="form-checkbox meterialform">
                      <div class="checkbox">
                        <label>
                        <input name="ewaybillApi" id="ewaybillApis" type="checkbox" value="EwaybillApi">
                        <i class="helper"></i>Eway-Bill API's</label>
                      </div>
                    </div>
                    <div class="form-checkbox meterialform">
                      <div class="checkbox">
                        <label>
                        <input name="gstApi" id="gstApis" type="checkbox" value="GSTApi">
                        <i class="helper"></i>GST API's</label>
                      </div>
                    </div>
                     <div class="form-checkbox meterialform">
                      <div class="checkbox">
                        <label>
                        <input name="einvoiceApi" id="einvoiceApis" type="checkbox" value="EinvoiceApi">
                        <i class="helper"></i>E-invoice API's</label>
                      </div>
                    </div>
                  </div>
				  </div>
				  </c:if>
				  
                <c:if test="${not empty erptext}">
                  <div class="labletxt">
                    <c:out value="${erptext}"/>
                  </div>
                  <div class="form-group"> <span class="errormsg" id="anyERPId_Msg"></span>
                    <input type="text"  name="anyerp" id="anyERPId" placeholder="ERPs" value=<c:out value="${user.anyerp}"/>>
                    
                    <label for="input" class="control-label"></label>
                    <i class="bar"></i> </div>
                  <div class="labletxt">Has any Branches</div>
                  <div class="form-group-inline">
                    <div class="form-radio">
                      <div class="radio">
                        <label>
                        <input name="branches" id="branches" type="radio" value="Yes">
                        <i class="helper"></i>Yes</label>
                      </div>
                    </div>
                    <div class="form-radio">
                      <div class="radio">
                        <label>
                        <input name="branches" id="branches" type="radio" value="No">
                        <i class="helper"></i>No</label>
                      </div>
                    </div>
                  </div>
                </c:if>
                <c:if test="${not empty nooftext}">
                  <div id="noOfBranches" style="display:none;">
                    <div class="labletxt">
                      <c:out value="${nooftext}"/>
                    </div>
                    <div class="form-group"> <span class="errormsg" id="numberOfClientsId_Msg"></span>
                      <input type="text" name="numberofclients" pattern="[0-9]+" id="numberOfClientsId" placeholder="150" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=<c:out value="${user.numberofclients}"/>>
                      <label for="input" class="control-label"></label>
                      <div class="help-block with-errors"></div>
                      <i class="bar"></i> </div>
                  </div>
                </c:if>
                <div class="form-group">
                  <input type="hidden" name="usertype" value="<c:out value="${usertype}"/>">
                  <input type="hidden" name="inviteId" value="<c:out value="${inviteId}"/>">
                  <input type="hidden" name="subscrid" value="<c:out value="${subscrid}"/>">
                  <input type="hidden" name="partnerType" value="Silver Partner">
                  <c:if test="${usertype eq userCenter}">
                    <input type="hidden" name="disable" value="true">
                  </c:if>
                  <br>
                  <input type="submit" class="btn btn-darkblue col-12" name="enrollbtn" id="enrollbtn" value="SUBMIT" onClick="updateSpinner(this)" style="height:34px">
                </div>
                <div class="form-group f-12 text-center"> By creating an account, you accept our <a href="#" onClick="window.open('terms','toolbar=no,scrollbars=yes,resizable=yes,top=300,left=300,width=500,height=500')">Terms</a> & <a href="#" onClick="window.open('privacy','toolbar=no,scrollbars=yes,resizable=yes,top=300,left=300,width=500,height=500')">Privacy.</a> </div>
              </div>
            </form:form>
          </div>
          <!-- enrollment form end -->
        </div>
        <div class="col-md-1 col-sm-12"> </div>
        <div class="col-md-6 col-sm-12 pt-5 pl-5">
          <!-- enrollment form right side begin -->
          <c:if test="${'CA/CMA/CS/Tax Professional Enrollment' eq typeheader}">
        <h1 style="font-size: 1.5rem;text-align:center;color:#8a8b8b;"> India's Best GST Software, We Simplify GST Return Filing </h1>
        <h5> TRUSTED BY MOST CA's AND COMPANIES NATIONALLY </h5>
      </c:if>
      <c:if test="${'Suvidha Centers' eq typeheader}">
        <h1 style="font-size: 1.5rem;text-align:center;color:#8a8b8b;"> MasterGST Suvidha Kendra (Center) is Your Own Business </h1>
        <h5>INCREASE YOUR NETWORK AND GROW YOUR BUSINESS </h5>
      </c:if>
      <c:if test="${'Small/Medium Business Enrollment' eq typeheader}">
        <h1 style="font-size: 1.5rem;text-align:center;color:#8a8b8b;"> India's Best GST Software, We Simplify GST Return Filing </h1>
        <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
      </c:if>
      <c:if test="${'Enterprise Enrollment' eq typeheader}">
        <h1 style="font-size: 1.5rem;text-align:center;color:#8a8b8b;"> India's Best GST Software, We Simplify GST Return Filing </h1>
        <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
      </c:if>
      <c:if test="${'Partner Enrollment' eq typeheader}">
        <h1 style="font-size: 1.5rem;text-align:center;color:#8a8b8b;">Be A Trusted Channel Partner of MasterGST GST Software </h1>
        <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
      </c:if>
      <c:if test="${'ASP/Developer Enrollment' eq typeheader}">
        <h1 style="font-size: 1.5rem;text-align:center;color:#8a8b8b;">India's Best GST API Developers Portal and Licensed GSP (GST Suvidha Provider) </h1>
        <h5>TRUSTED BY MOST DEVELOPERS, EPR's, POS AND ACCOUNTING SOFTWARE COMPANIES NATIONALLY</h5>
      </c:if>
		  <div class="row mt-5">      
             
            <div class="col-sm-12">
              <div class="img_abt_txt">
                <c:if test="${'CA/CMA/CS/Tax Professional Enrollment' eq typeheader}">
                  <ul>
                    <li>One Stop to Manage all your clients/ Business / GSTNs</li>
                    <li> Simple 3 Clicks to File GST Returns</li>
                    <li> Automatic Reconciliation and Shows Mismatch of invoices</li>
                    <li> Track by invoice wise status</li>
                    <li> Bulk Data Import and Export Errors to Excel</li>
                    <li> Role Based and Real Time Data access</li>
                    <li> Master Data Management</li>
                  </ul>
                </c:if>
                  <c:if test="${'Suvidha Centers' eq typeheader}">
              <ul>
                <li> Open Authorized Suvidha Kendra</li>
                <li> Earn up to 1,00,000 per month</li>
                <li> Easy 3 Steps to File GST Return </li>
                <li> Manage All Kendra's at Single place</li>
                <li> Center Wise Reports </li>
                <li> Grow your business</li>
              </ul>
            </c:if>
                <c:if test="${'Small/Medium Business Enrollment' eq typeheader}">
                  <ul>
                    <li>Manage Multiple Branches /Verticals</li>
                    <li> Avoid Penalties</li>
                    <li> Centralized Monitoring</li>
                    <li> Centralized Monitoring</li>
                    <li> Single Point of Access</li>
                    <li> Role based Access management</li>
                    <li> Real Time Invoices Sync</li>
                    <li> Approval Based Filing</li>
                  </ul>
                </c:if>
                <c:if test="${'Enterprise Enrollment' eq typeheader}">
                  <ul>
                    <li>Manage Multiple Branches /Verticals</li>
                    <li> Centralized Monitoring</li>
                    <li> Single Point of Access</li>
                    <li> Role based Access management</li>
                    <li> Real Time Invoices Sync</li>
                    <li> Approval Based Filing</li>
                  </ul>
                </c:if>
                <c:if test="${'Partner Enrollment' eq typeheader}">
                  <ul>
                    <li>Bring 100 Clients and make 25% Revenue Share</li>
                    <li>Earn minimum of  Rs.1,00,000 per month</li>
                    <li>One click to Invite your clients</li>
                    <li>Grow your network by share with Face Book,  Linked-in, Twitter and Social media</li>
                  </ul>
                </c:if>
                <c:if test="${'ASP/Developer Enrollment' eq typeheader}">
                  <ul>
                    <li>Developer friendly API Documentation</li>
                    <li> GST API Technical Support</li>
                    <li> GST API Usage Dashboard & Errors</li>
                    <li> Self Signup and GST API Sandbox Access</li>
                    <li> Simplified Billing</li>
                  </ul>
                </c:if>
              </div>
            </div>
          </div>
         <!-- <div class="row mt-5">
            <div class="col-sm-2">
              <div class="img_abt">
                <c:if test="${'Suvidha Centers' ne typeheader}"> <img src="${contextPath}/static/mastergst/images/gst-partner-enrollment/icon2.png" alt="icon" /> </c:if>
              </div>
            </div>
            <div class="col-sm-10">
              <div class="img_abt_txt">
                <c:if test="${'CA/CMA/CS/Tax Professional Enrollment' eq typeheader}">
                  <p>Integrated & low fee payments to pay Companies easily</p>
                </c:if>
                <c:if test="${'Small/Medium Business Enrollment' eq typeheader}">
                  <p>Integrated & low fee payments to pay Companies easily</p>
                </c:if>
                <c:if test="${'Enterprise Enrollment' eq typeheader}">
                  <p>Integrated & low fee payments to pay Companies easily</p>
                </c:if>
                <c:if test="${'Partner Enrollment' eq typeheader}">
                  <p>Integrated & low fee payments to pay Companies easily</p>
                </c:if>
                <c:if test="${'ASP/Developer Enrollment' eq typeheader}">
                  <p>Integrated & low fee payments to pay Companies easily</p>
                </c:if>
              </div>
            </div>
          </div> -->
          <div class="comentswrap">
            
              <div class="comentsbox">
                <c:if test="${'CA/CMA/CS/Tax Professional Enrollment' eq typeheader}">
                  <p> MasterGST makes it so simple to manage. No more spreadsheets, payment headaches, or compliance worries.</p>
                </c:if>
				<c:if test="${'Suvidha Centers' eq typeheader}">
                  <p> MasterGST makes it so simple to manage. No more spreadsheets, payment headaches, or compliance worries.</p>
                </c:if>
                <c:if test="${'Small/Medium Business Enrollment' eq typeheader}">
                  <p> MasterGST makes it so simple to manage. No more spreadsheets, payment headaches, or compliance worries.</p>
                </c:if>
                <c:if test="${'Enterprise Enrollment' eq typeheader}">
                  <p> MasterGST makes it so simple to manage. No more spreadsheets, payment headaches, or compliance worries.</p>
                </c:if>
                <c:if test="${'Partner Enrollment' eq typeheader}">
                  <p> MasterGST makes it so simple to manage. No more spreadsheets, payment headaches, or compliance worries.</p>
                </c:if>
                <c:if test="${'ASP/Developer Enrollment' eq typeheader}">
                  <p> MasterGST makes it so simple to manage. No more spreadsheets, payment headaches, or compliance worries.</p>
                </c:if>
              </div>
            
            <!--<div class="coments-user">
               <img src="${contextPath}/static/mastergst/images/gst-partner-enrollment/user.jpg" alt="User" class="img-fulid rounded-circle" width="42" height="42" /><span>SHRINIVAS MOHARIR, CEO, BVM Consultancy Services</span>
            </div>-->
          </div>
          <!-- enrollment form right side end -->
        </div>
        <!-- comment icon begin -->
        <div class="comenticon"> <img src="${contextPath}/static/mastergst/images/gst-partner-enrollment/icon3.png" alt="Comment" /> </div>
        <!-- comment icon end -->
      </div>
    </div>
  </div>
</div>
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">
	var erptext = '<c:out value="${erptext}"/>';
	$(function () {
		if(erptext == '') {
			$('#noOfBranches').show();
		}
		$('input[type="radio"]').click(function(){
			if($(this).attr("value") == 'Yes') {
				$('#noOfBranches').show();
			} else {
				$('#noOfBranches').hide();
			}
		});
	});
	function updateSpinner(btn) {
		if(validatesignup()){
			$(btn).addClass('btn-loader');
		}
	}
	function validatesignup(){
		var firstNameLength = /^[\s\S]{3,}$/;
		var passwordLength = /^[\s\S]{6,}$/;
		var phoneNumberRegex=/^\d{10}$/;
		var emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@(?:[a-zA-Z0-9]+\.[A-Za-z]{2,5})(?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;
		var firstNameId = $('#firstNameId').val();
		var businessNameId = $('#businessNameId').val();
		var emailId = $('#emailId').val();
		var passwordId = $('#passwordId').val();
		var mobileId = $('#mobileId').val();
		var addressId = $('#addressId').val();
		var type = '<c:out value="${typeheader}"/>';
		var c = 0;  
		if(type == 'Suvidha Centers' || type == 'Small/Medium Business Enrollment' || type == 'Enterprise Enrollment'){
			if(businessNameId ==""){
				c++;
			}else if(!firstNameLength.test(businessNameId)){
				c++;
		}
		}
		if(type == 'CA/CMA/CS/Tax Professional Enrollment' || type == 'Partner Enrollment' || type == 'ASP/Developer Enrollment'){
		if(firstNameId ==""){
			c++;
		}else if(!firstNameLength.test(firstNameId)){
			c++;
		}
		}
		if(emailId ==""){
			c++;
		}else if(!emailRegex.test(emailId)){
			c++;
		}
		if(passwordId ==""){
			c++;
		}else if(!passwordLength.test(passwordId)){
			c++;
		}
		if(mobileId ==""){
			c++;
		}else if(!phoneNumberRegex.test(mobileId)){
			c++;
		}
		if(addressId == ""){
			c++;
		}
		return c==0; 
	}
</script>
</body>
</html>
