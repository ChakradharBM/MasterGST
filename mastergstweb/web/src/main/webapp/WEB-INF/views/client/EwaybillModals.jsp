<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<div class="modal fade" id="extendEwayBillModal" tabindex="-1" role="dialog" aria-labelledby="extendModal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('extendEwayBillModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Extend EwayBill </h3>
        </div>
        <form:form method="POST" data-toggle="validator" class="meterialform" name="ewaybillextensionform" id="ewaybillextensionform">
        <div class="row  pl-5 pr-5 pt-3">
						
						 <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Extension Reason Code</p>
                            <span class="errormsg" id="extrsn_msg"></span>
                            <select class="" id="extendreason" name="extendreason" onchange="extendCodeSelection()" data-error="Please select reason code" required>
						         <option value="">-Select-</option> 
						         <option value="Natural Calamity">Natural Calamity</option> 
						         <option value="Law and Order Situation">Law and Order Situation</option> 
						         <option value="Transshipment">Transshipment</option>
						          <option value="Accident">Accident</option>
						          <option value="Others">Others</option>
		        		  </select>
                            <label for="reason" class="control-label"></label>
                            <i class="bar"></i> 
						 </div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Remaining Distance</p>
                            <span class="errormsg" id="dist_msg"></span>
                            <input type="text" class="" id="remainingDistance" name="remainingDistance" />
                            <label for="remainingDistance" class="control-label"></label>
                            <i class="bar"></i> 
						 </div>
						<!-- <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Consignment Status</p>
                            <span class="errormsg" id="con_msg"></span>
                            <select id="consignmentStatus">
                            <option value="">-Select-</option>
                            <option value="M">inMovement</option>
                            <option value="T">inTransit</option>
                            </select>
                            <label for="consignmentStatus" class="control-label"></label>
                            <i class="bar"></i> 
						 </div> -->
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Transit Type</p>
                            <span class="errormsg" id="tr_msg"></span>
                            <select id="transitType">
                            <option value="">-Select-</option>
                            <option value="R">Road</option>
                            <option value="W">Warehouse</option>
                            <option value="O">Others</option>
                            </select>
                            <label for="transitType" class="control-label"></label>
                            <i class="bar"></i> 
						 </div>
						 
						 <div class="form-group col-md-12 col-sm-12">
                            <p class="lable-txt astrich">Extension Remarks</p>
                            <span class="errormsg" id="exrmarks_msg"></span>
                            <textarea type="text" class="" id="extendrmrks" name="extendrmrks" placeholder="Extension Remarks" style="border:1px solid lightgray;resize:vertical;"></textarea>
                            
						 </div>
                </div>
        <span id="extend_error" class="extend_error pl-5" style="font-size:12px;color:red;"></span>
	  <input type="hidden" class="cancelcode" id="extendcode" name="extendcode">
      </form:form>
      </div>
      <div class="modal-footer text-right" style="display:block;border-top: 1px solid lightgray;">
        <button type="button" class="btn btn-greendark" id="btn_extend" style="padding:7px 12px;">Extend EwayBill</button>
        <button type="button" class="btn btn-blue-dark" onclick="closeAddCustomer('extendEwayBillModal')"  style="padding:7px 12px;">Cancel</button>
      </div>
    </div>
  </div>
</div>

     