<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Partner</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-partners/dashboard-partners.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>

	<%@include file="/WEB-INF/views/partners/header.jsp" %>
	 <div class="db-ca-wrap mt-4">
	     <div class="container">
	                <!-- Dashboard body start -->
					<c:choose>
                        <c:when test='${not empty bankdetails}'>
						<div class="formbox">
						<div class="whitebg m-auto">
						<div class="row">
						<div class="col-md-12 col-sm-12 m-auto">
						
						<h3 class="mt-0">Bank Details <a href="#" class="btn btn-blue-dark pull-right"  data-toggle="modal" data-target="#editBankDetailsModal">Edit Bank Details</a></h3>
						<div class="row">
						<div class="form-group col-md-6 col-sm-12">
							<div class="row">
							<label class="col-md-5 col-sm-12" for="name"><strong> Account Holder Name</strong><span class="coln-txt">:</span></label>
							<div class="col-md-7 col-sm-12" id="name">
								<span class="user_field user_details_field" id="FullName" data="fullname">${bankdetails.accountholdername}</span>
							</div>
							</div>
						</div>
						<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-5 col-sm-12" for="name"><strong>Bank Name</strong><span class="coln-txt">:</span></label>
							<div class="col-md-7 col-sm-12" id="name">
								<span class="user_field user_details_field" id="FullName" data="fullname">${bankdetails.bankname}</span>
							</div>
							</div>
						</div>
						</div>
						<div class="row">
						<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-5 col-sm-12" for="name"><strong>Branch Name</strong><span class="coln-txt">:</span></label>
							<div class="col-md-7 col-sm-12" id="name">
								<span class="user_field user_details_field" id="FullName" data="fullname">${bankdetails.branchname}</span>
							</div>
							</div>
						</div>
						<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-5 col-sm-12" for="name"><strong>Account Number</strong><span class="coln-txt">:</span></label>
							<div class="col-md-7 col-sm-12" id="name">
								<span class="user_field user_details_field" id="FullName" data="fullname">${bankdetails.accountnumber}</span>
							</div>
							</div>
						</div>
						</div>
						<div class="row">
						<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-5 col-sm-12" for="name"><strong>IFSC Code</strong><span class="coln-txt">:</span></label>
							<div class="col-md-7 col-sm-12" id="name">
								<span class="user_field user_details_field" id="FullName" data="fullname">${bankdetails.ifsccode}</span>
							</div>
						</div>	
						</div>
						
						<div class="form-group col-md-6 col-sm-12">
						<div class="row">
							<label class="col-md-5 col-sm-12" for="name"><strong>Pan Number</strong><span class="coln-txt">:</span></label>
							<div class="col-md-7 col-sm-12" id="name">
								<span class="user_field user_details_field" id="FullName" data="fullname">${bankdetails.pannumber}</span>
							</div>
						</div>
						</div>
						</div>
						</div>
						</div>
						</div>
						</div>
								</c:when>    
    						<c:otherwise>
	         <div class="row">
	                <!-- dashboard left block begin -->
                    <div class="col-md-5 col-sm-12 m-auto">
                     <h3>Bank Details</h3>
                    <div class="formbox">   
          		<form:form method="POST" data-toggle="validator" class="meterialform" name="bankform" action="${contextPath}/createpbankdetails/${id}/${fullname}" modelAttribute="bankdetails">
           		    <div class="whitebg">
						<div class="labletxt astrich">
	  	                     Account Holder Name
	                    </div>
	                   	<div class="form-group"> <span class="errormsg" id="accountholdername_Msg"></span>
	                   		<input type="text" id="accountholdername"  name="accountholdername"  data-error="Please enter Account Holder Name" required="required" data-error="" aria-describedby="accountholdername" placeholder="Jane Smith" value="">
	                       	<label for="input" class="control-label"></label>
	                       	<div class="help-block with-errors"></div>
                    		<i class="bar"></i> 
                       	</div>
                       	<div class="labletxt astrich">
							Bank Name		
					    </div>
					    <div class="form-group"> <span class="errormsg" id="bankname_Msg"></span>
	                   		<input type="text" id="bankname"  name="bankname" data-error="Please enter Bank Name" required="required" aria-describedby="bankname" value="">
	                       	<label for="input" class="control-label"></label>
	                       	<div class="help-block with-errors"></div>
                    		<i class="bar"></i> 
                       	</div>
						<div class="labletxt astrich">
							Branch Name		
					    </div>
					    <div class="form-group"> <span class="errormsg" id="branchname_Msg"></span>
	                   		<input type="text" id="branchname"  name="branchname" data-error="Please enter Branch Name" required="required" aria-describedby="branchname" value="">
	                       	<label for="input" class="control-label"></label>
	                       	<div class="help-block with-errors"></div>
                    		<i class="bar"></i> 
                       	</div>
						<div class="labletxt astrich">
							Account Number	
					    </div>
					    <div class="form-group"> <span class="errormsg" id="accountnumber_Msg"></span>
	                   		<input type="text" id="accountnumber"  name="accountnumber"  data-error="Please enter Account Number" required="required" aria-describedby="accountnumber" value="">
	                       	<label for="input" class="control-label"></label>
	                       	<div class="help-block with-errors"></div>
                    		<i class="bar"></i> 
                       	</div>
						<div class="labletxt astrich">
							IFSC Code	
					    </div>
					    <div class="form-group"> <span class="errormsg" id="ifsccode_Msg"></span>
	                   		<input type="text" id="ifsccode"  name="ifsccode"  data-error="Please enter Ifsc Code" required="required" aria-describedby="ifsccode" value="">
	                       	<label for="input" class="control-label"></label>
	                       	<div class="help-block with-errors"></div>
                    		<i class="bar"></i> 
                       	</div>
						<div class="labletxt astrich">
							PAN Number
					    </div>
					    <div class="form-group"> <span class="errormsg" id="pannumber_Msg"></span>
	                   		<input type="text" id="pannumber"  name="pannumber"  data-error="Please enter Pan Number" required="required" aria-describedby="pannumber" value="">
	                       	<label for="input" class="control-label"></label>
	                       	<div class="help-block with-errors"></div>
                    		<i class="bar"></i> 
                       	</div>
						
						<input type="hidden" name="userid" value="<c:out value="${id}"/>">
						<input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
						<input type="submit" class="btn btn-blue col-12 mb-2" value="ADD">
          	     	     </div>
          </form:form>
          </div>
