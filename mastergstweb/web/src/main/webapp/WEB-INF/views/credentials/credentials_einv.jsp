<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<%@include file="/WEB-INF/views/includes/script.jsp" %>
<c:set var="varClientId" value="<%=UUID.randomUUID().toString()%>"/>
<c:set var="varClientSecret" value="<%=UUID.randomUUID().toString()%>"/>
<script type="text/javascript">
	$(function () {
		$('.radio input[type="radio"]').click(function(){
			if($(this).attr("value") == 'Sandbox') {
				$('#sectionProduction').hide();
				$('#sectionSandbox').show();
				$('#clientid').val('${varClientId}').prop('readonly', true);
				$('#clientsecret').val('${varClientSecret}').prop('readonly', true);
			} else if($(this).attr("value") == 'Production') {
				$('#sectionProduction').show();
				$('#sectionSandbox').hide();
			}
		});
	});
</script>
</head>
<body>
<%@include file="/WEB-INF/views/includes/app_header.jsp" %>
<!-- header page begin -->
<div class="bodywrap">
	<div class="bodybreadcrumb main">
		<div class="container">
			<div class="row">
				<div class="col-sm-12">
					<div class="navbar-left">
						<ul>
							<li class="nav-item">
								<a class="nav-link urllink" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=gst" >GST Credentials</a>
							</li>
							<li class="nav-item">
								<a class="nav-link urllink" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=eway" >e-Way Bill Credentials</a>
							</li>
							<li class="nav-item">
								<a class="nav-link urllink active" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=einv" >e-Invoice Credentials</a>
							</li>
							<li class="nav-item">
								<a class="nav-link urllink" href="${contextPath}/credentials?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&creType=authorizationKeys" >Authorization Keys</a>
	 						</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div>
		<div class="db-inner">
			<!-- begin content  -->
		    <div class="container db-inner-txt" style="margin: 0px 75px;margin-top: 80px;">
		    	<div class="row cred-wrap" >
		        	<div class="credentialwrap">
		            	<div class="row">
		                	<div class="col-lg-3 col-md-2 col-sm-12 bdr-dashed-r">
		                    	<!-- left profile list begin -->
		                        <h4 class="presize">Welcome to MasterGST e-invoice API Portal, please follow below steps to create sandbox access </h4>
		                        <div class="row stpsblock">
		                        	<p class="parafnt"><span class="welspn"><strong>Step1:</strong></span>Create MasterGST e-Invoice Sandbox Credentials </p>
		                            <p class="parafnt"><span class="welspn"><strong>Step2:</strong></span>Use the MasterGST Registered Email, Client ID & Client Secret ID, GSTN User Name & GSTIN</p>
		                        </div>
		                   	</div>
		                    <div class="col-lg-9 col-md-9 col-sm-12">
		                    	<!--   profile table begin -->
		                        <div class="welhdr"><h5 class="welcomehr">Welcome to MasterGST e-Invoice Credentials</h5></div>
		                        <!-- Nav tabs -->
		                        <div class="customtable">
		                        	<div class="gstr-info-tabs"><div>&nbsp;</div>
										<ul class="nav nav-tabs" role="tablist">
											<li class="nav-item">
										    	<a class="nav-link active" data-toggle="tab" href="#credential" role="tab">Sandbox Keys</a>
										    </li>
										    <li class="nav-item">
										    	<a class="nav-link" data-toggle="tab" href="#production" role="tab">Production Keys</a>
										    </li>
										</ul>
									</div> 
		          					<!-- Tab panes -->
		          					<div class="tab-content">
						            	<div class="tab-pane active" id="credential" role="tabpanel">
						            		<c:if test="${EInvoiceSandBox == null }">
						                    	<h3 class="mt-1"><a href="" class="btn btn-darkblue pull-right btn-sm" data-toggle="modal" data-target="#create_credentials">Create Credentials</a></h3>
						                    </c:if>
						                    <table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
						                    	<thead>
						                        	<tr><th>Client ID</th><th>Client Secret ID</th><th>User Name</th><th>Password</th><th>Status</th></tr>
						                        </thead>
						                        <tbody>
													<c:if test="${EInvoiceSandBox != null }">
														<c:set var="isEnabled" value="Disabled"/>
														<c:if test="${EInvoiceSandBox.isenabled}">
															<c:set var="isEnabled" value="Enabled"/>
														</c:if>		
									                    <tr>
									                      <td>${EInvoiceSandBox.clientid}</td>
									                      <td>${EInvoiceSandBox.clientsecret}</td>
										                  <td>${EInvoiceSandBox.username}</td>
										                  <td>${EInvoiceSandBox.password}</td>
									                      <td>${isEnabled}</td>
									                    </tr>
						                    		</c:if>
		                            			</tbody>
		                        			</table>
		            					</div>
							            <div class="tab-pane" id="production" role="tabpanel"> 
								            <c:if test="${EInvoiceProduction == null }">
								            	<p class="prolisthrd">Your Production keys are not yet generated, please contact <a href="mailto:sales@mastergst.com" data-rel="external">sales@mastergst.com</a> to get production keys access</p>
								            	<p><b>Check List for Production Access:</b> </p>
								                <ol class="prokeylist">
									                <li> First Name:</li>
									                <li> Last Name:</li>
									                <li>Email:</li>
									                <li>Mobile Number:</li>
									                <li>Company Name as Per Registered:</li>
									                <li>Company Address: </li>
									                <li>Certificate of incorporation:</li>
									                <li>Company PAN card:</li>
									                <li> Address Proof (like power bill, Bank statement, First page of bank pass book):</li>
									                <li>Authorization letter on company letter head:</li>
									                <li>Cancelled Bank check</li>
									            </ol>
								            </c:if>
								            <c:if test="${EInvoiceProduction != null }">
								            	<c:set var="isEinvProdEnabled" value="Disabled"/>
												<c:if test="${EInvoiceProduction.isenabled}">
													<c:set var="isEinvProdEnabled" value="Enabled"/>
												</c:if>	
							            		<table id="dbTable-prod" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
							                    	<thead>
							                    		<tr><th>Client ID</th><th>Client Secret ID</th><th>Status</th></tr>
							                        </thead>
							                        <tbody>
										            	<tr>
										                	<td>${EInvoiceProduction.clientid}</td>
										                    <td>${EInvoiceProduction.clientsecret}</td>
										                    <td>${isEinvProdEnabled}</td>
										                </tr>
							                        </tbody>
							                  </table>
							            	</c:if>
							            </div>
		          					</div>
		              			</div>                   
		              		</div>            
						<!-- porfile table end -->
		                </div>
		            </div>
		        </div>
			</div>
		</div>
	<!-- end content  -->
	</div>
