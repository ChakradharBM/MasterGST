<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Customers</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<script src="${contextPath}/static/mastergst/js/profile/customer.js" type="text/javascript"></script>
<style>
#bgstnnumber{text-transform: uppercase;}
.bnkdetail .meterialform .checkbox input:checked~.helper::before,.termsdetails .meterialform .checkbox input:checked~.helper::before{height: 11px;width: 2px;left: 5px;top: 11px;}
.bnkdetail .meterialform .checkbox input:checked~.helper::after,.termsdetails .meterialform .checkbox input:checked~.helper::after{height: 6px;top: 7px;left: 0px;}
.bnkdetail .meterialform .checkbox .helper,.termsdetails .meterialform .checkbox .helper{border-radius:4px;color: #337ab7;}
#banksdetails.row .col-md-6{display:inline-block}
div.dataTables_filter input{height:30px!important;}
.gstr-info-tabs .nav-tabs .nav-link.active::before, .gstr-info-tabs .nav-tabs .nav-link:hover::before{border-top: 10px solid #ffffff;}
.customer_submit.hidden{display:none}
</style>
<script type="text/javascript">
	var customers=new Array();
	var table;
	$(function() {
		googlemapsinitialize();
	});
	$(document).ready(function() {
		$('#cpCustomerNav').addClass('active');
		$('#nav-client').addClass('active');
		
		//table.on('draw', rolesPermissions);
		//$("div.toolbar").html('<h4 style="margin-top: 6px;">Customers</h4><a href="${contextPath}/static/mastergst/template/customers_template.xls" class="pull-right mt-1 ml-2 vt-align"><img src="${contextPath}/static/mastergst/images/master/excel-icon.png" class="vt-align"/> Download Template</a><a href="#" class="btn btn-blue-dark permissionGeneral-Import_Templates"  data-toggle="modal" data-target="#customerimportModal" onclick="removemsg1()">Import</a><a href="#" class="btn btn-blue-dark permissionSettings-Customers-Add" data-toggle="modal" data-target="#editModal" onclick="populateElement()">Add</a><a href="${contextPath}/dwnldcustomerxls/${id}/${client.id}" class="btn btn-blue excel">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a><a href="#" id="customerDelete" class="btn btn-blue pull-right vt-align" onclick="showCustomersDeleteAllPopUp()">Delete</a> ');
		
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
                    <div class="col-md-10 col-sm-12 customtable dataTable_Customer p-0">
                   
                    <a href="${contextPath}/static/mastergst/template/customers_template.xls" class="pull-right mt-1 ml-2 vt-align"><img src="${contextPath}/static/mastergst/images/master/excel-icon.png" class="vt-align"/> Download Template</a>
                        <a href="#" class="btn btn-blue-dark permissionGeneral-Import_Templates"  data-toggle="modal" data-target="#customerimportModal" onclick="removemsg1()">Import</a>
                        <a href="#" class="btn btn-blue-dark permissionSettings-Customers-Add" onclick="shoeCustomersPopup()">Add</a>
                        <a href="${contextPath}/dwnldcustomerxls/${id}/${client.id}" class="btn btn-blue excel">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
                        <a href="#" id="customerDelete" class="btn btn-blue pull-right vt-align disabled" onclick="showCustomersDeleteAllPopUp()">Delete</a> <h4 class="mb-0">Customers</h4>
                        <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                <th class="text-center"><div class="checkbox"><label><input type="checkbox" id='customerCheck' onClick="updateCustomersMainSelection(this)"/><i class="helper"></i></label></div> </th>
                                    <th class="text-center">Customer Id</th>
									<th class="text-center">Name</th>
									<th class="text-center">Category</th>
									<th class="text-center">Mobile No</th>
									<th class="text-center">State</th>
									<th class="text-center">Email</th>
									<th class="text-center">Credit Period</th>
									<th class="text-center">Credit Amount</th>
									<th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody id="customersBody">
								
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
    <div class="modal fade" id="customerimportModal" role="dialog" aria-labelledby="importModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3>Import Customers</h3>

                    </div>
                     <!-- row begin -->
					<div class="p-4">
					<form:form method="POST" class="meterialform" id="itemsimportform" action="${contextPath}/uploadFile/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" enctype="multipart/form-data" style="border:none!important;">
					<div class="row">


         <fieldset style="width:  100%;">
		 <span class="errormsg" style="display:none!important;font-size:14px;"> please select a file</span>
                  <div class="filedragwrap" onClick="choosecustomerfileSheets()">
              <div id="filedragi" style="display: block;">
                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000">
                <div class="filedraginput"> <span class="choosefile importchoosefile" style="left:0%!important;">Choose File</span>
              <input type="file" name="file" id="customerFile" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,.csv"  style="opacity:0!important;display:none"> 
                </div>
                <div class="ortxt"> --( OR )--</div>
                <div id="filedragi">Drop file here</div>
              </div>
            </div>
            </fieldset>
            <div class="form-group col-md-12 col-sm-12" id="idSheet6" style="display:none;">
								<p class="lable-txt">File Name  :  <span id="messages6"></span></p>
								<div class="">&nbsp;</div>
								</div>
						<div class="form-group col-4">
							<input type="hidden" name="category" value="<%=MasterGSTConstants.CUSTOMERS%>">
							
							<input type="submit"  id="submitbutton6" class="btn btn-primary" value="submit" style="background-color: #314999; color:white;width:61%!important;font-size: 18px;text-transform: uppercase;margin-left: 30px;"/>
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
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="closecustomer()">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Customer Details</h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" id="customers_form" class="meterialform" name="userform" action="${contextPath}/cp_createcustomer/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="customer">
                    <div class="row gstr-info-tabs pl-5 pr-5 pt-1">
						<div class="form-group col-md-12 col-sm-12 mt-1 mb-1">
                            <div class="lable-txt"></div>
                            <div class="form-group-inline">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="type" id="type" type="radio" value="Business" checked/>
                                            <i class="helper"></i>Registered</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="type" id="type" type="radio" value="Individual"/>
                                            <i class="helper"></i>UnRegistered</label>
                                    </div>
                                </div>
                            </div>
                        </div>
						
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1 category_business">
                            <p class="lable-txt astrich">GSTIN/Unique ID <a href="#" onClick="invokePublicAPI(this)" class="btn btn-green btn-sm pull-right">Get GSTIN Details</a> </p>
                            <span class="errormsg" id="bgstnnumber_Msg" style="margin-top: -31px;"></span>
                            <input type="text" id="bgstnnumber" name="gstnnumber" aria-describedby="gstnnumber" onChange="updatePan(this.value)" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" maxlength="15" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4Z7" required/>
                            <label for="gstnnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="bmsg">
                            <p class="lable-txt astrich" id="idName">Business Name</p>
                            <span class="errormsg" id="businessName_Msg"></span>
                            <input type="text" id="bname" class="bname" name="name" required="required" data-error="Enter the Business Name" placeholder="Name" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 category_business">
                            <p class="lable-txt">Contact Person Name</p>
                            <span class="errormsg" id="contactPersonName_Msg"></span>
                            <input type="text" id="bcontactperson" name="contactperson" data-error="Enter the contact person name" placeholder="Jane Smith" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>


						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Customer ID</p>
                            <span class="errormsg" id="customerId_Msg"></span>
                            <input type="text" id="customerId" class="customerId" name="customerId" data-error="Enter the Customer id" placeholder="Customer ID" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                       
                       <div class="form-group col-md-6 col-sm-12  mt-1 mb-1">
                            <p class="lable-txt">Mobile Number</p>
                            <span class="errormsg" id="mobilenumber_Msg"></span>
                            <input type="text" id="bmobilenumber" name="mobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <script>$("#bmobilenumber").intlTelInput({"initialCountry":"in"});</script>
                            <label for="mobilenumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Email Id</p>
                            <span class="errormsg" id="emailId_Msg"></span>
                            <input type="text" id="email" class="email" name="email" pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' data-error="Please enter valid email address"  placeholder="janeSmith@gmail.com" value="" />
                            <label for="email" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

                             <!-- <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            </div> -->
						<br/>
			  <ul class="nav nav-tabs col-md-12 mt-3 pl-3" role="tablist" id="tabsactive">
					<li class="nav-item"><a class="nav-link active" id="groupstab" data-toggle="tab" href="#addresstab" role="tab"><span class="serial-num">1</span>Address</a></li>
					<li class="nav-item"><a class="nav-link " id="groups1tab" data-toggle="tab" href="#detailstab" role="tab"><span class="serial-num">2</span>Details</a></li>
					<li class="nav-item"><a class="nav-link" id="sgroupstab" data-toggle="tab" href="#banksdetails" role="tab"><span class="serial-num">3</span>Bank Details</a></li>
					<li class="nav-item"><a class="nav-link" id="termstab" data-toggle="tab" href="#termsdetails" role="tab"><span class="serial-num">4</span>Customer Notes</a></li>
			  </ul>
				<div class="tab-content col-md-12 mb-3 mt-1">
					<div class="tab-pane active col-md-12" id="addresstab" role="tabpane1" style="height:240px">
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
                            <input type="text" class="landline" id="landline" name="landline" data-minlength="10" maxlength="11" pattern="[0-9]+" data-error="Please enter valid landline number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <label for="landline" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
					
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
							<div class="lable-txt astrich">State</div>
							<div class="form-group">
								<span class="errormsg" id="state_Msg"></span>
								<input class="state" id="state" required="required" readonly="readonly" name="state" data-error="Please enter the valid stae name" pattern="\d{2}[a-zA-Z-]+\s*[a-zA-z]*\s*[a-zA-z]**\s*[a-zA-z]*" placeholder="State" />
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
								<input type="text" id="addresss" name="address" class="mapicon address" aria-describedby="address" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode >= 44 && event.charCode <= 47) || (event.charCode == 158) || (event.charCode == 32))" placeholder="Address" />
								<label for="address" class="control-label"></label>
								<i class="bar"></i> </div>
						</div>

						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Pin Code</p>
                            <span class="errormsg" id="mobilenumber_Msg"></span>
                            <input type="text" class="pincode" id="customerpincode" name="pincode" data-minlength="5" maxlength="10" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=""/>
                            <label for="pincode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">City</p>
                            <span class="errormsg" id="mobilenumber_Msg"></span>
                            <input type="text" class="city" id="customercity" name="city" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                            <label for="city" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
					</div>
					</div>
					<div class="tab-pane col-md-12 mt-0" id="detailstab" role="tabpane2" style="height:240px">
						<div class="row">
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Ledger Name</p>
                            <span class="errormsg" id="ledger_Msg"></span>
                            <input type="text" class="customerLedgerName" id="customerLedgerName" name="customerLedgerName" data-error="Please enter Ledger Name" value="" />
                            <label for="customerLedgerName" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                         <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Credit Period</p>
                            <span class="errormsg" id="credp_Msg"></span>
                            <input type="text" class="credit_p" id="credit_period" name="creditPeriod" placeholder="Number of Days"  pattern="[0-9]+" data-error="Please enter valid number of days" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <label for="creditp" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                         <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Credit Amount</p>
                            <span class="errormsg" id="creditamt_Msg"></span>
                            <input type="text" class="creditAmt" id="creditAmount" name="creditAmount" data-error="Please enter credit Amount" value="" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))"/>
                            <label for="creditAmt" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
						 <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Opening Balance as on 01-04-<span class="openingDtText"></span></p>
                            <span class="errormsg" id="openingb_Msg"></span>
                            <input type="text" class="openingbalance" id="openingbalance" name="openingbalance" placeholder="balance"  data-error="Please enter valid amount" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=""  />
                            <label for="openingb" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                            
                          <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 pan_number">
                            <p class="lable-txt">PAN Number</p>
                            <span class="errormsg" id="customerPanNumber_Msg"></span>
                            <input type="text" id="customerPanNumber" name="customerPanNumber" data-error="Enter the valid pan number" placeholder="PAN Number" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">TAN Number (PAN Based)</p>
                            <span class="errormsg" id="customerTanPanNumber_Msg"></span>
                            <input type="text" id="customerTanPanNumber" class="customerTanPanNumber" name="customerTanPanNumber" maxlength="10" pattern="^[A-Za-z]{4}\d{5}[A-Za-z]{1}$" data-error="Enter the pan tan number" placeholder="PAN TAN Number" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">TAN Number (GSTIN Based)</p>
                            <span class="errormsg" id="customerTanNumber_Msg"></span>
                            <input type="text" id="customerTanNumber" name="customerTanNumber" data-error="Enter the tan number" placeholder="TAN Number" maxlength="10" pattern="^[A-Za-z]{4}\d{5}[A-Za-z]{1}$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
						</div>
					</div>
					<div class="tab-pane col-md-12" id="banksdetails" role="tabpane3" style="height:240px">
					<div class="row">
						<div class="col-md-6 col-sm-12 bnkdetail mt-1">
                  <div class="form-check mb-2 mb-sm-0" style="margin-top:0px; margin-bottom:5px;margin-left:2px;">
                  <div class="meterialform mt-2" style="float:left">
                      <div class="checkbox pull-right" id="customerBankCheck" style="margin-top:-2px;">
                        <label>
                       <input class="addCustomerBankDetails" type="checkbox" name="addCustomerBankDetails">
                        <i class="helper"></i> Link Your Bank</label>
                      </div>
                    </div>
				  </div>
				  <br>
				  <p style="font-size:12px;display: inline-flex;">By Linking of Bank to this customer, bank details will be Automatically populate in Invoice Screen on selection of this customer. </p>
                </div>
				 <div class="col-md-6 col-sm-12 form-group" id="selectBankDiv3" style="display:none">
                  <p for="inlineFormInput" class="lable-txt mb-0">Select Bank</p>
                  <select class="form-control mb-2 mr-sm-2 mb-sm-0 selectCustomerBank" id="selectCustomerBank" onchange="selectCustomerBankName()">
                  </select>
				  <i class="bar"></i>
                </div>
                <div class="col-md-6 col-sm-12">
				</div>
				<!-- <div class="col-md-6 col-sm-12" style="height:96px">
			<p style="font-size:12px">By Linking of Bank to this customer, bank details will be Automatically populate in Invoice Screen on selection of this customer. </p>
				</div> -->
				<div id="selectBankDiv2" class="col-md-6 col-sm-12 pl-3 pr-3 pull-right" style="display:none"> 
				
              <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">Account Number <span style="float: right;">:</span></label>
			  <span id="customerBankAcctNo_txt" class="bold-txt col-md-6 p-0" style="font-size:12px"></span><br/>
              
			  
              <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">Account Name <span style="float: right;">:</span></label>
			  <span id="customerBankAccountName_txt" class="bold-txt col-md-6 p-0" style="font-size:12px"></span><br/>			  
              <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">Branch Name <span style="float: right;">:</span></label>
			  <span id="customerBankBranch_txt" class="bold-txt col-md-6 p-0" style="font-size:12px"></span><br/>
              
			  
              <label for="inlineFormInput" class="mb-0 col-md-6 pl-0" style="font-size:12px">IFSC Code <span style="float: right;">:</span></label>
			  <span id="customerBankIFSC_txt" class="bold-txt p-0 col-md-6"  style="font-size:12px"></span><br>
              
			  
			  </div>
					</div>
					</div>
					
				<div class="tab-pane col-md-12" id="termsdetails" role="tabpane4" style="height:240px">
					<div class="row">
						<div class="col-md-12 col-sm-12 termsdetails mt-1">
		                  <div class="form-check mb-2 mb-sm-0" style="margin-top:0px; margin-bottom:5px;margin-left:2px;">
			                  <div class="meterialform mt-2" style="float:left">
			                      <div class="checkbox pull-right" id="customerTermsCheck" style="margin-top:-2px;">
			                        <label>
			                       <input class="addCustomerTermsDetails" type="checkbox" name="isCustomerTermsDetails">
			                        <i class="helper"></i>Enable Notes</label>
			                      </div>
			                    </div>
						  </div>
	                </div>
	                 <div class="col-md-12 col-sm-12" id="selectTermsDiv" style="display:none">
	                 	<textarea type="text" class="custTerms" id="customerterms" name="customerterms" placeholder="Customer Notes" value="" style="border: 1px solid lightgray;width:100%;height:150px;"></textarea>
	                 </div>
                </div>
			</div>
					
					
				</div>
                      
						<input type="hidden" id="customerBankName" name="customerBankName">
						<input type="hidden" id="customerBankAcctNo" name="customerAccountNumber">
						<input type="hidden" id="customerBankAccountName" name="customerAccountName">
						<input type="hidden" id="customerBankBranch" name="customerBranchName">
						<input type="hidden" id="customerBankIFSC" name="customerBankIfscCode">
						<input type="hidden" id="oldcustomerid" value="">
							<input type="hidden" id="custuserid" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
							<input type="hidden" name="serialno" value="">
							<input type="hidden" name="createdDate" value=''>
							<input type="hidden" name="createdBy" value="">
							<input type="hidden" name="abcd" id="custid" value="">
					</div>
                    		<input type="submit" class="btn btn-blue-dark mb-3 hidden customer_submit" id="customersave" value="Save"/>
					</form:form>
                    <!-- row end -->

                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="customer_submit" class="btn btn-blue-dark m-0" tabindex="0" onclick="labelcustsubmit()">Save</label>
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close" onclick="closecustomer()">Cancel</a>
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
          <h3>Delete Customer </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Customer <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Customer</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<script>

