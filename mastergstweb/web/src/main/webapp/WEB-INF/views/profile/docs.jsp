<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Docs</title>
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
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active">Docs</li>
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
      <!-- Dashboard body start -->
      <div>
            <div class="db-inner" style="min-height: 465px!important;">
                <!-- begin content  -->
                <div class="container db-inner-txt docspage1">
                    <div class="row">
                        <div class="credentialwrap">
                            <div class="row">
                           
                                <div class="col-lg-4 col-md-4 col-sm-12 bdr-dashed-r">
                                    <!-- left profile list begin -->
                                  <h4 class="presize">Steps to configure: </h4>
                                  <div class="row stpsblock">
                                  <p class="col-md-2 col-sm-12"><strong>Step1:</strong></p>
                                  <p class="col-md-10 col-sm-12" style="font-size: 15px;">Choose the cloud and Click on Configure button</p></div>
                                  <div class="row stpsblock">
                                    <p class="col-md-2 col-sm-12 "><strong>Step2:</strong></p>
                                    <p class="col-md-10 col-sm-12" style="font-size: 15px;">Enter your Cloud Account credentials  </p></div>
                                    <div class="row stpsblock">
                                        <p class="col-md-2 col-sm-12 "><strong>Step3:</strong></p>
                                        <p class="col-md-10 col-sm-12" style="font-size: 15px;">Allow MasterGST to access your cloud account  </p></div>
                                    <div class="row stpsblock">
                                            <p class="col-md-2 col-sm-12 "><strong>Step4:</strong></p>
                                            <p class="col-md-10 col-sm-12" style="font-size: 15px;">Start uploading Documents from MasterGST.</p></div>
                                    <!-- left profile list end -->
                                </div>
                               
                                <div class="col-lg-8 col-md-8 col-sm-12">
                                    <!--   profile table begin -->
                                    <div class="welhdr">
                                        <h5 class="welcomehr" style="text-align:  center;font-size: 16px;">Welcome to MasterGST Docs, You seems not yet configured your cloud account, it is one time setup will take not less than a minute time. Please configure here for one time.</h5>
                                        
                                    </div>
                                    <!-- Nav tabs -->
                                    <div class="drive-div">
                                    <p id="success-msg" style="text-align:  center;margin-bottom: 0px;margin-top:  13px;font-weight:  bolder;font-size:  18px;">Click here to configure</p>
                                    <div class="drive-img-div">
                                   <a href="https://www.google.com/drive/" target="_blank" > <img src="${contextPath}/static/mastergst/images/master/Google_Drive_logo.jpg" style="height:137px; width:150px;padding:10px"></a>
                                   <p style="margin-top: 14px;font-weight:bolder;font-size:22px;color: #5769bb;">Google Drive</p>
                                    </div>
                                    </div>
                                    
                                    
                                    
                                    
                                    
                                    
                                    
                                                       
              </div>            
                                      <!--   profile table end -->
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    
                </div>
                <!-- end content  -->
            </div>
    </div>
  </div>
    
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
    <!-- Add Modal Start -->
    <div class="modal fade modal-right" id="addModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform bs-fancy-checks">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>Add Document</h3>

                    </div>
                    <!-- row begin -->
					<div class=" p-4">
					<form:form method="POST" class="meterialform" action="${contextPath}/uploadDoc/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" enctype="multipart/form-data">
					<div class="row">
						<div class="form-group form-group-file col-9">
						  <input type="file" name="file" id="docFile" />
						  <label for="docFile" class="control-label">Choose File</label>
						  <i class="bar"></i> </div>

						<div class="form-group col-3">
							<input type="submit" class="btn btn-blue" value="Upload"/>
						</div>
                    </div>
					</form:form>
					</div>
                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>
    <!-- Add Modal End -->
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
       
		  $("div.toolbar").html('<h4>Docs</h4><a href="#" class="btn btn-blue-dark"  data-toggle="modal" data-target="#addModal">Add</a> ');
 
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
		$(function() {
			$('#cpDocNav').addClass('active');
			$('#nav-client').addClass('active');
			$('#cp_center').css("display","none");
			$('#cp_Allcenter').css("display","none");
			$('#cp_centerFiling').css("display","none");
			$('#cp_centerClinet').css("display","none");
        	fetchDocs(true);
		});
		function fetchDocs(initial) {
			$.ajax({
				url: "${contextPath}/getdocs/${id}",
				async: true,
				cache: false,
				contentType: 'application/json',
				success : function(data) {
					if(initial) {
						$('.docspage1').hide();
						$('.docspage2').show();
					} else {
						$('.docspage1').show();
						$('.docspage2').hide();
					}
				}
			});
		}
		function updateCredentials() {
			fetchDocs(false);
			window.open('https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=768121484606-baaiub64egjkn5snlm7775fas9up35d1.apps.googleusercontent.com&redirect_uri=http://app.mastergst.com/login&response_type=code&scope=https://www.googleapis.com/auth/drive.file','_blank');
		}
    </script>
</body>

</html>