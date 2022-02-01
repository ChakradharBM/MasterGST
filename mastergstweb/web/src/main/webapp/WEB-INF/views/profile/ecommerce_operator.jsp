<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | E-Commerce Operator</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<style>
.dataTables_filter input{height:30px!important;}
div#dbTable1_length{margin-left: 15px;}
</style>
<script type="text/javascript">
	var eoperator=new Array();
	var table;
	$(function() {
		googlemapsinitialize();
	});
	$(document).ready(function() {
		$('#cpEcommerce').addClass('active');
		$('#nav-client').addClass('active');
		table = $('table.display').DataTable({
			dom: '<"toolbar"f>Blfrtip<"clear">', 		
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"responsive": true,
			"ordering": true,
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
		table.on('draw', rolesPermissions);
		$("div.toolbar").html('<h4 style="margin-top: 6px;">E-Commerce Operator</h4><a href="#" class="btn btn-blue-dark" data-toggle="modal" data-target="#editModal" onclick="populateElement()">Add</a> ');
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
		var stateoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-state ul").children().length == 0) {
						//$("#state").val('');
						$("#stateempty").show();
					} else {
						$("#stateempty").hide();
					}
				},
				maxNumberOfElements: 37
			}
		};
		$(".estate").easyAutocomplete(stateoptions);
		google.maps.event.addDomListener(window, 'load', initialize);
	});
	function initialize() {
		var address = document.getElementById('eaddresss');
		var autocomplete = new google.maps.places.Autocomplete(address);
	}
	
	function populateElement(opeartorId) {
		$('.with-errors').html('');
		$('.form-group').removeClass('has-error has-danger');
		$('#egstnnumber').val('');
		$('#ebname').val('');
		$('#operator').val('');
		$('.eemail').val('');
		$('.eaddress').val('');
		$('.epincode').val('');
		$('.ecity').val('');
		$('.elandline').val('');
		$('.estate').val('');
		$('#ecountriesList').val('');
		$('#emobilenumber').val('');
		$('#eaddresss').val('');
		$('#operatorPanNumber').val('');
		$("input[name='id']").remove();
		
		$.ajax({
			url: "${contextPath}/countrieslist",
			contentType: 'application/json',
			success : function(response) {
				$('#ecountriesList').append('<option value="India">India</option>');
				for(var i = 0; i < response.length; i++) {
					$('#ecountriesList').append("<option value="+response[i].name+">"+response[i].name+"</option>");    
				}
			}
		});
		if(opeartorId) {
			eoperator.forEach(function(ecoperator) {
				if(ecoperator.id == opeartorId) {
					$("form[name='userform']").append('<input type="hidden" name="id" value="'+ecoperator.id+'">');
							$('#ebname').val(ecoperator.name);
							$('#egstnnumber').val(ecoperator.gstnnumber);
							$('#operatorPanNumber').val(ecoperator.operatorPanNumber);
							$('#operator').val(ecoperator.operator);
							$('.eemail').val(ecoperator.email);
							$('#emobilenumber').val(ecoperator.mobilenumber);
							$('.address').val(ecoperator.address);
							$('.epincode').val(ecoperator.pincode);
							$('.ecity').val(ecoperator.city);
							$('.elandline').val(ecoperator.landline);
							$('.estate').val(ecoperator.state);
				}
			});
		}
	
	}
	function showDeletePopup(operatorid, name) {
		$('#deleteModal').modal('show');
		$('#delPopupDetails').html(name);
		$('#btnDelete').attr('onclick', "deleteOpearator('"+operatorid+"')");
	}

	function deleteOpearator(operatorid) {
		$.ajax({
			url: "${contextPath}/deloperator/"+operatorid,
			success : function(response) {
				table.row( $('#row'+operatorid) ).remove().draw();
			}
		});
	}
</script>
</head>

