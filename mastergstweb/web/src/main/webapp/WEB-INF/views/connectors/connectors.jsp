<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/connectors/connectors.css" media="all" />
</head>
<body>
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
<!--- breadcrumb start -->
 				
<div class="breadcrumbwrap main">
<div class="container">
	<div class="row">
        <div class="col-md-12 col-sm-12">
				<ol class="breadcrumb">
					<li class="breadcrumb-item"><a href="#">Connectors </a></li>
				</ol>
				<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>

<!--- breadcrumb end -->
  <div class="db-ca-wrap">
            <div class="container">
<div class="connectors-wrap">
                <div class="row">
               
                    <!--  begin -->
									 

<div class="col-md-9 col-sm-12">
     <h2>Welcome to MasterGST Connectors</h2>
		 <h6>Select data source type you want to connect with</h6>

		<div class="connecters-logos">
		<ul>
		<li><a href="#" data-toggle="tooltip" data-placement="bottom" title="Please contact sales@mastergst.com for additional details"><img src="${contextPath}/static/mastergst/images/connectors/connector-logo1.jpg" alt="Connector"></a></li>
		<li><a href="#" data-toggle="tooltip" data-placement="bottom" title="Please contact sales@mastergst.com for additional details"><img src="${contextPath}/static/mastergst/images/connectors/connector-logo2.jpg" alt="Connector"></a></li>
		<li><a href="#" data-toggle="tooltip" data-placement="bottom" title="Please contact sales@mastergst.com for additional details"><img src="${contextPath}/static/mastergst/images/connectors/connector-logo3.jpg" alt="Connector"></a></li>
		<li><a href="#" data-toggle="tooltip" data-placement="bottom" title="Please contact sales@mastergst.com for additional details"><img src="${contextPath}/static/mastergst/images/connectors/connector-logo4.jpg" alt="Connector"></a></li>
		<li><a href="#" data-toggle="tooltip" data-placement="bottom" title="Please contact sales@mastergst.com for additional details"><img src="${contextPath}/static/mastergst/images/connectors/connector-logo5.jpg" alt="Connector"></a></li>
		<li><a href="#" data-toggle="tooltip" data-placement="bottom" title="Please contact sales@mastergst.com for additional details"><img src="${contextPath}/static/mastergst/images/connectors/connector-logo6.jpg" alt="Connector"></a></li>
		
		</ul>
		</div>

</div>

<div class="col-md-3 col-sm-12">
<div class="rightside-wrap">
            <h5>Actions</h5>
            <ul>
             <li><span class="sm-img"><img src="${contextPath}/static/mastergst/images/connectors/connector-icon1.png" alt="Connector"></span><a href="#" data-toggle="tooltip" data-placement="bottom" title="Please contact sales@mastergst.com for additional details">Create Data Set</a></li>
              <li><span class="sm-img"><img src="${contextPath}/static/mastergst/images/connectors/connector-icon2.png" alt="Connector"></span><a href="#" data-toggle="tooltip" data-placement="bottom" title="Please contact sales@mastergst.com for additional details">Import Data</a> </li>
              <li><span class="sm-img"><img src="${contextPath}/static/mastergst/images/connectors/connector-icon3.png" alt="Connector"></span><a href="#" data-toggle="tooltip" data-placement="bottom" title="Please contact sales@mastergst.com for additional details">Connect to Data Sources</a></li>
              
            </ul>
          </div> 
</div>


</div>	 										<!--  end -->

</div>                    
                  
                </div>
            </div>
        </div>
<!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript"> 
	$(document).ready(function(){
		$('[data-toggle="tooltip"]').tooltip();
		$('#nav-connector').addClass('active');
	});
</script>
</body>
</html>
