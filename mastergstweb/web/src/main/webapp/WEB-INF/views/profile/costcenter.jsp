<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Cost Centers</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style type="text/css">div.dataTables_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;}</style>
<script type="text/javascript">
	var branches=new Array();
	var table,subTable, currentId='';
	$(document).ready(function() {
	var type	= '<c:out value="${type}"/>';
	if(type == 'compsbrnch'){
			$('#sbranchTab').click();
		}
		$('#cpCostCenterNav').addClass('active');
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
		$(".tabtable1 div.toolbar").html('<a href="#" class="btn btn-blue-dark permissionSettings-Branches-Add" data-toggle="modal" data-target="#addBranchModal" onclick="clearData(true)">Add</a> ');
		$(".tabtable2 div.toolbar").html('<a href="#" class="btn btn-blue-dark permissionSettings-Branches-Add" data-toggle="modal" data-target="#addSubBranchModal" onclick="clearData(false)">Add</a> ');
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
    				<li class="nav-item branch_tab"><a class="nav-link active" data-toggle="tab" id="branchTab" href="#branches" role="tab"><span class="serial-num">1</span> Cost Centers</a></li>
    				<li class="nav-item"><a class="nav-link" data-toggle="tab" id="sbranchTab" href="#sbranch" role="tab"><span class="serial-num">2</span> Sub Cost Centers</a></li>
 				 </ul>
 				</div>
 				  <div class="tab-content" style="margin-top: 34px;width: 82%;">
    					<div id="branches" class="tab-pane active" style="margin-top: 14px;">
     							<div class="tabtable1" style="width:120%">
        							<table id="dbTable1" class="display row-border dataTable meterialform">
            							<thead>
                							<tr>
												<th class="text-center">Cost Center Name</th>
                    							<th class="text-center">Action</th>
                							</tr>
            							</thead>
            							<tbody>
											<c:forEach items="${client.costCenter}" var="costcenter">
												<tr id="row${costcenter.id}">
													<td align="left">${costcenter.name}</td>
													<td class="actionicons"><a class="btn-edt permissionSettings-Branches-Edit" href="#" data-toggle="modal" data-target="#addBranchModal" onClick="populateElement('${costcenter.id}', '${costcenter.name}', 'CostCenter')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Branches-Delete" onClick="showDeletePopup('${costcenter.id}','${costcenter.name}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
												</tr>
											</c:forEach>
											
            							</tbody>
        							</table>
								</div>
						</div>
						<div id="sbranch" class="tab-pane ">
    							<div class="tabtable2 mt-3" style="width:123%">
        							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
            							<thead>
                							<tr>
                    							<th class="text-center">Cost Center Name</th>
                    							<th class="text-center">Sub Cost Center Name</th>
                    							<th class="text-center">Action</th>
                							</tr>
            							</thead>
            							<tbody>
												<c:forEach items="${client.costCenter}" var="costcenter">
												<c:if test="${not empty costcenter.subcostcenter}">
                        							<c:forEach items="${costcenter.subcostcenter}" var="subcostCenter">
														<tr id="row${subcostCenter.id}" class="row${costcenter.id}">
                                							<td align="left">${costcenter.name}</td>
															<td align="left">${subcostCenter.name}</td>
                                							<td class="actionicons"><a class="btn-edt permissionSettings-Branches-Edit" href="#" data-toggle="modal" data-target="#addSubBranchModal" onClick="populateSubBranchElement('${subcostCenter.id}','${costcenter.id}', '${subcostCenter.name}', 'SubCostCenter')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Branches-Delete" onClick="showSubBranchDeletePopup('${subcostCenter.id}','${subcostCenter.name}','${costcenter.id}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
                            							</tr>
													</c:forEach>
												</c:if>
											</c:forEach>
											
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
                        <h3>Add Cost Center<br /><span class="caption">Your the adding Cost Center under ${client.businessname} !</span></h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright">
                    <!-- row begin -->
                    <div class="row  p-3">
						<form:form method="POST" data-toggle="validator" class="meterialform" id="branchform" name="userform" style="border:none;padding:0px;margin:0px;">
                        <div class="branchbox addbranch row m-0">
                            <div class="col-sm-12 f-18-b">Add your Cost Center Details.</div>
                            <div class="form-group col-md-6 col-sm-12 brancherror" id="brancherror">
                                <p class="lable-txt astrich"> Cost Center Name</p>
                                <span class="errormsg refreshpage" id="branchName_Msg" style="margin-top: -18px;"></span><span class="errormsg bname" style="margin-top: -18px;"></span>
                                <input type="text" id="branchName" name="branchName" required="required" data-error="Please enter the Cost Center name" onchange="updateDetails()" placeholder="Jane Smith" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>
						<input type="hidden" id="branch_name" value="">
                        <div class=" col-12 mt-4 text-center" style="display:block">
                            <a type="button" onClick="branchadd('CostCenter')" class="btn btn-blue-dark branch_submit" style="display:none"><span aria-hidden="true">SAVE</span></a>
													
						</div>
					</form:form>
                    </div>

                    <!-- row end -->

                </div>
				<div class="modal-footer text-center" style="display:block">
					<label for="branch_submit" class="btn btn-blue-dark m-0" onClick="branchadd('CostCenter')" tabindex="0">Save</label>
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
                        <h3>Add Sub Cost Center<br /><span class="caption">Your the adding sub Cost Center under ${client.businessname} !</span></h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright">
                    <!-- row begin -->
                    <div class="row  p-3">
                       <form:form method="POST" data-toggle="validator" class="meterialform" name="userform" style="border:none;padding:0px;margin:0px;">
                        <div class="branchbox addsubBranch row m-0">
                            <div class="col-sm-12 f-18-b">Add your Sub Cost Center Details.</div>
                            <div class="form-group col-md-6 col-sm-12 brancherror">
                                <p class="lable-txt astrich"> Cost Center Name</p>
                                <span class="errormsg refreshpage" id="parentBranch_Msg" style="margin-top: -18px;"></span>
								<select id="parentBranch" class="mt-1" required="required" data-error="Please enter branch name" onchange="updateDetails()" name="parentBranch" value="" >
								<option value=""> - Select - </option>
								<c:forEach items="${client.costCenter}" var="costcenter">
									<option value="${costcenter.id}">${costcenter.name}</option>
								</c:forEach>
								</select>
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12 brancherror" id="sbrancherror">
                                <p class="lable-txt astrich"> Sub Cost Center Name</p>
                                <span class="errormsg refreshpage" id="subBranchName_Msg" style="margin-top: -18px;"></span><span class="errormsg sbname" style="margin-top: -18px;"></span>
                                <input type="text" id="subBranchName" name="subBranchName" required="required" onchange="updateDetails()"  data-error="Please enter the Sub Branch name for the branch" placeholder="Jane Smith" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>

                        <div class=" col-12 mt-4 text-center" style="display:block">
                            <a type="button" onClick="branchadd('SubCostCenter')" class="btn btn-blue-dark subbranch_submit" style="display:none"><span aria-hidden="true">SAVE</span></a>
						</div>
						</form:form>
                    </div>

                    <!-- row end -->

                </div>
				<div class="modal-footer text-center" style="display:block">
					<label for="subbranch_submit" class="btn btn-blue-dark m-0" onClick="branchadd('SubCostCenter')" tabindex="0">Save</label>
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
          <h3>Delete Cost Center</h3>
        </div>
        <div class="pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Cost Center <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Cost Center</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
    <script>
	function clearData(isBranch) {
		currentId = '';
		if(isBranch) {
			$('.errormsg.bname').text('');
			$('.errormsg.sbname').text('');
			$('.form-group').removeClass('has-error has-danger');
			$('.errormsg.refreshpage').html('');
			$('#branchName').val('');
		} else {
			$('.sbname').text('');
			$('.form-group').removeClass('has-error has-danger');
			$('.errormsg.refreshpage').html('');
			$('#parentBranch').val('');
			$('#subBranchName').val('');
		}
	}
    function populateElement(id, name, type) {
		currentId = id;
		$('.addbranch').val('');
		if(type == 'CostCenter') {
			$('#branchName').val(name);
		} else {
			$('#parentBranch').val(id);
			$('#subBranchName').val(name);
		}
	}
    function populateSubBranchElement(subranchid,id, name, type) {
		currentId = subranchid;
		$('.addbranch').val('');
		if(type == 'CostCenter') {
			$('#branchName').val(name);
		} else {
			$('#parentBranch').val(id);
			$('#subBranchName').val(name);
		}
	}
    $('#parentBranch').select2();

    function addClientInfoValidation(type) {
		var branchName = $('#branchName').val();
		var subBranchName = $('#subBranchName').val();
		var parentBranch = $('#parentBranch').val();
		var c=0;
		if(type == 'CostCenter') {	
			if(branchName ==""){
				$('#branchName_Msg').text("Please Enter CostCenter Name"); 
				c++;
			}else{
				$('#branchName_Msg').text(""); 
			}
		}else {
			if(subBranchName ==""){
				$('#subBranchName_Msg').text("Please Enter Sub CostCenter Name"); 
				c++;
			}else{
				$('#subBranchName_Msg').text(""); 
			}
			if(parentBranch ==""){
				$('#parentBranch_Msg').text("Please select Parent CostCenter"); 
				c++;
			}else{
				$('#parentBranch_Msg').text(""); 
			}
		}
		return c==0; 
    }
    
    function branchadd(type){
       	var name, parentId='';
   		var clientid='<c:out value="${client.id}"/>';
		var err=0;
    	if(type == 'CostCenter') {
			name = $('#branchName').val();
		} else {
			name = $('#subBranchName').val();
			parentId = $('#parentBranch').val();
		}
    	if(addClientInfoValidation(type)){
	        if(type == 'CostCenter'){
		    	$.ajax({
					type : "GET",
					async: false,
					contentType : "application/json",
					url: "${contextPath}/costcenterexists/"+clientid,
					data:{
						'name': name
					},
					success : function(response) {
						if(response == true){
							$('.bname').text('Cost center name already exists');
							$('#brancherror').addClass('has-error has-danger');
							err = 1;
						}else{
							$('.bname').text('');
							$('#brancherror').removeClass('has-error has-danger');
							addClientInfo(type);
						}
					}
				});
	    	}else{
	    		$.ajax({
	    			type : "GET",
	    			async: false,
	    			contentType : "application/json",
	    			url: "${contextPath}/subcostcenterexists/"+clientid,
	    			data:{
	    				'name': name
	    			},
	    			success : function(response) {
	       				if(response == true){
	       					$('.sbname').text('SubCostcenter name already exists');
	    					$('#sbrancherror').addClass('has-error has-danger');
	    					err = 1;
	    				}else{
	       					$('.sbname').text('');
	    					$('#sbrancherror').removeClass('has-error has-danger');
	    					addClientInfo(type);
	    				}
	       			}
	       		});
	    	}
   		} 
  	}
     
    function addClientInfo(type) {
   		var name, parentId='';
			if(type == 'CostCenter') {
				name = $('#branchName').val();
			} else {			
				name = $('#subBranchName').val();
				parentId = $('#parentBranch').val();
			}
			$.ajax({
				url: "${contextPath}/addcostcenter/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}",
				async: false,
				cache: false,
				data: {
					'type': type,
					'elemId': currentId,
					'name': name,
					'parentId': parentId
				},
				dataType: "text",
	            contentType: 'application/json',
				success : function(data) {
					if(type == "CostCenter"){
						location.href="${contextPath}/cp_costcenter/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=";
					} else {
						location.href="${contextPath}/cp_costcenter/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=compsbrnch";
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
			url: "${contextPath}/delcostcenter/"+branchId+"/"+clientid,
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
			url: "${contextPath}/delSubCostCenter/"+subBranchId+"/"+branchId+"/"+clientid,
			success : function(response) {
				subTable.row( $('#row'+subBranchId) ).remove().draw();
			}
		});
	}
	
	  function updateDetails(value) {
		var branchName = $('#branchName').val();
		var subBranchName = $('#subBranchName').val();
		var parentBranch = $('#parentBranch').val();
		var clientid='<c:out value="${client.id}"/>';
		$.ajax({
		type : "GET",
		async: false,
		contentType : "application/json",
		url: "${contextPath}/costcenterexits/"+clientid+"/"+branchName,
		success : function(response) {
			if(response == true){
				$('.bname').text('branch name already exists');
				$('#brancherror').addClass('has-error has-danger');
				
			}else{
				$('.bname').text('');
				$('#brancherror').removeClass('has-error has-danger');
			}
		}
		
	});
		$.ajax({
			type : "GET",
			async: false,
			contentType : "application/json",
			url: "${contextPath}/subcostcenterexits/"+clientid+"/"+subBranchName,
			success : function(response) {
				if(response == true){
					$('.sbname').text('Subbranch name already exists');
					$('#sbrancherror').addClass('has-error has-danger');
				}else{
					$('.sbname').text('');
					$('#sbrancherror').removeClass('has-error has-danger');
				}
			}
			
		});
		if(branchName == ''){
			$('#branchName_Msg').show();
		} else{
			$('#branchName_Msg').hide();
		}
		if(subBranchName == ''){
			$('#subBranchName_Msg').show();
		} else{
			$('#subBranchName_Msg').hide();
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