<body class="body-cls">
  <!-- header page begin -->
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
                    <!-- dashboard cp  begin -->
                    <div class="col-md-10 col-sm-12 customtable p-0">
                        <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
									<th class="text-center">Business Name</th>
									<th class="text-center">E-Commerce Operator</th>
									<th class="text-center">Mobile No</th>
									<th class="text-center">State</th>
									<th class="text-center">Email</th>
									<th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
								<c:forEach items="${eoperator}" var="ecoperator">
								<script type="text/javascript">
									var ecoperator = new Object();
									ecoperator.id = '<c:out value="${ecoperator.id}"/>';
									ecoperator.name = '<c:out value="${ecoperator.name}"/>';
									ecoperator.gstnnumber = '<c:out value="${ecoperator.gstnnumber}"/>';
									ecoperator.operator = '<c:out value="${ecoperator.operator}"/>';
									ecoperator.email = '<c:out value="${ecoperator.email}"/>';
									ecoperator.mobilenumber = '<c:out value="${ecoperator.mobilenumber}"/>';
									ecoperator.state = '<c:out value="${ecoperator.state}"/>';
									ecoperator.country='<c:out value="${ecoperator.country}"/>';
									ecoperator.address = '<c:out value="${ecoperator.address}"/>';
									ecoperator.pincode = '<c:out value="${ecoperator.pincode}"/>';
									ecoperator.city = '<c:out value="${ecoperator.city}"/>';
									ecoperator.landline = '<c:out value="${ecoperator.landline}"/>';
									ecoperator.operatorPanNumber = '<c:out value="${ecoperator.operatorPanNumber}"/>';
									eoperator.push(ecoperator);
									</script>
									<tr id="row${ecoperator.id}">
                                    <td align="left">${ecoperator.name}</td>
                                  	<td align="left">${ecoperator.operator}</td>
                                    <td align="left">${ecoperator.mobilenumber}</td>
                                    <td align="left">${ecoperator.state}</td>
                                    <td align="left">${ecoperator.email}</td>
                                    <td class="actionicons"><a class="btn-edt " href="#" data-toggle="modal" data-target="#editModal" onClick="populateElement('${ecoperator.id}')"><i class="fa fa-edit"></i> </a><a href="#" class="" onClick="showDeletePopup('${ecoperator.id}','${ecoperator.operator}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
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
				<div class="modal-header p-0">
				 <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>E-Commerce Operator</h3>
					</div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" id="ecommerce_form" class="meterialform" name="userform" action="${contextPath}/cp_createeoperator/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="operator">
                    <div class="row  pl-5 pr-5 pt-5">

						
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="eomsg">
                            <p class="lable-txt astrich" id="bName">Business Name</p>
                            <span class="errormsg" id="businessName_Msg"></span>
                            <input type="text" id="ebname" class="ebname" name="name" required="required" data-error="Enter the Business Name" placeholder="Name" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 category_business">
                            <p class="lable-txt astrich">E-Commerce Operator</p>
                            <span class="errormsg" id="operator_Msg"></span>
                            <input type="text" id="operator" name="operator" data-error="Enter the operator name" required="required"  placeholder="Jane Smith" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>


                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 pan_number">
                            <p class="lable-txt">PAN Number</p>
                            <span class="errormsg" id="operatorPanNumber_Msg"></span>
                            <input type="text" id="operatorPanNumber" name="operatorPanNumber" data-error="Enter the valid pan number" placeholder="PAN Number" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                       
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1 category_business">
                            <p class="lable-txt astrich">ECommerce GSTIN<a href="#" onClick="invokePublicAPI(this)" class="btn btn-green btn-sm pull-right">Get GSTIN Details</a> </p>
                            <span class="errormsg" id="egstnnumber_Msg" style="margin-top: -31px;"></span>
                            <input type="text" id="egstnnumber" name="gstnnumber" required="required"  aria-describedby="gstnnumber" onChange="updatePan(this.value)" pattern="^[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}$" data-error="Please enter Valid Ecommerce GSTIN.(Sample 07CQZCD1111I4C7)" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4C7" />
                            <label for="gstnnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12  mt-1 mb-1">
                            <p class="lable-txt">Mobile Number</p>
                            <span class="errormsg" id="emobilenumber_Msg"></span>
                            <input type="text" id="emobilenumber" name="mobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <script>$("#emobilenumber").intlTelInput({"initialCountry":"in"});</script>
                            <label for="mobilenumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                             
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="countries_list">
                            <p class="lable-txt astrich">Country</p>
                            <span class="errormsg" id="ecountry_Msg" style="margin-top:-33px"></span>
                            <select class="form-controll country" required="required" id="ecountriesList" name="country" data-error="Please select country"></select>
                            <label for="country" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                        
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
							<div class="lable-txt astrich">State</div>
							<div class="form-group">
								<span class="errormsg" id="estate_Msg"></span>
								<input class="estate" id="estate" required="required" readonly="readonly" name="state" data-error="Please enter the valid stae name" pattern="\d{2}[a-zA-Z-]+\s*[a-zA-z]*\s*[a-zA-z]**\s*[a-zA-z]*" placeholder="State" />
								<label for="state" class="control-label"></label>
								<div class="help-block with-errors"></div>
								<div id="stateempty" style="display:none">
									<div class="ddbox">
									  <p>Search didn't return any results.</p>
									</div>
								</div>
								<i class="bar"></i> </div>
						</div>
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
							<div class="lable-txt">Address</div>
							<div class="form-group">
								<span class="errormsg" id="eaddress_Msg"></span>
								<input type="text" id="eaddresss" name="address" class="mapicon address" aria-describedby="address" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode >= 44 && event.charCode <= 47) || (event.charCode == 158) || (event.charCode == 32))" placeholder="Address" />
								<label for="address" class="control-label"></label>
								<i class="bar"></i> </div>
						</div>

						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Pin Code</p>
                            <span class="errormsg" id="pincode_Msg"></span>
                            <input type="text" class="epincode" id="operatorpincode" name="pincode" data-minlength="5" maxlength="10" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=""/>
                            <label for="pincode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">City</p>
                            <span class="errormsg" id="city_Msg"></span>
                            <input type="text" class="ecity" id="opearatorcity" name="city" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                            <label for="city" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Email Id</p>
                            <span class="errormsg" id="emailId_Msg"></span>
                            <input type="text" id="eemail" class="eemail" name="email" pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' data-error="Please enter valid email address"  placeholder="janeSmith@gmail.com" value="" />
                            <label for="email" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Landline No</p>
                            <span class="errormsg" id="landline_Msg"></span>
                            <input type="text" class="elandline" id="elandline" name="landline" data-minlength="10" maxlength="11" pattern="[0-9]+" data-error="Please enter valid landline number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <label for="landline" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                         
                           <div class=" col-12 mt-5 text-center" style="display:block">
                            <input type="hidden" id="eoperatorid" name="eoperatorid" value="">
                           <input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
                       		 <input type="submit" class="btn btn-blue-dark mb-3 ecommerce_submit" id="operatorsave" style="display:none" value="Save"/>
							</div>
                    </div>
					</form:form>
                    <!-- row end -->

                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="ecommerce_submit" class="btn btn-blue-dark m-0" tabindex="0" onclick="labelecomsubmit()">Save</label>
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
            </div>
        </div>
    </div>
    <!-- Edit Modal End -->
