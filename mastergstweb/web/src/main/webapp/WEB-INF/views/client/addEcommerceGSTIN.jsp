 <%@include file="/WEB-INF/views/includes/taglib.jsp" %>
 
  <div class="modal fade" id="addEcommerceGSTIN" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform popupright bs-fancy-checks">
                     <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('addEcommerceGSTIN')">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>E-Commerce Operator</h3>

                    </div>
                    <!-- row begin -->
					<form method="POST" data-toggle="validator" id="aecommerce_form" class="meterialform" name="ecommerceform" action="${contextPath}/cp_createeoperator/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="operator">
                    <div class="row  pl-5 pr-5 pt-5">

						
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="aeomsg">
                            <p class="lable-txt astrich" id="bName">Business Name</p>
                            <span class="errormsg" id="abusinessName_Msg"></span>
                            <input type="text" id="aebname" class="aebname" name="name" required="required" data-error="Enter the Business Name" placeholder="Name" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 category_business">
                            <p class="lable-txt astrich">E-Commerce Operator</p>
                            <span class="errormsg" id="aoperator_Msg"></span>
                            <input type="text" id="aoperator" name="operator" data-error="Enter the operator name" required="required"  placeholder="Jane Smith" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>


                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1 apan_number">
                            <p class="lable-txt">PAN Number</p>
                            <span class="errormsg" id="operatorPanNumber_Msg"></span>
                            <input type="text" id="aoperatorPanNumber" name="operatorPanNumber" data-error="Enter the valid pan number" placeholder="PAN Number" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0)) || (event.charCode == 32))" pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                       
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1 category_business">
                            <p class="lable-txt astrich">ECommerce GSTIN<a href="#" onClick="invokePublicecomAPI(this)" class="btn btn-green btn-sm pull-right">Get GSTIN Details</a> </p>
                            <span class="errormsg" id="aegstnnumber_Msg" style="margin-top: -31px;"></span>
                            <input type="text" id="aegstnnumber" name="gstnnumber" required="required"  aria-describedby="gstnnumber" onChange="updateecomPan(this.value)" pattern="^[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}$" data-error="Please enter Valid Ecommerce GSTIN.(Sample 07CQZCD1111I4C7)" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4C7" />
                            <label for="gstnnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12  mt-1 mb-1">
                            <p class="lable-txt">Mobile Number</p>
                            <span class="errormsg" id="emobilenumber_Msg"></span>
                            <input type="text" id="aemobilenumber" name="mobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <script>$("#emobilenumber").intlTelInput({"initialCountry":"in"});</script>
                            <label for="mobilenumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                             
                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1" id="countries_list">
                            <p class="lable-txt astrich">Country</p>
                            <span class="errormsg" id="aecountry_Msg" style="margin-top:-33px"></span>
                            <select class="form-controll country" required="required" id="aecountriesList" name="country" data-error="Please select country"></select>
                            <label for="country" class="control-label"></label>
                            <i class="bar"></i>
                          </div>
                        
						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
							<div class="lable-txt astrich">State</div>
							<div class="form-group">
								<span class="errormsg" id="aestate_Msg"></span>
								<input class="estate" id="aestate" required="required" readonly="readonly" name="state" data-error="Please enter the valid stae name" pattern="\d{2}[a-zA-Z-]+\s*[a-zA-z]*\s*[a-zA-z]**\s*[a-zA-z]*" placeholder="State" />
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
								<span class="errormsg" id="aeaddress_Msg"></span>
								<input type="text" id="aeaddresss" name="address" class="mapicon address" aria-describedby="address" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode >= 44 && event.charCode <= 47) || (event.charCode == 158) || (event.charCode == 32))" placeholder="Address" />
								<label for="address" class="control-label"></label>
								<i class="bar"></i> </div>
						</div>

						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Pin Code</p>
                            <span class="errormsg" id="apincode_Msg"></span>
                            <input type="text" class="aepincode" id="aeoperatorpincode" name="pincode" data-minlength="5" maxlength="10" pattern="[0-9]+" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value=""/>
                            <label for="pincode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">City</p>
                            <span class="errormsg" id="acity_Msg"></span>
                            <input type="text" class="aecity" id="aeopearatorcity" name="city" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" value="" />
                            <label for="city" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Email Id</p>
                            <span class="errormsg" id="aemailId_Msg"></span>
                            <input type="text" id="aeemail" class="aeemail" name="email" pattern='^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$' data-error="Please enter valid email address"  placeholder="janeSmith@gmail.com" value="" />
                            <label for="email" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-6 col-sm-12 mt-1 mb-1">
                            <p class="lable-txt">Landline No</p>
                            <span class="errormsg" id="landline_Msg"></span>
                            <input type="text" class="aelandline" id="aelandline" name="landline" data-minlength="10" maxlength="11" pattern="[0-9]+" data-error="Please enter valid landline number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="" />
                            <label for="landline" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                         
                           <div class=" col-12 mt-5 text-center" style="display:block">
                           <input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
                       		 <input type="button" class="btn btn-blue-dark" value="Save" aria-label="Close" onclick="saveEcommerceGSTIN()"/>
							<a href="#" class="btn btn-blue-dark ml-2" onclick="closeAddCustomer('addEcommerceGSTIN')" aria-label="Close">Cancel</a>
							</div>
                    </div>
					</form>
                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>