<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Profile</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<style>div#dbTable1_length{margin-left: 15px;}div.dataTables_filter input{height:30px!important;}</style>
<script>
	var bankDetails=new Array();
	var table;
	$(document).ready(function() {
		table = $('table.display').DataTable({
			dom: '<"toolbar"f>Blfrtip<"clear">', 	
			"pageLength": 10,
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"responsive": true,
			"ordering": true,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
		$("div.toolbar").html('<h4 style="margin-top: 6px;">Bank Details</h4><a href="#" class="btn btn-blue-dark permissionSettings-Bank_Details-Add" data-toggle="modal" data-target="#addBankDetailsModal" onclick="populateElement()">Add</a> ');
		
		$('#bankdetails').submit(function(e) {
			var accno=$('#accountnumber').val();
			var clientid='<c:out value="${client.id}"/>';
			var err=0;
			var oldaccno = $('#oldaccno').val();
			if(oldaccno != accno || oldaccno == ''){
			$.ajax({
				type : "GET",
				async: false,
				contentType : "application/json",
				url: "${contextPath}/bankdetailsexits/"+clientid+"/"+accno,
				success : function(response) {
					if(response == true){
						$('.accmsg').text('Account number already exists');
						$('#accountnumber').addClass('has-error has-danger');
						err = 1;
						e.preventDefault();
					}else{
						$('.accmsg').text('');
						$('#accountnumber').removeClass('has-error has-danger');
					}
				}
			
			});
			}
			
		});
	});
	
	function populateElement(bankdetailId) {
		$('.with-errors').html('');
		$('.form-group').removeClass('has-error has-danger');
		$('#bankname').val('');
		$('#accountnumber').val('');
		$('#accountName').val('');
		$('#branchname').val('');
		$('#ifsccode').val('');
		$('#modeOfPayment').val('');
		$('#creditDays').val('');
		$('#creditTransfer').val('');
		$('#directDebit').val('');
		$('#balAmtToBePaid').val('');
		$('#dueDate').val('');
		$('#clntqrcodes').attr('src', "${contextPath}/static/mastergst/images/master/qrcode.png");
		$("input[name='id']").remove();
		$("input[name='createdDate']").val('');
		$("input[name='createdBy']").val('');
		if(bankdetailId) {
			bankDetails.forEach(function(bankdetail) {
				if(bankdetail.id == bankdetailId) {
					$('#bankname').val(bankdetail.bankname);
					$('#accountnumber').val(bankdetail.accountnumber);
					$('#accountName').val(bankdetail.accountName);
					$('#branchname').val(bankdetail.branchname);
					$('#ifsccode').val(bankdetail.ifsccode);
					$('#oldaccno').val(bankdetail.accountnumber);
					$('#modeOfPayment').val(bankdetail.modeOfPayment);
					$('#creditDays').val(bankdetail.creditDays);
					$('#creditTransfer').val(bankdetail.creditTransfer);
					$('#directDebit').val(bankdetail.directDebit);
					$('#balAmtToBePaid').val(bankdetail.balAmtToBePaid);
					$('#dueDate').val(bankdetail.dueDate);
					if(bankdetail.qrcodeid != null && bankdetail.qrcodeid != ""){
						var img = "${contextPath}/getlogo/"+bankdetail.qrcodeid;
						$('#clntqrcodes').attr('src', img);
						$("form[name='bankform']").append('<input type="hidden" name="qrcodeid" value="'+bankdetail.qrcodeid+'">');
					}else{
						var img = "${contextPath}/static/mastergst/images/master/qrcode.png";
						$('#clntqrcodes').attr('src', img);
					}
					
					$("form[name='bankform']").append('<input type="hidden" name="id" value="'+bankdetail.id+'">');
					$("input[name='createdDate']").val(bankdetail.createdDate);
					$("input[name='createdBy']").val(bankdetail.createdBy);
				}
			});
		}
	}
	function showDeletePopup(banknameid, name) {
		$('#deleteModal').modal('show');
		$('#delPopupDetails').html(name);
		$('#btnDelete').attr('onclick', "deleteProduct('"+banknameid+"')");
	}

	function deleteProduct(banknameid) {
		$.ajax({
			url: "${contextPath}/delbnkdtl/"+banknameid,
			success : function(response) {
				table.row($('#row'+banknameid)).remove().draw();
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
					 <!-- dashboard cp  begin -->
                    <div class="col-md-10 col-sm-12 customtable p-0">
                        <table id="dbTable1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                            <thead>
                                <tr>
									<th class="text-center">Bank Name</th>
									<th class="text-center">Account Number</th>
									<th class="text-center">Account Name</th>
									<th class="text-center">Branch Name</th>
									<th class="text-center">IFSC Code</th>
									<th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
								<c:forEach items="${bankdetails}" var="bankdetail">
								<script type="text/javascript">
									var bankdetail = new Object();
									bankdetail.id = '<c:out value="${bankdetail.id}"/>';
									bankdetail.bankname = '<c:out value="${bankdetail.bankname}"/>';
									bankdetail.accountnumber = '<c:out value="${bankdetail.accountnumber}"/>';
									bankdetail.accountName = '<c:out value="${bankdetail.accountName}"/>';
									bankdetail.branchname = '<c:out value="${bankdetail.branchname}"/>';
									bankdetail.ifsccode = '<c:out value="${bankdetail.ifsccode}"/>';
									bankdetail.modeOfPayment = '<c:out value="${bankdetail.modeOfPayment}"/>';
									bankdetail.creditTransfer = '<c:out value="${bankdetail.creditTransfer}"/>';
									bankdetail.creditDays = '<c:out value="${bankdetail.creditDays}"/>';
									bankdetail.directDebit = '<c:out value="${bankdetail.directDebit}"/>';
									bankdetail.balAmtToBePaid = '<c:out value="${bankdetail.balAmtToBePaid}"/>';
									bankdetail.dueDate = '<c:out value="${bankdetail.dueDate}"/>';
									bankdetail.qrcodeid ='<c:out value="${bankdetail.qrcodeid}"/>';
									bankdetail.createdDate = '<fmt:formatDate pattern="yyyy-MM-dd" value="${bankdetail.createdDate}" />';
									bankdetail.createdBy = '<c:out value="${bankdetail.createdBy}"/>';
									bankDetails.push(bankdetail);
								</script>
                                <tr id="row${bankdetail.id}">
                                    <td align="center">${bankdetail.bankname}</td>
                                    <td align="center">${bankdetail.accountnumber}</td>
									<td align="center">${bankdetail.accountName}</td>
                                    <td align="center">${bankdetail.branchname}</td>
                                    <td align="center">${bankdetail.ifsccode}</td>
                                    <td class="actionicons"><a class="btn-edt permissionSettings-Bank_Details-Edit" href="#" data-toggle="modal" data-target="#addBankDetailsModal" onClick="populateElement('${bankdetail.id}')"><i class="fa fa-edit"></i> </a><a href="#" class="permissionSettings-Bank_Details-Delete" onClick="showDeletePopup('${bankdetail.id}','${bankdetail.bankname}')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>
                                </tr>
								</c:forEach>
                            </tbody>
                        </table>

                    </div> 
	 
                </div>
            </div>
        </div>
    </div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>

    <!-- footer end here -->
	
	 <!-- Add Modal Start -->
    <div class="modal fade" id="addBankDetailsModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Bank Details</h3>
                    </div>
				</div>
                <div class="modal-body meterialform bs-fancy-checks">
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform bankform" id="bankdetails" name="bankform" action="${contextPath}/createbankdetails/${id}/${fullname}/${usertype}/${month}/${year}" enctype="multipart/form-data" modelAttribute="bankdetails">
					<div class="row  pl-5 pr-5 pt-4 pb-2">
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Bank Name</p>
                            <span class="errormsg" id="bankname_Msg"></span>
                            <input type="text" id="bankname" name="bankname" required="required"  data-error="Please enter Bank Name" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" placeholder="Bank Name" value="" />
                            <label for="bankname" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Account Number</p>
                            <span class="errormsg" id="accountnumber_Msg"></span><span class="errormsg accmsg" style="margin-top:-18px;"></span>
                            <input type="text" id="accountnumber" name="accountnumber" required="required" maxlength="18" minlength="9"  onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" onKeyUp="javascript:return move(this)" data-error="Please enter Account Number" pattern="^[0-9]*$" placeholder="Account Number" value="" />
                            <label for="accountnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Account Name</p>
                            <input type="text" id="accountName" name="accountName" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" onKeyUp="javascript:return move(this)"  placeholder="Account Name" value="" />
                            <label for="accountname" class="control-label"></label>
                            <i class="bar"></i> 
						</div>
														
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Branch Name</p>
                            <span class="errormsg" id="branchname_Msg"></span>
                            <input type="text" id="branchname" name="branchname" required="required"  data-error="Please enter Branch Name" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" placeholder="Branch Name" value="" />
                            <label for="branchname" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>

						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">IFSC Code</p>
                            <span class="errormsg" id="ifsccode_Msg"></span>
                            <input type="text" id="ifsccode" name="ifsccode" required="required" data-error="Please enter Ifsc Code" onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="IFSC Code" pattern="^[a-zA-Z0-9]*$" value="" />
                            <label for="ifsccode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Mode Of Payment</p>
                            <span class="errormsg" id="mode_Msg"></span>
                            <select id="modeOfPayment" name="modeOfPayment"><option value="">-Select-</option><option value=Cash>Cash</option><option value=Bank>Bank</option><option value=TDS-IT>TDS - IT</option><option value=TDS-GST>TDS - GST</option><option value=Discount>Discount</option><option value=Others>Others</option></select>
                            <label for="mode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Credit Days</p>
                            <span class="errormsg" id="credit_Msg"></span>
                            <input type="text" id="creditDays" name="creditDays" onKeyPress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" placeholder="Credit Days"  value="" />
                            <label for="creditdays" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Credit Transfer</p>
                            <span class="errormsg" id="credit_Msg"></span>
                            <input type="text" id="creditTransfer" name="creditTransfer" placeholder="Credit Transfer"  value="" />
                            <label for="credittransfer" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Direct Debit</p>
                            <span class="errormsg" id="debit_Msg"></span>
                            <input type="text" id="directDebit" name="directDebit" placeholder="Direct debit"  value="" />
                            <label for="directdebit" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Balance Amt To Be Paid</p>
                            <span class="errormsg" id="bal_Msg"></span>
                            <input type="text" id="balAmtToBePaid" name="balAmtToBePaid" placeholder="Balance Amt"  value="" />
                            <label for="balanceamt" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt">Due Date(DD/MM/YYYY)</p>
							<i class="fa fa-calendar"  style="position: absolute;top: 50%;right: 12%;"></i>
                            <span class="errormsg" id="duedate_Msg"></span>
                            <input type="text" id="dueDate" name="dueDate" placeholder="Due Date"  value="" />
                            <label for="dueDate" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
					<div class="col-md-6 col-sm-12">
						<span class="qrimgsize-wrap-thumb" style="top:100%;border-radius:0px;width: 160px;max-width: unset;max-height: unset;height: 110px;position:absolute;border: 1px solid #d5d5d5;left: 31%;"><img src="${contextPath}/static/mastergst/images/master/qrcode.png" alt="QRCode" class="imgsize-thumb" id="clntqrcodes" style="height: auto!important;max-height: 110px;border-radius:0px;"></span>
					</div>
					<div class="qrupload_scan_sign ml-3" style="margin-top: 11px;">
					<div class="" id="uplogo" style="position: absolute;left: 30%">
					<input type="file" name="file" class="clientqrcode" accept="image/gif, image/jpeg, image/png" onChange="saveClientQrcode(event)" style="position: relative;opacity: 0;z-index: 1;cursor: pointer;"/>
					<span class="uploadclientlogo">Add/Edit QRCode</span>
					</div>
					<p class="lable-txt">Upload QR Code :</p>
					<div class="helptxt mt-2" style="font-size:12px;font-weight:bold;margin-top: 4px;">(Please add White background QRCode only)</div>
					</div>
						

                        <div class=" col-12 mt-4 text-center" style="display:block">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						    <input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
							<c:if test='${not empty client && not empty client.id}'>
							<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">	
							</c:if>
							<input type="hidden" id="oldaccno"/>
							<input type="submit" class="btn btn-blue-dark bankdetail_submit" style="display:none" value="SAVE"/>
						</div>
                    </div>
					</form:form>
                    <!-- row end -->

                </div>
				<div class="modal-footer text-center" style="display:block">
				<label for="bankdetail_submit" class="btn btn-blue-dark m-0" tabindex="0" onclick="labelbanksubmit()">Save</label>
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
            </div>
        </div>
    </div>
    <!-- Add Modal End -->

	<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete Bank Details </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete Bank Details <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, it cannot be reversed.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Delete Bank</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
		  
    
<script type="text/javascript">
$('#dueDate').datetimepicker({
	timepicker : false,
	format: 'd/m/Y'
});
function labelbanksubmit(){
	$('#bankdetails').submit();
}
$('#bankdetails').submit(function(e){
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
	var ipAddress='';
	$(document).ready(function() {
		$('#cpBankNav').addClass('active');
		
	});
	function saveClientQrcode(event){
		var fi = event.target.files[0];
		var img = URL.createObjectURL(fi);
		$('#clntqrcodes').attr('src', img);
	}
</script>
<c:choose>
<c:when test='${not empty client && not empty client.id}'>
<script type="text/javascript">
	$(document).ready(function() {
		$('#nav-client').addClass('active');
	});
</script>
</c:when>
<c:otherwise>
<script type="text/javascript">
	$(document).ready(function() {
		$('#nav-team').addClass('active');
	});
</script>
</c:otherwise>
</c:choose>
</body>

</html>