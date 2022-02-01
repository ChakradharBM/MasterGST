<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Suppliers</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<script src="${contextPath}/static/mastergst/js/profile/supplier.js" type="text/javascript"></script>
<style>
#suppliergstnnumber{text-transform: uppercase;}
div#dbTable1_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;}
.stermsdetails .meterialform .checkbox input:checked~.helper::before{height: 11px;width: 2px;left: 5px;top: 11px;}
.stermsdetails .meterialform .checkbox input:checked~.helper::after{height: 6px;top: 7px;left: 0px;}
.stermsdetails .meterialform .checkbox .helper{border-radius:4px;color: #337ab7;}
</style>
<script type="text/javascript">
	var customers=new Array();
	var table;
	$(function() {
		googlemapsinitialize();
	});
	$(document).ready(function() {
		$('#cpSupplierNav').addClass('active');
		$('#nav-client').addClass('active');
		//table.on('draw', rolesPermissions);
		//$("div.toolbar").html('<h4 style="margin-top: 5px;">Suppliers</h4><a href="${contextPath}/static/mastergst/template/suppliers_template.xls" class="pull-right mt-1 ml-2 vt-align"><img src="${contextPath}/static/mastergst/images/master/excel-icon.png" class="vt-align"/> Download Template</a><a href="#" class="btn btn-blue-dark permissionGeneral-Import_Templates"  data-toggle="modal" data-target="#supplierimportModal" onclick="removemsg1()">Import</a><a href="#" class="btn btn-blue-dark permissionSettings-Suppliers-Add" data-toggle="modal" data-target="#editModal" onclick="populateElement()">Add</a><a href="${contextPath}/dwnldsupplierxls/${id}/${client.id}" class="btn btn-blue excel">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a> ');
	});
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
                    <div class="col-md-10 col-sm-12 customtable dataTable_Supplier p-0">
                    <a href="${contextPath}/static/mastergst/template/suppliers_template.xls" class="pull-right mt-1 ml-2 vt-align"><img src="${contextPath}/static/mastergst/images/master/excel-icon.png" class="vt-align"/> Download Template</a>
                    <a href="#" class="btn btn-blue-dark permissionGeneral-Import_Templates"  data-toggle="modal" data-target="#supplierimportModal" onclick="removemsg1()">Import</a>
                    <a href="#" class="btn btn-blue-dark permissionSettings-Suppliers-Add" data-toggle="modal" data-target="#editModal" onclick="populateElement()">Add</a>
                    <a href="${contextPath}/dwnldsupplierxls/${id}/${client.id}" class="btn btn-blue excel">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
                     <a href="#" id="supplierDelete" class="btn btn-blue pull-right vt-align disabled" onclick="showSuppliersDeleteAllPopUp()">Delete</a> <h4 class="mb-0">Suppliers</h4>
                        <table id="supplierTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                <th class="text-center"><div class="checkbox"><label><input type="checkbox" id='supplierCheck' onClick="updateSuppliersMainSelection(this)"/><i class="helper"></i></label></div> </th>
                                	<th class="text-center">Supplier Id</th>
									<th class="text-center">Name</th>
									<th class="text-center">Category</th>
									<th class="text-center">Mobile No</th>
									<th class="text-center">State</th>
									<th class="text-center">Email</th>
									<th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody id="supplierBody">
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
    <div class="modal fade" id="supplierimportModal" role="dialog" aria-labelledby="importModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3>Import Suppliers</h3>

                    </div>
                     <!-- row begin -->
					<div class="p-4">
					<form:form method="POST" class="meterialform" id="itemsimportform" action="${contextPath}/uploadFile/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" enctype="multipart/form-data" style="border:none!important;">
					<div class="row">


         <fieldset style="width:  100%;">
		 <span class="errormsg" style="display:none!important;font-size:14px;"> please select a file</span>
                  <div class="filedragwrap" onClick="choosesupplierfileSheets()">
              <div id="filedragi" style="display: block;">
                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000">
                <div class="filedraginput"> <span class="choosefile importchoosefile" style="left:0%!important;">Choose File</span>
              <input type="file" name="file" id="supplierFile" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,.csv"  style="opacity:0!important;display:none"> 
                </div>
                <div class="ortxt"> --( OR )--</div>
                <div id="filedragi">Drop file here</div>
              </div>
            </div>
            </fieldset>
            <div class="form-group col-md-12 col-sm-12" id="idSheet7" style="display:none;">
								<p class="lable-txt">File Name  :  <span id="messages7"></span></p>
								<div class="">&nbsp;</div>
								</div>
						<div class="form-group col-4">
							<input type="hidden" name="category" value="<%=MasterGSTConstants.SUPPLIERS%>">
							<input type="submit"  id="submitbutton7" class="btn btn-primary" value="submit" style="background-color: #314999; color:white;width:61%!important;font-size: 18px;text-transform: uppercase;margin-left: 30px;"/>
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
            <div class="modal-header p-0">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close" onClick="closesupplier()">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Supplier Details</h3>
                    </div>
            </div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" id="suppliers_form" class="meterialform" name="userform" action="${contextPath}/cp_createsupplier/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="customer">
                    <div class="row gstr-info-tabs pl-5 pr-5 pb-3" style="height:100%;overflow-x:hidden;overflow-y:auto;">
						<div class="form-group col-md-12 col-sm-12">
                            <div class="lable-txt"></div>
                            <div class="form-group-inline">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="type" id="type" type="radio" value="Business" checked />
                                            <i class="helper"></i>Registered</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="type" id="type" type="radio" value="Individual" />
                                            <i class="helper"></i>UnRegistered</label>
                                    </div>
                                </div>
                            </div>
                        </div>
						<div class="form-group col-md-6 col-sm-12 category_business">
                            <p class="lable-txt">GSTIN/Unique ID  <a href="#" onClick="invokePublicAPI(this)" class="btn btn-green btn-sm pull-right">Get GSTIN Details</a> </p>
                            <span class="errormsg" id="sgstnnumber_Msg" style="margin-top:-31px"></span>
                            <input type="text" id="suppliergstnnumber" name="gstnnumber" aria-describedby="gstnnumber" onChange="updatePan(this.value)" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" maxlength="15" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4Z7" />
                            <label for="gstnnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich" id="idName">Business Name</p>
                            <span class="errormsg" id="businessName_Msg"></span>
                            <input type="text" id="suppliername" name="name" required="required" placeholder="Name" value="" data-error="Enter the Business Name" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))"/>
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 category_business">
                            <p class="lable-txt">Contact Person Name</p>
                            <span class="errormsg" id="contactPersonName_Msg"></span>
                            <input type="text" id="suppliercontactperson" name="contactperson" placeholder="Jane Smith" data-error="Please enter the contact person name" value="" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))"/>
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt" id="idName">Supplier ID</p>
                            <span class="errormsg" id="supplierCustomerId_Msg"></span>
                            <input type="text" id="supplierCustomerId" name="supplierCustomerId" placeholder="Supplier ID" value="" data-error="Enter the Supplier ID" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))"/>
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Mobile Number</p>
                            <span class="errormsg" id="mobilenumber_Msg"></span>
                            <input type="text" id="suppliermobilenumber" name="mobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" value="" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" />
                            <script>$("#suppliermobilenumber").intlTelInput({"initialCountry":"in"});</script>
                            <label for="mobilenumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Email Id</p>
                            <span class="errormsg" id="emailId_Msg"></span>
                            <input type="email" id="supplieremail" name="email" pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' data-error="Please enter valid email address"  placeholder="janeSmith@gmail.com" value="" />
                            <label for="email" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
                            <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">FAX Number</p>
                            <span class="errormsg" id="faxno_Msg"></span>
                            <input type="text" class="faxno" id="faxno" name="faxNo" data-error="Please enter valid FAX No"  placeholder="12345" value="" />
                            <label for="faxno" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
						</div>
						
						<div class="row gstr-info-tabs pl-5" style="padding-left:19px;">
						 <ul class="nav nav-tabs col-md-12 pl-3" role="tablist" id="tabsactive">
							<li class="nav-item"><a class="nav-link active" id="groupstab" data-toggle="tab" href="#addresstab" role="tab">Address</a></li>
							<li class="nav-item"><a class="nav-link " id="groups1tab" data-toggle="tab" href="#detailstab" role="tab">Details</a></li>
							<li class="nav-item"><a class="nav-link " id="stermstab" data-toggle="tab" href="#snotestab" role="tab">Supplier Notes</a></li>
							<li class="nav-item"><a class="nav-link " id="sbanktab" data-toggle="tab" href="#bankdetailstab" role="tab">Bank Details</a></li>
							<li class="nav-item"><a class="nav-link " id="sadditab" data-toggle="tab" href="#additionaltab" role="tab">Additional Details</a></li>
						 </ul>
						<div class="tab-content col-md-12 mb-3 mt-1 pl-0">
							<div class="tab-pane active col-md-12" id="addresstab" role="tabpane1" style="height:210px">
							<div class="row">
									<div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="countries_list">
		                            <p class="lable-txt astrich">Country</p>
		                            <span class="errormsg" id="country_Msg" style="margin-top:-33px"></span>
		                            <select class="form-controll country" required="required" id="countriesList" name="country" data-error="Please select country"></select>
		                            <label for="country" class="control-label"></label>
		                            <i class="bar"></i>
		                          </div>
								<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Landline No</p>
		                            <span class="errormsg" id="landline_Msg"></span>
		                            <input type="text" class="supplierlandline" id="supplierlandline" name="landline" data-minlength="10" maxlength="11" pattern="[0-9]+" data-error="Please enter valid landline number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
		                            <label for="landline" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
							
								<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
									<div class="lable-txt astrich">State</div>
									<div class="form-group">
										<span class="errormsg" id="state_Msg"></span>
										<input class="state supplierstate" id="state" required="required" readonly="readonly" name="state" data-error="Please enter the valid stae name" pattern="\d{2}[a-zA-Z-]+\s*[a-zA-z]*\s*[a-zA-z]**\s*[a-zA-z]*" placeholder="State" />
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
										<span class="errormsg" id="address_Msg"></span>
										<input type="text" id="supplieraddresss" name="address" class="mapicon address" aria-describedby="address" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode >= 44 && event.charCode <= 47) || (event.charCode == 158) || (event.charCode == 32))" placeholder="Address" />
										<label for="address" class="control-label"></label>
										<i class="bar"></i> </div>
								</div>
		
								<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Pin Code</p>
		                            <span class="errormsg" id="mobilenumber_Msg"></span>
		                            <input type="text" class="pincode" id="supplierpincode" name="pincode" data-minlength="5" maxlength="10" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=""/>
		                            <label for="pincode" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		
								<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">City</p>
		                            <span class="errormsg" id="mobilenumber_Msg"></span>
		                            <input type="text" class="city" id="suppliercity" name="city" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
		                            <label for="city" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
							</div>
							</div>
							<div class="tab-pane col-md-12 mt-0" id="detailstab" role="tabpane2" style="height:210px">
								<div class="row">
								<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Ledger Name</p>
		                            <span class="errormsg" id="ledger_Msg"></span>
		                            <input type="text" class="supplierLedgerName" id="supplierLedgerName" name="supplierLedgerName" data-error="Please enter Ledger Name" value="" />
		                            <label for="customerLedgerName" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 pan_number">
		                            <p class="lable-txt">PAN Number</p>
		                            <span class="errormsg" id="customerPanNumber_Msg"></span>
		                            <input type="text" id="supplierPanNumber" name="supplierPanNumber" data-error="Enter the valid pan number" placeholder="PAN Number" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" value="" />
		                            <label for="input" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">TAN Number (PAN Based)</p>
		                            <span class="errormsg" id="customerTanPanNumber_Msg"></span>
		                            <input type="text" id="supplierTanPanNumber" class="supplierTanPanNumber" name="supplierTanPanNumber" maxlength="10" pattern="^[A-Za-z]{4}\d{5}[A-Za-z]{1}$" data-error="Enter the pan tan number" placeholder="PAN TAN Number" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
		                            <label for="name" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">TAN Number (GSTIN Based)</p>
		                            <span class="errormsg" id="customerTanNumber_Msg"></span>
		                            <input type="text" id="supplierTanNumber" name="supplierTanNumber" data-error="Enter the tan number" placeholder="TAN Number" maxlength="10" pattern="^[A-Za-z]{4}\d{5}[A-Za-z]{1}$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" value="" />
		                            <label for="input" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
								</div>
					</div>
				<div class="tab-pane col-md-12" id="bankdetailstab" role="tabpane4" style="height:210px">
					<div class="row">
									<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Beneficiary Name</p>
		                            <span class="errormsg" id="benefi_Msg"></span>
		                            <input type="text" class="beneficiaryName" id="beneficiaryName" name="beneficiaryName" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" placeholder="Beneficiary Name"  value="" />
		                            <label for="beneficiaryName" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Bank Name</p>
		                            <span class="errormsg" id="bankname_Msg"></span>
		                            <input type="text" class="bankName" id="bankName" name="bankName" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" placeholder="Branch Name" value="" />
		                            <label for="bankName" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Branch Address</p>
		                            <span class="errormsg" id="branchaddr_Msg"></span>
		                            <input type="text" class="branchAddress" id="branchAddress" name="branchAddress"  placeholder="Branch Address" value="" />
		                            <label for="branchAddress" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Account Number</p>
		                            <span class="errormsg" id="accNo_Msg"></span>
		                            <input type="text" class="accountNumber" id="accountNumber" name="accountNumber"  maxlength="18" minlength="9"  onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" pattern="^[0-9]*$" placeholder="Account Number" value="" />
		                            <label for="accountNumber" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                             <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">IFSC Code</p>
		                            <span class="errormsg" id="ifsc_Msg"></span>
		                            <input type="text" class="ifscCode" id="ifscCode" name="ifscCode" onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="IFSC Code" pattern="^[a-zA-Z0-9]*$"  value="" />
		                            <label for="ifscCode" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Account Type</p>
		                            <span class="errormsg" id="acctype_Msg"></span>
		                            <input type="text" class="accountType" id="accountType" name="accountType"  onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="Account Type" pattern="^[a-zA-Z0-9]*$"  value="" />
		                            <label for="accountType" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">MICR Code</p>
		                            <span class="errormsg" id="micr_Msg"></span>
		                            <input type="text" class="micrCode" id="micrCode" name="micrCode"  onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="MICR Code" pattern="^[a-zA-Z0-9]*$" value="" />
		                            <label for="micrCode" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                 </div>
					</div>
						<div class="tab-pane col-md-12" id="additionaltab" role="tabpane5" style="height:210px">
					<div class="row">
					<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Company Registration Number</p>
		                            <span class="errormsg" id="regno_Msg"></span>
		                            <input type="text" class="regNo" id="regNo" name="companyRegNo"  pattern="[0-9]+" data-error="Please enter valid landline number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
		                            <label for="regNo" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Date Of Inception(DD/MM/YYYY)</p>
									<i class="fa fa-calendar"  style="position: absolute;top: 50%;right: 12%;"></i>
		                            <span class="errormsg" id="incepitiondate_Msg"></span>
		                            <input type="text" class="dateofinception" id="dateofinception" name="dateofInception"  value="" />
		                            <label for="dateofinception" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                             <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Nature Of Expertise</p>
		                            <span class="errormsg" id="expertize_Msg"></span>
		                            <input type="text" class="natuteOfExpertise" id="natuteOfExpertise" name="natuteOfExpertise"  value="" />
		                            <label for="natuteOfExpertise" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Revenue TurnOver</p>
		                            <span class="errormsg" id="turnover_Msg"></span>
		                            <input type="text" class="turnover" id="turnover" name="turnover"  value="" />
		                            <label for="turnover" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Quality Programs And Certifications</p>
		                            <span class="errormsg" id="progandcert_Msg"></span>
		                            <input type="text" class="programsAndCert" id="programsAndCert" name="programsAndCert"  value="" />
		                            <label for="programsAndCert" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Full Time Employees</p>
		                            <span class="errormsg" id="fulltimeemployees_Msg"></span>
		                            <input type="text" class="fulltimeEmployees" id="fulltimeEmployees" name="fulltimeEmployees"  value="" />
		                            <label for="fulltimeEmployees" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                            <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
		                            <p class="lable-txt">Applicability Of Micro,Small,Medium Act 2006</p>
		                            <span class="errormsg" id="applicability_Msg"></span>
		                            <input type="text" class="applicability" id="applicability" name="applicability"  value="" />
		                            <label for="applicability" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
							</div>
					</div>
					<div class="tab-pane col-md-12" id="snotestab" role="tabpane3" style="height:210px">
					<div class="row">
						<div class="col-md-12 col-sm-12 stermsdetails mt-1">
		                  <div class="form-check mb-2 mb-sm-0" style="margin-top:0px; margin-bottom:5px;margin-left:2px;">
			                  <div class="meterialform mt-2" style="float:left">
			                      <div class="checkbox pull-right" id="supplierTermsCheck" style="margin-top:-2px;">
			                        <label>
			                       <input class="addSupplierTermsDetails" type="checkbox" name="isSupplierTermsDetails">
			                        <i class="helper"></i>Enable Notes</label>
			                      </div>
			                    </div>
						  </div>
	                </div>
	                 <div class="col-md-12 col-sm-12" id="selectsTermsDiv" style="display:none">
	                 	<textarea type="text" class="supTerms" id="supplierterms" name="supplierterms" placeholder="Supplier Notes" value="" style="border: 1px solid lightgray;width:100%;height:150px;"></textarea>
	                 </div>
                </div>
			</div>
						</div>
							<input type="hidden" id="suppuserid" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
							<input type="hidden" name="serialno" value="">
							<input type="hidden" id="oldsupplierid" value="">
							<input type="hidden" name="createdDate" value=''>
							<input type="hidden" name="createdBy" value="">
							<input type="hidden" name="abcd" id="supid" value="">
                    </div>
                      <div class=" col-12 mt-2 text-center" style="display:block">
                      	<input type="submit" class="btn btn-blue-dark  submit-supplier-form hidden" value="Save" style="display:none"/>
					  </div>
					</form:form>
                    <!-- row end -->
                </div>
                <div class="modal-footer text-center" style="display:block;border-top: 1px solid lightgray;">
                <label for="submit-supplier-form" class="btn btn-blue-dark m-0" tabindex="0" onclick="labelsupppliersubmit()">Save</label>
                <a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close" onClick="closesupplier()">Cancel</a>
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
          <h3>Delete Supplier </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Supplier <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Supplier</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>