</div>
<!--ggg-->
<c:if test="${otheruser=='no'}">
	<div class="modal fade" id="create_credentials" tabindex="-1" role="dialog" aria-labelledby="create_credentials" aria-hidden="true">
        <div class="modal-dialog modal-sm" role="document">
            <div class="modal-content">
            	<div class="bluehdr"><h5 class="modal-title" id="create_credentials">Sandbox Credentials</h5></div>
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"><img src="${contextPath}/static/mastergst/images/credentials/closebtn.png" alt="Close" class="cross" /></span> </button>
                </div>
                <form:form method="POST" name="meterialform" class="meterialform" action="createcredentials" modelAttribute="userkeys">
                	<input type="hidden" name="creType" value="${creType}"/>
                	<div class="modal-body">
                        <div class="labletxt">Name</div>
                        <div class="form-group">
                            <input type="text" required="required" name="keyname">
                            <label for="input" class="control-label"></label> <i class="bar"></i></div>
                        <div class="labletxt">Create Date</div>
                        <div class="form-group">
                            <input type="text" required="required" name="createdate" id="datetimepicker" value=<%=strDate%>  readonly>
                            <label for="input" class="control-label"></label> <i class="bar"></i></div>
                        <div class="labletxt">Client ID</div>
                        <div class="form-group">
                            <input type="text" required="required" id="clientid" name="clientid" value="${varClientId}"  readonly>
                            <label for="input" class="control-label"></label> <i class="bar"></i></div>
                        <div class="labletxt">Client Secret</div>
                        <div class="form-group">
                            <input type="text" required="required" id="clientsecret" name="clientsecret" value="${varClientSecret}"  readonly>
                            <label for="input" class="control-label"></label> <i class="bar"></i>
                        </div>
						<input type="hidden" name="stage" value="EInvoiceSandBox"/>
                        <input type="hidden" name="hiddenid" value=<c:out value="${id}"/>>
                 	</div>
                	<div class="modal-footer text-center crde-keys">
                    	<button type="button" class="btn btn-md btn-modeldarkblue m-auto" data-dismiss="modal"  id="sendNewSms" value=" Send " onclick="document.meterialform.submit();" >CREATE</button>
                	</div>
                </form:form>
            </div>
        </div>
    </div>
</c:if>
<!-- Create credentials Modal end -->
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">
	//button disable function
    var checker = document.getElementById('checkme');
	var sendbtn = document.getElementById('sendNewSms');
	// when unchecked or checked, run the function
	checker.onchange = function(){
		if(this.checked){
		    sendbtn.disabled = false;
		} else {
		    sendbtn.disabled = true;
		}
	}
    var table = $('table.display').DataTable({
		dom: 'Bfrtip',
        "pageLength": 5,
        "oSearch": false,
        "language": {
        	"paginate": {
            	"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
                "next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
            }
        }
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
    </script>
</body>
</html>