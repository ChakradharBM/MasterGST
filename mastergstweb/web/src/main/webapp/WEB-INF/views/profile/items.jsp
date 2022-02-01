<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Items</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/profile/items.js" type="text/javascript"></script>
<style>
.isexempted .meterialform .checkbox input:checked~.helper::before{height: 11px;width: 2px;left: 5px;top: 11px;}
.isexempted .meterialform .checkbox input:checked~.helper::after{height: 6px;top: 7px;left: 0px;}
.meterialform .checkbox input:checked~.helper, .meterialform .checkbox label:hover .helper{border-radius: 4px;}
div#dbTable1_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;}
.submit-item-form.hidden{display:none}
#itemform p.lable-txt.pl-3{padding-top: 15px;}
.image-wrap img{max-width:100px!important;}
.meterialform.bs-fancy-checks [type="checkbox"]:not(:checked) + enable_disc:before, .meterialform.bs-fancy-checks [type="checkbox"]:checked + enable_disc:before{width: 38px!important;}
.meterialform.bs-fancy-checks [type="checkbox"]:checked + enable_disc .ui:after{font-size: 10px!important;}
.meterialform.bs-fancy-checks [type="checkbox"]:checked + label:after{top: 2px!important!important;left: 22px!important!important;width: 12px!important;height: 12px!important;}
.meterialform.bs-fancy-checks [type="checkbox"]:not(:checked) + enable_disc:after, .meterialform.bs-fancy-checks [type="checkbox"]:checked + enable_disc:after, .meterialform.bs-fancy-checksde [type="checkbox"]:not(:checked) + enable_disc:after, .meterialform.bs-fancy-checksde [type="checkbox"]:checked + enable_disc:after{top: 2px!important!important;left: 22px!important;width: 12px!important;height: 12px!important;}
.meterialform.bs-fancy-checks [type="checkbox"]:not(:checked) + enable_disc .ui:before{font-size: 10px!important;}
.meterialform.bs-fancy-checks [type="checkbox"]:not(:checked) + enable_disc:before, .meterialform.bs-fancy-checks [type="checkbox"]:checked + enable_disc:before{width: 38px!important;}
/* .add-btn{width:15px!important;height:15px!important;} */
.checkbox-inline {position: relative;display: inline-block;padding-left: 20px;margin-bottom: 0;font-weight: 400; vertical-align: middle;cursor: pointer;}
.checkbox.checkbox-inline input:checked~.helper::before{height: 11px;width: 2px;left: 5px;top: 11px;}
.checkbox.checkbox-inline input:checked~.helper::after{height: 6px;top: 7px;left: 0px;}
.checkbox.checkbox-inline .helper{border-radius:4px;color: #337ab7;}

</style>
<script type="text/javascript">
	var items = new Array();
	var table;
	$(document).ready(function() {
		$('#cpItemNav').addClass('active');
		$('#nav-client').addClass('active');
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
                <div class="row gstr-info-tabs">
                    <!-- left side begin -->
                    <%@include file="/WEB-INF/views/profile/leftnav.jsp" %>
                    <!-- left side end -->

                    <!-- dashboard cp  begin -->
                    <div class="col-md-10 col-sm-12 customtable dataTable_Item p-0">
                    
                    <a href="${contextPath}/static/mastergst/template/item_template.xls" class="pull-right mt-1 ml-2 vt-align"><img src="${contextPath}/static/mastergst/images/master/excel-icon.png" class="vt-align"/> Download Template</a>
                    <a href="#" class="btn btn-blue-dark permissionGeneral-Import_Templates"  data-toggle="modal" data-target="#itemimportModal" onclick="removemsg1()">Import</a>
                    <a href="#" class="btn btn-blue-dark permissionSettings-Items-Add" onclick="showItemsPopup()">Add</a>
                    <a href="#" class="btn btn-blue-dark permissionSettings-Items-Add" data-toggle="modal" data-target="#stockAdjustModal" onclick="stockAdjustModal()">Stock Adjust</a>
                    <a href="${contextPath}/dwnldItemXls/${id}/${client.id}/${month}/${year}" class="btn btn-blue excel">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
                    <a href="#" id="itemDelete" class="btn btn-blue pull-right vt-align disabled" onclick="showItemsDeleteAllPopUp()">Delete</a><h4>Items (Products/Services)</h4>
                    
                    <table id="dbTable_item" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
                                <th class="text-center"><div class="checkbox"><label><input type="checkbox" id='itemCheck' onClick="updateItemsMainSelection(this)"/><i class="helper"></i></label></div> </th>
									<th class="text-center"> Item Code</th>
									<th class="text-center">Item Name</th>
									<th class="text-center">HSN/SAC	Code</th>
									<!--<th class="text-center">UQC</th>
									 <th class="text-center">Opening Stock</th> -->
									<th class="text-center">Current Stock</th>
									<th class="text-center">Stock Value</th>
									<th class="text-center">Selling Price</th>		
									<th class="text-center">Purchase Price</th>
									<!-- <th class="text-center">Exempted</th> -->
									<th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody id="itemsBody">
								
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
    <div class="modal fade" id="itemimportModal" role="dialog" aria-labelledby="importModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3>Import Items</h3>

                    </div>
                     <!-- row begin -->
					<div class="p-4">
					<form:form method="POST" class="meterialform" id="itemsimportform" action="${contextPath}/uploadFile/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" enctype="multipart/form-data" style="border:none!important;">
					<div class="row">


         <fieldset style="width:  100%;">
		 <span class="errormsg" style="display:none!important;font-size:14px;"> please select a file</span>
                  <div class="filedragwrap" onClick="chooseitemfileSheets()">
              <div id="filedragi" style="display: block;">
                <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000">
                <div class="filedraginput"> <span class="choosefile importchoosefile" style="left:0%!important;">Choose File</span>
              <input type="file" name="file" id="itemFile" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,.csv"  style="opacity:0!important;display:none"> 
                </div>
                <div class="ortxt"> --( OR )--</div>
                <div id="filedragi">Drop file here</div>
              </div>
            </div>
            </fieldset>
            <div class="form-group col-md-12 col-sm-12" id="idSheet5" style="display:none;">
								<p class="lable-txt">File Name  :  <span id="messages5" style="opacity:1"></span></p>
								<div class="">&nbsp;</div>
								</div>
						<div class="form-group col-4">
							<input type="hidden" name="category" value="<%=MasterGSTConstants.ITEMS%>">
							
							<input type="submit"  id="submitbutton3" class="btn btn-primary" value="submit" style="background-color: #314999; color:white;width:61%!important;font-size: 18px;text-transform: uppercase;margin-left: 30px;"/>
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
        <div class="modal-dialog modal-right" role="document" style="width:1000px;max-width:1000px">
            <div class="modal-content">
            <div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="closeitem()">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Item Details</h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform" name="userform" id="itemform" action="${contextPath}/cp_createitem/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="item">
                    <div class="row pt-4 pl-4 pr-0">
                    <p class="lable-txt pl-3 pr-5 pt-2">You want to add : </p>
                    <div class="form-group col-md-12 col-sm-12" style="display:contents;">
                            <div class="lable-txt"></div>
                            <div class="form-group-inline">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="itemType" id="product_type" type="radio" value="Product" checked/>
                                            <i class="helper"></i>Product</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="itemType" id="service_type" type="radio" value="Service"/>
                                            <i class="helper"></i>Service</label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    	<div class="col-md-10 pr-0 row">
	                        <div class="form-group col-md-6 col-sm-12">
									<span class="errormsg" id="itemNumber_Msg"></span>
									<div class="row p-0">
									<p class="lable-txt astrich col-md-5 pl-3 itemCode_txt">Item Code</p>
									<div class="col-md-7 pl-0">
									<input type="text" class="itemNumber" id="itemNumber" name="itemno" required="required" data-error="Please enter more than 3 characters" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" placeholder="Code" />
									<label for="input" class="control-label"></label>
									<div class="help-block with-errors"></div>
									<i class="bar"></i> 
									</div>
									</div>
							</div>
							<div class="form-group col-md-6 col-sm-12">
									  <span class="errormsg" id="productDescription_Msg"></span>
								<div class="row p-0">
									  <p class="lable-txt astrich col-md-5 pl-3 itemName_txt">Item Name</p>
									 <div class="col-md-7 pl-0">
									  <input type="text" class="itemName" id="itemName" name="description" required="required" data-error="Please enter the Item name" placeholder="Name" />
									  <label for="input" class="control-label"></label>
									  <div class="help-block with-errors"></div>
									  <i class="bar"></i> 
									  </div>
							  </div>
							  </div>
						  <!-- Item Group Name Start -->
								<div class="form-group col-md-6 col-sm-12 grpCodeDiv">
								<span class="errormsg" id="itemGroupNumber_Msg"></span>
										<div class="row p-0">
										<p class="lable-txt col-md-5 pl-3 item_grpCode_txt">Item Group Code</p>
										<div class="col-md-7 pl-0">
										<input type="text" class="itemGroupNumber" id="itemGroupNumber" name="itemgroupno" data-error="Please enter more than 3 characters" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" placeholder="Group Code" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> 
										</div>
										</div>
								</div>
								<div class="form-group col-md-6 col-sm-12 grpNameDiv">
								 <span class="errormsg" id="productDescription_Msg"></span>
										  <div class="row p-0">
										<p class="lable-txt col-md-5 pl-3 item_grpName_txt">Item Group Name</p>
										 <div class="col-md-7 pl-0">
										 <input type="text" class="itemGroupDescription" id="itemGroupDescription" name="groupdescription" data-error="Please enter the Item Group name" placeholder="Group Name" />
										 <label for="input" class="control-label"></label>
										  <div class="help-block with-errors"></div>
										  <i class="bar"></i> 
										  </div>
										  </div>
								</div>
						  <!-- Item Group Name End -->
						  		<div class="form-group col-md-12 col-sm-12 descDiv">
						  		<span class="errormsg" id="productDescription_Msg"></span>
										  <div class="row p-0">
										<p class="lable-txt pl-3 item_description_txt">Item Description</p>
										<div class="col-md-10 descInput pr-0" style="padding-left:52px;">
										 <input type="text" class="itemDescription" id="itemDescription" name="itemDescription" data-error="Please enter the Item Description" placeholder="Item Description" />
										 <label for="input" class="control-label"></label>
										  <div class="help-block with-errors"></div>
										  <i class="bar"></i> 
										 </div>
										 </div>
								</div>
								<div class="form-group col-md-6 col-sm-12 uqcDiv" id="profileLevelItemUqc">
								
										<span class="errormsg" id="unitofMeasurement_Msg" style="margin-top:-30px!important"></span>
										<div class="row p-0">
										<p class="lable-txt col-md-5 astrich pl-3">Unit of Measurement</p>
										<div class="col-md-7 pl-0">
										<input type="text" class="unitofMeasurement" id="unitofMeasurement" name="unit" required="required" data-error="Please enter unit of measurement" placeholder="UQC" onKeyPress="return ((event.charCode >=65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122))" name="unit" style="text-transform:uppercase;" />
										<label for="unitofMeasurement" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<div id="unitofMeasurementempty" style="display:none">
											<div class="ddbox">
											  <p>Search didn't return any results.</p>
											</div>
										</div>
										<i class="bar"></i> 
										</div>
										</div>
								</div>
								<div class="form-group col-md-6 col-sm-12 serviceDiv"></div>
								<div class="form-group col-md-6 col-sm-12 openingStockDiv">
									<span class="errormsg" id="openingStockLevel_Msg"></span>
										<div class="row p-0">
										<p class="lable-txt col-md-5 pl-3">Opening Stock</p>
										<div class="col-md-7 pl-0">
										<input type="text" class="openingStockLevel" id="openingStockLevel" name="openingStock" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="Opening Stock"  style="height:38px" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> 
										</div></div>
								</div>
								<div class="form-group col-md-6 col-sm-12 asOnDateStockDiv">
								<span class="errormsg" id="openingStockLevel_Msg"></span>
										<div class="row p-0">
										<p class="lable-txt col-md-5 pl-3">AS on Date</p>
										<div class="col-md-7 pl-0">
										<input type="text" class="asOndate" id="asOndate" name="asOnDate"  placeholder="DD/MM/YYYY"  style="height:38px" />
										<span style="position: absolute;top: 25%;right: 12%;"><i class="fa fa-calendar asOndatewrap"></i></span>
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> 
										</div></div>
								</div>
								<div class="form-group col-md-6 col-sm-12 safteyStockLevelDiv">
									<span class="errormsg" id="saftlyStockLevel_Msg"></span>
										<div class="row p-0">
										<p class="lable-txt col-md-5 pl-3">Safety Stock Level</p>
										<div class="col-md-7 pl-0">
										<input type="text" class="saftlyStockLevel" id="saftlyStockLevel" name="stocklevel" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="Stock Level"  style="height:38px" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> 
										</div></div>
								</div>
								<div class="form-group col-md-6 col-sm-12 mt-1 mb-1 reOrderDiv">
								    <div class="row p-0">
										<p class="lable-txt col-md-5 pl-3">Re-order Required </p>
										<div class="col-md-7 pl-0">
		                            <div class="form-group-inline">
		                                <div class="form-radio">
		                                    <div class="radio">
		                                        <label>
		                                            <input name="reOrderType" id="reorderYes" type="radio" value="Yes" checked/>
		                                            <i class="helper"></i>YES</label>
		                                    </div>
		                                </div>
		                                <div class="form-radio">
		                                    <div class="radio">
		                                        <label>
		                                            <input name="reOrderType" id="reorderNo" type="radio" value="No"/>
		                                            <i class="helper"></i>NO</label>
		                                    </div>
		                                </div>
		                            </div>
		                            </div>
		                            </div>
		                        </div>
		                       <div class="form-group col-md-6 col-sm-12 hsnSacDiv">
								<span class="errormsg" id="HSNSACCode_Msg"></span>
										<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3 astrich hsnSacTxt">HSN/SAC Code</p>
										
										<div class="col-md-7 pl-0">
										<input class="HSNCode" id="HSNCode" required="required" data-error="Please enter the HSN/SAC code" placeholder="HSN/SAC" name="code" />
										<label for="HSNCode" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<div id="HSNCodeempty" style="display:none">
											<div class="ddbox">
											  <p>Search didn't return any results.</p>
											</div>
										</div>
										<i class="bar"></i> 
										</div>
										</div>
								</div>
								<div class="form-group col-md-6 col-sm-12 gstDiv">
										<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">GST<span class="label-txt" style="font-size:12px;"> (Tax rate in %)</span></p>
										<div class="col-md-7 pl-0">
										<select id="taxrate_text" class="form-control taxrate" name="taxrate" style="height: 30.3px;">
											 <option value="" selected>--Select--</option>
											 <option value="0">0</option>
											 <option value="0.1">0.1</option>
											 <option value="0.25">0.25</option>
											 <option value="1">1</option>
											 <option value="1.5">1.5</option>
											 <option value="3">3</option>
											 <option value="5">5</option>
											 <option value="7.5">7.5</option>
											 <option value="12">12</option>
											 <option value="18">18</option>
											 <option value="28">28</option>
										 </select>
										<i class="bar"></i>
										</div></div>
								</div>
								<div class="form-group col-md-6 col-sm-12 itemSaleDiv">
								<span class="errormsg" id="recommendedSellingPriceForB2B_Msg"></span>
											<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3 itemSalesPriceTxt">Item Sale Price</p>
											<div class="col-md-7 pl-0">
											 <!-- <p class="rupee_sign" style="padding:2px;">&#x20B9;</p>  -->
											 <input type="text" class="salePrice rupee" id="salePrice" name="salePrice" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
											<label for="input" class="control-label"></label>
											<div class="help-block with-errors"></div>
											<i class="bar"></i> 
											</div>
											</div>
								</div>
								
								<div class="form-group col-md-6 col-sm-12 ItemPurchasePriceDiv">
									<span class="errormsg" id="recommendedSellingPriceForB2B_Msg"></span>
											<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">Item Purchase Price</p>
											<div class="col-md-7 row pl-0 pr-0">
											<div class="col-md-8 pr-0">
											<input type="text" class="purchasePrice rupee" id="purchasePrice" name="purchasePrice" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
											<label for="input" class="control-label"></label>
											<div class="help-block with-errors"></div>
											<i class="bar"></i> 
											</div>
											<select class="col-md-4 bar" style="font-size: 14px;margin-top: 5px;" id="purchaseGstType" name="purchaseGstType">
												<option value="Without GST">Without GST</option>
												<option value="With GST">With GST</option>
											</select>
											</div></div>
								</div>
								<div class="form-group col-md-6 col-sm-12 wholeSalePriceDiv">
								<span class="errormsg" id="wholesalePriceForB2B_Msg"></span>
										<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">Whole Sale Price</p>
											<div class="col-md-7 pl-0">
										
										<input type="text" class="wholeSalePrice rupee" id="wholeSalePrice" name="wholeSalePrice" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> </div></div>
								</div>
								<div class="form-group col-md-6 col-sm-12 mrpPriceDiv">
								<span class="errormsg" id="mrpPriceForB2B_Msg"></span>
										<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">MRP Price</p>
										<div class="col-md-7 pl-0">
										<input type="text" class="mrpPrice rupee" id="mrpPrice" name="mrpPrice" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> </div></div>
								</div>
							<div class="form-group col-md-6 col-sm-12 isDiscount">
								<div class="row col-md-12 m-0 pr-0 pl-0">
										<div class="meterialform col-md-5 mt-3 pr-0 pl-0" style="float:left;">
				                      <span class="lable-txt pl-0">Enable Discount</span>
			                    </div>
			                    <div class="form-group col-md-7 col-sm-12 mt-1 mb-1 p-0">
			                    <div class="form-check" style="margin-top: 10px;">
					                <input class="form-check-input addDiscount" id="isDiscount" style="margin-top:-2px;width:100px;" type="checkbox" name="isDiscount" value="false">
					                <label for="enable_disc" class="enable_disc" style="padding-left: 44px!important;"><span class="ui"></span></label>
					                </div>
					                <div class="discountval" style="display:none;margin-left: 51px;">
			                            <p class="lable-txt">Discount in</p>
			                           <select class="col-md-5 bar" name="discountType" style="font-size: 14px;min-width: 80px;margin-left: 9px;width: 80px; margin-top: -11px;">
												<option value="Percentage">%</option>
												<option value="Number">Number</option>
									</select>
                            </div>
                            
                        </div>
			                    <div class="form-group col-md-12 col-sm-12 p-0">
								<div class="discountval row col-md-12 pr-0 mr-0" style="display:none;">
								<span class="errormsg" id="discount_Msg"></span>
										<p class="lable-txt col-md-5 pt-3 pl-0">Discount Value</p>
										<div class="col-md-7 pl-2 pr-0">
										<input type="text" class="discountvalue" id="discountvalue" name="discount" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> 
										</div>
										</div>
								</div>
								</div>
								</div>
								<div class="form-group col-md-6 col-sm-12 isexempted">
								<div class="row col-md-12 m-0 pr-0 pl-0">
										<div class="meterialform col-md-5 mt-3 pr-0 pl-0" style="float:left;">
				                      <span class="lable-txt pl-0">Enable Exempted</span>
			                    </div>
			                    <div class="form-group col-md-7 col-sm-12 mt-1 mb-1 p-0">
			                    <div class="form-check" style="margin-top: 10px;">
					                <input class="form-check-input addExempted" id="isExempted" style="margin-top:-2px;width:100px;" type="checkbox" name="isExempted" value="false">
					                <label for="enable_exempt" class="enable_exempt" style="padding-left: 44px!important;"><span class="ui"></span></label>
					                </div>
					                <div class="exemptedval" style="display:none;margin-left: 51px;">
			                            <p class="lable-txt">Exempted in</p>
			                           <select class="col-md-5 bar" name="exemptedType" style="font-size: 14px;min-width: 80px;margin-left: 9px;width: 80px; margin-top: -11px;">
												<option value="Percentage">%</option>
												<option value="Number">Number</option>
									</select>
                            </div>
                            
                        </div>
			                    <div class="form-group col-md-12 col-sm-12 p-0">
								<div class="exemptedval row col-md-12 pr-0 mr-0" style="display:none;">
								<span class="errormsg" id="exmepted_Msg"></span>
										<p class="lable-txt col-md-5 pt-3 pl-0">Exempted Value</p>
										<div class="col-md-7 pl-2 pr-0">
										<input type="text" class="exmeptedvalue" id="exmeptedvalue" name="exmepted" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> 
										</div>
										</div>
								</div>
								</div>
								</div>
						   <div class="col-md-12">
						   <div class="row item_customFields">
						   </div>
	                    </div>
                        <div class=" col-12 mt-4 text-center" style="display:block">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">
							<input type="hidden" name="createdDate" value="">
							<input type="hidden" name="createdBy" value="">
							<input type="submit" class="btn btn-blue-dark submit-item-form hidden ml-3 mt-3" value="Save" style="padding: 4px 5px;">
						</div>
                    </div>
                    
                    <div class="col-md-2 p-0 imagesDiv d-none">
                    	<!-- <div class="image-wrap mb-4 mr-3" style="border: 2px dashed #4664be;border-radius: 5px;text-align: center;color: #4664be;float:right"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="position: absolute;margin-left: 38%;margin-top: 6px;"><input type="file" class="fileupload1" style="opacity: 0;position: absolute;height: 70px;margin-left: -36px;"><img class="upimage1" src="${contextPath}/static/mastergst/images/master/add-image.png" style="width: 80px;height: 80px;padding: 19px;"></div>
                    	<div class="image-wrap mb-4 mr-3" style="border: 2px dashed #4664be;border-radius: 5px;text-align: center;color: #4664be;float:right"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="position: absolute;margin-left: 38%;margin-top: 6px;"><input type="file" class="fileupload1" style="opacity: 0;position: absolute;height: 70px;margin-left: -36px;"><img class="upimage1" src="${contextPath}/static/mastergst/images/master/add-image.png" style="width: 80px;height: 80px;padding: 19px;"></div>
                    	<div class="image-wrap mb-4 mr-3" style="border: 2px dashed #4664be;border-radius: 5px;text-align: center;color: #4664be;float:right"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="position: absolute;margin-left: 38%;margin-top: 6px;"><input type="file" class="fileupload1" style="opacity: 0;position: absolute;height: 70px;margin-left: -36px;"><img class="upimage1" src="${contextPath}/static/mastergst/images/master/add-image.png" style="width: 80px;height: 80px;padding: 19px;"></div>
                    	<div class="image-wrap mb-4 mr-3" style="border: 2px dashed #4664be;border-radius: 5px;text-align: center;color: #4664be;float:right"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="position: absolute;margin-left: 38%;margin-top: 6px;"><input type="file" class="fileupload1" style="opacity: 0;position: absolute;height: 70px;margin-left: -36px;"><img class="upimage1" src="${contextPath}/static/mastergst/images/master/add-image.png" style="width: 80px;height: 80px;padding: 19px;"></div>
                    	<div class="image-wrap mb-4 mr-3" style="border: 2px dashed #4664be;border-radius: 5px;text-align: center;color: #4664be;float:right"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="position: absolute;margin-left: 38%;margin-top: 6px;"><input type="file" class="fileupload1" style="opacity: 0;position: absolute;height: 70px;margin-left: -36px;"><img class="upimage1" src="${contextPath}/static/mastergst/images/master/add-image.png" style="width: 80px;height: 80px;padding: 19px;"></div> -->
                    </div>
                    
                    </div>
                    <!--  <div class="row pl-4 pr-0">
	                    <div class="col-md-12 col-sm-12">
	                    	<p class="lable-txt">Item Notes</p>
	                    	<textarea rows="3" cols="23" class="" id="itemComments" name="itemComments" placeholder="Item Description" style="width:90%;border: 1px solid lightgray;"></textarea>
	                    </div>
                    </div> -->
					</form:form>
                    <!-- row end -->

                </div>
				<div class="modal-footer text-right" style="display:block;border-top: 1px solid lightgray;">
				<span style="float:left;cursor:pointer;" class="custFieldsLink" onclick="custFieldLink()"><i class="add-btn">+</i><a href="#" class="ml-2">Custom Field</a></span>
					<label for="submit-item-form" class="btn btn-blue-dark m-0" tabindex="0" onclick="labelitemsubmit()">Save</label>
					<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close" onclick="closeitem()">Cancel</a>
				</div>
            </div>
        </div>
    </div>
    <div class="modal fade" id="stockAdjustModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-right" role="document" style="width:750px;max-width:750px">
            <div class="modal-content">
             <div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="closecustomer()">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Stock Adjust</h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform stockForm" name="userform" id="itemform" action="${contextPath}/cp_createStock/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" modelAttribute="stockItem">
                    <div class="row pt-4 pl-4 pr-0">
                    	<div class="col-md-12 row">
	                        <div class="form-group col-md-6 col-sm-12">
									<span class="errormsg" id="itemNumber_Msg"></span>
									<div class="row p-0">
									<p class="lable-txt astrich col-md-5 pl-3">Item Code</p>
									<div class="col-md-7 pl-0">
									<input type="text" class="stockItemno" id="stockItemno" name="stockItemNo" required="required" data-error="Please enter more than 3 characters" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" placeholder="Code" />
									<div class='remainstockItemno_textempty dbbox' style="display:none;">
				                  		<p>Please add new item</p>
				                  		<input type='button' class='btn btn-sm btn-blue-dark' value='Add New Item' data-toggle='modal' onclick=''>
				                  	</div>
									
									<label for="input" class="control-label" ></label>
									<div class="help-block with-errors"></div>
									<i class="bar"></i> 
									</div>
									</div>
									<p style="font-size:12px;">(Please Enter Product Item Code Only)</p>
							</div>
							<div class="col-md-6 p-0 pl-3 mt-2">
							<div class="form-group col-md-12 col-sm-12">
								<div class="row p-0">
									  <p class="lable-txt astrich col-md-4 p-0 pt-1">Item Name</p>
									  <span class="col-md-1 pt-1">:</span>
									 <div class="col-md-6 pl-0">
									  <label><input type="text"class="control-label stockItemName" name="stockItemName" style="border:none;color:black" readonly/></label>
									  </div>
							  </div>
							  </div>
						  <!-- Item Group Name Start -->
								<div class="form-group col-md-12 col-sm-12">
										<div class="row p-0">
										<p class="lable-txt col-md-4 p-0 pt-1">Item Group Code</p>
										<span class="col-md-1 pt-1">:</span>
										<div class="col-md-6 pl-0">
										<!-- <label for="input" class="control-label stockItemGrpCode" style="color:black" name="stockItemGrpCode"></label> -->
										<label><input type="text"class="control-label stockItemGrpCode" name="stockItemGrpCode" style="border:none;color:black" readonly/></label>
										</div>
										</div>
								</div>
								<div class="form-group col-md-12 col-sm-12">
								 		  <div class="row p-0">
										<p class="lable-txt col-md-4 p-0 pt-1">Item Group Name</p>
										<span class="col-md-1 pt-1">:</span>
										 <div class="col-md-6 pl-0">
										 <!-- <label for="input" class="control-label stockItemGrpName" style="color:black" name="stockItemGrpName"></label> -->
										 <label><input type="text"class="control-label stockItemGrpName" name="stockItemGrpName" style="border:none;color:black" readonly/></label>
										  </div>
										  </div>
								</div>
						  <!-- Item Group Name End -->
						  		<div class="form-group col-md-12 col-sm-12">
									<span class="errormsg" id="openingStockLevel_Msg"></span>
										<div class="row p-0">
										<p class="lable-txt col-md-4 p-0 pt-1">Current Stock </p>
										<span class="col-md-1 pt-1">:</span>
										<div class="col-md-6 pl-0">
										<!-- <label for="input" class="control-label currentStock" style="color:black"></label> -->
										<label><input type="text"class="control-label currentStock" name="currentStock" style="border:none;color:black" readonly/></label>
										</div></div>
								</div>
								</div>
								<div class="form-group col-md-6 col-sm-12">
									<span class="errormsg" id="saftlyStockLevel_Msg"></span>
										<div class="row p-0">
										<p class="lable-txt col-md-5 pl-3">Stock Movement</p>
										<div class="col-md-7 pl-0">
										<div class="form-group-inline" style="padding-top: 2px;">
		                                <div class="form-radio">
		                                    <div class="radio">
		                                        <label>
		                                            <input name="stockMovement" id="add_sstock" type="radio" value="In" checked/>
		                                            <i class="helper"></i>In/Add <i class="add-btn">+</i></label>
		                                    </div>
		                                </div>
		                                <div class="form-radio">
		                                    <div class="radio">
		                                        <label>
		                                            <input name="stockMovement" id="reduce_stock" type="radio" value="Out"/>
		                                            <i class="helper"></i>Out/Reduce <i class="add-btn reduce_btn" style="background-color:red;">-</i></label>
		                                    </div>
		                                </div>
		                            </div>
										</div></div>
								</div>
								
								<div class="form-group col-md-6 col-sm-12">
								<span class="errormsg" id="stockItemQty_Msg"></span>
											<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">Item Quantity</p>
											<div class="col-md-7 pl-0">
											<input type="text" class="stockItemQty" id="stockItemQty" name="stockItemQty" onkeyup="adjustItemQuantity()" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
											<label for="input" class="control-label"></label>
											<div class="help-block with-errors"></div>
											<i class="bar"></i> 
											</div>
											</div>
								</div>
								<div class="form-group col-md-6 col-sm-12">
								<span class="errormsg" id="openingStockLevel_Msg"></span>
										<div class="row p-0">
										<p class="lable-txt col-md-5 pl-3">Date of Moment </p>
										<div class="col-md-7 pl-0">
										<input type="text" class="dateOfMovement" id="dateOfMovement" name="dateOfMovement"  placeholder="DD/MM/YYYY"  style="height:38px" />
										<span style="position: absolute;top: 25%;right: 12%;"><i class="fa fa-calendar dateOfMovementwrap"></i></span>
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> 
										</div></div>
								</div>
								<div class="form-group col-md-6 col-sm-12">
								<span class="errormsg" id="wholesalePriceForB2B_Msg"></span>
										<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">PO Number</p>
											<div class="col-md-7 pl-0">
										<input type="text" class="stockPoNo" id="stockPoNo" name="stockPoNo" placeholder="190" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> </div></div>
								</div>
								<div class="form-group col-md-6 col-sm-12">
									<span class="errormsg" id="recommendedSellingPriceForB2B_Msg"></span>
											<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">Item Purchase cost</p>
											<div class="col-md-7 pl-0">
											<input type="text" class="stockPurchaseCost" id="stockPurchaseCost" name="stockPurchaseCost" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
											<label for="input" class="control-label"></label>
											<div class="help-block with-errors"></div>
											<i class="bar"></i> 
											</div></div>
								</div>
								
								
								<div class="form-group col-md-6 col-sm-12">
								<span class="errormsg" id="mrpPriceForB2B_Msg"></span>
										<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">Vendor Name</p>
										<div class="col-md-7 pl-0">
										<input type="text" class="stockVendor" id="stockVendor" name="stockVendorName" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> </div></div>
								</div>
								<div class="form-group col-md-12 col-sm-12 mt-4">
								<span class="errormsg" id="mrpPriceForB2B_Msg"></span>
										<p class="lable-txt pb-2">Remarks / Comments</p>
										<textarea rows="4" cols="4" style="border: 1px solid lightgray;border-radius: 4px;resize: auto;" name="stockComments"></textarea>
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div> 
								</div>
						<div class=" col-12 mt-4 text-center" style="display:block">
							<input type="hidden" name="stockUnit" id="stockUnit" value="">
							<input type="hidden" id="stockOpeningStock" value="">
							<input type="hidden" id="companyitemid" value="">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">
							<input type="hidden" name="createdDate" value="">
							<input type="hidden" name="createdBy" value="">
							<!-- <input type="submit" class="btn btn-blue-dark submit-stock-form hidden ml-3 mt-3" value="Save" style="padding: 4px 5px;"> -->
						</div>
                    </div>
                    </div>
					</form:form>
                    <!-- row end -->

                </div>
				<div class="modal-footer text-right" style="display:block">
					<label for="submit-item-form" class="btn btn-blue-dark m-0" tabindex="0" onclick="labelStockAdjustSubmit()">Save</label>
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
          <h3>Delete Item </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Item <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Item</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<script>
var itemArray = new Array();var customFieldsData=null;
loadItemsTable('${id}', '${client.id}', '${month}', '${year}', '${usertype}', '${fullname}');
$(".fileupload1").change(function(e) {
	for (var i = 0; i < e.originalEvent.srcElement.files.length; i++) {
        var file = e.originalEvent.srcElement.files[i];
        var img = document.createElement("img");
        var reader = new FileReader();
        reader.onloadend = function() {
             img.src = reader.result;
        }
        reader.readAsDataURL(file);
        $("input.fileupload1").after(img);
        $(".upimage1").hide();
    }
});
function closeitem(){
	
	$('.itemCode_txt').text("Item Code");
	$('.itemName_txt').text("Item Name");
	$('.item_grpCode_txt').text("Item Group Code");
	$('.item_grpName_txt').text("Item Group Name");
	$('.item_description_txt').text("Item Description");
	$('.hsnSacTxt').text("HSN Code");
	$('.itemDescription').attr("placeholder","Item Description");
	$('.itemSalesPriceTxt').text('Item Sale Price');
	$('.item_description_txt').css("margin-left","2px");
	$('.descInput').css({"padding-left":"52px"});
	$('.descInput').addClass("pr-0");
	$('.openingStockDiv,.asOnDateStockDiv,.safteyStockLevelDiv,.reOrderDiv,.ItemPurchasePriceDiv,.wholeSalePriceDiv,.mrpPriceDiv,.serviceDiv,.imagesDiv').removeClass("d-none");
	$('.imagesDiv').hide();
	$('.asOndate,.taxrate,.HSNCode,.saftlyStockLevel,.Discount,.itemCategory,.recommendedSellingPriceForB2B,.itemDescription,.itemNumber,.itemName,.itemGroupNumber,.itemGroupDescription').val("");
	$('#saleGstType,#mrpPrice,#wholeSalePrice,#purchaseGstType,#purchasePrice,#salePrice').val("");
	$('.unitofMeasurement').val("");
		$(".discountval").hide();
		$('.discountvalue').val("");
		$('.addDiscount').prop("checked",false);
		$(".exemptedval").hide();
		$('.addExempted').prop("checked",false);
		$('.exmeptedvalue').val("");
	$('#recommendedRetailPriceForB2B').val("");
	$('#wholesalePriceForB2B').val("");
	$('#mrpPriceForB2B').val("");
	$('#openingStockLevel').val("");
	$('#closingStockLevel').val("");
	$('#itemComments').val("");
		$('#numType').prop("checked",true);
		$('#reorderNo').prop("checked",true);
		$('#product_type').prop("checked",true);
	
	var exp_val = $('.exmeptedvalue').val();
	if(exp_val == ''){
		$('.addExempted').prop('checked',false);
		$(".exemptedval").hide();
	}else{
		$('.addExempted').prop('checked',true);
		$(".exemptedval").show();
	}
	var dis_val = $('.discountvalue').val();
	if(dis_val == ''){
		$('.addDiscount').prop('checked',false);
		$(".discountval").hide();
	}else{
		$('.addDiscount').prop('checked',true);
		$(".discountval").show();
	}
	//$('input[type="hidden"][id="itemid"]','input[type="hidden"][id="itemcurrentstock"]','input[type="hidden"][id="itemtotalqtyusage"]').remove();
	$('input[type="hidden"][id="itemid"],input[type="hidden"][id="itemcurrentstock"],input[type="hidden"][id="itemtotalqtyusage"]').remove();
	$('form[name="userform"]').validator('update');
}
</script>
<script src="${contextPath}/static/mastergst/js/common/filedrag-map5.js" type="text/javascript"></script>
</body>

</html>