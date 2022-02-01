<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<%@include file="/WEB-INF/views/includes/script.jsp" %>
 
<%@include file="/WEB-INF/views/includes/swagger_script.jsp" %>
 <style>
 ul li{line-height: 1.5!important;}
.greytxt.alert.alert-info {background-color: #f5c6c2!important;border-color: #f5c6c2!important; margin-top: 20px;color: black;font-family: 'latolight',sans-serif;} 
.downld-txt{margin-left:8%; font-weight: 600!important;}
.dnl-summary{font-weight:600!important;}
.Methods-txt{font-size:20px!important}
.topbar , section.models , pre.errors-wrapper , .information-container.wrapper , .scheme-container{display:none!important}
 </style>
  
</head>

<%@include file="/WEB-INF/views/includes/app_header.jsp" %>
<div class="bodywrap">
<div class="bodybreadcrumb">
	<div class="container">
		<div class="row">
			<div class="col-sm-12">
				<div class="navbar-left">
					<ul>
						<c:if test="${apiType eq 'gst'}">
						<li class="nav-item">
							<a class="nav-link urllink active" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=gst" >GST APIs</a>
						</li>
						<li class="nav-item">
							<a class="nav-link urllink" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=eway" >e-Way Bill APIs</a>
						</li>
							<li class="nav-item">
								<a class="nav-link urllink" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=einv" >e-Invoice APIs</a>
							</li>
						</c:if>
						<c:if test="${apiType eq 'eway'}">
						<li class="nav-item">
							<a class="nav-link urllink" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=gst" >GST APIs</a>
						</li>
						<li class="nav-item">
							<a class="nav-link urllink active" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=eway" >e-Way Bill APIs</a>
							</li>
							<li class="nav-item">
								<a class="nav-link urllink" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=einv" >e-Invoice APIs</a>
							</li>
						</c:if>
						<c:if test="${apiType eq 'einv'}">
							<li class="nav-item">
								<a class="nav-link urllink" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=gst" >GST APIs</a>
							</li>
							<li class="nav-item">
								<a class="nav-link urllink" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=eway" >e-Way Bill APIs</a>
							</li>
							<li class="nav-item">
								<a class="nav-link urllink active" href="${contextPath}/apis?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&apiType=einv" >e-Invoice APIs</a>
						</li>
						</c:if>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>

<body class="swagger-section">
<div>
  <div class="db-inner">
      <!-- begin content  -->
    <div class="container db-inner-txt" style="margin: 0px 75px;margin-top: 80px;">
	
	<div class="row">
        
        <div class="col-sm-12">
        <c:choose >
        	<c:when test="${apiType eq 'gst'}">
        		<h3>Enabled GST APIs</h3>		
        	</c:when>
        	<c:when test="${apiType eq 'eway'}">
        		<h3>Enabled e-Way Bill APIs</h3>		
        	</c:when>
        	<c:when test="${apiType eq 'einv'}">
        		<h3>Enabled e-Invoice APIs</h3>		
        	</c:when>
        </c:choose>
        <h6>Some APIs enable automatically</h6>
        </div>
</div>
<c:if test="${apiType eq 'gst'}">
<div class="col-md-12 col-sm-12" style="width: 954px; margin-left: 6%;">
   <div class="greybox gst">
       <h5 class="boldtxt mb-2 text-center"><u>How to Start: </u></h5>
       <ol class="orderlist">
          <li>Create GSTN Sandbox Account (<strong>Please select GSP as</strong> <span style="color:#FF0000; font-weight:bold">"Tera Software Limited"</span> )  - <a href="https://docs.google.com/forms/d/e/1FAIpQLSfyJIrHuycgF1jiD9P47qjdiFH2Nmvba_1a9WnBLfgw_VSaYw/viewform" target="_blank"> Click here </a> </li>
          <li> Sign-up with MasterGST  - <a href="https://app.mastergst.com/signupall?inviteId=&subscrid=" target="_blank"> Sign up </a></li>
          <li> Login -> Credentials -> Create Credentials </li>
          <li> Once you receive the Sandbox Details from GSTN, start using GSTN Username  &amp; GSTN Number along with MasterGST API Credentials </li>
          <li> GST API Support Forum : <a href="https://groups.google.com/forum/#!forum/gst-suvidha-provider-gsp-discussion-group" target="_blank">Click here</a></li>
          <li>For Payload, Please <a href="https://developer.gst.gov.in/apiportal/taxpayer/returns" target="_blank">click here</a> GSTN API Documentation.</li>
       </ol>
   </div>
</div>
<div class="clearfix text-center">
             
              <p class="greytxt alert alert-info text-left"><span class="dnl-summary">Please refer the below GST API Documenation</span><br>
			  <span class="downld-txt">Please <a href="https://developer.gst.gov.in/apiportal/taxpayer/returns" target="_blank">click here</a> GSTN API Documenation.</span><br><span class="downld-txt"><a href="${contextPath}/static/mastergst/Return Filing through API_v1.1.docx" download>Click here</a> to download Return Filing Process Through API.</span><br><span class="downld-txt"><a href="${contextPath}/static/mastergst/GST API Error Codes.docx" download> Click here</a> to download Error Codes.</span></p>
			</div>
			<h2 class="text-center Methods-txt">Enabled GST API Methods and You can test here</h2><p class="mb-1 boldtxt text-center">Use Base URL as: <a href="#">https://api.mastergst.com</a></p>
</c:if>
<c:if test="${apiType eq 'eway'}">
<div class="col-md-12 col-sm-12" style="width: 954px; margin-left: 6%;">
    <div class="greybox eway">
        <h5 class="boldtxt mb-2 text-center"><u>How to Start: </u></h5>
        <ol class="orderlist">         
            <li> Sign-up with MasterGST  - <a href="https://app.mastergst.com/signupall?inviteId=&subscrid=" target="_blank"> Sign up </a></li>
            <li> Login -> Credentials -> E-wayBill Credentials -> Create Credentials </li>
            <li> Start using MasterGST e way Bill Client ID & Client Secret ID Credentials for your Sandbox Testing </li>        
        </ol>
    </div>
</div>
<div class="clearfix mb-3">&nbsp;</div>
  <div class="clearfix text-center">
             
              <p class="greytxt alert alert-info text-left"><span class="dnl-summary">For Payload / Test Data, Please refer the below Documenation</span><br>
			  <span class="downld-txt">eWay Bill API version 1.01 Documenation, <a href="${contextPath}/static/mastergst/ewaybill_api_24mar18.docx" download >Download here.</a></span>
			<br> <span class="downld-txt">eWay Bill API version 1.02 Documenation,<a href="${contextPath}/static/mastergst/ewayBillV1.02.01-14May2018-SENT.docx" download > Download here.</a> </span><br> <span class="downld-txt"> eWay Bill API version 1.03 Documenation, <a href="${contextPath}/static/mastergst/ewayBillV1.03-Changes-2018-10-05.docx" download >Download here.</a></span> </p>
            </div>
			<h2 class="text-center Methods-txt">Enabled EWAY Bill API Methods and You can test here</h2>
		<p class="mb-1 boldtxt text-center">Use Base URL as: <a href="#">https://api.mastergst.com</a></p>
		<p class="mb-1 boldtxt text-center">(Use Version 1.03(v1.03))</p>
</c:if>
<c:if test="${apiType eq 'einv'}">
	<div class="col-md-12 col-sm-12" style="width: 954px; margin-left: 6%;">
	    <div class="greybox eway">
	        <h5 class="boldtxt mb-2 text-center"><u>How to Start: </u></h5>
	        <ol class="orderlist">         
	            <li> Sign-up with MasterGST  - <a href="https://app.mastergst.com/signupall?inviteId=&subscrid=" target="_blank"> Sign up </a></li>
	            <li> Login -> Credentials -> E-invoice Credentials -> Create Credentials </li>
	            <li> Start using MasterGST e-Invoice Client ID & Client Secret ID Credentials for your Sandbox Testing </li>        
	        </ol>
	    </div>
	</div>
	<div class="clearfix mb-3">&nbsp;</div>
  		<!-- <div class="clearfix text-center">
        	<p class="greytxt alert alert-info text-left"><span class="dnl-summary">For Payload / Test Data, Please refer the below Documenation</span><br>
			<span class="downld-txt">e-Invoice API version 1.01 Documenation, <a href="${contextPath}/static/mastergst/ewaybill_api_24mar18.docx" download >Download here.</a></span>
			<br> <span class="downld-txt">e-Invoice Bill API version 1.02 Documenation,<a href="${contextPath}/static/mastergst/ewayBillV1.02.01-14May2018-SENT.docx" download > Download here.</a> </span><br> <span class="downld-txt"> eWay Bill API version 1.03 Documenation, <a href="${contextPath}/static/mastergst/ewayBillV1.03-Changes-2018-10-05.docx" download >Download here.</a></span> </p>
		</div> -->
		<div class="clearfix text-center">
              <p class="greytxt alert alert-info text-left"><span class="dnl-summary">Please refer the below E - invoice API Documenation</span><br>
			  <span class="downld-txt">Please <a href="http://einv-apisandbox.nic.in/" target="_blank">click here</a> E - invoice API Documenation.</span><br><span class="downld-txt"><a href="${contextPath}/static/mastergst/E-invoicing-error-codes.docx" download> Click here</a> to download e-invoice Error Codes.</span></p>
		</div>
		<h2 class="text-center Methods-txt">Enabled e-Invoice API Methods and You can test here</h2>
		<p class="mb-1 boldtxt text-center">Use Base URL as: <a href="#">https://api.mastergst.com</a></p>
</c:if>
  <div class="swagger-ui-wrap">
    <form id='api_selector'>
      <div class='input'><input placeholder="http://example.com/api" id="input_baseUrl" name="baseUrl" type="text"/></div>
      <div id='auth_container'></div>
     
    </form>
  </div>
  
<div id="message-bar" class="swagger-ui-wrap" data-sw-translate>&nbsp;</div>
<div id="swagger-ui-container" class="swagger-ui-wrap"></div>
<div class="col-sm-12">
        	<c:if test="${apiType eq 'gst'}">
        		<p class="mt-5"><span class="boldtxt">Error Codes :</span><a href="#" onClick="window.open('errorcodes','_blank','toolbar=no,scrollbars=yes,resizable=yes,top=300,left=300,width=700,height=500')"> Click here </a></p>		
				<p class="mt-2 mb-2"><span class="boldtxt">Additional Help :</span> Please follow us on <a href="https://groups.google.com/forum/#!forum/gst-suvidha-provider-gsp-discussion-group" target="_blank"> GSP/ASP Google Group</a></p>
				<p class="mb-4">To Create GSTN Sandbox Account <a href="https://goo.gl/forms/GWTsmI22Ei7YfPL02" target="_blank">click here</a></p>
        	</c:if>
			<c:if test="${apiType eq 'einv'}">
				<p class="mt-5"><span class="boldtxt">Error Codes :</span><a href="#" onClick="window.open('e-inovoice-errorcodes','_blank','toolbar=no,scrollbars=yes,resizable=yes,top=300,left=300,width=700,height=500')"> Click here </a></p>
				<p class="mt-2"><span class="boldtxt">State Codes :</span><a href="https://einvoice1.gst.gov.in/Others/MasterCodes" target="_blank"> Click here </a></p>
				<p class="mt-2 mb-2"><span class="boldtxt">Additional Help :</span> Please follow us on <a href="https://groups.google.com/forum/#!forum/gst-suvidha-provider-gsp-discussion-group"> GSP/ASP Google Group</a></p>
				<p class="mb-4">To Create GSTN Sandbox Account <a href="https://einv-apisandbox.nic.in/einvapiclient/EncDesc/Registration.aspx">click here</a></p>
			</c:if>
			<c:if test="${apiType eq 'eway'}">
				<p class="mt-5"><span class="boldtxt">Error Codes :</span><a href="#" onClick="window.open('eway-errorcodes','_blank','toolbar=no,scrollbars=yes,resizable=yes,top=300,left=300,width=700,height=500')"> Click here </a></p>
            <p class="not-imp mt-2"><span class="boldtxt">IMPORTANT NOTICE:</span> These are initial versions of e way Bill APIs and are subject to change as e way Bill system is still evolving and still in implementation phase. e way Bill API Developer Community will be informed about such changes through announcements through our portal and notifications.</p>
			</c:if>
        </div>
</div>

</div>
</div>	
	
<%@include file="/WEB-INF/views/includes/footer.jsp" %>



</body>
</html>