<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete E-commerce Operator </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Operator <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Operator</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
</body>
<script type="text/javascript">
function labelecomsubmit(){
	$('#ecommerce_form').submit();
}
$('#ecommerce_form').submit(function(e){
	var err = 0;
	$('#invoice').find('input').each(function(){
	    if(!$(this).prop('required')){
	    }else{
	    	
	    	var bca = $(this).val();
		 	   if( bca == ''){
		 		  err = 1;
		 		   $(this).parent().addClass('has-error has-danger');
		 	   }else{
		 		   $(this).parent().removeClass('has-error has-danger');
		 	   }
	    }
	});
	$('#invoice').find('select').each(function(){
	    if(!$(this).prop('required')){
	    }else{
				if (this.value == '-Select-' || this.value == '') {
					err = 1;
			       $(this).parent().addClass('has-error has-danger');
			    }else{
			    	$(this).parent().removeClass('has-error has-danger');
			    }
	    }
	});
	 if (err != 0) {
		return false;
	  }
});
function invokePublicAPI(btn) {
	var gstnno = $("#egstnnumber").val();
	updatePan(gstnno);
	var userid = '${id}';
	if(gstnno != '') {
		var gstnumber = gstnno.toUpperCase();
		$(btn).addClass('btn-loader');
		$.ajax({
			url: "${contextPath}/publicsearch?gstin="+gstnumber+"&userid="+userid,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response.error && response.error.message) {	
					if(response.error.message == 'SWEB_9035'){
						$('#egstnnumber_Msg').text("No Records Found");	
				  	} else{
						$('#egstnnumber_Msg').text(response.error.message);
				  	}
				}
				if(response.status_cd == '1') {
					if(response.data) {
						var address = "";
						if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){
							$('#ebname').val(response.data['lgnm']);
							if(response.data['lgnm'] != ''){
								$('#eomsg ul.list-unstyled li').html('');
								$('#eomsg').removeClass('has-error has-danger');
							}
						}else{
							$('#ebname').val(response.data['tradeNam']);
							if(response.data['tradeNam'] != ''){
								$('#eomsg ul.list-unstyled li').html('');
								$('#eomsg').removeClass('has-error has-danger');
							}
						}
					Object.keys(response.data).forEach(function(key) {
						if(key == 'pradr'){
						Object.keys(response.data['pradr']['addr']).forEach(function(key){
							if(response.data['pradr']['addr'][key] != ''){
								if(key != 'pncd' && key != 'stcd'){
									address = address.concat(response.data['pradr']['addr'][key]+",");
								}
								if(key == 'pncd'){
									$('#operatorpincode').val(response.data['pradr']['addr'][key]);
								}
								if(key == 'city'){
									$('#operatorcity').val(response.data['pradr']['addr'][key]);
								}
							}
						});
					}
					});
					$('#eaddresss').val(address.slice(0,-1));
					}
				}
				$(btn).removeClass('btn-loader');
				$('#operatorsave').removeClass('disabled');
			},
			error : function(e, status, error) {
				$(btn).removeClass('btn-loader');
			}
		});
	}
}

function updatePan(value) {
	if(value.length == 15) {
		$('#operatorPanNumber').val(value.substring(2,12));
		$('.pan_number .with-errors').html('');
		$('.pan_number').removeClass('has-error has-danger');
		$.ajax({
			url: "${contextPath}/srchstatecd?code="+value.substring(0,2),
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response) {
					$('.estate').val(response.name);
				}
			}
		});
	}
}
</script>
</html>