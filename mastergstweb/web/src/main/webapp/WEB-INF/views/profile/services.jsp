<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Services</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script type="text/javascript">
	var services = new Array();
	var table;
	$(document).ready(function() {
		$('#cpServiceNav').addClass('active');
		$('#nav-client').addClass('active');

		var sacoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/sacconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-HSNCode ul").children().length == 0) {
						//$("#HSNCode").val('');
						$("#HSNCodeempty").show();
					} else {
						$("#HSNCodeempty").hide();
					}
				}
			}
		};
		$("#HSNCode").easyAutocomplete(sacoptions);
		var uqcoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/uqcconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onLoadEvent: function() {
					if($("#eac-container-unitofMeasurement ul").children().length == 0) {
						$("#unitofMeasurementempty").show();
					} else {
						$("#unitofMeasurementempty").hide();
					}
				}
			}
		};
		$("#unitofMeasurement").easyAutocomplete(uqcoptions);
		table = $('table.display').DataTable({
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
	   
		$("div.toolbar").html('<h4>Services</h4><a href="${contextPath}/static/mastergst/template/service_template.xls" class="pull-right mt-1 ml-2 vt-align"><img src="${contextPath}/static/mastergst/images/master/excel-icon.png" class="vt-align"/> Download Template</a><a href="#" class="btn btn-blue-dark"  data-toggle="modal" data-target="#serviceimportModal" onclick="removemsg1()">Import</a><a href="#" class="btn btn-blue-dark"  data-toggle="modal" data-target="#editModal" onclick="populateElement()">Add</a> ');
 
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
	});
	function removemsg1(){
		$('.errormsg').css('display','none');
	} 
	function populateElement(serviceId) {
		$('.with-errors').html('');
		$('.form-group').removeClass('has-error has-danger');
		$('#productName').val('');
		$('#productDescription').val('');
		$('#unitofMeasurement').val('');
		$('#serviceCost').val('');
		$('#HSNCode').val('');
		$('#taxrate').val('');
		$("input[name='id']").remove();
		$("input[name='serialno']").val(services.length);
		$("input[name='createdDate']").val('');
		$("input[name='createdBy']").val('');

		if(serviceId) {
			services.forEach(function(service) {
				if(service.id == serviceId) {
					$('#productName').val(service.name);
					$('#productDescription').val(service.description);
					$('#unitofMeasurement').val(service.unit);
					$('#serviceCost').val(service.cost);
					$('#HSNCode').val(service.sac);
					$('#taxrate').val(service.taxrate);
					$("form[name='userform']").append('<input type="hidden" name="id" value="'+service.id+'">');
					$("input[name='serialno']").val(service.serialno);
					$("input[name='createdDate']").val(service.createdDate);
					$("input[name='createdBy']").val(service.createdBy);
				}
			});
		}
	}
	function showDeletePopup(serviceId, name) {
		$('#deleteModal').modal('show');
		$('#delPopupDetails').html(name);
		$('#btnDelete').attr('onclick', "deleteProduct('"+serviceId+"')");
	}

	function deleteProduct(serviceId) {
		$.ajax({
			url: "${contextPath}/delservice/"+serviceId,
			success : function(response) {
				table.row( $('#row'+serviceId) ).remove().draw();
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
										<th class="text-center">SN</th>
										<th class="text-center">Service Name </th>
										<th class="text-center">Service Description  </th>
										<th class="text-center">Unit of Measurement  </th>
										<th class="text-center">Service Cost  </th>
										<th class="text-center">SAC Code </th> 
										<th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
								<c:forEach items="${services}" var="service">
								<script type="text/javascript">
									var service = new Object();
									service.id = '<c:out value="${service.id}"/>';
									service.name = '<c:out value="${service.name}"/>';
									service.description = '<c:out value="${service.description}"/>';
									service.unit = '<c:out value="${service.unit}"/>';
									service.cost = '<c:out value="${service.cost}"/>';
									service.sac = '<c:out value="${service.sac}"/>';
									service.serialno = '<c:out value="${service.serialno}"/>';
									service.taxrate = '<c:out value="${service.taxrate}"/>';
									service.createdDate = '<fmt:formatDate pattern="yyyy-MM-dd" value="${service.createdDate}" />';
									service.createdBy = '<c:out value="${service.createdBy}"/>';
									services.push(service);
								</script>
								<c:set var = "varname" value = "${fn:split(service.sac, ':')}"/>
                                <tr id="row${service.id}">
                                    <td align="center">${service.serialno}</td>
                                    <td align="center">${service.name}</td>
                                    <td align="center">${service.description}</td>
									<td align="center">${service.unit}</td>
									<td align="center">${service.cost}</td>
                                    <td align="center">${varname[0]}</td>
                                    <td class="actionicons"><a class="btn-edt" href="#" data-toggle="modal" data-target="#editModal" onClick="populateElement('${service.id}')"><i class="fa fa-edit"></i> </a><a href="#" onClick="showDeletePopup('${service.id}','${service.name}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
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

	<!-- Import Modal Start -->
    <div class="modal fade" id="serviceimportModal" role="dialog" aria-labelledby="importModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3>Import Services</h3>

                    </div>
                    <!-- row begin -->
					<div class=" p-4" style="min-height:600px;">
					<form:form method="POST" class="meterialform" style="border:none!important" id="servicesimportform"  action="${contextPath}/uploadFile/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" enctype="multipart/form-data">
					<div class="row">


         <fieldset style="width:  100%;">
		 <span class="errormsg" style="display:none!important;font-size:14px;"> please select a file</span>
                  <div class="filedragwrap" onClick="chooseservicefileSheets()">
              <div id="filedrags" style="display: block;">
                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000">
                <div class="filedraginput"> <span class="choosefile importchoosefile" style="left:0%!important;">Choose File</span>
              <input type="file" name="file" id="serviceFile" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,.csv"  style="opacity:0!important;display:none"> 
                </div>
                <div class="ortxt"> --( OR )--</div>
                <div id="filedrags">Drop file here</div>
              </div>
            </div>
            </fieldset>
            <div class="form-group col-md-12 col-sm-12" id="idSheet4" style="display:none;">
								<p class="lable-txt">File Name  :  <span id="messages4"></span></p>
								<div class="">&nbsp;</div>
								</div>
						<div class="form-group col-4">
							<input type="hidden" name="category" value="<%=MasterGSTConstants.SERVICES%>">
							
							<input type="submit"  id="submitbutton2" class="btn btn-primary" value="submit" style="background-color: #314999; color:white;width:61%!important;font-size: 18px;text-transform: uppercase;margin-left: 30px;"/>
						</div>
                    </div>
					</form:form>
                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>
	</div>
    <!-- Import Modal End -->

		 <!-- Edit Modal Start -->
    <div class="modal fade" id="editModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>Services</h3>

                    </div>
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform" name="userform" action="${contextPath}/cp_createservice/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="service">
                    <div class="row  p-5">
                        <div class="form-group col-md-6 col-sm-12">
						  <p class="lable-txt astrich">Service Name</p>
						  <span class="errormsg" id="productName_Msg"></span>
						  <input type="text" id="productName" name="name" data-minlength="3" required="required" data-error="Please enter more than 3 characters" placeholder="BVMCS" value="" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" />
						  <label for="input" class="control-label"></label>
						  <div class="help-block with-errors"></div>
						  <i class="bar"></i> </div>
									 
						<div class="form-group col-md-6 col-sm-12">
						  <p class="lable-txt astrich">Service Description</p>
						  <span class="errormsg" id="productDescription_Msg"></span>
						  <input type="text" id="productDescription" name="description" required="required" placeholder="BVMCS GST Service" value="" />
						  <label for="input" class="control-label"></label>
						  <div class="help-block with-errors"></div>
						  <i class="bar"></i> </div>
									
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt astrich">Unit of Measurement</p>
							<span class="errormsg" id="unitofMeasurement_Msg"></span>
							<input id="unitofMeasurement" required="required" data-error="Please enter the Unit of Measurement of the product"  onkeypress="return ((event.charCode >=65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122))" name="unit"/>
							<label for="unitofMeasurement" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="unitofMeasurementempty" style="display:none">
								<div class="ddbox">
								  <p>Search didn't return any results.</p>
								</div>
							</div>
							<i class="bar"></i> </div>
						
						<div class="form-group col-md-6 col-sm-12">
						  <p class="lable-txt astrich">Service Cost</p>
						  <span class="errormsg" id="serviceCost_Msg"></span>
						  <input type="text" id="serviceCost" name="cost" required="required" data-error="Please enter the cost of Service" placeholder="Service Cost" value="" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46))" />
						  <label for="input" class="control-label"></label>
						  <div class="help-block with-errors"></div>
						  <i class="bar"></i> </div>
						  
						<div class="form-group col-md-6 col-sm-12">
						  <p class="lable-txt astrich">SAC Code</p>
						  <span class="errormsg" id="HSNCode_Msg"></span>
						  <input id="HSNCode" required="required" data-error="Please enter the HSN/SAC code" name="sac" placeholder="SAC" />
						  <label for="HSNCode" class="control-label"></label>
						  <div class="help-block with-errors"></div>
						  <div id="HSNCodeempty" style="display:none">
								<div class="ddbox">
								  <p>Search didn't return any results.</p>
								</div>
							</div>
						  <i class="bar"></i> </div>
						  
						<div class="form-group col-md-6 col-sm-12">
						  <p class="lable-txt">Tax Rate</p>
						  <input id="taxrate" name="taxrate" placeholder="Tax Rate" />
						  <label for="taxrate" class="control-label"></label>
						  <i class="bar"></i> </div>
						  
                        <div class=" col-12 mt-4 text-center" style="display:block">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">
							<input type="hidden" name="serialno" value="">
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
<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Service </h3>
        </div>
        <div class="pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Service <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Service</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<script>
$('#servicesimportform').submit(function(e) {
	  var err = 0;
	    if (!$('#messages4').html()) {    	
	      err = 1;
	    }
		  if (err != 0) {
		   $('.errormsg').css('display','block');
	    return false;
	  }
	});
	function chooseservicefileSheets(){
		$('#serviceFile')[0].click()
	}
</script>
<script src="${contextPath}/static/mastergst/js/common/filedrag-map4.js" type="text/javascript"></script>
</body>

</html>