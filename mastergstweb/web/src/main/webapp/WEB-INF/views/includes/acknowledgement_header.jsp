<nav class="navbar navbar-toggleable-md navbar-inverse fixed-top bg-inverse navbar-db nav-ca">
	<div class="container"> <!-- toggle button for device begin -->
    	<button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation"> <span class="navbar-toggler-icon"></span> </button>
        <!-- toggle button for device end -->
        <a class="navbar-brand" href="#"> <img src="${contextPath}/static/mastergst/images/master/logo-mastergst.png" alt="Master Gst"> </a>
        <div class="collapse navbar-collapse" id="navbarCollapse">
            <div class="mainnav-wrap">
                <!-- main menu begin -->
				<ul class="navbar-nav mainnav-left db-ca" style="margin-left: 30%;">
                    <li class="nav-item"> <a id="nav-dashboard" class="nav-link urllink active" href="#" link="${contextPath}/cp_acknowlegment/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Acknowledgement</a> </li>
                </ul>
                <ul class="navbar-nav mainnav-right move-not">
                      <li class="nav-item dropdown">
			             <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false"> <span class="dbspriticons notifybell"></span><span class="badge blue badge-circle">0</span> </a>
			         </li>
			         <c:set var = "varname" value = "${fn:split(fullname, ' ')}"/>
                    <li class="nav-item dropdown"> <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false"> <span class="rounded-circle img-sm-rounded">${fn:substring(varname[0], 0, 1)}<c:if test="${fn:length(varname) gt 1}">${fn:substring(varname[1], 0, 1)}</c:if>
					</span> <span class="user-name">${user.fullname}<span class="dbspriticons ddarw"></span></span></a>
					<div class="dropdown-menu dropdown-menu-right">
						<ul class="profilelist">
							<li class="rowbdr">
								<div class="row">
									<div class="col-md-3">
										<div class="icon-l"><span class="userimg"><img src="${contextPath}/static/mastergst/images/credentials/user1.jpg" alt="User" class="circle"></span> </div>
									</div>
									<div class="col-md-9">
										<div class="icon-r"><span class="usertxt" style="white-space: pre-line;">${user.fullname}</span> </div>
									</div>
								</div>
							</li>
							<li>
								<div class="icon-l"><span class="cred-sprite credicon1"></span> </div>
								<div class="icon-r"><span class="icontxt" id="acctUserMail">${user.email}</span> </div>
							</li>
							<li>
								<div class="icon-l"><span class="cred-sprite credicon2"></span> </div>
								<div class="icon-r"><span class="icontxt" id="acctUserCell">${user.mobilenumber}</span> </div>
							</li>
							<li>
								<div class="icon-l"><span class="cred-sprite credicon5"></span> </div>
								<div class="icon-r"><span class="icontxt" id="acctUserAddr">${user.address}</span> </div>
							</li>
							<li class="divider"></li>
							<li><!-- <a class="btn btn-blue-dark btn-sm pull-left" data-toggle="modal" data-target="#profileViewModal" onclick="googlemapsinitialize()">View Profile</a> --> <a class="btn btn-blue-dark btn-sm pull-right" href="${contextPath}/logout">Logout</a>   </li>
						</ul>
					</div>
					</li>
					
	            </ul>
			</div>
		</div>
	</div>
</nav>
<div class="alert alert-danger gst-notifications" role="alert">
	<img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="mr-2" onclick="closeNotifications()" />
	<span id="errorMessage"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="pull-right" onclick="closeNotifications()" />
</div>
<div class="alert alert-success gstsuccess-notifications" role="alert">
	<img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" style="float: left;" class="mr-2" onclick="closeNotifications()" />
	<span id="successMessage"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="Alert" class="pull-right" onclick="closeNotifications()" />
</div>