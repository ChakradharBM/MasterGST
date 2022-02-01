<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<%-- <jsp:include page="/WEB-INF/views/admin/userDetails.jsp" /> --%>
<c:set var="apisTabName" value='<%= (String) request.getParameter("apisUsageType")%>'/>
<div class="customtable tabtable col-sm-12">
	<div class="col-md-12 col-sm-12 customtable p-0 mt-3">
		<table id="gstApiUsageExceedsDataTable_${apisTabName}" class="display dataTable meterialform col-sm-12 p-0"  cellspacing="0" width="100%">
			<thead>
				<tr>
					<th class="text-center">Object Id</th>
					<th class="text-center">User Name</th>
					<th class="text-center">Email</th>
					<th class="text-center">Phone Number</th>
					<c:if test="${apisTabName eq 'APIUSERS'}">
						<th class="text-center">API Name</th>
					</c:if>
					<c:if test="${apisTabName eq 'WEBUSERS'}">
						<th class="text-center">User Type</th>
					</c:if>
					<th class="text-center">Sub. Start Date </th>
					<th class="text-center">Sub. Exipry date</th>
					<c:if test="${apisTabName eq 'APIUSERS'}">
					<th class="text-center">Allowed APIs</th>
					<th class="text-center">Processed APIs</th>
					<th class="text-center">Exceeds APIs</th>
					</c:if>
					<c:if test="${apisTabName eq 'WEBUSERS'}">
					<th class="text-center">Allowed Invoices</th>
					<th class="text-center">Processed Invoices</th>
					<th class="text-center">Exceeds Invoices</th>
					</c:if>
					<th class="text-center">Action</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>
</div>
<script type="text/javascript">
	$('#reports_lnk').addClass('active');
	var loginUser = '<c:out value="${fullname}"/>';
	$(function(){
		loadApiUsageExceedsTable('APIUSERS');
	});
	$('#${apisTabName}').on('click', function() {
		var tabRefName ='<c:out value="${apisTabName}"/>';
		loadApiUsageExceedsTable(tabRefName);
	});
</script>
