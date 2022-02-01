<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="apiVersionTabName" value='<%= (String)request.getParameter("apiVersionTabName") %>'/>

<div class="col-md-12 col-sm-12 customtable p-0 mt-3">
	<table id="gstApiDataTable_${apiVersionTabName}" class="display dataTable meterialform col-sm-12 p-0"  cellspacing="0" width="100%">
		<thead>
			<tr>
				<th>API TYPE</th>
				<th>API NAME</th>
				<th>Method</th>
				<th>Sandbox Version</th>
				<th>Production Version </th>
				<th>Web Implmentation</th>
				<th>Action</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>
<div id="apisVersinProgress-bar_${apiVersionTabName}" class="d-none"  style="color:red;font-size:20px;position:absolute;z-index:99;top:37%;left: 46%;">
	<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
</div>
<div class="modal fade" id="${apiVersionTabName}Modal" tabindex="-1" role="dialog" aria-labelledby="${apiVersionTabName}Modal" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="${apiVersionTabName}ModalTitle">Apis Version Info</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="form-group col-6">
		    			<label for="${apiVersionTabName}name" type="text">Api Name</label>
						<input class="form-control" id="${apiVersionTabName}name">
					</div>
					<div class="form-group col-6">
					    <label for="${apiVersionTabName}method">Method</label>
					    <select class="form-control" id="${apiVersionTabName}method"><option value="GET">GET</option><option value="POST">POST</option><option value="PUT">PUT</option><option value="DELETE">DELETE</option></select>
					</div>
				</div>
				<div class="row">
					<div class="form-group col-6">
		    			<label for="${apiVersionTabName}sVersion" type="text">Sandbox Version</label>
						<input class="form-control" id="${apiVersionTabName}sVersion">
					</div>
					<div class="form-group col-6">
					    <label for="${apiVersionTabName}pVersion"  type="text">Production Version</label>
					    <input class="form-control" id="${apiVersionTabName}pVersion">
					</div>
				</div>
				<div class="row">
					<div class="form-group  col-6">
		    			<label for="${apiVersionTabName}webimpl">Web Implementation</label>
						<select class="form-control" id="${apiVersionTabName}webimpl"><option value="1">TRUE</option><option value="0">FALSE</option></select>
					</div>
				</div>
				<input class="form-control" id="${apiVersionTabName}docid" type="hidden">
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary" onclick="updateApi('${apiVersionTabName}')">Update</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function(){
	var tabRefName ='${apiVersionTabName}';
	loadApiVersionsInfo(tabRefName);		
});

var is${apiVersionTabName}Loaded = false;

$('#${apiVersionTabName}').on('click',function() {
	var tabRefName ='${apiVersionTabName}';
	loadApiVersionsInfo(tabRefName);
});
</script>