<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Verticals</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<style>div.dataTables_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;}</style>
<script type="text/javascript">
	var verticals=new Array();
	var table, subTable, currentId='';
	$(document).ready(function() {
		var type	= '<c:out value="${type}"/>';
		if(type == 'compsvrtl'){
				$('#sverticalTab').click();
			}
		$('#cpVerticalNav').addClass('active');
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
		$(".tabtable1 div.toolbar").html('<h4 style="margin-top: 6px;">Verticals</h4><a href="#" class="btn btn-blue-dark permissionSettings-Verticals-Add" data-toggle="modal" data-target="#addVerticalModal" onclick="clearData(true)">Add</a> ');
		$(".tabtable2 div.toolbar").html('<h4 style="margin-top: 6px;">Sub Verticals</h4><a href="#" class="btn btn-blue-dark permissionSettings-Verticals-Add" data-toggle="modal" data-target="#addSubVerticalModal" onclick="clearData(false)">Add</a> ');
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
    						<li class="nav-item"><a class="nav-link active" data-toggle="tab" id="verticalTab" href="#verticalss" role="tab"><span class="serial-num">1</span> Verticals</a></li>
    						<li class="nav-item"><a class="nav-link" data-toggle="tab" id="sverticalTab" href="#svertical" role="tab"><span class="serial-num">2</span> Sub Verticals</a></li>
 				 		</ul>
 					</div>

                    <div class="tab-content" style="margin-top: 34px;width: 82%;">
    					<div id="verticalss" class="tab-pane active" style="margin-top: 14px;">
    						
     							<div class="tabtable1" style="width:120%">
        							<table id="dbTable1" class="display row-border dataTable meterialform">
            							<thead>
                							<tr>
                                    			<th class="text-center">Vertical Name</th>
                                    			<th class="text-center">Vertical Address</th> 
                                    			<th class="text-center">Action</th>
                               				 </tr>
                           				</thead>
                            			<tbody>
										<c:choose>
											<c:when test="${not empty companyUser}">
												<c:if test='${not empty companyUser.vertical}'>
													<c:forEach items = "${companyUser.vertical}" var="vertical">
															<c:forEach items="${client.verticals}" var="verticals">
																<c:if test='${vertical eq verticals.name}'>
																<tr id="row${branchs.id}">
																	<td align="left">${verticals.name}</td>
																	<td align="left">${verticals.address}</td>
																	<td class="actionicons"><a class="btn-edt permissionSettings-Verticals-Edit" href="#" data-toggle="modal" data-target="#addVerticalModal" onClick="populateElement('${verticals.id}', '${verticals.name}', '${verticals.address}', 'Vertical')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Verticals-Delete" onClick="showDeletePopup('${verticals.id}','${verticals.name}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
																</tr>
																</c:if>
															</c:forEach>
													</c:forEach>
												</c:if>
											</c:when>
											<c:otherwise>
												<c:forEach items="${client.verticals}" var="vertical">
													<tr id="row${vertical.id}">
														<td align="left">${vertical.name}</td>
														<td align="left">${vertical.address}</td>
														<td class="actionicons"><a class="btn-edt permissionSettings-Verticals-Edit" href="#" data-toggle="modal" data-target="#addVerticalModal" onClick="populateElement('${vertical.id}', '${vertical.name}', '${vertical.address}', 'Vertical')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Verticals-Delete" onClick="showDeletePopup('${vertical.id}','${vertical.name}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
													</tr>
											</c:forEach>
											</c:otherwise>
										</c:choose>
                            			</tbody>
                        			</table>
								</div>
						</div>
                        <div id="svertical" class="tab-pane ">
    							<div class="tabtable2 mt-3" style="width:123%">
        							<table id="dbTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
            							<thead>
                							<tr>
                                    			<th class="text-center">Vertical Name</th>
                                    			<th class="text-center">Sub Vertical Name</th>
                                    			<th class="text-center">Sub Vertical Address</th> 
                                    			<th class="text-center">Action</th>
                                			</tr>
                            			</thead>
                            			<tbody>
										<c:choose>
											<c:when test="${not empty companyUser}">
												<c:if test='${not empty companyUser.vertical}'>
													<c:forEach items = "${companyUser.vertical}" var="vertical">
													<c:forEach items="${client.verticals}" var="verticals">
													<c:if test='${vertical eq verticals.name}'>
													<c:if test="${not empty verticals.subverticals}">
															<c:forEach items="${verticals.subverticals}" var="subvertical">
														<tr id="row${subvertical.id}" class="row${verticals.id}">
                                							<td align="left">${verticals.name}</td>
															<td align="left">${subvertical.name}</td>
															<td align="left">${subvertical.address}</td>
                                							<td class="actionicons"><a class="btn-edt permissionSettings-Verticals-Edit" href="#" data-toggle="modal" data-target="#addSubVerticalModal" onClick="populateSubVerticalElement('${subvertical.id}','${verticals.id}', '${subvertical.name}', '${subvertical.address}', 'SubVertical')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Verticals-Delete" onClick="showSubVerticalDeletePopup('${subvertical.id}','${subvertical.name}','${verticals.id}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
                            							</tr>
													</c:forEach>
													 </c:if>	
														</c:if>
														</c:forEach>
													</c:forEach>
												</c:if>
											</c:when>
											<c:otherwise>
												<c:forEach items="${client.verticals}" var="vertical">
												<c:if test="${not empty vertical.subverticals}">
                                					<c:forEach items="${vertical.subverticals}" var="subvertical">
														<tr id="row${subvertical.id}" class="row${vertical.id}">
                                    						<td align="left">${vertical.name}</td>
															<td align="left">${subvertical.name}</td>
															<td align="left">${subvertical.address}</td>
                                    						<td class="actionicons"><a class="btn-edt permissionSettings-Verticals-Edit" href="#" data-toggle="modal" data-target="#addSubVerticalModal" onClick="populateSubVerticalElement('${subvertical.id}','${vertical.id}', '${subvertical.name}', '${subvertical.address}', 'SubVertical')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Verticals-Delete" onClick="showSubVerticalDeletePopup('${subvertical.id}','${subvertical.name}','${vertical.id}','${client.id}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
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
    <div class="modal fade" id="addVerticalModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" modelAttribute="Vertical" name="userform">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Vertical<br /><span class="caption">Your the adding vertical under ${client.businessname} !</span></h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright">
                    <!-- row begin -->
                    <div class="row  p-3">
						<form:form method="POST" data-toggle="validator" class="meterialform" name="userform" style="border:none;padding:0px;margin:0px;">
                        <div class="verticalbox addvertical row m-0">
                            <div class="col-sm-12 f-18-b">Add your Vertical Details.</div>
                            <div class="form-group col-md-6 col-sm-12 verticalerror" id="verticalerror">
                                <p class="lable-txt astrich"> Vertical Name</p>
                                <span class="errormsg refreshpage" id="verticalName_Msg" style="margin-top: -18px;"></span><span class="errormsg vname" style="margin-top: -18px;"></span>
                                <input type="text" id="verticalName" name="verticalName" required="required" data-error="Please enter the vertical name" onchange="updateDetails()" placeholder="Jane Smith" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12 verticalerror" id="verticalerror1">
                                <p class="lable-txt astrich">Vertical Address</p><span class="errormsg vaddr" style="margin-top: -18px;"></span>
                                <span class="errormsg refreshpage" id="verticalAddress_Msg" style="margin-top: -18px;"></span>
                                <input type="text" id="verticalAddress" name="verticalAddress" class="mapicon" required="required" onchange="updateDetails()" placeholder="MG Road, Plot 30, India" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>

                        <div class=" col-12 mt-4 text-center" style="display:block">
                            <a type="button" onClick="branchadd('Vertical')" class="btn btn-blue-dark vertical_submit" style="display:none"><span aria-hidden="true">SAVE</span></a>
						</div>
						</form:form>
                    </div>

                    <!-- row end -->

                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="vertical_submit" class="btn btn-blue-dark m-0" tabindex="0" onClick="branchadd('Vertical')">Save</label>
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>	
				</div>
            </div>
        </div>
    </div>
    <!-- Add Branch Modal End -->
	<!-- Add Sub Branch Modal Start -->
    <div class="modal fade" id="addSubVerticalModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" modelAttribute="Vertical" name="userform">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Sub vertical<br /><span class="caption">Your the adding sub vertical under ${client.businessname} !</span></h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright">
                    <!-- row begin -->
                    <div class="row  p-3">
                        <form:form method="POST" data-toggle="validator" class="meterialform" name="userform" style="border:none;padding:0px;margin:0px;">
                        <div class="verticalbox addsubVertical row m-0">
                            <div class="col-sm-12 f-18-b">Add your Sub Vertical Details.</div>
                            <div class="form-group col-md-6 col-sm-12 verticalerror">
                                <p class="lable-txt astrich"> Vertical Name</p>
                                <span class="errormsg refreshpage" id="parentVertical_Msg" style="margin-top: -18px;"></span>
								<select id="parentVertical" class="mt-1" required="required" data-error="Please enter vertical name" onchange="updateDetails()" name="parentVertical" value="" >
								<option value=""> - Select - </option>
								<c:choose>
									<c:when test="${not empty companyUser}">
										<c:if test='${not empty companyUser.vertical}'>
											<c:forEach items = "${companyUser.vertical}" var="vertical">
												<c:forEach items="${client.verticals}" var="verticals">
													<c:if test='${vertical eq verticals.name}'>
														<option value="${verticals.id}">${verticals.name}</option>
													</c:if>
												</c:forEach>
											</c:forEach>
										</c:if>		
									</c:when>
									<c:otherwise>
										<c:forEach items="${client.verticals}" var="vertical">
										<option value="${vertical.id}">${vertical.name}</option>
										</c:forEach>
									</c:otherwise>
								</c:choose>
								</select>
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12 verticalerror" id="sverticalerror">
                                <p class="lable-txt astrich"> Sub Vertical Name</p>
                                <span class="errormsg refreshpage" id="subVerticalName_Msg" style="margin-top: -18px;"></span><span class="errormsg svname" style="margin-top: -18px;"></span>
                                <input type="text" id="subVerticalName" name="subVerticalName" required="required" onchange="updateDetails()" data-error="Please enter the Sub Vertical name for the vertical" placeholder="Jane Smith" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12 verticalerror veradd_div" id="sverticalerror1">
                                <p class="lable-txt astrich">Vertical Address</p>
                                <span class="errormsg refreshpage" id="subVerticalAddress_Msg" style="margin-top: -18px;"></span><span class="errormsg svaddr" style="margin-top: -18px;"></span>
                                <input type="text" id="subVerticalAddress" name="verticalAddress" class="mapicon" onchange="updateDetails()" required="required" placeholder="MG Road, Plot 30, India" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>

                        <div class=" col-12 mt-4 text-center" style="display:block">
                            <a type="button" onClick="branchadd('SubVertical')" class="btn btn-blue-dark subvertical_submit" style="display:none"><span aria-hidden="true">SAVE</span></a>
						</div>
						</form:form>
                    </div>

                    <!-- row end -->

                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="subvertical_submit" class="btn btn-blue-dark m-0" tabindex="0" onClick="branchadd('SubVertical')">Save</label>
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
          <h3>Delete Vertical</h3>
        </div>
        <div class="pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Vertical <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Vertical</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
    <script>
	function clearData(isVertical) {
		currentId = '';
		if(isVertical) {
			$('.form-group').removeClass('has-error has-danger');
			$('.errormsg.refreshpage').html('');
			$('#verticalName').val('');
			$('#verticalAddress').val('');
			$('.vname').text('');
			$('.vaddr').text('');
			$('.svname').text('');
			$('.svaddr').text('');
		} else {
			$('.svname').text('');
			$('.svaddr').text('');
			$('.form-group').removeClass('has-error has-danger');
			$('.errormsg.refreshpage').html('');
			$('#parentVertical').val('');
			$('#subVerticalName').val('');
			$('#subVerticalAddress').val('');
		}
	}
    function populateElement(id, name, address, type) {
		currentId = id;
		$('.addvertical').val('');
		if(type == 'Vertical') {
			$('#verticalName').val(name);
			$('#verticalAddress').val(address);
		} else {
			$('#parentVertical').val(id);
			$('#subVerticalName').val(name);
			$('#subVerticalAddress').val(address);
		}
	}
    function populateSubVerticalElement(verticalid,id, name, address, type) {
		currentId = verticalid;
		$('.addvertical').val('');
		if(type == 'Vertical') {
			$('#verticalName').val(name);
			$('#verticalAddress').val(address);
		} else {
			$('#parentVertical').val(id);
			$('#subVerticalName').val(name);
			$('#subVerticalAddress').val(address);
		}
	}
    $('#parentVertical').select2();

    function addClientInfoValidation(type) {
		var verticalName = $('#verticalName').val();
		var verticalAddress = $('#verticalAddress').val();
		var subVerticalName = $('#subVerticalName').val();
		var subVerticalAddress = $('#subVerticalAddress').val();
		var parentVertical = $('#parentVertical').val();
		var c=0;
		if(type == 'Vertical') {	
			if(verticalName ==""){
				$('#verticalName_Msg').text("Please Enter Vertical Name"); 
				c++;
			}else{
				$('#verticalName_Msg').text(""); 
			}
			if(verticalAddress ==""){
				$('#verticalAddress_Msg').text("Please Enter Vertical Address"); 
				c++;
			}else{
				$('#verticalAddress_Msg').text(""); 
			}
		}else{
			if(subVerticalName ==""){
				$('#subVerticalName_Msg').text("Please Enter Sub Vertical Name"); 
				c++;
			}else{
				$('#subVerticalName_Msg').text(""); 
			}
			if(subVerticalAddress ==""){
				$('#subVerticalAddress_Msg').text("Please Enter Sub Vertical Address"); 
				c++;
			}else{
				$('#subVerticalAddress_Msg').text(""); 
			}
			if(parentVertical ==""){
				$('#parentVertical_Msg').text("Please select Parent Vertical"); 
				c++;
			}else{
				$('#parentVertical_Msg').text(""); 
			}
		}
		return c==0; 
    }
    function branchadd(type){
    	
    	var name, address, parentId='';
    	var svname, svaddr;
		var clientid='<c:out value="${client.id}"/>';
		var err=0;
		
		if(type == 'Vertical') {
			name = $('#verticalName').val();
			address = $('#verticalAddress').val();
		} else {
			svname = $('#subVerticalName').val();
			svaddr = $('#subVerticalAddress').val();
			parentId = $('#parentVertical').val();
		}
		
    	if(addClientInfoValidation(type)){
    		if(type == 'Vertical'){
    	$.ajax({
    		type : "GET",
    		async: false,
    		contentType : "application/json",
    		url: "${contextPath}/verticalsexits/"+clientid,
    		data:{
				'name': name,
				'address': address
			},
    		success : function(response) {
    			if(response == true){
    				$('.vname').text('vertical name already exists');
    				$('.vaddr').text('vertical address already exists');
    				$('#verticalerror').addClass('has-error has-danger');
    				$('#verticalerror1').addClass('has-error has-danger');
    				err = 1;
    				}else{
    				$('.vname').text('');
    				$('.vaddr').text('');
    				$('#verticalerror').removeClass('has-error has-danger');
    				$('#verticalerror1').removeClass('has-error has-danger');
    				addClientInfo(type);
    			}
    		}
    		
    	});
    	} else{
       			$.ajax({
    				type : "GET",
    				async: false,
    				contentType : "application/json",
    				url: "${contextPath}/subverticalsexits/"+clientid,
    				data:{
    					'name': name,
    					'address': address
    				},
    				success : function(response) {
       					if(response == true){
       						$('.svname').text('Subvertical name already exists');
    						$('.svaddr').text('Subvertical address already exists');
    						$('#sverticalerror').addClass('has-error has-danger');
    						$('#sverticalerror1').addClass('has-error has-danger');
    						err = 1;
    						} else{
    						$('.svname').text('');
    						$('.svaddr').text('');
    						$('#sverticalerror').removeClass('has-error has-danger');
    						$('#sverticalerror1').removeClass('has-error has-danger');
    						addClientInfo(type);
    					}
    				}
    				
    			});
    		}
    	}
    }
    
    function addClientInfo(type) {
		var name, address, parentId='';
		
			if(type == 'Vertical') {
				name = $('#verticalName').val();
				address = $('#verticalAddress').val();
			} else {
				$('#sverticalTab').click();
				name = $('#subVerticalName').val();
				address = $('#subVerticalAddress').val();
				parentId = $('#parentVertical').val();
			}
			
			$.ajax({
			url: "${contextPath}/addvertical/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}",
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
				if(type == 'Vertical'){
				location.href="${contextPath}/cp_verticals/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=";
				} else{
					location.href="${contextPath}/cp_verticals/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}?type=compsvrtl";
				}
			},
			error : function(data) {
			}
		});
		
    }
    
    function displayTable() {
    	var a=document.getElementByName("vertical");
    	if(a == "vertical") {
			$('#dbTable2').css("display","none");
    	} else {
			$('#dbTable1').css("display","none");
    	}
    }
	function showDeletePopup(verticalId, name, clientid) {
		$('#deleteModal').modal('show');
		$('#delPopupDetails').html(name);
		$('#btnDelete').attr('onclick', "deleteVertical('"+verticalId+"', '"+clientid+"')");
	}
	
	function showSubVerticalDeletePopup(subVerticalId, name, verticalId, clientid){
		$('#deleteModal').modal('show');
		$('#delPopupDetails').html(name);
		$('#btnDelete').attr('onclick', "deleteSubVertical('"+subVerticalId+"','"+verticalId+"', '"+clientid+"')");
	}

	function deleteVertical(verticalId,clientid) {
		$.ajax({
			url: "${contextPath}/delvertical/"+verticalId+"/"+clientid,
			success : function(response) {
				table.row( $('#row'+verticalId) ).remove().draw();
				subTable.rows( $('.row'+verticalId) ).remove().draw();
				$("#parentVertical option[value='"+verticalId+"']").remove();
			}
		});
	}
	
	function deleteSubVertical(subVerticalId,verticalId,clientid) {
		$.ajax({
			url: "${contextPath}/delSubVertical/"+subVerticalId+"/"+verticalId+"/"+clientid,
			success : function(response) {
				subTable.row( $('#row'+subVerticalId) ).remove().draw();
			}
		});
	}
	function updateDetails(value){
		var verticalName = $('#verticalName').val();
		var verticalAddress = $('#verticalAddress').val();
		var subVerticalName = $('#subVerticalName').val();
		var subVerticalAddress = $('#subVerticalAddress').val();
		var parentVertical = $('#parentVertical').val();
		var clientid='<c:out value="${client.id}"/>';
		
		$.ajax({
		type : "GET",
		async: false,
		contentType : "application/json",
		url: "${contextPath}/verticalsexits/"+clientid+"/"+verticalName+"/"+verticalAddress,
		success : function(response) {
			if(response == true){
				$('.vname').text('vertical name already exists');
				$('.vaddr').text('vertical address already exists');
				$('#verticalerror').addClass('has-error has-danger');
				$('#verticalerror1').addClass('has-error has-danger');
				
				}else{
				$('.vname').text('');
				$('.vaddr').text('');
				$('#verticalerror').removeClass('has-error has-danger');
				$('#verticalerror1').removeClass('has-error has-danger');
			}
		}
		
	});
		$.ajax({
			type : "GET",
			async: false,
			contentType : "application/json",
			url: "${contextPath}/subverticalsexits/"+clientid+"/"+subVerticalName+"/"+subVerticalAddress,
			success : function(response) {
				if(response == true){
					$('.svname').text('Subvertical name already exists');
					$('.svaddr').text('Subvertical address already exists');
					$('#sverticalerror').addClass('has-error has-danger');
					$('#sverticalerror1').addClass('has-error has-danger');
					
					}else{
					$('.svname').text('');
					$('.svaddr').text('');
					$('#sverticalerror').removeClass('has-error has-danger');
					$('#sverticalerror1').removeClass('has-error has-danger');
				}
			}
			
		});
		
		if(verticalName == ''){
			$('#verticalName_Msg').show();
		} else{
			$('#verticalName_Msg').hide();
		}
		if(verticalAddress == ''){
			$('#verticalAddress_Msg').show();
		} else{
			$('#verticalAddress_Msg').hide();
		}
		if(subVerticalName == ''){
			$('#subVerticalName_Msg').show();
		} else{
			$('#subVerticalName_Msg').hide();
		}
		if(subVerticalAddress == ''){
			$('#subVerticalAddress_Msg').show();
		} else{
			$('#subVerticalAddress_Msg').hide();
		}
		if(parentVertical == ''){
			$('#parentVertical_Msg').show();
		} else{
			$('#parentVertical_Msg').hide();
		}
	}
    </script>
</body>

</html>