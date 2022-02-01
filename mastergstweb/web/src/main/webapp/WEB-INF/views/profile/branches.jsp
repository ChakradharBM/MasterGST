<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Branches</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<style type="text/css">div.dataTables_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;}</style>
<script type="text/javascript">
	var branches=new Array();
	var table,subTable, currentId='';
	$(document).ready(function() {
	var type	= '<c:out value="${type}"/>';
	if(type == 'compsbrnch'){
			$('#sbranchTab').click();
		}
		$('#cpBranchNav').addClass('active');
		$('#nav-client').addClass('active');
		table = $('table#dbTable1').DataTable({
			dom: '<"toolbar"f>Blfrtip<"clear">', 	
			"pageLength": 10,
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"responsive": true,
			"ordering": true,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		table.on('draw', rolesPermissions);
		subTable = $('table#dbTable2').DataTable({
			dom: '<"toolbar"f>Blfrtip<"clear">', 	
			"pageLength": 10,
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"responsive": true,
			"ordering": true,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		subTable.on('draw', rolesPermissions);
		$(".tabtable1 div.toolbar").html('<h4 style="margin-top: 6px;">Branches</h4><a href="#" class="btn btn-blue-dark permissionSettings-Branches-Add" data-toggle="modal" data-target="#addBranchModal" onclick="clearData(true)">Add</a> ');
		$(".tabtable2 div.toolbar").html('<h4 style="margin-top: 6px;">Sub Branches</h4><a href="#" class="btn btn-blue-dark permissionSettings-Branches-Add" data-toggle="modal" data-target="#addSubBranchModal" onclick="clearData(false)">Add</a> ');
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
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
<!--- breadcrumb start -->
 
<div class="breadcrumbwrap">
<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active">Company Profile</li>
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
					<div class="col-md-10 col-sm-12 customtable p-0">
               <div class="gstr-info-tabs">
                 <ul class="nav nav-tabs">
    				<li class="nav-item branch_tab"><a class="nav-link active" data-toggle="tab" id="branchTab" href="#branches" role="tab"><span class="serial-num">1</span> Branches</a></li>
    				<li class="nav-item"><a class="nav-link" data-toggle="tab" id="sbranchTab" href="#sbranch" role="tab"><span class="serial-num">2</span> Sub Branches</a></li>
 				 </ul>
 				</div>
 				  <div class="tab-content" style="margin-top: 34px;width: 82%;">
    					<div id="branches" class="tab-pane active" style="margin-top: 14px;">
     							<div class="tabtable1" style="width:120%">
        							<table id="dbTable1" class="display row-border dataTable meterialform">
            							<thead>
                							<tr>
												<th class="text-center">Branch Name</th>
                    							<th class="text-center">Branch Address</th> 
                    							<th class="text-center">Action</th>
                							</tr>
            							</thead>
            							<tbody>
										<c:choose>
											<c:when test="${not empty companyUser}">
												<c:if test='${not empty companyUser.branch}'>
													<c:forEach items = "${companyUser.branch}" var="branch">
															<c:forEach items="${client.branches}" var="branchs">
																<c:if test='${branch eq branchs.name}'>
																<tr id="row${branchs.id}">
																	<td align="left">${branchs.name}</td>
																	<td align="left">${branchs.address}</td>
																	<td class="actionicons"><a class="btn-edt permissionSettings-Branches-Edit" href="#" data-toggle="modal" data-target="#addBranchModal" onClick="populateElement('${branchs.id}', '${branchs.name}', '${branchs.address}', 'Branch')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Branches-Delete" onClick="showDeletePopup('${branchs.id}','${branchs.name}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
																</tr>
																</c:if>
															</c:forEach>
													</c:forEach>
												</c:if>
											</c:when>
											<c:otherwise>
												<c:forEach items="${client.branches}" var="branch">
													<tr id="row${branch.id}">
														<td align="left">${branch.name}</td>
														<td align="left">${branch.address}</td>
														<td class="actionicons"><a class="btn-edt permissionSettings-Branches-Edit" href="#" data-toggle="modal" data-target="#addBranchModal" onClick="populateElement('${branch.id}', '${branch.name}', '${branch.address}', 'Branch')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Branches-Delete" onClick="showDeletePopup('${branch.id}','${branch.name}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
													</tr>
												</c:forEach>
											</c:otherwise>
										</c:choose>
            							</tbody>
        							</table>
								</div>
						</div>
						<div id="sbranch" class="tab-pane ">
							
    							<div class="tabtable2 mt-3" style="width:123%">
        							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
            							<thead>
                							<tr>
                    							<th class="text-center">Branch Name</th>
                    							<th class="text-center">Sub Branch Name</th>
                    							<th class="text-center">Sub Branch Address</th> 
                    							<th class="text-center">Action</th>
                							</tr>
            							</thead>
            							<tbody>
										<c:choose>
											<c:when test="${not empty companyUser}">
												<c:if test='${not empty companyUser.branch}'>
													<c:forEach items = "${companyUser.branch}" var="branch">
													<c:forEach items="${client.branches}" var="branchs">
													<c:if test='${branch eq branchs.name}'>
													<c:if test="${not empty branchs.subbranches}">
															<c:forEach items="${branchs.subbranches}" var="subbranch">
														<tr id="row${subbranch.id}" class="row${branchs.id}">
                                							<td align="left">${branchs.name}</td>
															<td align="left">${subbranch.name}</td>
															<td align="left">${subbranch.address}</td>
                                							<td class="actionicons"><a class="btn-edt permissionSettings-Branches-Edit" href="#" data-toggle="modal" data-target="#addSubBranchModal" onClick="populateSubBranchElement('${subbranch.id}','${branchs.id}', '${subbranch.name}', '${subbranch.address}', 'SubBranch')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Branches-Delete" onClick="showSubBranchDeletePopup('${subbranch.id}','${subbranch.name}','${branchs.id}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
                            							</tr>
													</c:forEach>
													 </c:if>	
														</c:if>
														</c:forEach>
													</c:forEach>
												</c:if>
											</c:when>
											<c:otherwise>
												<c:forEach items="${client.branches}" var="branch">
												<c:if test="${not empty branch.subbranches}">
                        							<c:forEach items="${branch.subbranches}" var="subbranch">
														<tr id="row${subbranch.id}" class="row${branch.id}">
                                							<td align="left">${branch.name}</td>
															<td align="left">${subbranch.name}</td>
															<td align="left">${subbranch.address}</td>
                                							<td class="actionicons"><a class="btn-edt permissionSettings-Branches-Edit" href="#" data-toggle="modal" data-target="#addSubBranchModal" onClick="populateSubBranchElement('${subbranch.id}','${branch.id}', '${subbranch.name}', '${subbranch.address}', 'SubBranch')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Branches-Delete" onClick="showSubBranchDeletePopup('${subbranch.id}','${subbranch.name}','${branch.id}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
                            							</tr>
													</c:forEach>
												</c:if>
											</c:forEach>
											</c:otherwise>
										</c:choose>											
             							</tbody>
        							</table>
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
	
<!-- Add Branch Modal Start -->
    <div class="modal fade" id="addBranchModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" modelAttribute="Branch" name="userform">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Branch<br /><span class="caption">Your the adding branch under ${client.businessname} !</span></h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright">
                    <!-- row begin -->
                    <div class="row  p-3">
						<form:form method="POST" data-toggle="validator" class="meterialform" id="branchform" name="userform" style="border:none;padding:0px;margin:0px;">
                        <div class="branchbox addbranch row m-0">
                            <div class="col-sm-12 f-18-b">Add your Branch Details.</div>
                            <div class="form-group col-md-6 col-sm-12 brancherror" id="brancherror">
                                <p class="lable-txt astrich"> Branch Name</p>
                                <span class="errormsg refreshpage" id="branchName_Msg" style="margin-top: -18px;"></span><span class="errormsg bname" style="margin-top: -18px;"></span>
                                <input type="text" id="branchName" name="branchName" required="required" data-error="Please enter the branch name" onchange="updateDetails()" placeholder="Jane Smith" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12 brancherror" id="brancherror1">
                                <p class="lable-txt astrich">Branch Address</p>
                                <span class="errormsg refreshpage" id="branchAddress_Msg" style="margin-top: -18px;"></span><span class="errormsg baddr" style="margin-top: -18px;"></span>
                                <input type="text" id="branchAddress" name="branchAddress" class="mapicon" required="required" onchange="updateDetails()"  placeholder="MG Road, Plot 30, India" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>
                        
                        <p class="lable-txt text-center" id="mainBranchAddress" style="display:none;">(If you change the Address, Client/Company address also change!)</p>
						<input type="hidden" id="branch_name" value="">
                        <div class=" col-12 mt-4 text-center" style="display:block">
                            <a type="button" onClick="branchadd('Branch')" class="btn btn-blue-dark branch_submit" style="display:none"><span aria-hidden="true">SAVE</span></a>
													
						</div>
					</form:form>
                    </div>

                    <!-- row end -->

                </div>
				<div class="modal-footer text-center" style="display:block">
					<label for="branch_submit" class="btn btn-blue-dark m-0" onClick="branchadd('Branch')" tabindex="0">Save</label>
					<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
            </div>
        </div>
    </div>
    <!-- Add Branch Modal End -->
	<!-- Add Sub Branch Modal Start -->
    <div class="modal fade" id="addSubBranchModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" modelAttribute="Branch" name="userform">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Sub branch<br /><span class="caption">Your the adding sub branch under ${client.businessname} !</span></h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright">
                    <!-- row begin -->
                    <div class="row  p-3">
                       <form:form method="POST" data-toggle="validator" class="meterialform" name="userform" style="border:none;padding:0px;margin:0px;">
                        <div class="branchbox addsubBranch row m-0">
                            <div class="col-sm-12 f-18-b">Add your Sub Branch Details.</div>
                            <div class="form-group col-md-6 col-sm-12 brancherror">
                                <p class="lable-txt astrich"> Branch Name</p>
                                <span class="errormsg refreshpage" id="parentBranch_Msg" style="margin-top: -18px;"></span>
								<select id="parentBranch" class="mt-1" required="required" data-error="Please enter branch name" onchange="updateDetails()" name="parentBranch" value="" >
								<option value=""> - Select - </option>
								<c:choose>
								<c:when test="${not empty companyUser}">
									<c:if test='${not empty companyUser.branch}'>
										<c:forEach items = "${companyUser.branch}" var="branch">
											<c:forEach items="${client.branches}" var="branchs">
												<c:if test='${branch eq branchs.name}'>
													<option value="${branchs.id}">${branchs.name}</option>			
												</c:if>
											</c:forEach>
										</c:forEach>
									</c:if>					
								</c:when>
								<c:otherwise>
									<c:forEach items="${client.branches}" var="branch">
									<option value="${branch.id}">${branch.name}</option>
									</c:forEach>
								</c:otherwise>
								</c:choose>
								</select>
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12 brancherror" id="sbrancherror">
                                <p class="lable-txt astrich"> Sub Branch Name</p>
                                <span class="errormsg refreshpage" id="subBranchName_Msg" style="margin-top: -18px;"></span><span class="errormsg sbname" style="margin-top: -18px;"></span>
                                <input type="text" id="subBranchName" name="subBranchName" required="required" onchange="updateDetails()"  data-error="Please enter the Sub Branch name for the branch" placeholder="Jane Smith" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12 brancherror subbraadd_div" id="sbrancherror1">
                                <p class="lable-txt astrich">Branch Address</p>
                                <span class="errormsg refreshpage" id="subBranchAddress_Msg" style="margin-top: -18px;"></span><span class="errormsg sbaddr" style="margin-top: -18px;"></span>
                                <input type="text" id="subBranchAddress" name="branchAddress" class="mapicon" onchange="updateDetails()" required="required"  placeholder="MG Road, Plot 30, India" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>

                        <div class=" col-12 mt-4 text-center" style="display:block">
                            <a type="button" onClick="branchadd('SubBranch')" class="btn btn-blue-dark subbranch_submit" style="display:none"><span aria-hidden="true">SAVE</span></a>
						</div>
						</form:form>
                    </div>

                    <!-- row end -->

                </div>
				<div class="modal-footer text-center" style="display:block">
					<label for="subbranch_submit" class="btn btn-blue-dark m-0" onClick="branchadd('SubBranch')" tabindex="0">Save</label>
					<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
            </div>
        </div>
    </div>
    <!-- Add Sub Branch Modal End -->
    <div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Branch</h3>
        </div>
        <div class="pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Branch <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Branch</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
    <script>
	function clearData(isBranch) {
		$('#mainBranchAddress').css('display','none');
		currentId = '';
		if(isBranch) {
			$('.errormsg.bname').text('');
			$('.baddr').text('');
			$('.errormsg.sbname').text('');
			$('.sbaddr').text('');
			$('.form-group').removeClass('has-error has-danger');
			$('.errormsg.refreshpage').html('');
			$('#branchName').val('');
			$('#branchAddress').val('');
		} else {
			$('.sbname').text('');
			$('.sbaddr').text('');
			$('.form-group').removeClass('has-error has-danger');
			$('.errormsg.refreshpage').html('');
			$('#parentBranch').val('');
			$('#subBranchName').val('');
			$('#subBranchAddress').val('');
		}
	}
    function populateElement(id, name, address, type) {
    	$('#mainBranchAddress').css('display','none');
		currentId = id;
		$('.addbranch').val('');
		if(type == 'Branch') {
			
			if(name.toUpperCase() == 'Main Branch'.toUpperCase()){
				$('#mainBranchAddress').css('display','block');
			}
			$('#branchName').val(name);
			$('#branchAddress').val(address);
		} else {
			$('#parentBranch').val(id);
			$('#subBranchName').val(name);
			$('#subBranchAddress').val(address);
		}
	}
    function populateSubBranchElement(subranchid,id, name, address, type) {
		currentId = subranchid;
		$('.addbranch').val('');
		if(type == 'Branch') {
			$('#branchName').val(name);
			$('#branchAddress').val(address);
		} else {
			$('#parentBranch').val(id);
			$('#subBranchName').val(name);
			$('#subBranchAddress').val(address);
		}
	}
    $('#parentBranch').select2();

    function addClientInfoValidation(type) {
		var branchName = $('#branchName').val();
		var branchAddress = $('#branchAddress').val();
		var subBranchName = $('#subBranchName').val();
		var subBranchAddress = $('#subBranchAddress').val();
		var parentBranch = $('#parentBranch').val();
		var c=0;
		if(type == 'Branch') {	
			if(branchName ==""){
				$('#branchName_Msg').text("Please Enter Branch Name"); 
				c++;
			}else{
				$('#branchName_Msg').text(""); 
			}
			if(branchAddress ==""){
				$('#branchAddress_Msg').text("Please Enter Branch Address"); 
				c++;
			}else{
				$('#branchAddress_Msg').text(""); 
			}
		}else {
			if(subBranchName ==""){
				$('#subBranchName_Msg').text("Please Enter Sub Branch Name"); 
				c++;
			}else{
				$('#subBranchName_Msg').text(""); 
			}
			if(subBranchAddress ==""){
				$('#subBranchAddress_Msg').text("Please Enter Sub Branch Address"); 
				c++;
			}else{
				$('#subBranchAddress_Msg').text(""); 
			}
			if(parentBranch ==""){
				$('#parentBranch_Msg').text("Please select Parent Branch"); 
				c++;
			}else{
				$('#parentBranch_Msg').text(""); 
			}
		}
		return c==0; 
    }
    
    function branchadd(type){
       	var name, address, parentId='';
   		var clientid='<c:out value="${client.id}"/>';
		var err=0;
		
    	if(type == 'Branch') {
			name = $('#branchName').val();
			address = $('#branchAddress').val();
		} else {
			name = $('#subBranchName').val();
			address = $('#subBranchAddress').val();
			parentId = $('#parentBranch').val();
		}
    	if(addClientInfoValidation(type)){
        if(type == 'Branch'){
    	$.ajax({
			type : "GET",
			async: false,
			contentType : "application/json",
			url: "${contextPath}/branchesexits/"+clientid,
			data:{
				'name': name,
				'address': address
			},
			success : function(response) {
				if(response == true){
					$('.bname').text('branch name already exists');
					$('.baddr').text('branch address already exists');
					$('#brancherror').addClass('has-error has-danger');
					$('#brancherror1').addClass('has-error has-danger');
					err = 1;
				}else{
					$('.bname').text('');
					$('.baddr').text('');
					$('#brancherror').removeClass('has-error has-danger');
					$('#brancherror1').removeClass('has-error has-danger');
					addClientInfo(type);
					
				}
			}
		
		});
    	}else{
    		$.ajax({
    			type : "GET",
    			async: false,
    			contentType : "application/json",
    			url: "${contextPath}/subbranchesexits/"+clientid,
    			data:{
    				'name': name,
    				'address': address
    			},
    			success : function(response) {
       				if(response == true){
       					$('.sbname').text('Subbranch name already exists');
    					$('.sbaddr').text('Subbranch address already exists');
    					$('#sbrancherror').addClass('has-error has-danger');
    					$('#sbrancherror1').addClass('has-error has-danger');
    					err = 1;
    				}else{
       					$('.sbname').text('');
    					$('.sbaddr').text('');
    					$('#sbrancherror').removeClass('has-error has-danger');
    					$('#sbrancherror1').removeClass('has-error has-danger');
    					addClientInfo(type);
    				}
       			}
       		});
    	}
   	} 
  	}
     
    function addClientInfo(type) {
   		var name, address, parentId='';
			if(type == 'Branch') {
				name = $('#branchName').val();
				address = $('#branchAddress').val();
			} else {			
				name = $('#subBranchName').val();
				address = $('#subBranchAddress').val();
				parentId = $('#parentBranch').val();
			}
			$.ajax({
			url: "${contextPath}/addbranch/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}",
			async: false,
			cache: false,
			data: {
				'type': type,
				'elemId': currentId,
				'name': name,
				'address': address,
				'parentId': parentId
			},
			dataType: "text",
            contentType: 'application/json',
			success : function(data) {
				if(type == "Branch"){
				location.href="${contextPath}/cp_branches/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=";
				} else {
					location.href="${contextPath}/cp_branches/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=compsbrnch";
				}
			},
			error : function(data) {
			}
		});
			$('#sbranchTab').click();
    }
    
    function displayTable() {
    	var a=document.getElementByName("branch");
    	if(a == "branch") {
			$('#dbTable2').css("display","none");
    	} else {
			$('#dbTable1').css("display","none");
    	}
    }
    function showDeletePopup(branchId, name, clientid) {
		$('#deleteModal').modal('show');
		$('#delPopupDetails').html(name);
		$('#btnDelete').attr('onclick', "deleteBranch('"+branchId+"', '"+clientid+"')");
	}

	function deleteBranch(branchId,clientid) {
		$.ajax({
			url: "${contextPath}/delbranch/"+branchId+"/"+clientid,
			success : function(response) {
				table.row( $('#row'+branchId) ).remove().draw();
				subTable.rows( $('.row'+branchId) ).remove().draw();
				$("#parentBranch option[value='"+branchId+"']").remove();
			}
		});
	}
	
	function showSubBranchDeletePopup(subBranchId, name, branchId, clientid){
		$('#deleteModal').modal('show');
		$('#delPopupDetails').html(name);
		$('#btnDelete').attr('onclick', "deleteSubBranch('"+subBranchId+"','"+branchId+"', '"+clientid+"')");
	}
	function deleteSubBranch(subBranchId,branchId,clientid) {
		$.ajax({
			url: "${contextPath}/delSubBranch/"+subBranchId+"/"+branchId+"/"+clientid,
			success : function(response) {
				subTable.row( $('#row'+subBranchId) ).remove().draw();
			}
		});
	}
	
	  function updateDetails(value) {
		var branchName = $('#branchName').val();
		var branchAddr = $('#branchAddress').val();
		var subBranchName = $('#subBranchName').val();
		var subBranchAddress = $('#subBranchAddress').val();
		var parentBranch = $('#parentBranch').val();
		var clientid='<c:out value="${client.id}"/>';
		
		
		$.ajax({
		type : "GET",
		async: false,
		contentType : "application/json",
		url: "${contextPath}/branchesexits/"+clientid+"/"+branchName+"/"+branchAddr,
		success : function(response) {
			if(response == true){
				$('.bname').text('branch name already exists');
				$('.baddr').text('branch address already exists');
				$('#brancherror').addClass('has-error has-danger');
				$('#brancherror1').addClass('has-error has-danger');
				
				}else{
				$('.bname').text('');
				$('.baddr').text('');
				$('#brancherror').removeClass('has-error has-danger');
				$('#brancherror1').removeClass('has-error has-danger');
			}
		}
		
	});
		$.ajax({
			type : "GET",
			async: false,
			contentType : "application/json",
			url: "${contextPath}/subbranchesexits/"+clientid+"/"+subBranchName+"/"+subBranchAddress,
			success : function(response) {
				if(response == true){
					$('.sbname').text('Subbranch name already exists');
					$('.sbaddr').text('Subbranch address already exists');
					$('#sbrancherror').addClass('has-error has-danger');
					$('#sbrancherror1').addClass('has-error has-danger');
				}else{
					$('.sbname').text('');
					$('.sbaddr').text('');
					$('#sbrancherror').removeClass('has-error has-danger');
					$('#sbrancherror1').removeClass('has-error has-danger');
				}
			}
			
		});
		if(branchName == ''){
			$('#branchName_Msg').show();
		} else{
			$('#branchName_Msg').hide();
		}
		if(branchAddr == ''){
		$('#branchAddress_Msg').show();
		} else{
			$('#branchAddress_Msg').hide();
		}
		if(subBranchName == ''){
			$('#subBranchName_Msg').show();
			} else{
				$('#subBranchName_Msg').hide();
			}
		if(subBranchAddress == ''){
			$('#subBranchAddress_Msg').show();
			} else{
				$('#subBranchAddress_Msg').hide();
			}
		if(parentBranch == ''){
			$('#parentBranch_Msg').show();
			} else{
				$('#parentBranch_Msg').hide();
			}
	}  
    </script>
</body>

</html>