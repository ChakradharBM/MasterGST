<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Groups</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-cp/dashboard-cp.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
<!--- breadcrumb start -->
 		
<div class="breadcrumbwrap">
<div class="container">
	<div class="row">
        <div class="col-md-12 col-sm-12">
				<ol class="breadcrumb">
					<c:choose>
					<c:when test='${not empty client && not empty client.id}'>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">${client.businessname}</a></li>
						<li class="breadcrumb-item active">Company Profile</li>
					</c:when>
					<c:otherwise>
					<li class="breadcrumb-item active">Team</li>
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
                                    <th class="text-center">
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" />
                                                <i class="helper"></i></label>
                                        </div>
                                    </th>
                                    <th class="text-center">Group Name</th>
                                    <th class="text-center">Description</th>
                                    <th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
								<c:forEach items="${groups}" var="group">
                                <tr>
                                    <td>
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" />
                                                <i class="helper"></i></label>
                                        </div>
                                    </td>
                                    <td align="center">${group.name}</td>
                                    <td align="center">${group.description}</td>
                                    <td class="actionicons"><a class="btn-edt" href="#" data-toggle="modal" data-target="#editModal${group.id}"><i class="fa fa-edit"></i> </a></td>
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
    
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
    <!-- Add Modal Start -->
    <div class="modal fade" id="addModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform bs-fancy-checks">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>Add Group</h3>

                    </div>
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform" name="groupform" action="${contextPath}/creategroup/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="group">
					<div class="row  p-5">
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Group Name</p>
                            <span class="errormsg" id="userName_Msg"></span>
                            <input type="text" id="userName" name="name" data-minlength="3" required="required" data-error="Please enter more than 3 characters" placeholder="1001" value="" />
                            <label for="userName" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Description</p>
                            <span class="errormsg" id="role_Msg"></span>
                            <input type="text" id="description" name="description" required="required" placeholder="190" value="" />
                            <label for="description" class="control-label"></label>
                            <i class="bar"></i> </div>
														
						<div class="bdr-b col-12 mb-3" style="display:block">&nbsp;</div>
													
						<%-- <div class="form-group col-md-12 col-sm-12">
							<p class="lable-txt mb-4">Select the Permissions</p>
							<c:forEach items="${features}" var="feature" varStatus="loop">
							<div class="form-group col-md-12 col-sm-12">
								<p class="lable-txt">${feature.featurename}</p>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" id="${feature.featureid}${loop.index}view" value="View" name='permissions["${feature.featurename}"][0].name' />
									<label for="${feature.featureid}${loop.index}view"><span class="ui"></span>
									</label> <span class="labletxt">View</span> </div>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" id="${feature.featureid}${loop.index}admin" value="Admin" name='permissions["${feature.featurename}"][1].name' />
									<label for="${feature.featureid}${loop.index}admin"><span class="ui"></span>
									</label> <span class="labletxt">Admin</span> </div>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" id="${feature.featureid}${loop.index}delete" value="Delete" name='permissions["${feature.featurename}"][2].name' />
									<label for="${feature.featureid}${loop.index}delete"><span class="ui"></span>
									</label> <span class="labletxt">Delete</span> </div>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" id="${feature.featureid}${loop.index}create" value="Create" name='permissions["${feature.featurename}"][3].name' />
									<label for="${feature.featureid}${loop.index}create"><span class="ui"></span>
									</label> <span class="labletxt">Create</span> </div>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" id="${feature.featureid}${loop.index}update" value="Update" name='permissions["${feature.featurename}"][4].name' />
									<label for="${feature.featureid}${loop.index}update"><span class="ui"></span>
									</label> <span class="labletxt">Update</span> </div>
							</div>
							</c:forEach>
						</div> --%>
						<div class="bdr-b col-12" style="display:block">&nbsp;</div>

                        <div class=" col-12 mt-4 text-center" style="display:block">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<c:if test='${not empty client && not empty client.id}'>
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
							</c:if>
							<input type="submit" class="btn btn-blue-dark" value="ADD"/>
						</div>
                    </div>
					</form:form>
                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>
    <!-- Add Modal End -->
		 <!-- Edit Modal Start -->
	<c:forEach items="${groups}" var="group">
    <div class="modal fade" id="editModal${group.id}" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform bs-fancy-checks">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>Edit Group</h3>

                    </div>
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform" name="groupform" action="${contextPath}/creategroup/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="group">
					<input type="hidden" name="id" value="${group.id}" />
                    <div class="row  p-5">
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Group Name</p>
                            <span class="errormsg" id="userName_Msg"></span>
                            <input type="text" id="userName" name="name" data-minlength="3" required="required" data-error="Please enter more than 3 characters" placeholder="1001" value="${group.name}" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Description</p>
                            <span class="errormsg" id="role_Msg"></span>
                            <input type="text" id="description" name="description" required="required" placeholder="190" value="${group.description}" />
                            <label for="description" class="control-label"></label>
                            <i class="bar"></i> </div>
														
						<div class="bdr-b col-12 mb-3" style="display:block">&nbsp;</div>
													
						<%-- <div class="form-group col-md-12 col-sm-12">
							<p class="lable-txt mb-4">Select the Permissions</p>
							<c:set var="permissionArray" value="${['View','Admin','Delete','Create','Update']}" />
							<c:forEach items="${features}" var="feature" varStatus="fLoop">
							<c:set var="fName" value="${feature.featurename}" />
							<div class="form-group col-md-12 col-sm-12">
								<p class="lable-txt">${feature.featurename}</p>
								<c:forEach items="${permissionArray}" var="pName" varStatus="loop">
									<c:set var="contains" value="false" />
									<c:forEach items="${group.permissions[fName]}" var="permission">
									<c:if test="${pName eq permission.name}">
										<c:set var="contains" value="true" />
									</c:if>
									</c:forEach>
									<div class="form-check form-check-inline">
										<c:if test="${contains == true}">
										<input class="form-check-input" type="checkbox" id="${feature.featureid}${fLoop.index}${pName}" checked="checked" value="${pName}" name='permissions["${feature.featurename}"][${loop.index}].name' />
										</c:if>
										<c:if test="${contains != true}">
										<input class="form-check-input" type="checkbox" id="${feature.featureid}${fLoop.index}${pName}" value="${pName}" name='permissions["${feature.featurename}"][${loop.index}].name' />
										</c:if>
										<label for="${feature.featureid}${fLoop.index}${pName}"><span class="ui"></span>
										</label> <span class="labletxt">${pName}</span>
									</div>
								</c:forEach>
							</div>
							</c:forEach>
						</div> --%>
						<div class="bdr-b col-12" style="display:block">&nbsp;</div>

                        <div class="col-12 text-center mt-3" style="display:block">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<c:if test='${not empty client && not empty client.id}'>
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
							</c:if>
							<input type="hidden" name="createdDate" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${group.createdDate}" />'>
							<input type="hidden" name="createdBy" value="${group.createdBy}">
							<input type="submit" class="btn btn-blue-dark" value="Save"/>
							<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>						
						</div>
                    </div>
					</form:form>
                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>
	</c:forEach>
    <!-- Edit Modal End -->
    <script type="text/javascript">
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
       
		  $("div.toolbar").html('<h4>Groups</h4><a href="#" class="btn btn-blue-dark"  data-toggle="modal" data-target="#addModal">Add</a> ');
 
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
			$('#cpGroupNav').addClass('active');
        });
    </script>
	<c:choose>
	<c:when test='${not empty client && not empty client.id}'>
	<script type="text/javascript">
    	$(document).ready(function() {
			$('#nav-client').addClass('active');
        });
    </script>
	</c:when>
	<c:otherwise>
	<script type="text/javascript">
    	$(document).ready(function() {
			$('#nav-team').addClass('active');
        });
    </script>
	</c:otherwise>
	</c:choose>

</body>

</html>