<script src="${contextPath}/static/mastergst/js/common/filedrag-map7.js" type="text/javascript"></script>
</body>
<script type="text/javascript">
var supplierArray = new Array();
loadSupplierTable('${id}', '${client.id}', '${month}', '${year}', '${usertype}', '${fullname}');
$('.dateofinception').datetimepicker({
  	timepicker: false,
  	format: 'd/m/Y',
  	scrollMonth: true
});
	function updatePan(value) {
		if(value.length == 15) {
			$('#supplierPanNumber').val(value.substring(2,12));
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
						$('.supplierstate').val(response.name);
					}
				}
			});
		}
	}
function closesupplier(){
	$('#suppliergstnnumber,#suppliername,#suppliercontactperson,#supplierCustomerId,#suppliermobilenumber,#supplieremail,.faxno,.supplierlandline,.state,.address,.pincode,.city,.supplierLedgerName,#supplierPanNumber,.supplierTanPanNumber,#supplierTanNumber,.supTerms,.beneficiaryName,.bankName,.branchAddress,.accountNumber,.ifscCode,.accountType,.micrCode,.regNo,.dateofinception,.natuteOfExpertise,.turnover,.programsAndCert,.fulltimeEmployees,.applicability').val('');
	$('input[name=type][value="Business"]').prop('checked', 'checked');
	$('.category_business').css("display","block");
	$('.addCustomerBankDetails').prop("checked",false);
	$("#selectTermsDiv").css("display","none");
	$('.addSupplierTermsDetails').prop("checked",false);
	$('#supid').val('').attr('name', 'abcd');
	$('form[name="userform"]').validator('update');
}
</script>
</html>