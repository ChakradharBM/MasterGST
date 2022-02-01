<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | SMTP</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<script src="${contextPath}/static/mastergst/js/signups/google-address.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/profile/smtp.js" type="text/javascript"></script>
<script type="text/javascript">
	var table;
	$(document).ready(function() {
		$('#cp_center').css("display","block");
		$('#cp_Allcenter').css("display","block");$('#cp_centerFiling').css("display","block");
		$('#cp_centerClinet').css("display","block");
		$('#cpSmtpNav').addClass('active');$('#nav-team').addClass('active');
		$('.nonAspAdmin').addClass('active');
		table = $('table.display').DataTable({
			"dom": '<"toolbar">frtip',
			"pageLength": 10,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		var tabheader = '<h4>SMTP Details</h4>';
		if(!$('#detailsSize').length || parseInt($('#detailsSize').val())==0){
			tabheader += '<a href="#" class="btn btn-blue-dark"  onclick="addSmtpDetails()">Add SMTP Details</a> ';
		}
		$("div.toolbar").html(tabheader);
		var headertext = [],
			headers = document.querySelectorAll("table.display th"),
			tablerows = document.querySelectorAll("table.display th"),
			tablebody = document.querySelector("table.display tbody");
		for (var i = 0; i < headers.length; i++) {var current = headers[i];headertext.push(current.textContent.replace(/\r?\n|\r/, ""));}
		for (var i = 0, row; row = tablebody.rows[i]; i++) {for (var j = 0, col; col = row.cells[j]; j++) {col.setAttribute("data-th", headertext[j]);}}
	});
	
	function addSmtpDetails(){
		$('#addSmtpDetails').modal('show');
	}
	
</script>
</head>
<body class="body-cls">
  <!-- header page begin -->
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
					<ol class="breadcrumb">
						<li class="breadcrumb-item">
							<c:choose>
								<c:when test="${usertype eq userCenter}">
									<a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a>
								</c:when>
								<c:otherwise>
									<a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a>
								</c:otherwise>
							</c:choose>
						</li>
						<li class="breadcrumb-item active">SMTP</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
        <div class="db-ca-wrap">
            <div class="container">
                <div class="row">
                    <!-- left side begin -->
                    <%@include file="/WEB-INF/views/profile/leftnav.jsp" %>
                    <!-- left side end -->
                    <!-- dashboard cp  begin -->
                    <div class="col-md-10 col-sm-12 customtable p-0">
                   		 <c:if test="${not empty smtpDetails}">
							<input type="hidden" name="detailsSize" id="detailsSize" value="${smtpDetails.size()}"/>
							</c:if>
                        <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead><tr><th>Bussiness Name</th><th class="text-center">Host</th><th class="text-center">Port</th>
                            <th class="text-center">From</th>
                            <th class="text-center">UserName</th>
                            <th class="text-center">Actions</th>
                            </tr></thead>
                            <tbody>
							<c:if test="${not empty smtpDetails}">
							<c:forEach items="${smtpDetails}" var="smtpDetail">
								<tr>
                                    <td>${smtpDetail.bussinessName}</td>
                                    <td>${smtpDetail.host}</td>
                                    <td class="text-left">${smtpDetail.port}</td>
                                    <td class="text-left">${smtpDetail.from}</td>
                                    <td class="text-left">${smtpDetail.username}</td>
                                    <td class="text-center"><a class="btn-edt smtp-Edit" href="#" onClick="editSmtpDetails('${smtpDetail.docId}')"><i class="fa fa-edit"></i> </a></td>
                                </tr>
							</c:forEach>
							</c:if>
                            </tbody>
                        </table>
                    </div> 
  <!-- dashboard cp table end -->
</div>                    
                </div>
            </div>
        </div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
</body>
<!-- Add SMTP Details Modal -->
<div class="modal fade" id="addSmtpDetails" role="dialog" aria-labelledby="addSmtpDetailsModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
     
        <div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add SMTP Details </h3>
                         <span class="errormsg" id="smtp_error"></span>
                    </div>
				</div>
       <form class="meterialform" name="smtpdetails" id="smtpdetails">
       <input type="hidden" class="form-control" name="clientId" id="clientId"/>
        <div class="modal-body meterialform bs-fancy-checks" >
         <div class="row pl-4 pt-4 pr-4">
          	<div class="col-md-6 col-sm-12">
	                       <label for="host" class="control-label"><p class="lable-txt astrich">Host</p></label>
	                       <span class="errormsg" id="host_Msg"></span>
	                       <input type="text" class="form-control" id="host" name="host" required="required"  data-error="Please enter host" onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || event.charCode == 64 || event.charCode == 46 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="host" maxlength="50" size="50"/>
			</div>
	
			<div class="col-md-3 col-sm-6">
	                       <label for="port" class="control-label"><p class="lable-txt astrich">Port</p></label>
	                       <span class="errormsg" id="port_Msg"></span>
	                       <input type="text" class="form-control" id="port" name="port" required="required" data-error="Please enter port" onKeyPress="return (event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" size="5" maxlength="5" placeholder="port" />
			</div>
			<div class="col-md-3 col-sm-6">
                     <label for="auth" class="control-label"><p class="lable-txt astrich">Auth</p></label>
                     <span class="errormsg" id="auth_Msg"></span>
                     <select name="auth" class="form-control" id="auth">
                     		<option value="true">True</option>
                     		<option value="false">False</option>
                     </select>
			</div>
			<div class="col-md-6 col-sm-12">
                     <label for="username" class="control-label"><p class="lable-txt astrich">User Name</p></label>
                     <span class="errormsg" id="username_Msg"></span>
                     <input type="text" class="form-control" id="username" name="username" required="required" data-error="Please enter username"  size="35" maxlength="35" placeholder="user name" />
			</div>
			<div class="col-md-6 col-sm-12">
                     <label for="password" class="control-label"><p class="lable-txt astrich">Password</p></label>
                     <span class="errormsg" id="password_Msg"></span>
                     <input type="text" class="form-control" id="password" name="password" required="required" data-error="Please enter password" size="35" maxlength="35" placeholder="password" />
			</div>
			<div class="col-md-6 col-sm-12">
                     <label for="from" class="control-label"><p class="lable-txt astrich">Run for</p></label>
                     <span class="errormsg" id="schedlueExpressionVal_Msg"></span>
                     <select class="form-control" id="schedlueExpressionVal" name="schedlueExpressionVal" required="required" >
                     	<option value="">- Select -</option>
                     	<option value="24">Once a day</option>
                   		<option value="12">Every 12 hours</option>
                   		<option value="8">Every 8 hours</option>
                   		<option value="6">Every 6 hours</option>
                     	<option value="4">Every 4 hours</option>
                     </select>
			</div>
			<div class="col-md-6 col-sm-12">
                     <label for="from" class="control-label"><p class="lable-txt astrich">From Address</p></label>
                     <span class="errormsg" id="from_Msg"></span>
                     <input type="text" class="form-control" id="from" name="from" required="required" data-error="Please enter from" onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || event.charCode == 64 || event.charCode == 46 || (event.charCode >= 48 && event.charCode <= 57))" size="25" placeholder="from" maxlength="40" />
			</div>
			<div class="col-md-6 col-sm-12">
                     <label for="toAddress" class="control-label"><p class="lable-txt astrich">TO Address</p></label>
                     <span class="errormsg" id="toAddress_Msg"></span>
                     <textarea id="toAddress" class="form-control" name="toAddress" required="required" rows="3" cols="25" ></textarea>
			</div>
			<div class="col-md-6 col-sm-12">
                     <label for="ccAddress" class="control-label"><p class="lable-txt astrich">CC Address</p></label>
                     <span class="errormsg" id="ccAddress_Msg"></span>
                     <textarea id="ccAddress" class="form-control" name="ccAddress" required="required" rows="3" cols="25"></textarea>
			</div>
			</div>
		</div>
		</form>
      
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary"  id="cancelBtn" data-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" onclick="saveSmtpDetails('<c:out value="${id}"/>', '<c:out value="${fullname}"/>', '<c:out value="${usertype}"/>', '<c:out value="${month}"/>', '<c:out value="${year}"/>')">Save</button>
      </div>
    </div>
  </div>
</div>
</html>