<!-- Add Item Modal Start -->
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
    <div class="modal fade modal-right" id="addItemModal" role="dialog" aria-labelledby="addItemModal" aria-hidden="true" style="overflow-y:hidden;height:100vh;">
        <div class="modal-dialog modal-md" role="document" style="width:1000px;max-width:1000px">
            <div class="modal-content" style="height:100vh;">
             <div class="modal-header p-0">
				<button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('addItemModal')">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3>Item Details</h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks" style="max-height:unset;height:unset;overflow-y:auto;">                    
					<form method="POST" data-toggle="validator" id="itemForm" class="meterialform" name="userform" action="${contextPath}/cp_createadditem/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="item" style="height: 87%;overflow-y: auto;overflow-x: hidden;">
                    <div class="row pt-2 pl-4 pr-0">
	                     <p class="lable-txt pl-3 pr-5 pt-2">You want to add : </p>
	                    <div class="form-group col-md-12 col-sm-12" style="display:contents;">
	                            <div class="lable-txt"></div>
	                            <div class="form-group-inline">
	                                <div class="form-radio">
	                                    <div class="radio">
	                                        <label>
	                                            <input name="itemType" id="itemproduct_type" type="radio" value="Product" checked/>
	                                            <i class="helper"></i>Product</label>
	                                    </div>
	                                </div>
	                                <div class="form-radio">
	                                    <div class="radio">
	                                        <label>
	                                            <input name="itemType" id="itemservice_type" type="radio" value="Service"/>
	                                            <i class="helper"></i>Service</label>
	                                    </div>
	                                </div>
	                            </div>
	                        </div>
	                        <div class="col-md-10 pr-0 row">
	                        <div class="form-group col-md-6 col-sm-12" id="itemCode">
								<span class="errormsg" id="itemNumber_Msg" style="margin-top:-33px"></span>
	                            <div class="row p-0">
									<p class="lable-txt astrich col-md-5 pl-3">Item Code</p>
									<div class="col-md-7 pl-0">
										<input type="text" id="itemNumber" class="itemNumber" name="itemno" required="required" placeholder="Code" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i>
									</div>
								</div>
							 </div>
							<div class="form-group col-md-6 col-sm-12" id="itemName">
								  <span class="errormsg" id="productDescription_Msg" style="margin-top:-33px"></span>
								  <div class="row p-0">
									  <p class="lable-txt astrich col-md-5 pl-3">Item Name</p>
									  <div class="col-md-7 pl-0">
										  <input type="text" id="itemDescription" name="description" class="itemName" required="required" placeholder="Name"  />
										  <label for="input" class="control-label"></label>
										    <div class="help-block with-errors"></div>
										  <i class="bar"></i> 
									  </div>
								  </div>
							  </div>
								<div class="form-group col-md-6 col-sm-12">
								    <div class="row p-0">
										<span class="errormsg" id="itemGroupNumber_Msg"></span>
										<p class="lable-txt col-md-5 pl-3">Item Group Code</p>
										 <div class="col-md-7 pl-0">
												<input type="text" class="itemGroupNumber" id="itemGroupNumber" name="itemgroupno" data-error="Please enter more than 3 characters" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" placeholder="Group Code" />
												<label for="input" class="control-label"></label>
												<div class="help-block with-errors"></div>
												<i class="bar"></i>
												</div>
									</div>
								</div>
								<div class="form-group col-md-6 col-sm-12">
									  <span class="errormsg" id="productDescription_Msg"></span>
									  <div class="row p-0">
										  <p class="lable-txt col-md-5 pl-3">Item Group Name</p>
										  <div class="col-md-7 pl-0">
											 <input type="text" class="itemGroupDescription" id="itemGroupDescription" name="groupdescription" data-error="Please enter the Item Group name" placeholder="Group Name" />
											 <label for="input" class="control-label"></label>
											  <div class="help-block with-errors"></div>
											  <i class="bar"></i>
										  </div>
									  </div>
								</div>
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
								<div class="form-group col-md-6 col-sm-12" id="itemUqc">
										<span class="errormsg" id="unitofMeasurement_Msg" style="margin-top:-33px"></span>
										<div class="row p-0">
											<p class="lable-txt astrich col-md-5 pl-3">Unit of Measurement</p>
											<div class="col-md-7 pl-0">
												<input type="text" class="unitofMeasurement" id="unit" name="unit" required="required" onKeyPress="return ((event.charCode >=65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122))"  style="text-transform: uppercase!important;" placeholder="UQC" />
												<label for="input" class="control-label"></label>
												<div id="unitempty" style="display:none">
													<div class="ddbox">
													  <p>Search didn't return any results.</p>
													</div>
												</div>
												  <div class="help-block with-errors"></div>
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
										<input type="text" class="openingStockLevel" id="itemopeningStockLevel" name="openingStock" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="Opening Stock"  style="height:38px" />
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
										<input type="text" class="asOndate" id="itemasOndate" name="asOnDate"  placeholder="DD/MM/YYYY"  style="height:38px" />
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
										<input type="text" class="saftlyStockLevel" id="itemsaftlyStockLevel" name="stocklevel" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="Stock Level"  style="height:38px" />
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
		                                            <input name="reOrderType" id="itemreorderYes" type="radio" value="Yes" checked/>
		                                            <i class="helper"></i>YES</label>
		                                    </div>
		                                </div>
		                                <div class="form-radio">
		                                    <div class="radio">
		                                        <label>
		                                            <input name="reOrderType" id="itemreorderNo" type="radio" value="No"/>
		                                            <i class="helper"></i>NO</label>
		                                    </div>
		                                </div>
		                            </div>
		                            </div>
		                            </div>
		                        </div>
								<div class="form-group col-md-6 col-sm-12" id="itemHsn">
									<span class="errormsg" id="HSNSACCode_Msg" style="margin-top:-33px"></span>
									  <div class="row p-0">
											<p class="lable-txt astrich col-md-5 pl-3">HSN/SAC Code</p>
											<div class="col-md-7 pl-0">
												<input id="ItemHSNCode" required="required" name="code" placeholder="HSN/SAC"/>
												<label for="ItemHSNCode" class="control-label"></label>
												<div id="ItemHSNCodeempty" style="display:none">
													<div class="ddbox">
													  <p>Search didn't return any results.</p>
													</div>
												</div>
												  <div class="help-block with-errors"></div>
												<i class="bar"></i>
										</div>
									</div>
								</div>
								<div class="form-group col-md-6 col-sm-12">
								       <div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">GST<span class="label-txt" style="font-size:12px;"> (Tax rate in %)</span></p>
											<div class="col-md-7 pl-0">
												<select id="taxrate_text" class="form-control" name="taxrate">
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
												<label for="input" class="control-label"></label>
												<i class="bar"></i>
												</div>
										</div> 
								</div>

                        <div class="form-group col-md-6 col-sm-12 itemSaleDiv">
								<span class="errormsg" id="recommendedSellingPriceForB2B_Msg"></span>
											<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3 itemSalesPriceTxt">Item Sale Price</p>
											<div class="col-md-7 pl-0">
											 <!-- <p class="rupee_sign" style="padding:2px;">&#x20B9;</p>  -->
											 <input type="text" class="salePrice rupee" id="itemsalePrice" name="salePrice" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
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
											<input type="text" class="purchasePrice rupee" id="itempurchasePrice" name="purchasePrice" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
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
										
										<input type="text" class="wholeSalePrice rupee" id="itemwholeSalePrice" name="wholeSalePrice" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> </div></div>
								</div>
								<div class="form-group col-md-6 col-sm-12 mrpPriceDiv">
								<span class="errormsg" id="mrpPriceForB2B_Msg"></span>
										<div class="row p-0">
											<p class="lable-txt col-md-5 pl-3">MRP Price</p>
										<div class="col-md-7 pl-0">
										<input type="text" class="mrpPrice rupee" id="itemmrpPrice" name="mrpPrice" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
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
					                <input class="form-check-input addDiscount" id="itemisDiscount" style="margin-top:-2px;width:100px;" type="checkbox" name="isDiscount" value="false">
					                <label for="enable_disc" class="enable_disc" style="padding-left: 44px!important;"><span class="ui"></span></label>
					                </div>
					                <div class="discount_val" style="display:none;margin-left: 51px;">
			                            <p class="lable-txt">Discount in</p>
			                           <select class="col-md-5 bar" name="discountType" style="font-size: 14px;min-width: 80px;margin-left: 9px;width: 80px; margin-top: -11px;">
												<option value="Percentage">%</option>
												<option value="Number">Number</option>
									</select>
                            </div>
                            
                        </div>
			                    <div class="form-group col-md-12 col-sm-12 p-0">
								<div class="discount_val row col-md-12 pr-0 mr-0" style="display:none;">
								<span class="errormsg" id="discount_Msg"></span>
										<p class="lable-txt col-md-5 pt-3 pl-0">Discount Value</p>
										<div class="col-md-7 pl-2 pr-0">
										<input type="text" class="discountvalue" id="Discount" name="discount" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
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
					                <input class="form-check-input addExempted" id="itemisExempted" style="margin-top:-2px;width:100px;" type="checkbox" name="isExempted" value="false">
					                <label for="enable_exempt" class="enable_exempt" style="padding-left: 44px!important;"><span class="ui"></span></label>
					                </div>
					                <div class="exempted_val" style="display:none;margin-left: 51px;">
			                            <p class="lable-txt">Exempted in</p>
			                           <select class="col-md-5 bar" name="exemptedType" style="font-size: 14px;min-width: 80px;margin-left: 9px;width: 80px; margin-top: -11px;">
												<option value="Percentage">%</option>
												<option value="Number">Number</option>
									</select>
                            </div>
                            
                        </div>
			                    <div class="form-group col-md-12 col-sm-12 p-0">
								<div class="exempted_val row col-md-12 pr-0 mr-0" style="display:none;">
								<span class="errormsg" id="exmepted_Msg"></span>
										<p class="lable-txt col-md-5 pt-3 pl-0">Exempted Value</p>
										<div class="col-md-7 pl-2 pr-0">
										<input type="text" class="exmeptedvalue" id="itemexmeptedvalue" name="exmepted" pattern="^[0-9,]+(\.[0-9]{1,2})?$" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 46)  || (event.charCode == 44))" placeholder="190" />
										<label for="input" class="control-label"></label>
										<div class="help-block with-errors"></div>
										<i class="bar"></i> 
										</div>
										</div>
								</div>
								</div>
								</div>
								<div class="col-md-12">
								<div class="row item_custom_FieldsaItem">
								</div>
								</div>
                        <div class="col-12 mt-4 text-center" style="display:block">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">
							<input type="hidden" name="rowno" class="rowno" value="">
							<input type="hidden" name="createdDate" value="">
							<input type="hidden" name="createdBy" value="">
							
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
					</form>
					 <div class="modal-footer text-right" style="display:block;border-top: 1px solid lightgray;">
					 <span style="float:left;cursor:pointer;" class="addItemCustLink" onclick="itemCustomFieldsLink()"><i class="add-btn">+</i><a href="#" class="ml-2">Custom Field</a></span>
					<input type="button" class="btn btn-blue-dark itmcustsave" id="invadditem" value="Save" onclick="saveItem()" aria-label="Close"/>
					<a href="#" class="btn btn-blue-dark ml-2" onclick="closeAddCustomer('addItemModal')" aria-label="Close">Cancel</a>
				</div> 
                </div>
            </div>
        </div>
    </div>
    <!-- Edit Modal End -->
    <script>
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
				},
				maxNumberOfElements: 43
			}
		};
		$("#unitofMeasurement").easyAutocomplete(uqcoptions);
		$('.addExempted').click(function(){
			if($(".addExempted").is(':checked')){
			  $(".exempted_val").show();
			  $('#itemisExempted').val('true');
		    }else{
		      $(".exempted_val").hide();
		      $('#itemisExempted').val('false');
		   }
		});
		$('.addDiscount').click(function(){
			if($(".addDiscount").is(':checked')){
			  $(".discount_val").show();
			  $('#itemisDiscount').val('true');
		    }else{
		      $(".discount_val").hide();
		      $('#itemisDiscount').val('false');
		   }
		});
    </script>