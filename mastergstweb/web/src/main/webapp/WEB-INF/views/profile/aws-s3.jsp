<%@page import="java.util.List"%>
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Acknowledgment Users</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script type="text/javascript">
$(function(){
	$('.acknowledgementhead').addClass('active');
	var counts=0;var groupArray=[];
	<c:forEach items="${listOfGroups}" var="group">
	<c:if test="${not empty group.groupName}">
		if(counts == 0){
			groupArray.push('<c:out value="${group.groupName}"/>');	
			$("#groupName").append($("<option></option>").attr("value","${group.groupName}").text("${group.groupName}"));
			}
		 if(jQuery.inArray('<c:out value="${group.groupName}"/>', groupArray ) == -1){
			groupArray.push('<c:out value="${group.groupName}"/>');
			$("#groupName").append($("<option></option>").attr("value","${group.groupName}").text("${group.groupName}"));
		}
		counts++; 
	</c:if>
</c:forEach>
});
</script>
</head>
<body class="body-cls">
 <%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
				<ol class="breadcrumb">
					<li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li>
     				<li class="breadcrumb-item active">Acknowledgement Users</li>
				</ol>
				<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>
<div class="db-ca-wrap">
    <div class="container">
      <!-- Dashboard body start -->
      <div>
            <div class="db-inner" style="min-height: 465px!important;">
                <!-- begin content  -->
                <div class="container db-inner-txt docspage1">
                    <div class="row">
                        <div class="credentialwrap">
                            <div class="row">
                                <div class="col-lg-4 col-md-4 col-sm-12 bdr-dashed-r">
                                    <!-- left profile list begin -->
                                  <h4 class="presize">Steps to configure: </h4>
                                  <div class="row stpsblock">
                                  <p class="col-md-2 col-sm-12"><strong>Step1:</strong></p>
                                  <p class="col-md-10 col-sm-12" style="font-size: 15px;">Choose the cloud and Click on Configure button</p></div>
                                  <div class="row stpsblock">
                                    <p class="col-md-2 col-sm-12 "><strong>Step2:</strong></p>
                                    <p class="col-md-10 col-sm-12" style="font-size: 15px;">Enter your Cloud Account credentials  </p></div>
                                    <div class="row stpsblock">
                                        <p class="col-md-2 col-sm-12 "><strong>Step3:</strong></p>
                                        <p class="col-md-10 col-sm-12" style="font-size: 15px;">Allow MasterGST to access your cloud account  </p></div>
                                    <div class="row stpsblock">
                                            <p class="col-md-2 col-sm-12 "><strong>Step4:</strong></p>
                                            <p class="col-md-10 col-sm-12" style="font-size: 15px;">Start uploading Documents from MasterGST.</p></div>
                                    <!-- left profile list end -->
                                </div>                        
                                <div class="col-lg-8 col-md-8 col-sm-12">
                                    <!--   profile table begin -->
                                    <div class="welhdr">
                                        <h5 class="welcomehr" style="text-align:  center;font-size: 16px;">Welcome to MasterGST Acknowledgements, You seems not yet configured your cloud account, it is one time setup will take not less than a minute time. Please configure here for one time.</h5>
                                    </div>
                                    <!-- Nav tabs -->
                                    <div class="drive-div">
                                    <a id="success-msg" href="#" style="text-align:  center;margin-bottom: 0px;margin-top:  13px;font-weight:  bolder;font-size:  18px; display:block;" onclick="configureAmazonS3()">Click here to configure</a>
                                    <div class="drive-img-div">
	                                   <a href="#" onclick="configureAmazonS3()" > <img src="${contextPath}/static/mastergst/images/master/amazon-s3.png" style="padding:10px"></a>
	                                   <!-- <p style="margin-top: 14px;font-weight:bolder;font-size:22px;color: #5769bb;">Google Drive</p> -->
                                    </div>
                                    </div>                  
              					</div>            
                                      <!--   profile table end -->
                                </div>
                            </div>
                       </div> 
                  </div>
             </div>
        </div>
    </div>
</div>
<div class="modal fade" id="configureAwsS3Model" role="dialog" aria-labelledby="configureAwsS3Model" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document" style="top:14%">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Configure AWS S3 Credentials </h3>
          <span class="errormsg" id="aws_error"></span>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          	<div class="form-group col-md-6 col-sm-12">
	                       <label for="accessKey" class="control-label"><p class="lable-txt astrich">Access Key</p></label>
	                       <span class="errormsg" id="accessKey_Msg"></span>
	                       <input type="text" id="accessKey" name="accessKey" required="required"  data-error="Please enter Access Key" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" placeholder="Access Key" size="45"/>
			</div>
	
			<div class="form-group col-md-6 col-sm-12">
	                       <label for="accessSecret" class="control-label"><p class="lable-txt astrich">Access Secret</p></label>
	                       <span class="errormsg" id="accessSecret_Msg"></span>
	                       <input type="text" id="accessSecret" name="accessSecret" required="required" data-error="Please enter Access Secret" onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" size="45" placeholder="Access Secret" />
			</div>
			<div class="form-group col-md-6 col-sm-12">
	                       <label for="bucketName" class="control-label"><p class="lable-txt astrich">Bucket Name</p></label>
	                       <span class="errormsg" id="bucketName_Msg"></span>
	                       <input type="text" id="bucketName" name="bucketName" required="required" data-error="Please enter Bucket Name" onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" size="45" placeholder="Bucket Name" />
			</div>
			<div class="form-group col-md-6 col-sm-12">
	                       <label for="regionName" class="control-label"><p class="lable-txt astrich">Region Name</p></label>
	                       <span class="errormsg" id="regionName_Msg"></span>
	                       <select id="regionName" name="regionName" style="width:330px">
	                       	<option value="-1">- Select -</option>
	                       	<%List<String> regions = (List)request.getAttribute("regions");
	                    		for(String region : regions) {%>
	                    		   <option><%= region %></option>
	                    		   <%} %>
	                       </select>
			</div>
			<div class="form-group col-md-6 col-sm-12">
	                       <label for="groupName" class="control-label" style="display: inline;"><p class="lable-txt">Group Name</p></label>
	                       <span class="errormsg" id="groupName_Msg"></span>
	                       <select id="groupName" name="groupName"  style="width:330px">
	                       	<option value="">- Select -</option>
	                       </select>
			</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnSaveAwsCredentials" onclick="saveAwsS3Credentials('<c:out value="${id}"/>', '<c:out value="${fullname}"/>', '<c:out value="${usertype}"/>', '<c:out value="${month}"/>', '<c:out value="${year}"/>')">Save</button>
        <button type="button" class="btn btn-primary" id="cancelBtn" data-dismiss="modal">Cancel</button>
      </div>
    </div>
  </div>
</div>
</div>
<script>
</script>
<script src="${contextPath}/static/mastergst/js/common/filedrag-map-gstno.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/profile/profile_storage.js" type="text/javascript"></script>
</body>
<script type="text/javascript">
</script>
</html>