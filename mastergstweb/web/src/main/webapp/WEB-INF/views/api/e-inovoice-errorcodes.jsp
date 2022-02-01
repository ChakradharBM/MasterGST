<!DOCTYPE html>
<html lang="en">
<title>MasterGST - Error Codes For E-invoice</title>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript">
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-98474020-1', 'auto');
  ga('send', 'pageview');

</script>
<style>
	table th{padding:12px;font-weight:300}
</style>
</head>

<body lang=EN-US link=blue vlink="#954F72" style='tab-interval:.5in'>

<div class="container " >
<h4 style="width:10o%;text-align:center">API Error codes List</h4>
<table class="table table-bordered table-striped" border="1" cellspacing="0" cellpadding="0" width="579" style="width: 434.25pt;background: rgb(255 255 255); border: none;width:100%!important">
 <thead style="background-color: #5769bb;color: white;font-weight:bold;">
  <tr>
	<th style="width:9%">Error Code	    </th>
	<th>Error Messege	</th>
	<th style="width:35%">Reason for Error</th>	
	<th style="width:35%">Resolution      </th>
  <tr>
 </thead>
 <tbody>
<tr><th>1004</th><th colspan="3">Header GSTIN is required	</th></tr>	
<tr><th>1005</th><th>Invalid Token</th><th>	1. Token has expired 2. While calling other APIs, wrong GSTIN / User Id/ Token passed in the request header	</th><th>1. Token is valid for 6 hours , if it has expired, call the Auth. API again and get new token 2. Pass correct values for GSTIN, User Id and Auth Token in the request headers while calling APIs other than Auth API</th></tr>
<tr><th>1006</th><th colspan="3">User Name is required	</th></tr>	
<tr><th>1007</th><th>Authentication failed. Pls. inform the helpdesk</th><th>	Wrong formation of request payload	</th><th>Prepare the request payload as per the API documentation</th></tr>
<tr><th>1008</th><th>Invalid login credentials</th><th>	Either UserId or Password are wrong</th><th>	Pass the correct UserId and Password</th></tr>
<tr><th>1010</th><th>Invalid Client-ID/Client-Secret</th><th>	Either the ClientId or the ClientSecret passed in the request header is wrong	</th><th>Pass the correct ClientId and the ClientSecret</th></tr>
<tr><th>1011</th><th colspan="3">Client Id is required		</th></tr>
<tr><th>1012</th><th colspan="3">Client Secret is required	</th></tr>	
<tr><th>1013</th><th>Decryption of password failed</th><th>	Auth.API is not able to decrypt the password</th><th>	Use the correct public key for encrypting the password while calling the Auth API. The public key is sent by mail while providing the access to Production environment as well as available for download from the portal under API user management. This public key is different on Sandbox and Production and it is different from the one used for verification of the signed content.Refer to the developer portal for encryption method used and sample code.</th></tr>
<tr><th>1014</th><th>Inactive User	</th><th>Status of the GSTIN is inactive or not enabled for E Invoice</th><th>	Please verify whether the GSTIN is active and enabled for E Invoice from the E Invoice portal</th></tr>
<tr><th>1015</th><th>Invalid GSTIN for this user</th><th>	The GSTIN of the user who has generated the auth token is different from the GSTIN being passed in the request header</th><th>	Send the correct GSTIN in the header for APIs other than Auth API</th></tr>
<tr><th>1016</th><th>Decryption of App Key failed</th><th>	Auth.API is not able to decrypt the password</th><th>	Use the correct public key for encrypting the appkey while calling the Auth API. The public key is sent by mail while providing the access to Production environment as well as available for download from the portal under API user management. This public key is different on Sandbox and Production and it is different from the one used for verification of the signed content.Refer to the developer portal for encryption method used and sample code.</th></tr>
<tr><th>1017</th><th>Incorrect user id/User does not exists	</th><th>User id passed in request payload is incorrect</th><th>	Pass the correct user id. If not available, please log in to the portal using the main user id (the one without ')</th></tr>
<tr><th>1018</th><th>Client Id is not mapped to this user</th><th>	The UserId is not mapped to the ClientId that is being sent as request header</th><th>	Please send the correct userId for the respective clientId. If using direct integration as well as through GSP or through multiple GSPs, please pass the correct set of ClientId</th></tr>
<tr><th>1019</th><th>Incorrect Password	</th><th>Password is wrong</th><th>	Use the correct password, if forgotten, may use forgot password option in the portal</th></tr>
<tr><th>3001</th><th colspan="3">Requested data is not available	</th></tr>	
<tr><th>3002</th><th colspan="3">Invalid login credentials		    </th></tr>
<tr><th>3003</th><th>Password should contains atleast one upper case, one lower case, one number and one special characters like [%,$,#,@,_,!,*]</th><th>	Password being set is very simple	</th><th>Password should contains atleast one upper case, one lower case, one number and one special characters like [%,$] </th></tr>
<tr><th>3004</th><th>This username is already registered. Please choose a different username.</th><th>	User id is already available in the system</th><th>	Use a different user id</th></tr>
<tr><th>3005</th><th colspan="3">Requested data is not found	</th></tr>	
<tr><th>3006</th><th>Invalid Mobile Number	</th><th>The Mobile number provided is incorrect</th><th>	Provide the correct mobile number, Incase the number has changed, may update it in GSTN Common Portal and try after some time. If issue still persists, contact helpdesk with complete details of the issue.</th></tr>
<tr><th>3007</th><th>You have exceeded the limit of creating sub-users</th><th>	The number of sub user creation limit is exceeded	</th><th>Up to 10 subusers for each of the main GSTIN and additional places of business can be created</th></tr>
<tr><th>3008</th><th>Sub user exists for this user id</th><th>	There is already a subuser with the same user id is already created</th><th>	Use a different user id for the sub user creation</th></tr>
<tr><th>3009</th><th colspan="3">Pls provide the required parameter or payload		</th></tr>
<tr><th>3010</th><th colspan="3">The suffix login id should contain 4 or lesser than 4 characters</th></tr>		
<tr><th>3011</th><th colspan="3">Data not Found		</th></tr>
<tr><th>3012</th><th>Mobile No. is blank for this GSTIN ..Pl use update from GST Common Portal option to get the mobile number, if updated in GST Common Portal.</th><th>	The GSTIN master data does not have mobile number in eInvoice System</th><th>	Please get the mobile number updated at the GSTN common portal</th></tr>
<tr><th>3013</th><th>Your registration under GST has been cancelled , however if you are a transporter then use the Enrollment option.</th></tr>		
<tr><th>3014</th><th colspan="3">Gstin Not Allowed	</th></tr>	
<tr><th>3015</th><th>Sorry, your GSTIN is deregistered in GST Common Portal</th><th>	Attempting to use a GSTIN which is cancelled</th><th>	Please check the status of the GSTIN on the GSTN common portal. If it is active, contact the helpdesk with GSTIN details</th></tr>
<tr><th>3016</th><th colspan="3">Your registration under GST is inactive and hence you cannot register, however if you are a transporter then use the Enrollment option.</th></tr>		
<tr><th>3017</th><th colspan="3">You were given provisional ID which was not activated till the last date. Hence your details are not available with GST portal. However if you are a transporter then use the Enrollment option.	</th></tr>	
<tr><th>3019</th><th colspan="3">subuser details are not saved please try again	</th></tr>	
<tr><th>3020</th><th colspan="3">Internal Server Error pls try after sometime	</th></tr>	
<tr><th>3021</th><th>There are no subusers for this gstin</th><th>	Some user action has failed due to internal server issue or unexpected user data</th><th>	Try after some time, if issue still persists, report to helpdesk with complete details of the issue</th></tr>
<tr><th>3022</th><th colspan="3">The Given Details Already Exists		</th></tr>
<tr><th>3023</th><th>The New PassWord And Old PassWord Cannot Be Same</th><th>	While changing the password, new password can not be same as old password</th><th>	The New and Ols password should be different while changing the password</th></tr>
<tr><th>3024</th><th>Change of password unsuccessfull,pls check the existing password</th><th>	Password could not be changed since the current password provided is incorrect</th><th>	Provide the correct current password while changing the password</th></tr>
<tr><th>3025</th><th>Already This Account Has Been Freezed</th><th>	Trying to freeze an account which is already frozen</th><th>	You can freeze only active account</th></tr>
<tr><th>3027</th><th>You are already registered Pl.use already created username and password to login to the system.If you have forgotten username or password,then use Forgot Username or Forgot Passowrd options to get the username and password!!"</th><th>	Attempting to create another account which is already created</th><th>	Use forgot password option to retrieve the user name or password in case currently not available</th></tr>
<tr><th>3029</th><th>GSTIN is inactive or cancelled</th><th>	GSTIN is inactive or cancelled by department or tax payer .</th><th>	Check the correctness of the GSTIN and its status. If you are sure that it is active, Pl use the 'Sync GSTIN from GST CP' API to get it verified from the GST Portal. If it is active at GST portal, it will return you with the new status. If you get the status as 'Active', then you can re-fire your request to generate the IRN. If you are not able to verify through API, you can go to einvocie1.gst.gov.in portal and use the 'Tax Payer / GSTIN' option in search menu to check the status manually from GST Portal and use 'Update' button to get it updated from GST Common Portal, if required. If you are satisfied with result, you can re-fire the request.</th></tr>
<tr><th>3030</th><th>Invalid Gstin</th><th>	GSTIN provided is incorrect</th><th>	Provide the correct GSTIN</th></tr>
<tr><th>3031</th><th>Invalid User Name</th><th>	Attempting to login with wrong user id</th><th>	Use the correct user id</th></tr>
<tr><th>3032</th><th>Enrolled Transporter cannot login to e-Invoice Portal. You need to be GST registered. For more clarifications , please read the FAQs under Web Login section .</th><th>	The user who is not registered with GSTN but enrolled as transporter in E Way Bill portal is trying to login to eInvoice system</th><th>	This is not allowed</th></tr>
<tr><th>3033</th><th>Your account has been Freezed as GSTIN is inactive	</th><th>User is trying to login with an account which is freezed since the GSTIN is not active</th><th>	Check the status of the GSTIN on the GSTN common portal. If active in common portal, report the same to helpdesk		</th></tr>
<tr><th>3034</th><th>Your account has been cancelled as GSTIN is inactive</th><th>	User is trying to login with an account which is cancelled since the GSTIN is not active</th><th>	Check the status of the GSTIN on the GSTN common portal. If active in common portal, report the same to helpdesk</th></tr>
<tr><th>3035</th><th>Your account has been suspended as GSTIN is inactive</th><th>	User is trying to login with an account which is suspended since the GSTIN is not active</th><th>	Check the status of the GSTIN on the GSTN common portal. If active in common portal, report the same to helpdesk</th></tr>
<tr><th>3036</th><th>Your account has been inactive</th><th>	Attempting to logging with a user id which is not active</th><th>	Check the status of the user id, if in freeze state, create a new account</th></tr>
<tr><th>3037</th><th>CommonEnrolled Transporter not allowed this site</th><th>	A user with common enrolled opting is trying to use the eInvoice system</th><th>	eInvoice system can not be used by the GSTIN which has opted for common enrolment under E Way Bill</th></tr>
<tr><th>3042</th><th>Invalid From Pincode or To Pincode</th><th>	PIN code passed is wrong</th><th>	Pass the correct PIN code. Check the PIN code on the portal under Search -> Master Codes</th></tr>
<tr><th>3043</th><th>Something went wrong, please try again after sometime</th><th>	Attempting to carryout some on the system or passing some data which is not expected</th><th>	Please check the data or the operation which you have just performed. If issue still persists, please share the complete details to the helpdesk</th></tr>
<tr><th>3044</th><th>This registration only for tax payers not GSP.</th><th>	The option is available for the the Taxpayers and not for GSPs</th><th>	This option is not available / applicable for GSP</th></tr>
<tr><th>3045</th><th>Sorry you are not registered, Please use the registration option.</th></tr>		
<tr><th>3046</th><th>Sorry you are not enabled for e-Invoicing System on Production.</th><th>	Ineligible taxpayer is trying to register for the eInvoice system</th><th>	In case the turnover is above 500crores in any of the financial years in GST regime, use the enrol option in eInvoice portal</th></tr>
<tr><th>3052</th><th colspan="3">Transporter Id {0} is cancelled or invalid.	</th></tr>	
<tr><th>3053</th><th colspan="3">Unauthorised access		</th></tr>
<tr><th>3058</th><th colspan="3">Data not Saved		        </th></tr>
<tr><th>3059</th><th colspan="3">Client-Id for this PAN is not generated check your IP-Whitelisting status.</th></tr>		
<tr><th>3060</th><th colspan="3">Please wait for officer approval		                                   </th></tr>
<tr><th>3061</th><th colspan="3">Your Request has been rejected, please register again		               </th></tr>
<tr><th>3062</th><th colspan="3">Already Registered		                                                   </th></tr>
<tr><th>3063</th><th colspan="3">You are already enabled for e-invoicing		                           </th></tr>
<tr><th>3064</th><th colspan="3">Sorry, This GSTIN is deregistered		                                   </th></tr>
<tr><th>3065</th><th colspan="3">You are not allowed to use this feature.		                           </th></tr>
<tr><th>3066</th><th colspan="3">There is no pin codes availables for this state.		                   </th></tr>
<tr><th>3067</th><th colspan="3">Client secret unsuccessfull,pls check the existing client secret.		   </th></tr>
<tr><th>3068</th><th colspan="3">There is no Api user.		                                               </th></tr>
<tr><th>3069</th><th colspan="3">Sorry,you have not directly integrated with E-invoice API.		           </th></tr>
<tr><th>3070</th><th colspan="3">Sorry,you have not registered.		                                       </th></tr>
<tr><th>3071</th><th colspan="3">Sorry,you have already linked this Gstin to your Client Id.		       </th></tr>
<tr><th>3072</th><th colspan="3">Sorry,Your GSTIN not enabled by the Direct Integrator.		               </th></tr>
<tr><th>3073</th><th colspan="3">You are already registered.		                                       </th></tr>
</tbody></table>

<p class="MsoNormal"><o:p>&nbsp;</o:p></p>

</div>

</body>

</html>