</script>
<script src="${contextPath}/static/mastergst/js/common/filedrag-map6.js" type="text/javascript"></script>
</body>
<script type="text/javascript">
loadCustomerTable('${id}', '${client.id}', '${month}', '${year}', '${usertype}', '${fullname}');
var customerArray = new Array();
var customerClientBankDetails;

function updatePan(value) {
	if(value.length == 15) {
		$('#customerPanNumber').val(value.substring(2,12));
		$('.pan_number .with-errors').html('');
		$('.pan_number').removeClass('has-error has-danger');
		$.ajax({
			url: _getContextPath()+"/srchstatecd?code="+value.substring(0,2),
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response) {
					$('.state').val(response.name);
				}
			}
		});
	}
}
function closecustomer(){
	$('#bgstnnumber,#bname,#bcontactperson,#customerId,#bmobilenumber,#email,.landline,.state,.address,.pincode,.city,.customerLedgerName,.credit_p,.creditAmt,.openingbalance,#customerPanNumber,.customerTanPanNumber,#customerTanNumber,.custTerms,#customerBankName,#customerBankAcctNo,#customerBankAccountName,#customerBankBranch,#customerBankIFSC,#oldcustomerid').val('');
	$('input[name=type][value="Business"]').prop('checked', 'checked');
	$('.category_business').css("display","block");
	$('.addCustomerBankDetails').prop("checked",false);
	$("#selectBankDiv2").hide();
	$("#selectBankDiv3").hide();
	$("#selectTermsDiv").css("display","none");
	$('.addCustomerTermsDetails').prop("checked",false);
	$('#custid').val('').attr('name', 'abcd');
	$('form[name="userform"]').validator('update');
}
</script>
</html>