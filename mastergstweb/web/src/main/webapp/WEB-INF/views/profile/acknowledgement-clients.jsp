<%@page import="java.util.List"%>
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<title>MasterGST | Acknowledgement Users</title>
	<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/ca-dashboard.css" media="all" />
	<script type="text/javascript" src="${contextPath}/static/mastergst/js/client/dashboard.js"></script>
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
	<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
	<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
	<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
	<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
	<script src="${contextPath}/static/mastergst/js/profile/acknowledgement-user.js" type="text/javascript"></script>
	<script src="${contextPath}/static/mastergst/js/common/filedrag-map-gstno.js" type="text/javascript"></script>
	<script src="${contextPath}/static/mastergst/js/profile/profile_storage.js" type="text/javascript"></script>
	 <style>
		button.multiselect.dropdown-toggle.btn-block.text-left.btn.btn-default{width: 168px;text-align: center!important;}
		#clients_group .form-group .multiselect-container.dropdown-menu{width:300px!important}
		div#dbTableCredentials_savedCredentialsTab_filter input{height:30px!important;}
		#configaws_btn{padding: 6px 15px 5px;font-weight: bold;float: right; margin-right: 10px; background-color: #314999;color: #fff!important; font-size: 14px;text-transform: uppercase;}
	</style>
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
<body>
<c:choose>
	<c:when test='${not empty client && not empty client.id}'>
		<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
	</c:when>
	<c:otherwise>
		<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
	</c:otherwise>
</c:choose>
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
				<ol class="breadcrumb"><li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li><li class="breadcrumb-item active"><c:choose><c:when test="${usertype eq userEnterprise}">Acknowledgement Users</c:when><c:otherwise>Acknowledgement Users</c:otherwise></c:choose></li></ol>
				<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>
 <div class="db-ca-wrap" style="padding-top:105px!important">
    <div class="container">
		<div class="row gstr-info-tabs">
					<div style="display:block;width:100%;padding-right: 1%;">
			 <a href="${contextPath}/cp_acknowledgementUser/${id}/${fullname}/${usertype}/${month}/${year}" class="btn btn-blue-dark" role="button" style="float:right;padding: 4px 25px;margin-top: 5px;" >Back</a>
			 </div>
			<ul class="nav nav-tabs col-md-12 mt-3 pl-4" role="tablist" id="tabsactive">
				<li class="nav-item"><a class="nav-link active tabName" id="savedCredentialsTab" data-toggle="tab" href="#credentialsTab" role="tab">Cloud Configurations</a></li>
			 </ul>
			 
			<div class="tab-content col-md-12 mb-3 mt-1 p-0">
				<div class="tab-pane active col-md-12" id="credentialsTab" role="tabpane1">
					<jsp:include page="/WEB-INF/views/profile/acknowledgement-user_tab.jsp">
						<jsp:param name="id" value="${id}" />
						<jsp:param name="fullname" value="${fullname}" />
						<jsp:param name="usertype" value="${usertype}" />
						<jsp:param name="returntype" value="<%=MasterGSTConstants.GSTR1%>" />
						<jsp:param name="contextPath" value="${contextPath}" />
						<jsp:param name="month" value="${month}" />
						<jsp:param name="year" value="${year}" />
						<jsp:param name="clients" value="${clients}"/>
						<jsp:param name="ackTabName" value="savedCredentialsTab"/>
					</jsp:include>
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
	                       <select id="groupName" name="groupName" style="width:330px">
	                       	<option value="">- Select -</option>
	                       </select>
			</div>
      </div>
      <input type="hidden" id="credential_userid" />
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnSaveAwsCredentials" onclick="saveAwsS3Credentials('<c:out value="${id}"/>', '<c:out value="${fullname}"/>', '<c:out value="${usertype}"/>', '<c:out value="${month}"/>', '<c:out value="${year}"/>')">Save</button>
        <button type="button" class="btn btn-primary" id="cancelBtn" data-dismiss="modal">Cancel</button>
      </div>
    </div>
  </div>
</div>
</div>
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
</body>
</html>