</div>
</div>

</c:otherwise>
</c:choose>
<!-- Edit Bank details modal Start -->
		<div class="modal fade modal-right" id="editBankDetailsModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform bs-fancy-checks">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>Edit Bank Details</h3>

                    </div>
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform" name="bankform" action="${contextPath}/createpbankdetails/${id}/${fullname}" modelAttribute="bankdetails">
					<input type="hidden" name="id" value="${bankdetails.id}" />
                    <div class="row  p-5">
					<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Account Holder Name</p>
                            <span class="errormsg" id="accountholdername_Msg"></span>
                            <input type="text" id="accountholdername" name="accountholdername" required="required" data-error="Please enter Account Holder Name" placeholder="Account Holder Name" value="${bankdetails.accountholdername}" />
                            <label for="accountholdername" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Bank Name</p>
                            <span class="errormsg" id="bankname_Msg"></span>
                            <input type="text" id="bankname" name="bankname" required="required" data-error="Please enter Bank Name" placeholder="Bank Name" value="${bankdetails.bankname}" />
                            <label for="bankname" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                         
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Account Number</p>
                            <span class="errormsg" id="accountnumber_Msg"></span>
                            <input type="text" id="accountnumber" name="accountnumber" required="required" data-error="Please enter Account Number" placeholder="Account Number"  value="${bankdetails.accountnumber}" />
                            <label for="accountnumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
														
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Branch Name</p>
                            <span class="errormsg" id="branchname_Msg"></span>
                            <input type="text" id="branchname" name="branchname" required="required" data-error="Please enter Branch Name" placeholder="Branch Name" value="${bankdetails.branchname}"/>
                            <label for="branchname" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>

						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">IFSC Code</p>
                            <span class="errormsg" id="ifsccode_Msg"></span>
                            <input type="text" id="ifsccode" name="ifsccode" required="required" data-error="Please enter Ifsc Code" placeholder="IFSC Code" value="${bankdetails.ifsccode}" />
                            <label for="ifsccode" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>
						
						<div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Pan Number</p>
                            <span class="errormsg" id="pannumber_Msg"></span>
                            <input type="text" id="pannumber" name="pannumber" required="required" data-error="Please enter Pan Number" placeholder="Pan Number" value="${bankdetails.pannumber}" />
                            <label for="pannumber" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> 
						</div>				

						
						<div class="bdr-b col-12">&nbsp;</div>

                        <div class="col-12 text-center mt-3">
							<input type="hidden" name="userid" value="<c:out value="${id}"/>">
							<input type="hidden" name="fullname" value="<c:out value="${fullname}"/>">
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
	<!-- Edit Bank Details modal end -->
</div>
</div>

    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
		<script type="text/javascript">

  
	$(document).ready(function(){
		$('#nav_bankDetails').addClass('active');
	});
</script>
</body>
</html>