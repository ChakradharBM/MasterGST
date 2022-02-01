<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<%@include file="/WEB-INF/views/includes/script.jsp"%>
</head>
<body>
	<%@include file="/WEB-INF/views/includes/app_header.jsp"%>
	<!-- header page begin -->
	<div class="bodywrap">
		<div class="bodybreadcrumb main">
			<div class="container">
				<div class="row">
					<div class="col-sm-12">
						<div class="navbar-left">
							<ul>
								<li class="nav-item"><a class="nav-link urllink" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=gst">GST Credentials</a></li>
								<li class="nav-item"><a class="nav-link urllink" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=eway">e-Way	Bill Credentials</a></li>
								<li class="nav-item"><a class="nav-link urllink" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=einv" >e-invoice Credentials</a></li>
								<li class="nav-item"><a class="nav-link urllink active" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=authorizationKeys">Authorization Keys</a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div>
			<div class="db-inner">
				<!-- begin content  -->
				<div class="container db-inner-txt" style="margin-top: 80px;">
					<div class="row cred-wrap pl-5 pr-5">
						<div class="credentialwrap" style="width:100%">
							<div class="row">
								<div class="col-lg-12 col-md-12 col-sm-12">
									<!--   profile table begin -->
									<div class="welhdr">
										<h5 class="welcomehr">Welcome to MasterGST Authorization Keys</h5>
									</div>
									<!-- Nav tabs -->
									<div class="customtable">
										<!-- Tab panes -->
										<div class="tab-content">
											<div class="tab-pane active" id="credential" role="tabpanel">
												<table id="headerkeysTable" class="row-border dataTable meterialform" width="100%">
													<thead>
														<tr>
															<th>GST UserName</th>
															<th>Transaction No(txn)</th>
															<th>Authtoken</th>
															<th>Updated Date</th>
															<th>Keys Type</th>
															<th>Action</th>
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${headerkeys}" var="headerkey">
															<tr>
																<td>${headerkey.gstusername}</td>
																<td>${headerkey.txn}</td>
																<td>${headerkey.authtoken}</td>
																<td><fmt:formatDate value="${headerkey.updatedDate}" pattern="dd-MM-yyyy hh:mm:ss a" /></td>
																<td>${headerkey.headerKeysType}</td>
																<td>
																	<a href="#" id="${headerkey.id}" onClick="showDeleteAuthkeysPopup('${headerkey.id}','${headerkey.userid}')">
																		<img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;">
																	</a>
																</td>
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
					</div>
				</div>
			</div>
			<!-- end content  -->
		</div>
	</div>
	<!-- Modal -->
	<div class="modal fade" id="deleteAuthKeysModal" role="dialog" aria-labelledby="deleteAuthKeysModal" aria-hidden="true">
		<div class="modal-dialog col-6 modal-center" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true"> 
						<img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span>
					</button>
					<div class="invoice-hdr bluehdr">
						<h3>Delete Authorization Keys</h3>
					</div>
					<div class=" pl-4 pt-4 pr-4">
						<h6>Are you sure you want to delete this Authorization key <span id="delPopupDetails"></span> ? </h6>
						<p class="smalltxt text-danger">
							<strong>Note:</strong>On deletion of this Authorization Key user needs to be re-authenticate using his OTP.
						</p>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" id="btnDeleteHeaderkeys" data-dismiss="modal">Delete Authorization key</button>
					<button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
				</div>
			</div>
		</div>
	</div>
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->
	<script type="text/javascript">
		$('#headerkeysTable').DataTable({
			dom: 'Blfrtip', 	
	        "pageLength": 10,
	        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"] ],
	        "language": {
	        	"paginate": {
	            	"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
	                "next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
	             }
	        }
		});
		function formatUpdatedDate(data){
			var createdDt = new Date(data) ;
		    var month = createdDt.getUTCMonth() + 1; 
			var day = createdDt.getUTCDate();
			var year = createdDt.getUTCFullYear();
			var hours = createdDt.getHours();
			var minutes = createdDt.getMinutes();
			var seconds = createdDt.getSeconds();
			
			return day+'-'+month+'-'+year+' '+hours+':'+minutes+':'+seconds;
		}
		function showDeleteAuthkeysPopup(headerid,userid){
			$('#deleteAuthKeysModal').modal("show");
			$('#btnDeleteHeaderkeys').attr("onclick", "deleteAuthkeys('"+headerid+"','"+userid+"')");
		}	
		function deleteAuthkeys(headerkeyid,userid){
			$.ajax({
				url: "${contextPath}/deleteAspHeaderkeys?headerkeyid="+headerkeyid+"&id="+userid,
			    type: 'GET',
			    async: false,
				cache: false,
			    success : function(responsedata){
			    	if ($.fn.DataTable.isDataTable("#headerkeysTable")) {
						$('#headerkeysTable').DataTable().clear().destroy();
					}
			    	$('#headerkeysTable tbody').empty();
			    	if(responsedata.length>0){
			    		content="";
			    		for(var i=0;i<responsedata.length;i++){
			    			var gstusername="";
			    			var txn="";
			    			var authtoken="";
			    			var updatedDate="";
			    			var headerKeysType="";
			    			if(responsedata[i].gstusername != null && responsedata[i].gstusername !=""){
			    				gstusername=responsedata[i].gstusername;
			    			}
			    			if(responsedata[i].txn != null && responsedata[i].txn !=""){
			    				txn=responsedata[i].txn;
			    			}
			    			if(responsedata[i].authtoken != null && responsedata[i].authtoken !=""){
			    				authtoken=responsedata[i].authtoken;
			    			}
			    			if(responsedata[i].updatedDate != null && responsedata[i].updatedDate !=""){
			    				updatedDate=formatUpdatedDate(responsedata[i].updatedDate);
			    				
			    			}
			    			if(responsedata[i].headerKeysType != null && responsedata[i].headerKeysType !=""){
			    				headerKeysType=responsedata[i].headerKeysType;
			    			}
			    			content+="<tr><td>"+gstusername+"</td><td>"+txn+"</td><td>"+authtoken+"</td><td>"+updatedDate+"</td><td>"+headerKeysType+"</td>";
			    			content+='<td><a href="#" onClick="showDeleteAuthkeysPopup(\''+responsedata[i].headerid+'\',\''+responsedata[i].userid+'\')"'+'><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top:-6px;"></a></td></tr>';
			    		}
			    		$('#headerkeysTable tbody').append(content);
			    	}
			    	$('#headerkeysTable').DataTable({
						dom: 'Blfrtip', 	
				        "pageLength": 10,
				        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
				        "language": {
				        	"paginate": {
				            	"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
				                "next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				             }
				       	}
					});
			    }, error:function(){}
			});
		}
	</script>
</body>
</html>