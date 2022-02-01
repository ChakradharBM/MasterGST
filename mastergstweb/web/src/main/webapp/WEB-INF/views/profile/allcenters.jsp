<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Users</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>

<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<script src="${contextPath}/static/mastergst/js/signups/google-address.js" type="text/javascript"></script>
<style>
.dt-buttons {white;left: 59%;float: left;position: relative;color:white}
button.dt-button.buttons-excel.buttons-html5, button.dt-button.buttons-print,
button.dt-button.buttons-pdf.buttons-html5 {background-color: #8ee3fe;	margin-right: 26px;	color: #435a9e !important;font-size: 14px;padding: 5px 10px!important;	border-radius: 4px;	border: 1px;width: 138px;cursor:pointer;}
.dt-buttons :hover{background-color:#364365!important; color:#ffffff!important}
div#msgTable_length {position: relative;}
.dataTables_length select{border:1px solid}
#filingStatusProcess {position:absolute;  z-index: 9; font-size: 23px; left: 50%;  color: #374583; width:30em; height:20px;margin-left: -15em; top:220px}
</style>
<script type="text/javascript">
var msgTab;
</script>
</head>
<body class="body-cls">
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
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cp_ClientsReports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Global Reports</a></li>
						<li class="breadcrumb-item active">All Suvidha Kendra Usage Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="db-ca-wrap">
            <div class="container">
			<div class="col-sm-12"><a href="${contextPath}/cp_ClientsReports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a></div>			
	<div class="bodywrap" style="min-height: 500px;">
	
		<div class="db-ca-wrap" style="padding-top:0px!important;margin-top:0px!important">
			<div class="container">
						<div class="customtable db-ca-view tabtable1">
							<table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
									<th class="text-center">Center Name</th>
									<th class="text-center">Contact person</th>
									<th class="text-center"># of Clients</th>
									<th class="text-center">Total Invoices</th>
									<th class="text-center"># Of Clients Added</th>
									<th class="text-center"># of Invoices Used</th>
                                </tr>
                            </thead>
                            <tbody>
								<c:forEach items="${centers}" var="center">
								<tr>
                                    <td align="center">${center.name}</td>
                                    <td align="center">${center.contactperson}</td>
                                    <td align="center">${center.totalClients}</td>
                                    <td align="center">${center.totalInvoices}</td>
									<td align="center">${center.usedClients}</td>
									<td align="center">${center.usedInvoices}</td>
                                </tr>
								</c:forEach>
                            </tbody>
                        </table>
						</div>
			</div>
		</div>
	</div>
	</div>
	</div>
	<!-- footer begin here -->
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->

</body>
<script type="text/javascript">
$(document).ready(function(){
	$('#msgs_lnk').addClass('active');
	$('.nonAspReports').addClass('active');

	var table = $('table.display').DataTable({
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
		$("div.toolbar").html('<h4>All Suvidha Kendra Usage</h4>');
		var headertext = [],
			headers = document.querySelectorAll("table.display th"),
			tablerows = document.querySelectorAll("table.display th"),
			tablebody = document.querySelector("table.display tbody");
		for (var i = 0; i < headers.length; i++) {
			var current = headers[i];
			headertext.push(current.textContent.replace(/\r?\n|\r/, ""));
		}
		for (var i = 0, row; row = tablebody.rows[i]; i++) {
			for (var j = 0, col; col = row.cells[j]; j++) {
				col.setAttribute("data-th", headertext[j]);
			}
		}
});
	
</script>

</html>