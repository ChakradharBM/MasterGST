<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Roles</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-cp/dashboard-cp.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-ca/dashboard-cas.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/profileedit/roles.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style>.tooltip{width:200px}
div#dbTable1_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;}
#addModal .modal-body.meterialform.bs-fancy-checks , .edit_modal .modal-body.meterialform.bs-fancy-checks{height: calc(100vh - 150px);overflow-y: auto;overflow-x: hidden;}
#addModal .modal-dialog.modal-lg.modal-right , .edit_modal .modal-dialog.modal-lg.modal-right{overflow: hidden;width: 900px;  max-width: max-content;} 
</style>
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

<!--- breadcrumb start -->
 
<c:choose>
<c:when test='${not empty client && not empty client.id}'>
<div class="breadcrumbwrap">
</c:when>
<c:otherwise>
<div class="breadcrumbwrap">
</c:otherwise>
</c:choose>
<div class="container">
	<div class="row">
        <div class="col-md-12 col-sm-12">
				<ol class="breadcrumb">
					<c:choose>
					<c:when test='${not empty client && not empty client.id}'>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active">Company Profile</li>
					</c:when>
					<c:otherwise>
					<li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li>
					<li class="breadcrumb-item active">Roles</li>
					</c:otherwise>
					</c:choose>
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

                    <!-- dashboard cp table begin -->
										
   <div class="col-md-10 col-sm-12 customtable p-0">
                        <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                    <th class="text-center">Role Name</th>
                                    <th class="text-center">Description</th>
                                    <!-- <th class="text-center">Permissions</th> -->
                                    <th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
								<c:forEach items="${roles}" var="role">
                                <tr>
                                    <td align="left">${role.name}</td>
                                    <td align="left">${role.description}</td>
									<!-- <td align="center"></td> -->
                                    <td class="actionicons"><a class="btn-edt permissionSettings-Roles-Edit" href="#" data-toggle="modal" data-target="#editModal${role.id}" onclick="editrole('${role.id}')"><i class="fa fa-edit"></i> </a></td>
                                </tr>
								</c:forEach>
                            </tbody>
                        </table>

                    </div> 
	 
  <!-- dashboard cp table end -->
</div>                    
                  
                </div>
            </div>
        </div>
