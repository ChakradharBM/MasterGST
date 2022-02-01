<%@include file="/WEB-INF/views/includes/taglib.jsp" %> 
 <div class="modal fade einv-buyer-disp-ship" id="ebuyeraddrModal" role="dialog" aria-labelledby="ebuyeraddrModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr addrHdr" style="width:100%">
                        <h3 style="color:white;">Add Buyer Details</h3>
                    </div>
				</div>
				<form:form method="POST" data-toggle="validator" class="buyerform meterialform" id="buyerform">
                <div class="modal-body meterialform bs-fancy-checks">
                    <!-- row begin -->
					<div class="row  pl-5 pr-5 pt-3">
						 <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Address 1</p>
                            <span class="errormsg" id="buyerAddress1_msg"></span>
                            <input type="text" id="buyerAddress1" value="" />
                            <label for="address1" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Address 2</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="buyerAddress2"  value="" />
                            <label for="address2" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Location</p>
                            <span class="errormsg" id="buyerloc_msg"></span>
							<input type="text" id="buyerloc" value="" required data-error="please enter location"/>
                            <label for="location" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
							<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Pincode</p>
                            <span class="errormsg" id="buyerPincode_msg"></span>
                            <input type="text" id="buyerPincode" required data-error="Please enter Pincode" value="" />
                            <label for="Pincode" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">State Code</p>
                            <span class="errormsg" id="buyerStatecode_msg"></span>
                            <input type="text" id="buyerStatecode" name="" required="required" data-error="Please enter State Code" value="" />
                            <label for="statecode" class="control-label"></label>
							
							<div id="buyerStatecodeempty" style="display:none">
		                  		<div class="ddbox"><p>Search didn't return any results.</p></div>
		                  </div>
                            <i class="bar"></i> 
						</div>
                    </div>
                    <!-- row end -->
                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="" class="btn btn-blue-dark"  onclick="buyerAddrDetails()" id="buyer_save">Save</label>
				<a href="#" class="btn btn-blue-dark" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
				 </form:form>
            </div>
        </div>
    </div>
	   	    <div class="modal fade einv-buyer-disp-ship" id="edispatchaddrModal" role="dialog" aria-labelledby="edispatchaddrModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr addrHdr" style="width:100%">
                        <h3 style="color:white;">Add Dispatcher Details</h3>
                    </div>
				</div>
				<form:form method="POST" data-toggle="validator" class="dispatchform meterialform" id="dispatchform">
                <div class="modal-body meterialform bs-fancy-checks">
                    <!-- row begin -->
					<div class="row  pl-5 pr-5 pt-3">
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Name</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="dispatchTradeName" value="" />
                            <label for="accountnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						 <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Address 1</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="dispatchAddress1" value="" />
                            <label for="address1" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Address 2</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="dispatchAddress2"  value="" />
                            <label for="address2" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Location</p>
                            <input type="text" id="dispatchloc" value="" required data-error="please enter location"/>
                            <label for="location" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
							<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Pincode</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="dispatchPincode" required="required"  data-error="Please enter Pincode" value="" />
                            <label for="Pincode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">State Code</p>
                            <span class="errormsg" id="dispatchState"></span>
                            <input type="text" id="dispatchStatecode" name="" required="required" data-error="Please enter State Code" value="" />
                            <label for="statecode" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="dispatchStatecodeempty" style="display:none">
		                  		<div class="ddbox"><p>Search didn't return any results.</p></div>
		                  </div>
                            <i class="bar"></i> 
						</div>
                    </div>
                    <!-- row end -->
                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="" class="btn btn-blue-dark"  onclick="dispatchAddrDetails()" id="dispatch_save">Save</label>
				<a href="#" class="btn btn-blue-dark" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
				 </form:form>
            </div>
        </div>
    </div>
    	    <div class="modal fade einv-buyer-disp-ship" id="eshipaddrModal" role="dialog" aria-labelledby="eshipaddrModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr addrHdr" style="width:100%">
                        <h3 style="color:white;">Add Shipment Details</h3>
                    </div>
				</div>
                <div class="modal-body meterialform bs-fancy-checks">
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform shipmentform" id="shipmentform">
					<div class="row  pl-5 pr-5 pt-3">
					 <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">GSTIN/Unique ID <a href="#" onClick="einvokegstnPublicAPI(this,'shipment')" class="btn btn-green btn-sm pull-right">Get GSTIN Details</a> </p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="shipmentGstno" required = "required" data-error="Please Enter Valid GSTIN" aria-describedby="dispatchGstno" onChange="eupdatePan(this.value,'shipment')" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" maxlength="15" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4Z7" />
                            <label for="dispatchGstno" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
					  <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Legal Name</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="shipmentName" name="" value="" />
                            <label for="Name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Trade Name</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="shipmentTradeName" required="required"  value="" />
                            <label for="accountnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						 <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Address 1</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="shipmentAddress1" value="" />
                            <label for="address1" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Address 2</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="shipmentAddress2"  value="" />
                            <label for="address2" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Location</p>
                            <input type="text" id="shipmentloc" value="" required data-error="please enter location"/>
                            <label for="location" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Pincode</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="shipmentPincode" required="required"  data-error="Please enter Pincode" value="" />
                            <label for="Pincode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">State Code</p>
                            <span class="errormsg" id=""></span>
                            <input type="text" id="shipmentStatecode" required="required" data-error="Please enter State Code" value="" />
                            <label for="statecode" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="shipmentStatecodeempty" style="display:none">
		                  		<div class="ddbox"><p>Search didn't return any results.</p></div>
		                  </div>
                            <i class="bar"></i> 
						</div>												
                    </div>
					</form:form>
                    <!-- row end -->
                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="" class="btn btn-blue-dark"  onclick="shipmentAddrDetails()" id="shipment_save">Save</label>
				<a href="#" class="btn btn-blue-dark" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
            </div>
        </div>
    </div>