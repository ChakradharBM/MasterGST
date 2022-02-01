<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="remindersTabName" value='<%= (String)request.getParameter("remindersTabName") %>'/>
<div class="row">
<c:if test="${remindersTabName eq 'customerstab' || remindersTabName eq 'supplierstab'}">
		<div id="group_and_client" class="group_and_client">
			<div class="form-group" style="display:inline;">
				<label for="multeselectclient">Clients/ Business :</label>
				<select id="multeselectclient_${remindersTabName}" class="multeselectclient multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${centers}" var="clients">
						<option value="${clients.id}">${clients.businessname}- ${clients.gstnnumber}</option>
					</c:forEach>
				</select>
				<span id="clientsErrorMsg_${remindersTabName}"></span>
			</div>
		</div>
</c:if>	
	<div class="col-md-12 col-sm-12 customtable p-0">
		<table id="dbTableReminders_${remindersTabName}" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th class="text-center">
						<div class="checkbox">
							<label><input type="checkbox" id="remCheck_${remindersTabName}" onclick="sendAllRemindersSelection(this, '${remindersTabName}')" /><i class="helper"></i></label>
						</div>
					</th>
					<c:if test="${remindersTabName eq 'clientstab'}">
						<th></th>
						<th class="text-center">Business / Client Name</th>
					</c:if>
					<c:if test="${remindersTabName eq 'customerstab'}">
						<th class="text-center">Client Name</th>
						<th class="text-center">Business / Customer Name</th>
					</c:if>
					<c:if test="${remindersTabName eq 'supplierstab'}">
						<th class="text-center">Client Name</th>
						<th class="text-center">Business / Supplier Name</th>
					</c:if>
					<th class="text-center">EmailId</th>
					<th class="text-center">Contact Number</th>
					<th class="text-center">GSTIN</th>
					<th class="text-center">State</th>
					<th class="text-center" width="12%!important">Action</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>
</div>
<script type="text/javascript">
	$(function(){
		var tabName="<c:out value='${remindersTabName}'/>";
		var usrId = '${user.id}';
		var usrname = '${user.fullname}';
		var usremail = '${user.email}';
		var usrmobile = '${user.mobilenumber}';
		var userSignatureDetails = '${usersignature}';
		var usrDetails = userSignatureDetails;
		usrDetails = usrDetails.replaceAll(/#mgst#/g, "\r\n");
		clientsArray[tabName]=new Array();
		$('#multiselect').css('width','220px');
		$('#multeselectclient_${remindersTabName}').multiselect({
			nonSelectedText: '- client/business Name -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {applyClient('${remindersTabName}');},
			onSelectAll: function() {applyClient('${remindersTabName}');},
			onDeselectAll: function() {applyClient('${remindersTabName}');}
		});			
		<c:forEach items="${centers}" var="clients">
			clientsArray[tabName].push("${clients.id}");
		</c:forEach>
		loadRemindersData("${id}", "<c:out value='${remindersTabName}'/>", usrId, usrname, usremail, usrmobile, usrDetails);
	});
</script>
