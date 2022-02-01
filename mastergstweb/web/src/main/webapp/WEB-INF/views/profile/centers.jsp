<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Customers</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>

<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<script type="text/javascript">
	var centers=new Array();
	$(function() {
		googlemapsinitialize();
	});
	$(document).ready(function() {
		$('#cp_center').css("display","block");
		$('#cp_Allcenter').css("display","block");
		$('#cp_centerFiling').css("display","block");
		$('#cp_centerClinet').css("display","block");
		$('#cpCenterNav').addClass('active');
		$('#nav-team').addClass('active');
		$('.nonAspAdmin').addClass('active');
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
		$("div.toolbar").html('<h4>All Suvidha Kendras</h4><a href="#" class="btn btn-blue-dark"  onclick="addCenter()">Add Suvidha Kendra</a> ');
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

		google.maps.event.addDomListener(window, 'load', initialize);
	});
	function initialize() {
		var centeraddress = document.getElementById('centeraddress');
		var autocomplete = new google.maps.places.Autocomplete(centeraddress);
	}
	
	function addCenter(){
		var allowedCenters = $('#allowedCentersCount').val();
		var rowCount = $('#dbTable1 tr').length - 1;
		if(rowCount < allowedCenters){
			$('#editModal').modal('show');
			$('#centername').val('');
			$('#contactPerson').val('');
			$('#emailID').val('');
			$('#centremobileNumber').val('');
			$('#password').val('');
			$('#centeraddress').val('');
			$("input[name='id']").remove();
			$("input[name='createdDate']").val('');
			$("input[name='createdBy']").val('');
		}else{
			$('#errorMessage').html('Please contact MasterGST support team at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992.');
			$('#errorMessage').parent().show();
		}
	}
	
	function populateElement(centerId) {
		$('.with-errors').html('');
		$('.form-group').removeClass('has-error has-danger');
		$('#centername').val('');
		$('#contactPerson').val('');
		$('#emailID').val('');
		$('#centremobileNumber').val('');
		$('#password').val('');
		$('#centeraddress').val('');
		$("input[name='id']").remove();
		$("input[name='createdDate']").val('');
		$("input[name='createdBy']").val('');

		if(centerId) {
			centers.forEach(function(center) {
				if(center.id == centerId) {
					$('#centername').val(center.name);
					$('#contactPerson').val(center.contactperson);
					$('#emailID').val(center.email);
					$('#centremobileNumber').val(center.mobilenumber);
					$('#password').val(center.password);
					$('#centeraddress').val(center.address);
					$("form[name='userform']").append('<input type="hidden" name="id" value="'+center.id+'">');
					$("input[name='createdDate']").val(center.createdDate);
					$("input[name='createdBy']").val(center.createdBy);
				}
			});
		}
	}
</script>
</head>

<body class="body-cls">
  <!-- header page begin -->
  <c:choose>
	<c:when test='${not empty client && not empty client.id}'>
		<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
	</c:when>
	<c:otherwise>
		<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
	</c:otherwise>
	</c:choose>
		<!--- breadcrumb start -->
 		
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></li>
						<li class="breadcrumb-item active">Suvidha Kendras</li>
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

                    <!-- dashboard cp  begin -->
					<input hidden  id="allowedCentersCount" value="${allowedCenters}">
                    <div class="col-md-10 col-sm-12 customtable p-0">
                        <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
									<th>Name</th>
									<th class="text-center">Contact</th>
									<th class="text-center">Mobile No</th>
									<th class="text-center">Email</th>
									<th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
								<c:forEach items="${centers}" var="center">
								<script type="text/javascript">
									var center = new Object();
									center.id = '<c:out value="${center.id}"/>';
									center.name = '<c:out value="${center.name}"/>';
									center.contactperson = '<c:out value="${center.contactperson}"/>';
									center.email = '<c:out value="${center.email}"/>';
									center.mobilenumber = '<c:out value="${center.mobilenumber}"/>';
									center.password = '<c:out value="${center.password}"/>';
									center.address = '<c:out value="${center.address}"/>';
									center.createdDate = '<fmt:formatDate pattern="yyyy-MM-dd" value="${center.createdDate}" />';
									center.createdBy = '<c:out value="${center.createdBy}"/>';
									centers.push(center);
								</script>
                                <tr>
                                    <td align="center">${center.name}</td>
                                    <td align="center">${center.contactperson}</td>
                                    <td align="center">${center.mobilenumber}</td>
                                    <td align="center">${center.email}</td>
                                    <td class="actionicons">
                                    	<c:choose>
                                    		<c:when test="${useremail ne center.email}"> 
                                    			<a class="btn-edt" href="#" data-toggle="modal" data-target="#editModal" onClick="populateElement('${center.id}')"><i class="fa fa-edit"></i> </a>
                                    		</c:when>
                                    		<c:otherwise>
                                    		</c:otherwise>
                                    	</c:choose>
                                    </td>
                                    	
                                                	
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
		

		 <!-- Edit Modal Start -->
    <div class="modal fade" id="editModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>Center Details</h3>

                    </div>
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform" name="userform" action="${contextPath}/cp_createcenter/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="center">
                    <div class="row  p-5">

						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich" id="idName">Center Name</p>
                            <span class="errormsg" id="businessName_Msg"></span>
                            <input type="text" id="centername" name="name" required="required" data-error="Please enter te center name" placeholder="Name" value="" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12 category_business">
                            <p class="lable-txt astrich">Contact Person Name</p>
                            <span class="errormsg" id="contactPersonName_Msg"></span>
                            <input type="text" id="contactPerson" name="contactperson" placeholder="Jane Smith" required="required" data-error="Please enter the contact person name" value="" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Mobile Number</p>
                            <span class="errormsg" id="mobilenumber_Msg"></span>
                            <input type="text" id="centremobileNumber" name="mobilenumber" required="required" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" value="" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" />
                            <script>$("#centremobileNumber").intlTelInput({"initialCountry":"in"});</script>
                            <label for="mobilenumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
						
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Email Id</p>
                            <span class="errormsg" id="emailId_Msg"></span>
                            <input type="email" id="emailID" name="email" required="required" pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' data-error="Please enter valid email address"  placeholder="janeSmith@gmail.com" value="" />
                            <label for="email" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Password</p>
                            <span class="errormsg" id="password_Msg"></span>
                            <input type="password" id="password" name="password" data-minlength="6" data-error="Please enter valid password" placeholder="*******" value="" />
                            <label for="email" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
							
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Address</p>
                            <input type="text" id="centeraddress" name="address" class="mapicon" value="" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode >= 44 && event.charCode <= 47) || (event.charCode == 158) || (event.charCode == 32))"/>
                            <label for="address" class="control-label"></label>
                            <i class="bar"></i> </div>

                        <div class=" ">&nbsp;</div>

                        <div class=" col-12 mt-4 text-center" style="display:block">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="createdDate" value=''>
							<input type="hidden" name="createdBy" value="">
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
    <!-- Edit Modal End -->

</body>

</html>