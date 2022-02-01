<nav class="navbar navbar-toggleable-md navbar-inverse fixed-top bg-inverse">
      <!-- toggle button for device begin -->
      <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>
      <!-- toggle button for device end -->
      <a class="navbar-brand" href="https://www.mastergst.com" target="_blank">
      <img src="${contextPath}/static/mastergst/images/master/logo-mastergst.png" alt="Master Gst" />
      </a>
      <div class="collapse navbar-collapse" id="navbarCollapse">
        <div class="mainnav-wrap">
          <ul class="navbar-nav mainnav-right">
	<c:choose>
		<c:when test="${headerflag=='signup'}">
        <li class="nav-item">
              <a class="nav-link" href="login">Login</a>
         </li>
		</c:when>    
		<c:when test="${headerflag=='login'}">
			<li class="nav-item">
              <a class="nav-link" href="signupall?inviteId=&subscrid=">Signup</a>
			</li>
		</c:when>  
		<c:otherwise>
			<li class="nav-item">
              <a class="nav-link" href="login"> Login</a>
			</li>
			<li class="nav-item">
              <a class="nav-link" href="signupall?inviteId=&subscrid=">Signup</a>
			</li>
		</c:otherwise>
	</c:choose>
			
			
            <li class="nav-item circle">
              <a class="nav-link" href="tel:+91 7901022478" data-toggle="tooltip" data-placement="left" title="+91 7901022478"> <img src="${contextPath}/static/mastergst/images/master/phone-icon.png" alt="Phone" /> </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>