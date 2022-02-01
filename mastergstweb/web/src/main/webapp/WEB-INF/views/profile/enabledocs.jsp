<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Docs</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-cp/dashboard-cp.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<style>
.drive-img-div{text-align: center;padding-top:2%;}
.container.db-inner-txt.docspage1 { margin-top:5px!important; min-height:auto!important}

</style>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
<!--- breadcrumb start -->
 		
<div class="breadcrumbwrap">
<div class="container">
	<div class="row">
        <div class="col-md-12 col-sm-12">
				<ol class="breadcrumb">
					<c:choose>
					<c:when test='${not empty client && not empty client.id}'>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active">Company Profile</li>
					</c:when>
					<c:otherwise>
					<li class="breadcrumb-item active">Team</li>
					</c:otherwise>
					</c:choose>
				</ol>
				<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>

<!--- breadcrumb end -->
        <div class="db-ca-wrap">
            <div class="container">
                <div class="row">
                    <!-- left side begin -->
                    <%@include file="/WEB-INF/views/profile/leftnav.jsp" %>
                    <!-- left side end -->

					<div class="col-md-10 col-sm-12" style="min-height: 465px!important;">
						<div class="docspage2" style="background-color:white;display:none">
							<p style="text-align:  center;color:  green; font-size:  23px;    padding-top: 20px;    padding-bottom: 20px;">You have Successfully configured your cloud account</p>
						</div>
						<div class="container db-inner-txt docspage1">
							<div class="row">
								<div class="credentialwrap">
									<div class="row">
										<div class="col-lg-4 col-md-4 col-sm-12 bdr-dashed-r">
											<h4 class="presize">Steps to configure: </h4>
											<div class="row stpsblock">
												<p class="col-md-2 col-sm-12"><strong>Step1:</strong></p>
												<p class="col-md-10 col-sm-12" style="font-size: 15px;">Choose the cloud and Click on Configure button</p>
											</div>
											<div class="row stpsblock">
												<p class="col-md-2 col-sm-12 "><strong>Step2:</strong></p>
												<p class="col-md-10 col-sm-12" style="font-size: 15px;">Enter your Cloud Account credentials  </p>
											</div>
											<div class="row stpsblock">
												<p class="col-md-2 col-sm-12 "><strong>Step3:</strong></p>
												<p class="col-md-10 col-sm-12" style="font-size: 15px;">Allow MasterGST to access your cloud account  </p>
											</div>
											<div class="row stpsblock">
												<p class="col-md-2 col-sm-12 "><strong>Step4:</strong></p>
												<p class="col-md-10 col-sm-12" style="font-size: 15px;">Start uploading Documents from MasterGST.</p>
											</div>
										</div>

										<div class="col-lg-8 col-md-8 col-sm-12">
											<div class="welhdr">
												<h5 class="welcomehr" style="text-align:  center;font-size: 16px;">Welcome to MasterGST Docs, You seems not yet configured your cloud account, it is one time setup will take not less than a minute time. Please configure here for one time.</h5>
											</div>
											<div class="drive-div">
												<p id="success-msg" style="text-align:  center;margin-bottom: 0px;margin-top:  13px;font-weight:  bolder;font-size:  18px;">Click here to configure</p>
												<div class="drive-img-div">
													<a href="#" onClick="updateCredentials()"> <img src="${contextPath}/static/mastergst/images/master/Google_Drive_logo.jpg" style="height:150px; width:150px;padding:10px"></a>
													<p style="margin-top: 14px;font-weight:bolder;font-size:22px;color: #5769bb;">Google Drive</p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
	 
				</div>
                  
			</div>
		</div>
	</div>
    
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
    
	<script type="text/javascript">
	$(function() {
		fetchDocs(true);
	});
	function fetchDocs(initial) {
		$.ajax({
			url: "${contextPath}/getdocs/${id}",
			async: true,
			cache: false,
			contentType: 'application/json',
			success : function(data) {
				if(initial) {
					$('.docspage1').hide();
					$('.docspage2').show();
				} else {
					$('.docspage1').show();
					$('.docspage2').hide();
				}
			}
		});
	}
	function updateCredentials() {
		fetchDocs(false);
		window.open('https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=768121484606-baaiub64egjkn5snlm7775fas9up35d1.apps.googleusercontent.com&redirect_uri=http://app.mastergst.com/login&response_type=code&scope=https://www.googleapis.com/auth/drive.file','_blank');
	}
    </script>
</body>

</html>