<c:set var="permArray" value="${['View','Add','Edit','Delete']}" />
<c:set var="permArrayYN" value="${['View-Yes','Add-Yes','Edit-Yes','Delete-Yes']}" />
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
    <!-- Add Modal Start -->
    <div class="modal fade" id="addModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document" style="width:1000px;">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Role</h3>
                    </div>
				</div>
    			<form:form method="POST" data-toggle="validator" class="meterialform" name="roleform" action="${contextPath}/createrole/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="role">
                <div class="modal-body meterialform bs-fancy-checks">
                    
                    <!-- row begin -->
					<div class="row gstr-info-tabs pl-5 pr-5 pt-2">
                        <div class="form-group col-md-6 col-sm-12 pl-0">
                            <p class="lable-txt astrich">Role Name</p>
                            <span class="errormsg" id="userName_Msg"></span><span class="errormsg rolename" style="margin-top: -18px;"></span>
                            <input type="text" id="userName" name="name" data-minlength="3" required="required" data-error="Please enter more than 3 characters" placeholder="Name" value="" />
                            <label for="userName" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Description</p>
                            <span class="errormsg" id="role_Msg"></span>
                            <input type="text" id="description" name="description" required="required" data-error="please enter description"  placeholder="Description" value="" />
                            <label for="description" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
														
						<!-- <div class="bdr-b col-12 mb-1" >&nbsp;</div> -->
						<p class="lable-txt text-left mt-1" style="width: 100%;font-size:16px;">Select the Permissions</p>
						<ul class="nav nav-tabs" id="myTab" role="tablist" style="width:100%">
						  <li class="nav-item">
						    <a class="nav-link active" id="books-tab" data-toggle="tab" href="#books" role="tab" aria-controls="books" aria-selected="true">Books</a>
						  </li>
						   <li class="nav-item">
						    <a class="nav-link" id="returns-tab" data-toggle="tab" href="#returns" role="tab" aria-controls="returns" aria-selected="false">Returns</a>
						  </li>
						  <li class="nav-item">
						    <a class="nav-link" id="masters-tab" data-toggle="tab" href="#masters" role="tab" aria-controls="masters" aria-selected="false">Masters</a>
						  </li>
						  <li class="nav-item">
						    <a class="nav-link" id="exdown-tab" data-toggle="tab" href="#exdown" role="tab" aria-controls="exdown" aria-selected="false">Downloads</a>
						  </li>
						  <li class="nav-item">
						    <a class="nav-link" id="gaction-tab" data-toggle="tab" href="#gaction" role="tab" aria-controls="gaction" aria-selected="false">GSTN Actions</a>
						  </li>
						  <li class="nav-item">
						    <a class="nav-link" id="general-tab" data-toggle="tab" href="#general" role="tab" aria-controls="general" aria-selected="false">General</a>
						  </li>
						  <li class="nav-item">
						    <a class="nav-link" id="reports-tab" data-toggle="tab" href="#reports" role="tab" aria-controls="reports" aria-selected="false">Reports</a>
						  </li>
						   <li class="nav-item">
						    <a class="nav-link" id="newreturns-tab" data-toggle="tab" href="#newreturns" role="tab" aria-controls="newreturns" aria-selected="false">New Returns</a>
						  </li>
						    <li class="nav-item">
						    <a class="nav-link" id="newreturnActions-tab" data-toggle="tab" href="#newreturnActions" role="tab" aria-controls="newreturnActions" aria-selected="false">New Return Actions</a>
						  </li>
						  <c:if test="${acknowledgementTab eq 'tabenable'}">
							 <li class="nav-item">
							    <a class="nav-link" id="acknowledgement-tab" data-toggle="tab" href="#acknowledgement" role="tab" aria-controls="acknowledgement" aria-selected="false">Acknowledgement</a>
							  </li>
						  </c:if>
						</ul>
						<div class="tab-content" id="myTabContent" style="width: 100%;">
						  <div class="tab-pane fade show active" id="books" role="tabpanel" aria-labelledby="books-tab" >
						  <div class="row">
						  	<div class="form-group col-md-12 col-sm-12">
							<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${not empty permissions.value}'>
										<c:if test="${permissionEntry.key eq 'Invoices'}">
										<c:if test='${vloop.count == 1}'>
											<div class=""><strong>${permissionEntry.key}</strong></div>
											<div class="bdr-b col-12 mb-2"></div>
											<div class="form-group row col-md-12 col-sm-12">
												<span class="labletxt col-md-4"><strong>Module</strong></span>
												<c:forEach items="${permArray}" var="perm">
													<span class="labletxt col-md-2"><strong>${perm}</strong></span>
												</c:forEach>
											</div>
											<div class="bdr-b col-12 mb-2"></div>
										</c:if>
										<div class="form-group row col-md-12 col-sm-12">
											<div class="col-md-4">${permissions.key}</div>
											<c:forEach items="${permArray}" var="perm" varStatus="loop">
												<div class="form-check form-check-inline col-md-2 col-sm-12 p-0 m-0" style="padding-left:15px!important">
													<%-- <input class="form-check-input ${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key}-${perm}" type="checkbox" id="permission${loop.index}" name='permissions["${permissionEntry.key}-${permissions.key}-${perm}"][${loop.index}].name' value="${perm}" checked /> --%>
													<input class="form-check-input ${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key}-${perm} tooltip" data-toggle="tooltip" type="checkbox" id="${permissions.key}permission${loop.index}" name='permissions["${permissionEntry.key}-${permissions.key}-${perm}"][${loop.index}].name' value="${perm}" checked />				
													<label for="${permissions.key}permission${loop.index}"><span class="ui"></span>
													</label>
												</div>
											</c:forEach>
										</div>
										</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>
							</div>
						  </div>
						  <div class="tab-pane fade" id="masters" role="tabpanel" aria-labelledby="masters-tab">
						  	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${not empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'Settings'}">
										<c:if test='${vloop.count == 1}'>
										
											<div class=""><strong>${permissionEntry.key}</strong></div>
											<div class="bdr-b col-12 mb-2"></div>
											<div class="form-group row col-md-12 col-sm-12">
												<span class="labletxt col-md-4"><strong>Function</strong></span>
												<c:forEach items="${permArray}" var="perm">
													<span class="labletxt col-md-2"><strong>${perm}</strong></span>
												</c:forEach>
											</div>
											<div class="bdr-b col-12 mb-2"></div>
										</c:if>
										<div class="form-group row col-md-12 col-sm-12">
											<div class="col-md-4">${permissions.key}</div>
											<c:forEach items="${permArray}" var="perm" varStatus="loop">
												<div class="form-check form-check-inline col-md-2 col-sm-12 p-0 m-0" style="padding-left:15px!important">
													<%-- <input class="form-check-input ${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key}-${perm}" type="checkbox" id="permission${loop.index}" name='permissions["${permissionEntry.key}-${permissions.key}-${perm}"][${loop.index}].name' value="${perm}" checked /> --%>
													<input class="form-check-input ${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key}-${perm} tooltip" data-toggle="tooltip" type="checkbox" id="${permissions.key}permission${loop.index}" name='permissions["${permissionEntry.key}-${permissions.key}-${perm}"][${loop.index}].name' value="${perm}" checked />				
													<label for="${permissions.key}permission${loop.index}"><span class="ui"></span>
													</label>
												</div>
											</c:forEach>
										</div>
									</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
						  </div>
						  <div class="tab-pane fade" id="exdown" role="tabpanel" aria-labelledby="exdown-tab">
						  <div class="row">
						  	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<c:choose>
												<c:when test="${permissionEntry.key eq 'Excel Download In Books And Returns'}">
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
												</c:when>
												<c:when test="${permissionEntry.key eq 'Excel Download In Reports'}">
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
												</c:when>
												<c:otherwise>
													
												</c:otherwise>
											</c:choose>		
										</c:if>
										<c:choose>
											<c:when test="${permissionEntry.key eq 'Excel Download In Books And Returns'}">
												<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="expermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
														<label for="expermission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
											</c:when>
											<c:when test="${permissionEntry.key eq 'Excel Download In Reports'}">
												<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="exrpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
														<label for="exrpermission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
											</c:when>
											
											<c:otherwise>
												
											</c:otherwise>
										</c:choose>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>
						  </div>
						   <div class="tab-pane fade" id="gaction" role="tabpanel" aria-labelledby="gaction-tab">
						  	<div class="row">
						   	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'GSTN Actions'}">
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>		
												<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="gpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
														<label for="gpermission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
									</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>			
						  </div>
						  <div class="tab-pane fade" id="general" role="tabpanel" aria-labelledby="general-tab">
						  	<div class="row">
						   	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'General'}">
										<c:if test='${vloop.count == 1}'>
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
												<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="gapermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
														<label for="gapermission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
											</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'All Configurations'}">
										<c:if test='${vloop.count == 1}'>
													<div class="col-md-12 mb-2 configurationTabs"><strong>${permissionEntry.key}</strong></div>
										</c:if>
												<div class="col-md-6 col-sm-12 configurationTabs" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="acpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
														<label for="acpermission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
												
											</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>
						  </div>
						   <div class="tab-pane fade" id="reports" role="tabpanel" aria-labelledby="reports-tab">
						  	<div class="row">
						   	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'Reports'}">
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
												<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="rpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
														<label for="rpermission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
										</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							
							<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'Inventory Reports'}">
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
												<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="irpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
														<label for="irpermission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
										</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>
						  </div>
						  <div class="tab-pane fade" id="newreturns" role="tabpanel" aria-labelledby="newreturns-tab">
						  	<div class="row">
						   	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
										<c:if test="${permissionEntry.key eq 'New Returns'}">
											<c:if test='${vloop.count == 1}'>
												<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
											</c:if>
											<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
													<input class="form-check-input" type="checkbox" id="rpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													<label for="rpermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
										</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>
						  </div>
						  <div class="tab-pane fade" id="newreturnActions" role="tabpanel" aria-labelledby="newreturnActions-tab">
						   <div class="row">
						  	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<c:choose>
												<c:when test="${permissionEntry.key eq 'Ewaybill Actions'}">
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
												</c:when>
												<c:when test="${permissionEntry.key eq 'Einvoice Actions'}">
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
												</c:when>
												<c:otherwise></c:otherwise>
											</c:choose>		
										</c:if>
										<c:choose>
											<c:when test="${permissionEntry.key eq 'Ewaybill Actions'}">
												<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="actebillpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
														<label for="actebillpermission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
											</c:when>
											<c:when test="${permissionEntry.key eq 'Einvoice Actions'}">
												<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="acteinvpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
														<label for="acteinvpermission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
											</c:when>
											<c:otherwise></c:otherwise>
										</c:choose>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>
						  </div>
						  <div class="tab-pane fade" id="returns" role="tabpanel" aria-labelledby="returns-tab">
						  <div class="row">
						   	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
						 					 <c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
														<c:if test='${GSTReturnsSummury.active == "true" && permissionEntry.key eq GSTReturnsSummury.returntype}'>
															<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
														</c:if>
												  </c:forEach>
												  </c:if>
												  <c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
													<c:if test='${GSTReturnsSummury.active == "true" && permissionEntry.key eq GSTReturnsSummury.returntype}'>
														<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
															<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
																<input class="form-check-input" type="checkbox" id="${permissionEntry.key}grspermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
																<label for="${permissionEntry.key}grspermission${vloop.index}"><span class="ui"></span>
																</label> <span class="labletxt">${permissions.key}</span>
															</div>
														</div>
													</c:if>
							</c:forEach>
							</c:if>
							</c:forEach>
							</c:forEach>
							</div>
						  	<c:if test='${empty client}'>
							<div class="row pl-2">
							<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test="${permissionEntry.key eq 'GSTR1'}">
																	
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										
										<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
											<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<input class="form-check-input" type="checkbox" id="gstr1${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
												<label for="gstr1${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
												</label> <span class="labletxt">${permissions.key}</span>
											</div>
										</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR2'}">
																	
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										
										<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
											<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<input class="form-check-input" type="checkbox" id="gstr2${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
												<label for="gstr2${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
												</label> <span class="labletxt">${permissions.key}</span>
											</div>
										</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR3B'}">
																	
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										
										<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
											<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<input class="form-check-input" type="checkbox" id="gstr3b${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
												<label for="gstr3b${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
												</label> <span class="labletxt">${permissions.key}</span>
											</div>
										</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR4'}">
																	
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										
										<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
											<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<input class="form-check-input" type="checkbox" id="gstr4${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
												<label for="gstr4${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
												</label> <span class="labletxt">${permissions.key}</span>
											</div>
										</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR5'}">
																	
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										
										<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
											<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<input class="form-check-input" type="checkbox" id="gstr5${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
												<label for="gstr5${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
												</label> <span class="labletxt">${permissions.key}</span>
											</div>
										</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR6'}">
																	
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										
										<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
											<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<input class="form-check-input" type="checkbox" id="gstr6${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
												<label for="gstr6${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
												</label> <span class="labletxt">${permissions.key}</span>
											</div>
										</div>				
									</c:if>
									</c:if>
									<c:if test="${cuser.accessMultiGSTNSearch eq true || cuser.accessMultiGSTNSearch eq 'true'}">
									<c:if test="${permissionEntry.key eq 'Multi GSTIN'}">						
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
											<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<input class="form-check-input" type="checkbox" id="multigstn${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
												<label for="multigstn${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
												</label> <span class="labletxt">${permissions.key}</span>
											</div>
										</div>				
									</c:if>
									</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>
						</c:if>	
						  </div>
						  <div class="tab-pane fade" id="acknowledgement" role="tabpanel" aria-labelledby="acknowledgement-tab">
											<div class="row">
						  	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
								  	<c:if test="${permissionEntry.key eq 'Acknowledgement'}">
										<c:if test='${empty permissions.value}'>
											<c:if test='${vloop.count == 1}'>
												<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
											</c:if>
												<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
													<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<input class="form-check-input" type="checkbox" id="${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}"/>
														<label for="${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
														</label> <span class="labletxt">${permissions.key}</span>
													</div>
												</div>
										</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
											</div>				
						  </div>
						</div>
						</div>
					</div>
					<div class="bdr-b col-12" style="display:block">&nbsp;</div>
                     <div class=" col-12 mt-2 mb-2 text-center" style="display:block">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<c:if test='${not empty client && not empty client.id}'>
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
							</c:if>
							<input type="submit" class="btn btn-blue-dark" value="SAVE"/>
							<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>						
						</div>
						</form:form>
                    </div>
					<!-- row end -->
                </div>
            </div>
    <!-- Add Modal End -->
		 <!-- Edit Modal Start -->
	<c:forEach items="${roles}" var="role">
    <div class="modal fade edit_modal" id="editModal${role.id}" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document" style="width:1000px">
            <div class="modal-content">
            <div class="modal-header p-0">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span></button>
                    <div class="bluehdr" style="width:100%"><h3>Edit Role</h3></div></div>
                    <!-- row begin -->
					<form:form method="POST" id="edit_role" data-toggle="validator" class="meterialform" name="roleform" action="${contextPath}/createrole/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="role">
					<div class="modal-body meterialform bs-fancy-checks">
					<input type="hidden" name="id" value="${role.id}" />
					<input type="hidden" name="duplicate_username" id="duplicate_username" value="${role.name}"/>
					
                    <div class="row  pl-5 pr-5 pt-1 gstr-info-tabs">
                        <div class="form-group col-md-6 col-sm-12 pl-0">
                            <p class="lable-txt astrich">Role Name</p>
                            <span class="errormsg" id="userName_Msg"></span>
                            <input type="text" id="userName" name="name" data-minlength="3" required="required" data-error="Please enter more than 3 characters" placeholder="1001" value="${role.name}" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Description</p>
                            <span class="errormsg" id="role_Msg"></span>
                            <input type="text" id="description" name="description" required="required" placeholder="190" value="${role.description}" />
                            <label for="description" class="control-label"></label>
                            <i class="bar"></i> </div>
						<!-- <div class="bdr-b col-12 mb-1" style="display:block">&nbsp;</div> -->
						<p class="lable-txt text-left mt-2" style="font-size:16px;">Select the Permissions</p>
						<ul class="nav nav-tabs" id="myeditTab" role="tablist" style="width:100%">
							<li class="nav-item"><a class="nav-link active" id="ebooks-tab" data-toggle="tab" href="#ebooks${role.id}" role="tab" aria-controls="ebooks${role.id}" aria-selected="true">Books</a></li>
							<li class="nav-item"><a class="nav-link" id="ereturns-tab" data-toggle="tab" href="#ereturns${role.id}" role="tab" aria-controls="ereturns${role.id}" aria-selected="false">Returns</a></li>
							<li class="nav-item"><a class="nav-link" id="emasters-tab" data-toggle="tab" href="#emasters${role.id}" role="tab" aria-controls="emasters${role.id}" aria-selected="false">Masters</a></li>
							<li class="nav-item"><a class="nav-link" id="eexdown-tab" data-toggle="tab" href="#eexdown${role.id}" role="tab" aria-controls="eexdown${role.id}" aria-selected="false">Downloads</a></li>
							<li class="nav-item"><a class="nav-link" id="egaction-tab" data-toggle="tab" href="#egaction${role.id}" role="tab" aria-controls="egaction${role.id}" aria-selected="false">GSTN Actions</a></li>
							<li class="nav-item"><a class="nav-link" id="egeneral-tab" data-toggle="tab" href="#egeneral${role.id}" role="tab" aria-controls="egeneral${role.id}" aria-selected="false">General</a></li>
							<li class="nav-item"><a class="nav-link" id="ereports-tab" data-toggle="tab" href="#ereports${role.id}" role="tab" aria-controls="ereports${role.id}" aria-selected="false">Reports</a></li>
							<li class="nav-item"><a class="nav-link" id="enewreturns-tab" data-toggle="tab" href="#enewreturns${role.id}" role="tab" aria-controls="enewreturns${role.id}" aria-selected="false">New Returns</a></li>
							<li class="nav-item"><a class="nav-link" id="enewreturnsAct-tab" data-toggle="tab" href="#enewreturnsAct${role.id}" role="tab" aria-controls="enewreturnsAct${role.id}" aria-selected="false">New Returns Actions</a></li>
							<c:if test="${acknowledgementTab eq 'tabenable'}">
								<li class="nav-item">
							    	<a class="nav-link" id="eacknowledgement-tab" data-toggle="tab" href="#eacknowledgement${role.id}" role="tab" aria-controls="eacknowledgement${role.id}" aria-selected="false">Acknowledgement</a>
							  	</li>
						  	</c:if>
							</ul>
						<div class="tab-content" id="myeditTabContent" style="width: 100%;">
						  <div class="tab-pane fade show active" id="ebooks${role.id}" role="tabpanel" aria-labelledby="books-tab" >
						 	<div class="form-group col-md-12 col-sm-12 pl-0">
						<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
							<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
							<c:if test='${not empty permissions.value}'>
							<c:if test="${permissionEntry.key eq 'Invoices'}">
							<c:if test='${vloop.count == 1}'>
							<div class=""><strong>${permissionEntry.key}</strong></div>
							<div class="bdr-b col-12 mb-2"></div>
							<div class="form-group row col-md-12 col-sm-12">
							<span class="labletxt col-md-4"><strong>Module</strong></span>
							<c:forEach items="${permArray}" var="perm" varStatus="loop">
							<span class="labletxt col-md-2"><strong>${perm}</strong></span>
							</c:forEach>
							</div>
							<div class="bdr-b col-12 mb-2"></div>
							</c:if>
							
							<div class="form-group row col-md-12 col-sm-12">
							<div class="col-md-4">${permissions.key}</div>
							<c:forEach items="${permArray}" var="perm" varStatus="loop">
							<div class="form-check form-check-inline col-md-2 col-sm-12 p-0 m-0" style="padding-left:15px!important">
								<c:set var="contains" value="false" />
								<c:forEach items="${role.permissions[permissionEntry.key]}" var="rperm">
								<c:set var="per" value="${perm}"/>
									<%
									  String resp = "-Yes";
									  String test = pageContext.getAttribute("per").toString();
									  resp = test + resp;
									  pageContext.setAttribute("resp", resp);
									%>
								<c:if test="${permissions.key eq rperm.name && rperm.status eq resp}">
									<c:set var="contains" value="true" />
								</c:if>
								</c:forEach>
								<c:if test="${contains == true}">
								<input class="form-check-input ${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key}-${perm} tooltip" type="checkbox" id="${permissions.key}permission${loop.index}" name='permissions["${permissionEntry.key}-${permissions.key}-${perm}"][${loop.index}].name' value="${perm}" checked />
								</c:if>
								<c:if test="${contains == false}">
								<input class="form-check-input ${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key}-${perm} tooltip" type="checkbox" id="${permissions.key}permission${loop.index}" name='permissions["${permissionEntry.key}-${permissions.key}-${perm}"][${loop.index}].name' value="${perm}" />
								</c:if>
								<label for="${permissions.key}permission${loop.index}"><span class="ui"></span>
								</label>
							</div>
							</c:forEach>
							</div>
							</c:if>
							</c:if>
							</c:forEach>
							</c:forEach>
						 </div>
						  </div>
						  <div class="tab-pane fade" id="emasters${role.id}" role="tabpanel" aria-labelledby="emasters-tab">
						  <div class="form-group col-md-12 col-sm-12 pl-0">
						<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
							<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
							<c:if test='${not empty permissions.value}'>
							<c:if test="${permissionEntry.key eq 'Settings'}">
							<c:if test='${vloop.count == 1}'>
							<div class=""><strong>${permissionEntry.key}</strong></div>
							<div class="bdr-b col-12 mb-2"></div>
							<div class="form-group row col-md-12 col-sm-12">
							<span class="labletxt col-md-4"><strong>Function</strong></span>
							<c:forEach items="${permArray}" var="perm" varStatus="loop">
							<span class="labletxt col-md-2"><strong>${perm}</strong></span>
							</c:forEach>
							</div>
							<div class="bdr-b col-12 mb-2"></div>
							</c:if>
							<div class="form-group row col-md-12 col-sm-12">
							<div class="col-md-4">${permissions.key}</div>
							<c:forEach items="${permArray}" var="perm" varStatus="loop">
							<div class="form-check form-check-inline col-md-2 col-sm-12 p-0 m-0" style="padding-left:15px!important">
								<c:set var="contains" value="false" />
								<c:forEach items="${role.permissions[permissionEntry.key]}" var="rperm">
								<c:set var="per" value="${perm}"/>
									<%
									  String resp = "-Yes";
									  String test = pageContext.getAttribute("per").toString();
									  resp = test + resp;
									  pageContext.setAttribute("resp", resp);
									%>
								<c:if test="${permissions.key eq rperm.name && rperm.status eq resp}">
									<c:set var="contains" value="true" />
								</c:if>
								</c:forEach>
								<c:if test="${contains == true}">
								<input class="form-check-input ${role.id}-${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key}-${perm} tooltip" type="checkbox" id="${permissions.key}permission${loop.index}" name='permissions["${permissionEntry.key}-${permissions.key}-${perm}"][${loop.index}].name' value="${perm}" checked />
								</c:if>
								<c:if test="${contains == false}">
								<input class="form-check-input ${role.id}-${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key} ${permissionEntry.key}-${permissions.key}-${perm} tooltip" type="checkbox" id="${permissions.key}permission${loop.index}" name='permissions["${permissionEntry.key}-${permissions.key}-${perm}"][${loop.index}].name' value="${perm}" />
								</c:if>
								<label for="${permissions.key}permission${loop.index}"><span class="ui"></span>
								</label>
							</div>
							</c:forEach>
							</div>
							</c:if>
							</c:if>
							</c:forEach>
							</c:forEach>
						 </div>
						  </div>
						  <div class="tab-pane fade" id="eexdown${role.id}" role="tabpanel" aria-labelledby="eexdown-tab">
						   <div class="row">
						  <c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<c:choose>
												<c:when test="${permissionEntry.key eq 'Excel Download In Books And Returns'}">
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
												</c:when>
												<c:when test="${permissionEntry.key eq 'Excel Download In Reports'}">
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
												</c:when>
												<c:otherwise></c:otherwise>
											</c:choose>		
										</c:if>
						   	<c:choose>
						  		<c:when test="${permissionEntry.key eq 'Excel Download In Books And Returns'}">
							 				<div class="form-group row col-md-6 col-sm-6 pl-5" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<c:set var="contains" value="false" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
													<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
														<c:set var="contains" value="true" />
													</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
													<input class="form-check-input" type="checkbox" id="expermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
													<input class="form-check-input" type="checkbox" id="expermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="expermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
											</c:when>
											<c:when test="${permissionEntry.key eq 'Excel Download In Reports'}">
							 				<div class="form-group row col-md-6 col-sm-6 pl-5" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<c:set var="contains" value="false" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
													<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
														<c:set var="contains" value="true" />
													</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
													<input class="form-check-input" type="checkbox" id="exrpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
													<input class="form-check-input" type="checkbox" id="exrpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="exrpermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
											</c:when>
											<c:otherwise></c:otherwise>
								</c:choose>
							</c:if>
							</c:forEach>
							</c:forEach>
							</div>
						  </div>
						  <div class="tab-pane fade" id="egaction${role.id}" role="tabpanel" aria-labelledby="egaction-tab">
						   <div class="row">
						  <c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'GSTN Actions'}">
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>	
						  					<div class="form-group row col-md-6 col-sm-6 pl-5" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<c:set var="contains" value="false" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
													<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
														<c:set var="contains" value="true" />
													</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
													<input class="form-check-input" type="checkbox" id="gapermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
													<input class="form-check-input" type="checkbox" id="gapermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="gapermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
							</div>
						  </c:if>
						  </c:if>
						  </c:forEach>
						  </c:forEach>
						  </div>
						  </div>
						  <div class="tab-pane fade" id="egeneral${role.id}" role="tabpanel" aria-labelledby="egeneral-tab">
						   <div class="row">
						   <c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'General'}">
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>	
						  					<div class="form-group row col-md-6 col-sm-6 pl-5" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<c:set var="contains" value="false" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
													<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
														<c:set var="contains" value="true" />
													</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
													<input class="form-check-input" type="checkbox" id="gpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
													<input class="form-check-input" type="checkbox" id="gpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="gpermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
										</c:if>
									</c:if>
								</c:forEach>
								</c:forEach>
								
								<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'All Configurations'}">
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2 configurationTabs"><strong>${permissionEntry.key}</strong></div>
										</c:if>	
						  					<div class="form-group row col-md-6 col-sm-6 pl-5 configurationTabs" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<c:set var="contains" value="false" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
													<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
														<c:set var="contains" value="true" />
													</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
													<input class="form-check-input" type="checkbox" id="acermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
													<input class="form-check-input" type="checkbox" id="acermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="gpermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
										</c:if>
									</c:if>
								</c:forEach>
								</c:forEach>
							</div>
						  </div>
						  <div class="tab-pane fade" id="enewreturns${role.id}" role="tabpanel" aria-labelledby="enewreturns-tab">
						   <div class="row">
						   <c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
										<c:if test="${permissionEntry.key eq 'New Returns'}">
											<c:if test='${vloop.count == 1}'>
												<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
											</c:if>	
						  					<div class="form-group row col-md-6 col-sm-6 pl-5" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
													<c:set var="contains" value="false" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
														<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
															<c:set var="contains" value="true" />
														</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
														<input class="form-check-input" type="checkbox" id="rpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
														<input class="form-check-input" type="checkbox" id="rpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="rpermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
										</c:if>
									</c:if>
								</c:forEach>
								</c:forEach>
							</div>
						  </div>
				      <div class="tab-pane fade" id="enewreturnsAct${role.id}" role="tabpanel" aria-labelledby="enewreturnsAct-tab">
						   <div class="row">
						  <c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<c:choose>
												<c:when test="${permissionEntry.key eq 'Ewaybill Actions'}">
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
												</c:when>
												<c:when test="${permissionEntry.key eq 'Einvoice Actions'}">
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
												</c:when>
												<c:otherwise></c:otherwise>
											</c:choose>		
										</c:if>
						   	<c:choose>
						  		<c:when test="${permissionEntry.key eq 'Ewaybill Actions'}">
							 				<div class="form-group row col-md-6 col-sm-6 pl-5" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<c:set var="contains" value="false" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
													<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
														<c:set var="contains" value="true" />
													</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
													<input class="form-check-input" type="checkbox" id="expermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
													<input class="form-check-input" type="checkbox" id="expermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="expermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
											</c:when>
											<c:when test="${permissionEntry.key eq 'Einvoice Actions'}">
							 				<div class="form-group row col-md-6 col-sm-6 pl-5" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<c:set var="contains" value="false" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
													<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
														<c:set var="contains" value="true" />
													</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
													<input class="form-check-input" type="checkbox" id="exrpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
													<input class="form-check-input" type="checkbox" id="exrpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="exrpermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
											</c:when>
											<c:otherwise></c:otherwise>
								</c:choose>
							</c:if>
							</c:forEach>
							</c:forEach>
							</div>
						  </div>
						  <div class="tab-pane fade" id="ereports${role.id}" role="tabpanel" aria-labelledby="ereports-tab">
						   <div class="row">
						   <c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'Reports'}">
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>	
						  					<div class="form-group row col-md-6 col-sm-6 pl-5" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<c:set var="contains" value="false" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
													<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
														<c:set var="contains" value="true" />
													</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
													<input class="form-check-input" type="checkbox" id="rpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
													<input class="form-check-input" type="checkbox" id="rpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="rpermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
										</c:if>
									</c:if>
								</c:forEach>
								</c:forEach>
								
								<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test='${empty permissions.value}'>
									<c:if test="${permissionEntry.key eq 'Inventory Reports'}">
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>	
						  					<div class="form-group row col-md-6 col-sm-6 pl-5" >
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
												<c:set var="contains" value="true" />
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
													<c:if test="${permissions.key eq permission.name && permission.status eq 'No'}">
														<c:set var="contains" value="false" />
													</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
													<input class="form-check-input" type="checkbox" id="irpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
													<input class="form-check-input" type="checkbox" id="irpermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="irpermission${vloop.index}"><span class="ui"></span>
													</label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
										</c:if>
									</c:if>
								</c:forEach>
								</c:forEach>
							</div>
						  </div>
						  <div class="tab-pane fade" id="ereturns${role.id}" role="tabpanel" aria-labelledby="ereturns-tab">
						   <div class="row">
						  	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
							<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
							<c:if test='${empty permissions.value}'>
								<c:if test='${vloop.count == 1}'>
											<c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
												<c:if test='${GSTReturnsSummury.active == "true" && permissionEntry.key eq GSTReturnsSummury.returntype}'>
													<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
												</c:if>
											</c:forEach>
								</c:if>
								<c:set var="contains" value="false" />
									<c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
										<c:if test='${GSTReturnsSummury.active == "true" && permissionEntry.key eq GSTReturnsSummury.returntype}'>
											<div class="form-group row col-md-6 col-sm-6 pl-5">
												<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
													<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
														<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
															<c:set var="contains" value="true" />
														</c:if>
													</c:forEach>
													<c:if test="${contains == true}">
														<input class="form-check-input" type="checkbox" id="grspermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
													</c:if>
													<c:if test="${contains == false}">
														<input class="form-check-input" type="checkbox" id="grspermission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
													</c:if>
													<label for="grspermission${vloop.index}"><span class="ui"></span></label> <span class="labletxt">${permissions.key}</span>
												</div>
											</div>
										</c:if>
									</c:forEach>
							</c:if>
							</c:forEach>
							</c:forEach>
						
						<c:if test='${empty client}'>
							<div class="row pl-5 ">
							<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
									<c:if test="${permissionEntry.key eq 'GSTR1'}">						
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										<c:set var="contains" value="false" />
										<div class="form-group row col-md-6 col-sm-6 pl-5">
															<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
															<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
																<c:set var="contains" value="true" />
															</c:if>
															</c:forEach>
															<c:if test="${contains == true}">
															<input class="form-check-input" type="checkbox" id="gstr1${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
															</c:if>
															<c:if test="${contains == false}">
															<input class="form-check-input" type="checkbox" id="gstr1${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
															</c:if>
															<label for="gstr1${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
															</label> <span class="labletxt">${permissions.key}</span>
															</div>
															</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR2'}">						
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										<c:set var="contains" value="false" />
										<div class="form-group row col-md-6 col-sm-6 pl-5">
															<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
															<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
																<c:set var="contains" value="true" />
															</c:if>
															</c:forEach>
															<c:if test="${contains == true}">
															<input class="form-check-input" type="checkbox" id="gstr2${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
															</c:if>
															<c:if test="${contains == false}">
															<input class="form-check-input" type="checkbox" id="gstr2${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
															</c:if>
															<label for="gstr2${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
															</label> <span class="labletxt">${permissions.key}</span>
															</div>
															</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR3B'}">						
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										<c:set var="contains" value="false" />
										<div class="form-group row col-md-6 col-sm-6 pl-5">
															<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
															<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
																<c:set var="contains" value="true" />
															</c:if>
															</c:forEach>
															<c:if test="${contains == true}">
															<input class="form-check-input" type="checkbox" id="gstr3b${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
															</c:if>
															<c:if test="${contains == false}">
															<input class="form-check-input" type="checkbox" id="gstr3b${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
															</c:if>
															<label for="gstr3b${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
															</label> <span class="labletxt">${permissions.key}</span>
															</div>
															</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR4'}">						
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										<c:set var="contains" value="false" />
										<div class="form-group row col-md-6 col-sm-6 pl-5">
															<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
															<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
																<c:set var="contains" value="true" />
															</c:if>
															</c:forEach>
															<c:if test="${contains == true}">
															<input class="form-check-input" type="checkbox" id="gstr4${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
															</c:if>
															<c:if test="${contains == false}">
															<input class="form-check-input" type="checkbox" id="gstr4${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
															</c:if>
															<label for="gstr4${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
															</label> <span class="labletxt">${permissions.key}</span>
															</div>
															</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR5'}">						
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										<c:set var="contains" value="false" />
										<div class="form-group row col-md-6 col-sm-6 pl-5">
															<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
															<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
																<c:set var="contains" value="true" />
															</c:if>
															</c:forEach>
															<c:if test="${contains == true}">
															<input class="form-check-input" type="checkbox" id="gstr5${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
															</c:if>
															<c:if test="${contains == false}">
															<input class="form-check-input" type="checkbox" id="gstr5${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
															</c:if>
															<label for="gstr5${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
															</label> <span class="labletxt">${permissions.key}</span>
															</div>
															</div>				
									</c:if>
									</c:if>
									<c:if test="${permissionEntry.key eq 'GSTR6'}">						
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										<c:set var="contains" value="false" />
										<div class="form-group row col-md-6 col-sm-6 pl-5">
															<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
															<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
																<c:set var="contains" value="true" />
															</c:if>
															</c:forEach>
															<c:if test="${contains == true}">
															<input class="form-check-input" type="checkbox" id="gstr6${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
															</c:if>
															<c:if test="${contains == false}">
															<input class="form-check-input" type="checkbox" id="gstr6${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
															</c:if>
															<label for="gstr6${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
															</label> <span class="labletxt">${permissions.key}</span>
															</div>
															</div>				
									</c:if>
									</c:if>
									<c:if test="${cuser.accessMultiGSTNSearch eq true || cuser.accessMultiGSTNSearch eq 'true'}">
									<c:if test="${permissionEntry.key eq 'Multi GSTIN'}">						
									<c:if test='${empty permissions.value}'>
										<c:if test='${vloop.count == 1}'>
											<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
										</c:if>
										<c:set var="contains" value="false" />
										<div class="form-group row col-md-6 col-sm-6 pl-5">
															<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
														<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
															<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
																<c:set var="contains" value="true" />
															</c:if>
															</c:forEach>
															<c:if test="${contains == true}">
															<input class="form-check-input" type="checkbox" id="multigstn${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked />
															</c:if>
															<c:if test="${contains == false}">
															<input class="form-check-input" type="checkbox" id="multigstn${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" />
															</c:if>
															<label for="multigstn${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
															</label> <span class="labletxt">${permissions.key}</span>
															</div>
															</div>				
									</c:if>
									</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>
						</c:if>
						</div>
						</div>
						<div class="tab-pane fade" id="eacknowledgement${role.id}" role="tabpanel" aria-labelledby="eacknowledgement-tab">
							<div class="row">
						  	<c:forEach items="${permissionMap}" var="permissionEntry" varStatus="ploop">
								<c:forEach items="${permissionEntry.value}" var="permissions" varStatus="vloop">
								  	<c:if test="${permissionEntry.key eq 'Acknowledgement'}">
										<c:if test='${empty permissions.value}'>
											<c:if test='${vloop.count == 1}'>
												<div class="col-md-12 mb-2"><strong>${permissionEntry.key}</strong></div>
											</c:if>
											<c:set var="contains" value="false" />
											<c:forEach items="${role.permissions[permissionEntry.key]}" var="permission">
												<c:if test="${permissions.key eq permission.name && permission.status eq 'Yes'}">
													<c:set var="contains" value="true" />
												</c:if>
											</c:forEach>
											<div class="col-md-6 col-sm-12" style="padding-left: 2rem!important;">
											<div class="form-check form-check-inline col-md-12 col-sm-12 pl-0">
											<c:if test="${contains == true}">
												<input class="form-check-input" type="checkbox" id="${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}" checked/>
											</c:if>
											<c:if test="${contains == false}">
												<input class="form-check-input" type="checkbox" id="${permissionEntry.key}permission${vloop.index}" name='permissions["${permissionEntry.key}"][${vloop.index}].name' value="${permissions.key}"/>
											</c:if>
											<label for="${permissionEntry.key}permission${vloop.index}"><span class="ui"></span>
											</label> <span class="labletxt">${permissions.key}</span>
											</div>
											</div>
										</c:if>
									</c:if>
								</c:forEach>
							</c:forEach>
							</div>				
						  </div>
						</div>

            </div>
        </div>
						<div class="bdr-b col-12" style="display:block;height:3px;">&nbsp;</div>
						 <div class="col-12 text-center mt-3 mb-2" style="display:block">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<c:if test='${not empty client && not empty client.id}'>
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
							</c:if>
							<input type="hidden" name="createdDate" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${role.createdDate}" />'>
							<input type="hidden" name="createdBy" value="${role.createdBy}">
							<input type="submit" class="btn btn-blue-dark" value="Save"/>
							<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>						
						</div>
                  </form:form>
                    <!-- row end -->

                
    </div> </div></div>
	</c:forEach>
    <!-- Edit Modal End -->
    <script type="text/javascript">
        var table = $('table.display').DataTable({
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
		  $("div.toolbar").html('<h4 style="margin-top: 5px;">Roles</h4><a href="#" class="btn btn-blue-dark permissionSettings-Roles-Add"  data-toggle="modal" onClick="populateElement1()" data-target="#addModal">Add</a> ');
  			$('#role').submit(function(e) {
 				var username = $('#userName').val();
				var userid = '<c:out value="${id}"/>';
				var clientid = '<c:out value="${client.id}"/>';
				var err=0;
				$.ajax({
					type : "GET",
					async: false,
					contentType : "application/json",
					url: "${contextPath}/rolesexits/"+userid+"/"+clientid+"/"+username,
					success : function(response) {
						if(response == true){
							$('.rolename').text('Role name already exists');$('#userName').addClass('has-error has-danger');
							err = 1;
							e.preventDefault();
						}else{
							$('.rolename').text('');
							$('#userName').removeClass('has-error has-danger');
						}
					}
				});		
			});
	
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
		$(document).ready(function() {
			$('.nonAspAdmin').addClass('active');
			$('#cpRoleNav').addClass('active');
        });
		function populateElement1() {
			$('.with-errors').html('');
			$('.form-group').removeClass('has-error has-danger');
			$('#userName').val('');
			$('#description').val('');
			$('#Acknowledgementpermission1').attr('checked','true')
		}
		function editrole(roleid){
					var username = $('#userName').val();
					var userid = '<c:out value="${id}"/>';
					var clientid = '<c:out value="${client.id}"/>';
					var err=0;
					var duplicate_username = $('#duplicate_username').val();
					if(duplicate_username == username){
						err = 0;
					}else{
					$.ajax({
						type : "GET",
						async: false,
						contentType : "application/json",
						url: "${contextPath}/rolesexits/"+userid+"/"+clientid+"/"+username,
						success : function(response) {
							if(response == true){
								$('.rolename').text('Role name already exists');$('#userName').addClass('has-error has-danger');
								err = 1;e.preventDefault();return false;
							}else{
							$('.rolename').text('');$('#userName').removeClass('has-error has-danger');
							}
						}
					});
				}	
						if ($('.'+roleid+'-Settings-Configurations').is(":checked")) {
							$('.configurationTabs').show();
				        } else {
				        	$('.configurationTabs').hide();
				        }
					
		}
    </script>
	<c:choose>
	<c:when test='${not empty client && not empty client.id}'>
	<script type="text/javascript">
    	$(document).ready(function() {
			$('#nav-client').addClass('active');
			$('#cp_center').css("display","none");
			$('#cp_Allcenter').css("display","none");
			$('#cp_centerFiling').css("display","none");
			$('#cp_centerClinet').css("display","none");
        });
    </script>
	</c:when>
	<c:otherwise>
	<script type="text/javascript">
    	$(document).ready(function() {
			$('#nav-team').addClass('active');
			$('#cp_center').css("display","block");
			$('#cp_Allcenter').css("display","block");
			$('#cp_centerFiling').css("display","block");
			$('#cp_centerClinet').css("display","block");
        });
    </script>
	</c:otherwise>
	</c:choose>
</body>

</html>