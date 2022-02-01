<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Profile</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<style>.profilebg{padding-top: 25px;}.toolbar{display:inline-block}
div.dataTables_length{margin-left: 18px;margin-top: 11px;}
#turnover_table_wrapper .toolbar{float:left;}
.hidden{display:none}
.fixed-foot{display: block;text-align: center;}
#gstnerrMsg{text-align: right; position: absolute; padding-right: 0px;}
</style>
<script>

function saveClientLogo(event){
	$("#logofilesizeerror").html("");
	var fi = event.target.files[0];
	var size=event.target.files[0].size;
	var file = Math.round((size / 1024));
	if(file > 101){
		$("#logofilesizeerror").html("Please Upload a image less than 100kb");		
	}else{
		$("#logofilesizeerror").html("");
		var img = URL.createObjectURL(fi);
		$('#clntlogo').attr('src', img);
		$('#clntlogos').attr('src', img);
		$('#clntslogo').ajaxSubmit( {
			url: $("#clntslogo").attr("action"),
			dataType: 'json',
			type: 'POST',
			cache: false,
			success: function(response) {
				
			},
			error: function(e) {
			}
		});
	}
}
function deleteLogo(logoid){
	var urlStr = _getContextPath()+'/deleteLogo/'+logoid+'/'+clientId;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		success : function(response) {
			location.reload(true);
		}
	});
}
function removemsg(){
	$('.errormsg').css('display','none');
	$('.with-errors').html('');
	$('.form-group').removeClass('has-error has-danger');
	if(client_Type == "UnRegistered"){
		$('#gstnnumber,#lastName,#gstname,#filingOption,#dealerType').removeAttr("required");
		$('.gstinStateLable_txt').removeClass("astrich");
	}else{
		$('#gstnnumber,#lastName,#gstname,#filingOption,#dealerType').attr("required",true);
		$('.gstinStateLable_txt').addClass("astrich");
	}
}
function updateGstUserName(){
	var gstNo = $('.abgstnnumber').val();
	if(client_Type == "UnRegistered"){
		if(gstNo != ""){
			$('#gstname').attr("required",true);
		}else{
			$('#gstname').removeAttr("required");
		}
	}else{
		$('#gstname').attr("required",true);
	}
}
</script>
<style>
#addclntgstnno,#gstnnumber,#lastName{text-transform: uppercase;}
</style>
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
                    <div class="col-md-10 col-sm-12 profilebg bs-fancy-checks">
                        <!-- title -->
						<c:if test='${not empty client}'>
						<c:if test='${not empty client.businessname}'>
							<c:set var = "varfn" value = "${client.firstname}"/>
							<c:set var = "varln" value = "${client.lastname}"/>
						<div class="edit_active_wrap">
						<span class="viewprotxt" style="margin-top: -5px;float: right;"> <a class="btn btn-blue-dark abt_clnt_edit pull-right permissionSettings-Company_Details-Edit" data-toggle="modal" data-target="#editBusinessModal" onclick="removemsg()">Edit Client</a> </span>
                        <span class="col-md-2 col-sm-12" style="margin-top:14px">
                                <div class="form-check form-check-inline pull-right">
                                    <input class="form-check-input" type="checkbox" id="admin1" value="true" checked />
                                    <label for="admin1"><span class="ui"></span>
                                    </label> <span class="labletxt" style="margin-top:0px!important">Active</span>
                                </div>
                            </div>
						</span>
						<div class="row profile mb-3" style="width: 100%;">
                            <div class="col-md-12 col-sm-12"><p style="margin:0px!important;font-size:24px;color:black;padding:0;font-weight: 600;font-family: inherit;line-height: 1.1;"><span class="pull-left">
							<span class="pull-left imgsize-wrap-thumb"><c:if test="${not empty client.logoid}"><img src="${contextPath}/getlogo/${client.logoid}" alt="Logo" class="imgsize-thumb" id="clntlogos"></c:if>
							<c:if test="${empty client.logoid}"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize-thumb" id="clntlogos"></c:if>
							</span>
                              <span class="pull-left p-2">  <span class="comptxt" style="text-align: left!important;" title="${client.businessname}"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 70)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span>
								<span class="gstidtxt" style="font-size:14px">GSTIN - ${client.gstnnumber}<c:if test="${not empty client.cinNumber}"> - CIN - ${client.cinNumber}</c:if><c:if test="${not empty client.msmeNo}"> - MSME NO - ${client.msmeNo}</c:if></span></span></span>
								</p></div>
							<form method="POST" action="${contextPath}/clntlogo/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" id="clntslogo" enctype="multipart/form-data" class="meterialform">
							<input type="file" name="file" class="clientlogo permissionSettings-Company_Details-Edit" id="clientlogo" onChange="saveClientLogo(event)" style="width:120px;"/>
							<span class="uploadclientlogo permissionSettings-Company_Details-Edit">Add/Edit Logo</span>
							</form>
							<c:if test="${not empty client.logoid}">
								<c:choose>
									<c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">
										<a href='javascript:void(0)' class='logo_delete' onclick='deleteLogo("${client.logoid}")' data-toggle='tooltip' title='Click here to Delete Your Client Logo'> <span class='fa fa-trash-o' style="margin-top:8px;"></span></a>
									</c:when>
									<c:otherwise>
										<a href='javascript:void(0)' class='logo_delete' onclick='deleteLogo("${client.logoid}")' data-toggle='tooltip' title='Click here to Delete Your Business Logo'> <span class='fa fa-trash-o' style="margin-top:8px;"></span></a>
									</c:otherwise>
								</c:choose>
							</c:if>
							<div class="small" style="font-size:12px;color:red;margin:8px;" id="logofilesizeerror"></div>
							<div class="small" style="font-size:12px;color:red;margin:8px;">* Max Image Size : 100kb.</div>		
                        </div>
                        <!-- profiletable start -->
                        <div class="profiletable">
                            <div class="profiletable-row">
                                <div class="profiletable-col hdr">Dealer Type
                                    <div class="profiletable-col"> ${client.dealertype} </div>
                                </div>
                                <div class="profiletable-col hdr">Authorized PAN
                                    <div class="profiletable-col"> ${client.signatoryPAN}</div>
                                </div>
                                <div class="profiletable-col hdr">State
                                    <div class="profiletable-col"> ${client.statename}</div>
                                </div>
                                <div class="profiletable-col hdr">Mobile No
                                    <div class="profiletable-col"> ${client.mobilenumber}</div>
                                </div>
                                <div class="profiletable-col hdr">Email ID /Business Mail ID
                                    <div class="profiletable-col"> ${client.email}</div>
                                </div>
                            </div>
							<div class="profiletable-row">
                                <div class="profiletable-col hdr">Authorized Name
                                    <div class="profiletable-col"> ${client.signatoryName} </div>
                                </div>
                                <div class="profiletable-col hdr">GST Username 
                                    <div class="profiletable-col"> ${client.gstname}</div>
                                </div>
								<div class="profiletable-col hdr">GST Password
                                    <div class="profiletable-col"> ${client.portalpassword}</div>
                                </div>
                                <div class="profiletable-col hdr">Address
                                    <div class="profiletable-col"> ${client.address}</div>
                                </div>
								 <div class="profiletable-col hdr">Group Name
                                    <div class="profiletable-col"> ${client.groupName}</div>
                                </div>
								<!--<div class="profiletable-col hdr">GSTN Number
                                    <div class="profiletable-col"> ${client.gstnnumber}</div>
                                </div>-->
								
                            </div>
                        </div>
                        <!-- profiletable end -->

                        <!-- <div class="add_businesstitle"> <a href="#" class="btn btn-blue-dark" id="addbusiness" data-toggle="modal" data-target="#addBusinessModal"><span class="plustxt">+</span>Add Business</a></div> -->
                        <!-- view edit box begin -->

                        <div class="row add_busines_wrap">
                        </div>
                        <div class="viewprotxt" style="margin-top: -5px;float: right;"> 
								<a class="btn btn-blue-dark abt_clnt_edit pull-right" data-toggle="modal" data-target="#AddTurnoverModal">Add Turnover</a>
							</div>
                        <div class="turnover" id="turnover" style="    margin-top: 26px;">
                        	<div class="col-md-12 col-sm-12 customtable p-0">
                        		<table id="turnover_table" class="row-border dataTable meterialform" cellspacing="0" width="100%">
                        			<thead><tr><th>s.no</th><th>Year</th><th>Turnover</th><th>Action</th></tr></thead>
                        			<tbody>
                        				<c:forEach items="${client.turnovergoptions}" var="turnover" varStatus="loop">
                        					<tr>
	                        					<td>${loop.count}</td><td>${turnover.year}</td><td class="ind_formats">${turnover.turnover}</td>
	                        					<td><a class="btn-edt" href="#" data-toggle="modal" data-target="#EditTurnoverModal" onClick="populateEditTurnover('${client.id}','${turnover.year}','${turnover.turnover}')"><i class="fa fa-edit"></i> </a></td>
                        					</tr>
                        				</c:forEach>
                        			</tbody>
                        		</table>
                        	</div>
                        </div>
						<div class="row add_busines_wrap" style="margin-top:12px">
							<div class="col-md-12 col-sm-12 bdr-l">
								<div class="lable-txt">GST Returns Configuration</div>
								<div class="profiletable">
								<c:if test='${not empty lGSTReturnsSummury}'>
								<c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
									<div class="form-check form-check-inline">
										<c:if test='${GSTReturnsSummury.active == "true"}'>
											<input class="form-check-input gstreturn" type="checkbox" id="${GSTReturnsSummury.returntype}" value="true" checked />
										</c:if>
										<c:if test='${GSTReturnsSummury.active == "false"}'>
										<input class="form-check-input gstreturn" type="checkbox" id="${GSTReturnsSummury.returntype}" value="false" />
										</c:if>
										<label for="${GSTReturnsSummury.returntype}"><span class="ui"></span>
										</label> <span class="labletxt">${GSTReturnsSummury.returntype}</span>
									</div>
								</c:forEach>
								</c:if>
								</div>
							</div>
						</div>
                        <!-- view edit box end -->
						</c:if>
						</c:if>
                    </div>
					<!-- dashboard cp end -->
                </div>
            </div>
        </div>
    </div>
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>

    <!-- footer end here -->
	<!-- Turnover Modal Start -->
    <div class="modal fade" id="AddTurnoverModal" role="dialog" aria-labelledby="AddTurnoverModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
            <div class="modal-header p-0">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>ADD Turnover<br /></h3>
                    </div>
            </div>
                <div class="modal-body meterialform popupright">
                    
                    <!-- row begin -->
					<form:form method="POST" id="add_tur_form" data-toggle="validator" class="meterialform" action="${contextPath}/addturnover/${id}/${fullname}/${usertype}/${month}/${year}">
                    <div class="row p-4">
						<div class="form-group col-md-6 col-sm-12">
						  <p class="lable-txt astrich">Financial year</p>
						  <span class="errormsg" id="turnoverfinancialyear_Msg"></span>
						  <select id="financialyear" name="financialyear" required="required" data-error="Please select financial year">
						  	<option value="2021-2022">2021-2022</option>
						  	<option value="2020-2021">2020-2021</option>
						  	<option value="2019-2020">2019-2020</option>
						  	<option value="2018-2019">2018-2019</option>
						  	<option value="2017-2018">2017-2018</option>
						  	<option value="APR2017-JUN2017">APR2017-JUN2017</option>
						  </select>
						  <label for="input" class="control-label"></label>
						  <div class="help-block with-errors"></div>
						  <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Turnover</p>
                            <span class="errormsg" id="turnover_Msg"></span>
                            <input type="text" id="turnover" class="turnover" name="turnover" required="required" data-error="Please enter your turnover" placeholder="Turnover"/>
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                        <div class="col-12 text-center mt-3" style="display:block">
						<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">
						<input type="submit" class="btn btn-blue-dark hidden submit_turnover_form" value="Save" />
						
						</div>
                    </div>
					</form:form>
					<!-- row end -->
                </div>
                <div class="modal-footer text-center" style="display: block;">
                <label for="submit_turnover_form" class="btn btn-blue-dark mb-0" tabindex="0" onClick="submitTurnover()">Save</label>
                <a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
                </div>
            </div>
        </div>
    </div>
    <!-- Turnover Modal End -->
    <!-- Edit Turnover Modal Start -->
    <div class="modal fade" id="EditTurnoverModal" role="dialog" aria-labelledby="EditTurnoverModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
                <div class="modal-body meterialform popupright" style="300px;">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>Edit Turnover<br /></h3>
                    </div>
                    <!-- row begin -->
				     <form:form method="POST" id="add_tur_form" data-toggle="validator" class="meterialform" action="${contextPath}/addturnover/${id}/${fullname}/${usertype}/${month}/${year}">
				    <div class="row p-4">
				   	<div class="form-group col-md-6 col-sm-12">
						  <p class="lable-txt astrich">Financial year</p>
						  <span class="errormsg" id="turnoverfinancialyear_Msg"></span>
						  <select id="editfinancialyear" name="financialyear" required="required" data-error="Please select financial year">
						  	<option value="2021-2022">2021-2022</option>
						  	<option value="2020-2021">2020-2021</option>
						  	<option value="2019-2020">2019-2020</option>
						  	<option value="2018-2019">2018-2019</option>
						  	<option value="2017-2018">2017-2018</option>
						  	<option value="APR2017-JUN2017">APR2017-JUN2017</option>
						  </select>
						  <label for="input" class="control-label"></label>
						  <div class="help-block with-errors"></div>
						  <i class="bar"></i> </div>
                        <div class="form-group col-md-6 col-sm-12">
                            <p class="lable-txt astrich">Turnover</p>
                            <span class="errormsg" id="turnover_Msg"></span>
                            <input type="text" class="turnover" id="editturnover" name="turnover" required="required" data-error="Please enter valid turnover" placeholder="Turnover" pattern="[0-9]+(\.[0-9][0-9]?)?"/>
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i> </div>
                             <div class="col-12 text-center mt-3" style="display:block">
						<input type="hidden" name="clientid" value="<c:out value="${client.id}"/>">
						<input type="submit" class="btn btn-blue-dark" value="Save" />
						<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
						</div>				
                    </div>
                    </form:form>
				<!-- row end -->
                </div>                       
            </div>
        </div>
    </div>
    <!--Edit Turnover Modal End -->
	<!-- Edit Business Modal Start -->
    <div class="modal fade" id="editBusinessModal" role="dialog" aria-labelledby="editBusinessModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Edit Business<br /></h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright">
                    
                    <!-- row begin -->
					<form:form method="POST" data-toggle="validator" class="meterialform" name="clientform" action="${contextPath}/updateclient/${id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="client">
                    <div class="row gstr-info-tabs p-4">
	                     <div class="form-group col-md-6 col-sm-12">
	                            <p class="lable-txt astrich">Authorized Signatory Name</p>
	                            <span class="errormsg" id="contactPersonName_Msg"></span>
	                            <input type="text" id="contactPersonName" name="signatoryName" data-minlength="3" required="required" data-error="Please enter more than 3 characters" placeholder="Jane Smith" value="${client.signatoryName}" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))"/>
	                            <label for="input" class="control-label"></label>
								<div class="help-block with-errors"></div>
	                            <i class="bar"></i> </div>
	
							<div class="form-group col-md-6 col-sm-12">
							  <p class="lable-txt astrich gstinStateLable_txt">Authorized Person PAN No</p>
							  <span class="errormsg" id="lastName_Msg"></span>
							  <input type="text" id="lastName" name="signatoryPAN" required="required" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" data-error="Please enter Valid PAN.(Sample AAAAA9999A)" placeholder="PAN Number" value="${client.signatoryPAN}" />
							  <label for="input" class="control-label"></label>
							  <div class="help-block with-errors"></div>
							  <i class="bar"></i> </div>
	
	                        <div class="form-group col-md-6 col-sm-12">
	                            <p class="lable-txt">Email Id</p>
	                            <span class="errormsg" id="emailId_Msg"></span>
	                            <input type="email" id="emailId" name="email" data-error="Please enter valid email address" placeholder="john@gmail.com" value="${client.email}" />
	                            <label for="input" class="control-label"></label>
								<div class="help-block with-errors"></div>
	                            <i class="bar"></i> </div>
	
	                        <div class="form-group col-md-6 col-sm-12">
	                            <p class="lable-txt">Mobile Number</p>
	                            <span class="errormsg" id="mobileNumber_Msg"></span>
	                            <p class="indiamobilecode">+91</p><input type="text" id="mobileId" name="mobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" placeholder="9848012345" value="${client.mobilenumber}" />
	                            <label for="input" class="control-label"></label>
								<div class="help-block with-errors"></div>
	                            <i class="bar"></i> </div>
	
							<div class="form-group col-md-6 col-sm-12">
							  <p class="lable-txt astrich">State</p>
							  <span class="errormsg" id="StateName_Msg"></span>
							  <input id="statename" required="required" name="statename" placeholder="State" value="${client.statename}" readonly/>
							  <label for="input" class="control-label"></label>
							  <div class="help-block with-errors"></div>
							  <!--<div id="statenameempty" style="display:none">
									<div class="ddbox">
									  <p>Search didn't return any results.</p>
									</div>
								</div>-->
							  <i class="bar"></i> </div>
							  
							  <div class="form-group col-md-6 col-sm-12">
											<p class="lable-txt astrich gstinStateLable_txt">Dealer Type</p>
											<div class="form-group">
												<span class="errormsg" id="subnumber_Msg"></span>
												<select id="dealerType" name="dealertype" value="${client.dealertype}" required="required">
													<option value="">-- Select Dealer Type --</option>
													<option value="NonCompound" <c:if test="${client.dealertype == 'NonCompound'}">selected</c:if>>Regular / Non Compound Dealer</option>
													<option value="Compound" <c:if test="${client.dealertype == 'Compound'}">selected</c:if>>Composition / Compound Dealer</option>
													<option value="Casual" <c:if test="${client.dealertype == 'Casual'}">selected</c:if>>Casual Taxable Person</option>
													<option value="InputServiceDistributor" <c:if test="${client.dealertype == 'InputServiceDistributor'}">selected</c:if>>Input Service Distributor (ISD)</option>
													<option value="SezUnit" <c:if test="${client.dealertype == 'SezUnit'}">selected</c:if>>SEZ Unit</option>
													<option value="SezDeveloper" <c:if test="${client.dealertype == 'SezDeveloper'}">selected</c:if>>SEZ Developer</option>
												</select>
												<label for="dealerType" class="control-label"></label>
												<div class="help-block with-errors"></div>
												<i class="bar"></i> </div>
										</div>
	
	                        <div class="form-group col-md-6 col-sm-12">
	                            <p class="lable-txt astrich gstinStateLable_txt">GSTIN Login / User Name <a id="btnValidateClient" href="#" onClick="invokeOTP(this)" class="btn btn-green btn-sm pull-right">Verify</a></p>
	                            <span class="errormsg" id="gSTNPortalUserName_Msg"></span>
	                            <input type="text" id="gstname" name="gstname" required="required" data-error="please enter gstin login/username"  placeholder="JohnSmith" value="${client.gstname}" />
	                            <label for="input" class="control-label"></label>
								<div class="help-block with-errors"></div>
	                            <i class="bar"></i> </div>
	
	                        <div class="form-group col-md-6 col-sm-12">
	                            <p class="lable-txt">GSTN Portal Password </p>
	                            <span class="errormsg" id="gSTNPortalPassword_Msg"></span>
	                            <input type="password" id="gSTNPortalPassword" name="portalpassword" placeholder="JohnSmith" value="${client.portalpassword}" />
	                            <label for="input" class="control-label"></label>
								<div class="help-block with-errors"></div>
	                            <i class="bar"></i> </div>
	                            <div class="form-group col-md-6 col-sm-12">
	                            <p class="lable-txt astrich">Pincode</p>
	                            <span class="errormsg" id="Pincode_Msg"></span>
	                            <input type="text" id="clientPincode" name="pincode" placeholder="123323" required value="${client.pincode}"/>
	                            <label for="input" class="control-label"></label>
								<div class="help-block with-errors"></div>
	                            <i class="bar"></i> </div>
	
								
							
					<ul class="nav nav-tabs col-md-12 mt-3 pl-3" role="tablist" id="tabsactive">
						<li class="nav-item"><a class="nav-link active" id="businessTab" data-toggle="tab" href="#businesstab" role="tab">Business Details</a></li>
						<li class="nav-item"><a class="nav-link " id="othersTab" data-toggle="tab" href="#otherstab" role="tab">Other Details</a></li>
			  		</ul>
				<div class="tab-content col-md-12 mb-3 mt-1 p-0">
					<div class="tab-pane active col-md-12" id="businesstab" role="tabpane1" style="height:240px">
							<div class="row">
							 <div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt astrich">Business Name</p>
		                            <span class="errormsg" id="businessName_Msg"></span>
		                            <input type="text" id="businessName" name="businessname" data-minlength="3" required="required" data-error="Please enter more than 3 characters" placeholder="Torent" value="${client.businessname}" />
		                            <label for="input" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
							
							 <div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt astrich gstinStateLable_txt">Business GSTIN Number</p>
		                            <span class="errormsg" id="businessGSTNNumber_Msg"></span>
		                            <input type="text" class="abgstnnumber" id="gstnnumber" name="gstnnumber" onchange="updateGstUserName()" required="required" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" placeholder="07CQZCD1111I4Z7" maxlength="15" onKeyPress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" value="${client.gstnnumber}" />
		                            <label for="input" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                    <div class="form-group col-md-6 col-sm-12">
							  <p class="lable-txt astrich gstinStateLable_txt">PAN Number</p>
							  <span class="errormsg" id="panNo_Msg"></span>
							  <input type="text" id="pannumber" name="pannumber" required="required" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" pattern="^[A-Za-z]{5}\d{4}[A-Za-z]{1}$" data-error="Please enter Valid PAN.(Sample AAAAA9999A)" placeholder="PAN Number" value="${client.pannumber}" />
							  <label for="input" class="control-label"></label>
							  <div class="help-block with-errors"></div>
							  <i class="bar"></i> </div>        
		                            <div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt astrich">Business Address</p>
		                            <span class="errormsg" id="businessAddress_Msg"></span>
		                            <input type="text" id="editBusinessAddress" name="address" class="mapicon" required="required" placeholder="MG Road, Plot No 20, India" value="${client.address}" />
		                            <label for="input" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                             <div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt">Books Starting From(DD/MM/YYYY)</p><i class="fa fa-calendar"  style="position: absolute;top: 50%;right: 12%;"></i>
		                            <input type="text" id="journalEnteringDate" name="journalEnteringDate"  data-error="Please enter valid Date" placeholder="dd/mm/yyyy" value="${journalDate}" />
		                            <label for="input" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                           
									<div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt">Business Email Id</p>
		                            <input type="email" id="businessEmailId" name="businessEmail" data-error="Please enter valid email address" placeholder="john@gmail.com" value="${client.businessEmail}" />
		                            <label for="input" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		                            
		                             <div class="form-group col-md-6 col-sm-12">
		                            <p class="lable-txt">Business Mobile Number</p>
		                            <p class="indiamobilecode">+91</p><input type="text" id="mobileId" name="businessMobilenumber" data-minlength="10" maxlength="10" pattern="[0-9]+" data-error="Please enter valid mobile number" placeholder="9848012345" value="${client.businessMobilenumber}" />
		                            <label for="input" class="control-label"></label>
									<div class="help-block with-errors"></div>
		                            <i class="bar"></i> </div>
		
							</div>
						</div>
						<div class="tab-pane col-md-12" id="otherstab" role="tabpane2" style="height:240px">
								<div class="row">
										<div class="form-group col-md-6 col-sm-12">
				                            <p class="lable-txt">Group Name</p>
				                            <input type="text" id="groupName" name="groupName" placeholder="Group Name" value="${client.groupName}" />
				                            <label for="input" class="control-label"></label>
											 <div id="groupnameempty" style="display:none">
												<div class="ddbox">
													<p>Search didn't return any results.</p>
												</div>
											</div>
				                            <i class="bar"></i> </div>
										 <div class="form-group col-md-6 col-sm-12">
				                            <p class="lable-txt">CIN Number</p>
				                            <input type="text" id="sinnumber" name="cinNumber" data-error="Please enter valid CIN Number" maxlength="21" placeholder="12345" value="${client.cinNumber}" onkeypress="return ((event.charCode >= 65 &amp;&amp; event.charCode <= 90) || (event.charCode >= 97 &amp;&amp; event.charCode <= 122) || (event.charCode >= 48 &amp;&amp; event.charCode <= 57) || (event.charCode == 0))"/>
				                            <label for="input" class="control-label"></label>
											<div class="help-block with-errors"></div>
				                            <i class="bar"></i> </div>
				                            
				                            <div class="form-group col-md-6 col-sm-12">
				                            <p class="lable-txt">LUT Number</p>
				                            <input type="text" id="lutnumber" name="lutNumber" data-error="Please enter valid LUT Number"  maxlength="15" placeholder="ER8766480061750" value="${client.lutNumber}" onkeypress="return ((event.charCode >= 65 &amp;&amp; event.charCode <= 90) || (event.charCode >= 97 &amp;&amp; event.charCode <= 122) || (event.charCode >= 48 &amp;&amp; event.charCode <= 57) || (event.charCode == 0))"/>
				                            <label for="input" class="control-label"></label>
											<div class="help-block with-errors"></div>
				                            <i class="bar"></i> </div>
				                            
				                             <div class="form-group col-md-6 col-sm-12">
				                            <p class="lable-txt">LUT Start Date(dd/mm/yyyy)</p>
				                            <input type="text" id="lutstartDate" name="lutStartDate"  aria-describedby="lutStartDate" placeholder="dd/mm/yyyy" data-error="Please enter valid LUT Start Date"  value="${client.lutStartDate}"/>
				                            <label for="input" class="control-label"></label>
											<div class="help-block with-errors"></div>
				                            <i class="bar"></i> </div>
				                            
				                            <div class="form-group col-md-6 col-sm-12">
				                            <p class="lable-txt">LUT Expiry Date(dd/mm/yyyy)</p>
				                            <input type="text" id="lutexpiryDate" name="lutExpiryDate"  aria-describedby="lutExpiryDate" placeholder="dd/mm/yyyy" data-error="Please enter valid LUT Expiry Date"  value="${client.lutExpiryDate}"/>
				                            <label for="input" class="control-label"></label>
											<div class="help-block with-errors"></div>
				                            <i class="bar"></i> </div>
				                            
				                              <div class="form-group col-md-6 col-sm-12">
				                            <p class="lable-txt">MSME Number</p>
				                            <input type="text" id="msmeNo" name="msmeNo"  aria-describedby="msmeNo" placeholder="12345" data-error="Please enter valid MSME No"  value="${client.msmeNo}"/>
				                            <label for="msmeNo" class="control-label"></label>
											<div class="help-block with-errors"></div>
				                            <i class="bar"></i> </div>
								
								</div>
						</div>
					
					</div>
					  <div class="col-12 text-center mt-3" style="display:block">
						<input type="hidden" name="id" value="<c:out value="${client.id}"/>">
						<input type="submit" class="btn btn-blue-dark hidden" id="submit-form" value="Save" />
						</div>
                    </div>
					</form:form>

                    <!-- row end -->

                </div>
                <div class="modal-footer fixed-foot">
				<label for="submit-form" class="btn btn-blue-dark mb-0" tabindex="0">Save</label>
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
				</div>
            </div>
        </div>
    </div>
    <!-- Edit Business Modal End -->
	<!-- Add Branch Modal Start -->
    <div class="modal fade modal-right" id="addBranchModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform popupright">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>Add Branch or Sub branch<br /><span class="caption">Your the adding branch or sub branch under ${client.businessname} !</span></h3>

                    </div>
                    <!-- row begin -->
                    <div class="row  p-3">

                        <div class="form-group col-md-12 col-sm-12">
                            <div class="f-18-b">Please Select the option</div>
                            <div class="form-group-inline branchboxchecks">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="addbranch" id="addbranch" type="radio" value="addbranch" checked />
                                            <i class="helper"></i>Branch</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="addbranch" id="addbranch" type="radio" value="addsubBranch" />
                                            <i class="helper"></i>Sub Branch</label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class=" ">&nbsp;</div>
						
						
                        <div class="branchbox addbranch row m-0">
                            <div class="col-sm-12 f-18-b">Add your Branch Details.</div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich"> Branch Name</p>
                                <span class="errormsg" id="branchName_Msg" style="margin-top: -18px;"></span>
                                <input type="text" id="branchName" name="branchName" required="required" data-error="Please enter the branch name" placeholder="Jane Smith" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich">Branch Address</p>
                                <span class="errormsg" id="branchAddress_Msg" style="margin-top: -18px;"></span>
                                <input type="text" id="branchAddress" name="branchAddress" class="mapicon" required="required" placeholder="MG Road, Plot 30, India" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>

                        <div class="branchbox addsubBranch row m-0">
                            <div class="col-sm-12 f-18-b">Add your Sub Branch Details.</div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich"> Branch Name</p>
                                <span class="errormsg" id="parentBranch_Msg" style="margin-top: -18px;"></span>
								<select id="parentBranch" class="mt-1" required="required" data-error="Please enter branch name" name="parentBranch" value="" >
								<option value=""> - Select - </option>
								<c:forEach items="${client.branches}" var="branch">
								<option value="${branch.id}">${branch.name}</option>
								</c:forEach>
								</select>
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich"> Sub Branch Name</p>
                                <span class="errormsg" id="subBranchName_Msg" style="margin-top: -18px;"></span>
                                <input type="text" id="subBranchName" name="subBranchName" required="required" data-error="Please enter the Sub Branch name for the branch" placeholder="Jane Smith" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich">Branch Address</p>
                                <span class="errormsg" id="subBranchAddress_Msg" style="margin-top: -18px;"></span>
                                <input type="text" id="subBranchAddress" name="branchAddress" class="mapicon" required="required" placeholder="MG Road, Plot 30, India" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>

                        <div class=" col-12 mt-4 text-center">
                            <a type="button" onClick="addClientInfo('Branch')" class="btn btn-blue-dark"><span aria-hidden="true">ADD</span></a></div>
                    </div>

                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>
    <!-- Add Branch Modal End -->
    <!-- Edit Branch Modal Start -->
    <div class="modal fade modal-right" id="editBranchModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform popupright">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3> Edit Branch / Sub Branch<br /><span class="caption">Your the adding branch or sub branch under ${client.businessname} !</span></h3>

                    </div>
                    <!-- row begin -->
                    <div class="row  p-3">

                        <div class="form-group col-md-12 col-sm-12 mb-4">
                            <div class="f-18-b">Please Select the option Edit</div>
                            <div class="form-group-inline editbranchboxchecks">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="editbranch" id="editbranch" type="radio" value="editbranch" checked/>
                                            <i class="helper"></i>Branch</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="editbranch" id="editbranch" type="radio" value="editsubbranch" />
                                            <i class="helper"></i>Sub Branch</label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- editbranch start -->
                        <div class="editbranchbox editbranch">
                             <div class="customtable lightblue col-12 mb-2" id="tablebranch">

                                <table class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                    <thead>
                                        <tr>
                                            <th>
                                                <div class="checkbox">
                                                </div>
                                            </th>
                                            <th> Branch Code</th>
                                            <th> Branch Name</th>
                                            <th> State</th>
                                        </tr>
                                    </thead>
                                    <tbody>
										<c:forEach items="${client.branches}" var="branch">
                                        <tr>
                                            <td>
                                                <div class="checkbox">
                                                    <label>
                                                        <input type="checkbox" name="chkEditBranch" onClick="updateBranchSelection('${branch.id}','${branch.name}','${branch.address}')" />
                                                        <i class="helper"></i></label>
                                                </div>
                                            </td>
                                            <td>${branch.code}</td>
                                            <td>${branch.name}</td>
                                            <td>${branch.address}</td>
                                        </tr>
										</c:forEach>

                                    </tbody>
                                </table>
                            </div>

                         
							<div class="editbranch_sh current" id="editbranch_sh_v"></div>
                            <div class="row m-0 editbranch_sh" id="editbranch_sh_e" style="display:none;">
								<div class=" bdr-b mb-3 col-12"></div>
                                <div class="col-sm-12 f-18-b">Edit Branch</div>

                                <!-- <div class="customselectfield form-group col-md-6 col-sm-12">
                                    <p class="lable-txt">State</p>
                                    <span class="arw-custom"><i class="fa fa-caret-down"></i></span>
                                    <select class="form-control" style="width:100%">
                                        <option>Andhrapradesh</option>
                                        <option>Telangana</option>
                                        <option>Karnataka</option>
                                    </select>
                                </div>

                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt">Branch Code</p>
                                    <span class="errormsg" id="branchCode_Msg"></span>
                                    <input type="text" id="branchCode" name="branchCode" required="required" placeholder="Jane Smith" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div> -->

                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Branch Name</p>
                                    <span class="errormsg" id="editBranchName_Msg" style="margin-top: -18px;"></span>
                                    <input type="text" id="editBranchName" name="branchName" required="required" placeholder="Jane Smith" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div>
                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Branch Address</p>
                                    <span class="errormsg" id="editBranchAddress_Msg" style="margin-top: -18px;"></span>
                                    <input type="text" id="editBranchAddress" name="branchAddress" class="mapicon" required="required" placeholder="MG Road, Plot 30, India" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div>
								
								<div class="col-12 text-center mt-3"><a href="#" id="editBranchSave" class="btn btn-blue-dark"  aria-label="Close" onClick="updateBranch()">Save</a> <a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
								</div>
                            </div>
                         </div>
                        <!-- editbranch end -->

                        <!-- editsubbranch start -->
                        <div class="editbranchbox editsubbranch">
                              <div class="customtable lightblue col-12  mb-2" id="tablesubbranch">

                                <table class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                    <thead>
                                        <tr>
                                            <th>
                                                <div class="checkbox">
                                                    
                                                </div>
                                            </th>
                                            <th> Sub Branch Code</th>
                                            <th>Sub Branch Name</th>
                                            <th> Branch Name</th>
                                        </tr>

                                    </thead>
                                    <tbody>
										<c:forEach items="${client.branches}" var="branch">
										<c:if test="${not empty branch.subbranches}">
                                        <c:forEach items="${branch.subbranches}" var="subbranch">
										<tr>
                                            <td>
                                                <div class="checkbox">
                                                    <label>
														<input type="checkbox" name="chkEditSubBranch" onClick="updateSubBranchSelection('${subbranch.id}','${subbranch.name}','${subbranch.address}','${branch.id}')" />
                                                        <i class="helper"></i></label>
                                                </div>
                                            </td>
                                            <td>${subbranch.code}</td>
                                            <td>${subbranch.name}</td>
                                            <td>${branch.name}</td>
                                        </tr>
										</c:forEach>
										</c:if>
										</c:forEach>

                                    </tbody>
                                </table>
                            </div>

							<div class="editsubbranch_sh current" id="editsubbranch_sh_v"></div>
                            <div class="row m-0 editsubbranch_sh" id="editsubbranch_sh_e" style="display:none;"> 
                            <div class=" bdr-b mb-3 col-12"></div>
                                <div class="col-sm-12 f-18-b">Edit Sub Branch</div>

                                <div class="customselectfield form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Branch Name</p>
									<span class="errormsg" id="editSubBranchParent_Msg" style="margin-top: -18px;"></span>
                                    <span class="arw-custom"><i class="fa fa-caret-down"></i></span>
									<select id="editSubBranchParent" class="mt-1" required="required" name="editSubBranchParent" value="" >
									<option value=""> - Select - </option>
									<c:forEach items="${client.branches}" var="branch">
									<option value="${branch.id}">${branch.name}</option>
									</c:forEach>
									</select>
									<label for="input" class="control-label"></label>
                                <i class="bar"></i>
                                </div>
  								<!-- <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt">Sub Branch Code</p>
                                    <span class="errormsg" id="subBranchCode_Msg"></span>
                                    <input type="text" id="subBranchCode" name="subBranchCode" required="required" placeholder="Jane Smith" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div> -->
                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Sub Branch Name</p>
                                    <span class="errormsg" id="editSubBranchName_Msg" style="margin-top: -18px;"></span>
                                    <input type="text" id="editSubBranchName" name="subBranchName" required="required" placeholder="Jane Smith" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div>
                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Branch Address</p>
                                    <span class="errormsg" id="editSubBranchAddress_Msg" style="margin-top: -18px;"></span>
                                    <input type="text" id="editSubBranchAddress" name="branchAddress" class="mapicon" required="required" placeholder="MG Road, Plot 30, India" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div>

								<div class="col-12 text-center mt-3"><a href="#" id="editSubBranchSave" onClick="updateSubBranch()" class="btn btn-blue-dark" aria-label="Close">Save</a> <a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
								</div>
							</div>
                            
                        </div>
                        <!-- editsubbranch end -->

                    </div>

                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>
    <!-- Edit  Branch Modal End -->
		
		   <!-- Add Verticals Modal Start -->
    <div class="modal fade modal-right" id="addVarticalsModal" role="dialog" aria-labelledby="addVarticalsModal" aria-hidden="true">
        <div class="modal-dialog modal-md" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform popupright">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3>Add Verticals or Sub Verticals<br /><span class="caption">You are adding Verticals or sub Verticals under ${client.businessname} !</span></h3>

                    </div>
                    <!-- row begin -->
                    <div class="row  p-3">

                        <div class="form-group col-md-12 col-sm-12">
                            <div class="f-18-b">Please Select the option</div>
                            <div class="form-group-inline verticalboxchecks">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="addvertical" id="addvertical" type="radio" value="addvertical" checked />
                                            <i class="helper"></i>Verticals</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="addvertical" id="addvertical" type="radio" value="addsubvertical" />
                                            <i class="helper"></i>Sub Verticals</label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class=" ">&nbsp;</div>

                        <div class="verticalbox addvertical row m-0">
                            <div class="col-sm-12 f-18-b">Add your Verticals Details.</div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich"> Verticals Name</p>
                                <span class="errormsg" id="verticalName_Msg" style="margin-top: -18px;"></span>
                                <input type="text" id="verticalName" name="branchName" required="required" data-error="Please enter the vertical name" placeholder="Jane Smith" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich">Verticals Address</p>
                                <span class="errormsg" id="verticalAddress_Msg" style="margin-top: -18px;"></span>
                                <input type="text" id="verticalAddress" name="branchAddress" class="mapicon" required="required" data-error="Please enter the address of the verticals" placeholder="MG Road, Plot 30, India" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>

                        <div class="verticalbox addsubvertical row m-0">
                            <div class="col-sm-12 f-18-b">Add your Sub Verticals Details.</div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich"> Verticals Name</p>
                                <span class="errormsg" id="parentVertical_Msg" style="margin-top: -18px;"></span>
								<select id="parentVertical" class="mt-1" required="required" name="parentVertical" data-error="Please enter the Vertical name" value="" >
								<option value=""> - Select - </option>
								<c:forEach items="${client.verticals}" var="vertical">
								<option value="${vertical.id}">${vertical.name}</option>
								</c:forEach>
								</select>
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich"> Sub Verticals Name</p>
                                <span class="errormsg" id="subVerticalName_Msg" style="margin-top: -18px;"></span>
                                <input type="text" id="subVerticalName" name="subBranchName" required="required" placeholder="Jane Smith" data-error="Please enter the sub vertical name" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                            <div class="form-group col-md-6 col-sm-12">
                                <p class="lable-txt astrich">Verticals Address</p>
                                <span class="errormsg" id="subVerticalAddress_Msg" style="margin-top: -18px;"></span>
                                <input type="text" id="subVerticalAddress" name="branchAddress" class="mapicon" required="required" data-error="Please enter the vertical address" placeholder="MG Road, Plot 30, India" value="" />
                                <label for="input" class="control-label"></label>
                                <i class="bar"></i> </div>
                        </div>

                        <div class=" col-12 mt-4 text-center">
                            <a type="button" onClick="addClientInfo('Vertical')" class="btn btn-blue-dark"><span aria-hidden="true">ADD</span></a></div>
                    </div>

                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>
    <!-- Add Verticals Modal End -->
    <!-- Edit Verticals Modal Start -->
    <div class="modal fade modal-right" id="editVarticalsModal" role="dialog" aria-labelledby="editVarticalsModal" aria-hidden="true">
        <div class="modal-dialog modal-md" role="document">
            <div class="modal-content">

                <div class="modal-body meterialform popupright">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr">
                        <h3> Edit Verticals / Sub Verticals<br /><span class="caption">You are adding branch or sub branch under ${client.businessname} !</span></h3>

                    </div>
                    <!-- row begin -->
                    <div class="row  p-3">

                        <div class="form-group col-md-12 col-sm-12 mb-4">
                            <div class="f-18-b">Please Select the option Edit</div>
                            <div class="form-group-inline editverticalboxchecks">
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="editvertical" id="editvertical" type="radio" value="editvertical" checked/>
                                            <i class="helper"></i>Verticals</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="editvertical" id="editvertical" type="radio" value="editsubvertical" />
                                            <i class="helper"></i>Sub Verticals</label>
                                    </div>
                                </div>

                            </div>

                        </div>

                        <!-- editbranch start -->
                        <div class="editverticalbox editvertical">
                             <div class="customtable lightblue col-12 mb-2" id="tablevertical">

                                <table class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                    <thead>
                                        <tr>
                                            <th>
                                                <div class="checkbox">
                                                    <label>
                                                        <input type="checkbox"/>
                                                       
                                                </div>
                                            </th>
                                            <th> Verticals Code</th>
                                            <th> Verticals Name</th>
                                            <th> State</th>
                                        </tr>

                                    </thead>
                                    <tbody>
										<c:forEach items="${client.verticals}" var="vertical">
										<tr>
                                            <td>
                                                <div class="checkbox">
                                                    <label>
														<input type="checkbox" name="chkEditVertical" onClick="updateVerticalSelection('${vertical.id}','${vertical.name}','${vertical.address}')" />
                                                        <i class="helper"></i></label>
                                                </div>
                                            </td>
                                            <td>${vertical.code}</td>
                                            <td>${vertical.name}</td>
                                            <td>${vertical.address}</td>
                                        </tr>
										</c:forEach>

                                    </tbody>
                                </table>
                            </div>

                         
							<div class="editvertical_sh current" id="editvertical_sh_v"></div>
                            <div class="row m-0 editvertical_sh" id="editvertical_sh_e" style="display:none;">
								<div class=" bdr-b mb-3 col-12"></div>
                                <div class="col-sm-12 f-18-b">Edit Verticals</div>

                                <!-- <div class="customselectfield form-group col-md-6 col-sm-12">
                                    <p class="lable-txt">State</p>
                                    <span class="arw-custom"><i class="fa fa-caret-down"></i></span>
                                    <select class="form-control" style="width:100%">
                                        <option>Andhrapradesh</option>
                                        <option>Telangana</option>
                                        <option>Karnataka</option>
                                    </select>
                                </div>

                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt">Verticals Code</p>
                                    <span class="errormsg" id="branchCode_Msg"></span>
                                    <input type="text" id="branchCode" name="branchCode" required="required" placeholder="Jane Smith" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div> -->

                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Verticals Name</p>
                                    <span class="errormsg" id="editVerticalName_Msg" style="margin-top: -18px;"></span>
                                    <input type="text" id="editVerticalName" name="branchName" required="required" placeholder="Jane Smith" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div>
                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Verticals Address</p>
                                    <span class="errormsg" id="editVerticalAddress_Msg" style="margin-top: -18px;"></span>
                                    <input type="text" id="editVerticalAddress" name="branchAddress" class="mapicon" required="required" placeholder="MG Road, Plot 30, India" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div>
								
								<div class="col-12 text-center mt-3"><a href="#" onClick="updateVertical()" id="editVerticalSave" class="btn btn-blue-dark" aria-label="Close">Save</a> <a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
								</div>
                            </div>
                         </div>
                        <!-- editbranch end -->

                        <!-- editsubbranch start -->
                        <div class="editverticalbox editsubvertical">
                              <div class="customtable lightblue col-12  mb-2" id="tablesubvertical">

                                <table class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                                    <thead>
                                        <tr>
                                            <th>
                                                <div class="checkbox">
                                                    <label>
                                                        <input type="checkbox"   />
                                                      
                                                </div>
                                            </th>
                                            <th> Sub Verticals Code</th>
                                            <th>Sub Verticals Name</th>
                                            <th> Verticals Name</th>
                                        </tr>

                                    </thead>
                                    <tbody>
										<c:forEach items="${client.verticals}" var="vertical">
										<c:if test="${not empty vertical.subverticals}">
                                        <c:forEach items="${vertical.subverticals}" var="subvertical">
										<tr>
                                            <td>
                                                <div class="checkbox">
                                                    <label>
                                                        <input type="checkbox" name="chkEditSubVertical" onClick="updateSubVerticalSelection('${subvertical.id}','${subvertical.name}','${subvertical.address}','${vertical.id}')" />
                                                        <i class="helper"></i></label>
                                                </div>
                                            </td>
                                            <td>${subvertical.code}</td>
                                            <td>${subvertical.name}</td>
                                            <td>${vertical.name}</td>
                                        </tr>
										</c:forEach>
										</c:if>
										</c:forEach>

                                    </tbody>
                                </table>
                            </div>

							<div class="editsubvertical_sh current" id="editsubvertical_sh_v"></div>
                            <div class="row m-0 editsubvertical_sh" id="editsubvertical_sh_e" style="display:none;"> 
                            <div class=" bdr-b mb-3 col-12"></div>
                                <div class="col-sm-12 f-18-b">Edit Sub Verticals</div>

                                <div class="customselectfield form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Verticals Name</p>
									 <span class="errormsg" id="editSubVerticalParent_Msg" style="margin-top: -18px;"></span>
                                    <span class="arw-custom"><i class="fa fa-caret-down"></i></span>
									<select id="editSubVerticalParent" class="mt-1" required="required" name="editSubVerticalParent" value="" >
									<option value=""> - Select - </option>
									<c:forEach items="${client.verticals}" var="vertical">
									<option value="${vertical.id}">${vertical.name}</option>
									</c:forEach>
									</select>
									<label for="input" class="control-label"></label>
                                <i class="bar"></i>
                                </div>
  								<!-- <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt">Sub Verticals Code</p>
                                    <span class="errormsg" id="subBranchCode_Msg"></span>
                                    <input type="text" id="subBranchCode" name="subBranchCode" required="required" placeholder="Jane Smith" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div> -->
                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Sub Verticals Name</p>
                                    <span class="errormsg" id="editSubVerticalName_Msg" style="margin-top: -18px;"></span>
                                    <input type="text" id="editSubVerticalName" name="subBranchName" required="required" placeholder="Jane Smith" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div>
                                <div class="form-group col-md-6 col-sm-12">
                                    <p class="lable-txt astrich">Verticals Address</p>
                                    <span class="errormsg" id="editSubVerticalAddress_Msg" style="margin-top: -18px;"></span>
                                    <input type="text" id="editSubVerticalAddress" name="branchAddress" class="mapicon" required="required" placeholder="MG Road, Plot 30, India" value="" />
                                    <label for="input" class="control-label"></label>
                                    <i class="bar"></i> </div>

								<div class="col-12 text-center mt-3"><a href="#" onClick="updateSubVertical()" id="editSubVerticalSave" class="btn btn-blue-dark" aria-label="Close">Save</a> <a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Cancel</a>
								</div>
							</div>
                            
                        </div>
                        <!-- editsubbranch end -->

                    </div>

                    <!-- row end -->

                </div>

            </div>
        </div>
    </div>
    <!-- Edit  Verticals Modal End -->
	<!-- enableAccessModal Start -->
	<div class="modal fade" id="enableAccessModal" tabindex="-1" role="dialog" aria-labelledby="enableAccessModal" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr" style="width:100%">
                        <h4>Important Step before your upload invoices  </h4>
                    </div>
				</div>
                <div class="modal-body">
                    <div class=" gstnuser-wrap">
                        <div class="formboxwrap p-0" style="min-height:unset">
						<div class="alert alert-info" id="idClientError"></div>	
						  <h6><span class="steptext"><strong><u>Step 1 </u> : </strong></span>Click here to <a href="https://services.gst.gov.in/services/login" target="_blank">Enable API Access</a>, Follow <a href="${contextPath}/static/mastergst/Enable-API-Access-On-GST-Portal.pdf" target="_blank">Help Guide</a> </h6>
						  <p class="txt-sm"><span class="steptext">&nbsp;</span>Login into your <a href="https://services.gst.gov.in/services/login" target="_blank">GSTN portal</a> and enable authorization. For detailed follow this guidence. </p>
						  <span class=""><span class="steptext pull-left">&nbsp;</span>
						  <a href="https://services.gst.gov.in/services/login" target="_blank" class="btn btn-blue-dark btn-sm pull-left mt-3">Enable API Access</a>
						  <a href="${contextPath}/static/mastergst/Enable-API-Access-On-GST-Portal.pdf" target="_blank" class="btn btn-sm btn-green pull-right mt-3">See Help Guide</a>
						  </span>
                         <div class="mb-3 mt-5" style="border-bottom:1px solid #f5f5f5;width:100%;">&nbsp;</div> 
						<h6><span class="steptext"><strong><u>Step 2 </u> : </strong></span> Verify GSTIN User Name</h6>
 					  
                            <div class="col-md-12 col-sm-12 m-auto p-0">
                                <div class="formbox otpbox mt-3">
                                    <form class="meterialform row" id="accessotpEntryForm">
                                    	<span class="errormsg col-md-12" id="gstnerrMsg"></span>
										<span class="steptext">&nbsp;</span>
										<div class="col-md-5 col-sm-12">
                                                <div class="lable-txt">Enter Your GSTIN Login/User Name</div>
                                                <div class="form-group">
                                                    <span class="errormsg" id="gstnUserIdMsg"></span>
                                                    <input type="text" id="gstnUserId" name="gstnUserId" required="required" data-error="Please enter the GSTN Username" aria-describedby="gstnUserId" placeholder="GSTIN Login/User Name">
                                                    <label for="input" class="control-label"></label>
                                                    <i class="bar"></i> </div>
                                            </div>
											<div class="col-md-4 col-sm-12">
												<a href="#" onClick="showOtp();" class="btn btn-red btn-sm mt-4">Verify Now</a>
											</div>
											
									<div class="whitebg gstn-otp-wrap">                                 
									<h5><span class="steptext"><strong><u>Step 3 </u> : </strong></span>GSTN has sent you an OTP please enter here for verification.</h5>  
                                         
                                            <!-- serverside error begin -->
                                            <div class="errormsg mt-2"> </div>
                                            <!-- serverside error end -->
                                            <div class="errormsg" id="otp_Msg"></div>  
                                            <div class="otp_form_input"  style="display:block;margin-top:30px">
											<div class="col-12" style="display:block"></div>
											<div class="row">
													<span class="steptext col-sm-12">&nbsp;</span>
													<div class="col-md-9 col-sm-12">
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="1" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp2" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="2" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp3" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="3" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp4" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="4" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp5" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="5" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="accessotp6" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="6" placeholder="0"/>
													<div class="help-block with-errors"></div>
													</div>
													<div class="col-md-3 col-sm-12 pull-right">
													<a href="#" onClick="validOtp()" class="btn btn-blue btn-sm btn-verify">Submit</a> 
													</div>
													 <h6 class="col-md-9 col-sm-12 mt-3" style="display:inline-block;width:100%;text-align:center;">Didn&acute;t  Receive OTP? <a href="#" onClick="otpTryAgain('apiNotEnabled')">try again</a></h6>
                                                </div>
                                               
                                            </div>                                      
                                     </div>
                                    </form>
                                </div>

                               <div class="modal-footer text-center" style="display:block">
					<div class="formbox otpbox-suces m-0" style="box-shadow:none">
                                    <form class="meterialform">
                                        <div class="whitebg row">

                                            <!-- serverside error begin -->
                                            <div class="errormsg"> </div>
                                            <!-- serverside error end -->
											<span class="steptext">&nbsp;</span>
                                            <div class="col-sm-10 pl-0" style="display:none;">
										
                                                <div class="mb-5 text-center greenbox" id="idVerifyClient"></div>
	
                                            </div>
											<div class="form-group col-3 m-auto">
												<input type="button" value="Close" class="btn btn-blue-dark btn-sm" data-dismiss="modal" aria-label="Close" />
											</div>
                                        </div>

                                    </form>
                                </div>
                            </div>
                        </div>

                    </div>

                </div>
		</div>
            </div>
        </div>
    </div>
	<!-- enableAccessModal End -->
	<!-- otpModal Start -->
	<div class="modal fade" id="otpModal"  tabindex="-1" role="dialog" aria-labelledby="otpModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<button type="button" id="otpModalClose" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
					</button>
					<div class="invoice-hdr bluehdr">
						<h3>Add Client - Verify OTP</h3>
					</div>
					<div class="p-4">
						<div class="formboxwrap p-0" style="min-height: unset;">
							<h3> Filing GST Made Simple, & Pay your Tax easily </h3>
							<h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
							<div class="col-md-12 col-sm-12 m-auto">
								<div class="formbox otpbox">
									<form class="meterialform" id="otpEntryForm" data-toggle="validator">
										<div class="whitebg">
											<h2> Verifying GSTIN User Name for smooth filing</h2>
											<h6>OTP has been sent to your GSTIN registered mobile number & e-mail, Please enter the same below
											</h6>
											<!-- serverside error begin -->                    
											<div class="errormsg"> </div>
											<!-- serverside error end --> 
											<span class="errormsg" id="cotp_Msg"></span>
											<div class="col-sm-12 otp_form_input" style="display:block;margin-top:30px">
												<div class=" ">
													<div class=" "></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp1" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="11" placeholder="0" />
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp2" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="12" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp3" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="13" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp4" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="14" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp5" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="15" placeholder="0"/>
													<div class="help-block with-errors"></div>
													<input type="text" name="otp" class="form-control invoice_otp" id="otp6" required="required"  data-minlength="1" maxlength="1" pattern="[0-9]+" data-error="Please enter numeric number" tabindex="16" placeholder="0"/>
													<div class="help-block with-errors"></div>
												</div>
												<h6 style="display: inline-block;text-align: center;width: 100%;">Didn't receive OTP? <a href="#" onClick="otpTryAgain('apiEnabled')">try again</a></h6>
											</div>
										</div>
										<div class="p-2 text-center">
											<p><a href="#" onClick="validateOtp()" class="btn btn-lg btn-blue btn-verify">Verify OTP</a></p>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- otpModal End -->	
<script type="text/javascript">
	var uploadResponse, ipAddress='';
   var table = $('table.display').DataTable({
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
		$("#tablebranch div.toolbar").html('<h4>Branch List</h4>');
		$("#tablesubbranch div.toolbar").html('<h4>Sub Branch List</h4>');
		$("#tablevertical div.toolbar").html('<h4>Vertical List</h4>');
		$("#tablesubvertical div.toolbar").html('<h4>Sub Vertical List</h4>');
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
 
   $('#lutstartDate,#lutexpiryDate,#journalEnteringDate').datetimepicker({
	  	timepicker: false,
	  	format: 'd/m/Y',
	  	scrollMonth: true
	});
		var activeStatus = '<c:out value="${client.active}"/>';
		$(document).ready(function() {
			$('#cpAboutNav').addClass('active');
			$('#nav-client').addClass('active');
			if(activeStatus != '') {
				if(activeStatus == 'true') {
					$("#admin1").prop("checked", true);
				} else {
					$("#admin1").prop("checked", false);
				}
			}
			$(".otp_form_input .form-control").keyup(function () {
				if (this.value.length == this.maxLength) {
					$(this).next().next('.form-control').focus();
				}
			});
			OSREC.CurrencyFormatter.formatAll({selector : '.ind_formats'});
				/*--- Edit branch modal ---*/
			$('.gstreturn').change(function() {
				var returnSummary = new Object();
				$('.gstreturn').each(function() {
					var status = $(this).is(':checked');
					var returnType = $(this).attr('id');
					returnSummary[returnType] = status;
				});
				$.ajax({
					type: "POST",
					url: "${contextPath}/clntreturns/${client.id}/${month}/${year}",
					async: false,
					cache: false,
					data: JSON.stringify(returnSummary),
					contentType: 'application/json',
					success : function(data) {
					}
				});
			});

			$('#admin1').change(function() {
				var status = $(this).is(':checked');
				$.ajax({
					url: "${contextPath}/clntactive/${client.id}",
					async: false,
					cache: false,
					data: {
						'active': status
					},
					success : function(data) {
					}
				});
			});
			var groupname = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/srchgroupName?query="+ phrase + "&id=${id}&format=json";
			},
			getValue: "groupName",
			list: {
				onChooseEvent: function() {
					var custData = $("#groupName").getSelectedItemData();
					$('#groupName').val(custData.groupName);
				}
			}

		};
		$("#groupName").easyAutocomplete(groupname);
				/*--- View Button Dropdown ---*/
						$(".btn-view-dd  a.dropdown-item").click(function() {
							var link = $(this).attr('href');
							var showIt = $(link);
							var hideIt = $(".editbranch_sh.current, .editsubbranch_sh.current");
			
							hideIt.fadeOut(100, function(){
									hideIt.removeClass("current");
									showIt.addClass("current");
									showIt.fadeIn(100);
							});
					});

			google.maps.event.addDomListener(window, 'load', initialize);
        });

		
		function uploadClientInfo(type, id, name, address, parentId) {
			$.ajax({
				url: "${contextPath}/updateclientinfo/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}",
				async: false,
				cache: false,
				data: {
					'type': type,
					'elemId': id,
					'name': name,
					'address': address,
					'parentId': parentId
				},
				dataType: "text",
                contentType: 'application/json',
				success : function(data) {
					location.href="${contextPath}/about/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}";
				},
				error : function(data) {
				}
			});
		}
		function invokeOTP(btn) {
			$(btn).addClass('btn-loader');
			var state = $('#statename').val();
			var gstname = $('#gstname').val();
			var gstnnumber = $('.abgstnnumber').val();
			$('#cotp_Msg').text('').css("display","none");
			$('#otp_Msg').text('').css("display","none");
			$('#gstnerrMsg').text('').css("display","none");
			$("#accessotpEntryForm")[0].reset();
			$("#otpEntryForm")[0].reset();
			if(state != '' && gstname != '') {
				$.ajax({
					url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
					async: false,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(response) {
						uploadResponse = response;
						if(uploadResponse.status_cd == '1') {
							$('#otpModal').modal("show");
						} else if(uploadResponse.error 
							&& uploadResponse.error.error_cd == 'AUTH4037') {
							$('#enableAccessModal').modal('show');
							$('#idClientError').html("We noticed that your GSTIN <strong>( "+gstnnumber+" )</strong> doesn't have API Access with the GSTN Portal Login/User Name <strong>"+gstname+"</strong> Please enable the API access and update GSTN User Name correctly, Please follow below steps.");
							$('#gstnUserId').val(gstname);
						} else if(uploadResponse.error && uploadResponse.error.message) {
							errorNotification(uploadResponse.error.message);
						}
					$(btn).removeClass('btn-loader'); 
					},
					error : function(e, status, error) {
						$(btn).removeClass('btn-loader');
						if(e.responseText) {
							errorNotification(e.responseText);
						}
					}
				});
			}
		}
		function showOtp(){
			
			var state = $('#statename').val();
			var gstname = $('#gstnUserId').val();
			$.ajax({
				url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					uploadResponse = response;
					$('#gstnerrMsg').text('').css("display","none");
					if(uploadResponse.status_cd == '1'){
						$('.gstn-otp-wrap').show();
					}else if(uploadResponse.error 
							&& uploadResponse.error.error_cd == 'AUTH4037') {
							$('#gstnerrMsg').text("Your Api Access is not enabled,please enable").css("display","block");
							$('#gstnUserId').val(gstname);
					} else if(uploadResponse.error && uploadResponse.error.message) {
						errorNotification(uploadResponse.error.message);
					}
				},
				error : function(e, status, error) {
					if(e.responseText) {
						$('#idClientError').html(e.responseText);
					}
				}
			});
		}
		function validOtp() {
			var gstnUserId = $('#gstnUserId').val();
			var otp1 = $('#accessotp1').val();
			var otp2 = $('#accessotp2').val();
			var otp3 = $('#accessotp3').val();
			var otp4 = $('#accessotp4').val();
			var otp5 = $('#accessotp5').val();
			var otp6 = $('#accessotp6').val();
			if(otp1=="" || otp2=="" || otp3=="" || otp4=="" || otp5=="" || otp6==""){
				$('#otp_Msg').text('Please Enter otp').css("display","block");
			}else{
				var otp = otp1+otp2+otp3+otp4+otp5+otp6;
				var gstnnumber = $('#gstnnumber').val();
				var pUrl = "${contextPath}/ihubauth/"+otp;
				$("#accessotpEntryForm")[0].reset();
				$('#gstnUserId').val(gstnUserId);
				$.ajax({
					type: "POST",
					url: pUrl,
					async: false,
					cache: false,
					data: JSON.stringify(uploadResponse),
					dataType:"json",
					contentType: 'application/json',
					success : function(authResponse) {
						if(authResponse == null || authResponse == ''){
							$('#otp_Msg').text('We noticed your Internet is slow,Please try again').css("display","block");
						}else{
						if(authResponse.status_cd == '1') {
							$('.gstn-otp-wrap').hide();
							$('#idVerifyClient').parent().show();
							$('#idVerifyClient').html("Verified OTP Number successfully. Your User Name for GSTN Number (<strong>"+gstnnumber+"</strong>) verified.");
						}else{
							$('#otp_Msg').text('Please Enter valid otp').css("display","block");
						}
						}
					},
					error : function(e, status, error) {
						if(e.responseText) {
							$('#idClientError').html(e.responseText);
						}
					}
				});
			}
		}
		function validateOtp() {
			var otp1 = $('#otp1').val();
			var otp2 = $('#otp2').val();
			var otp3 = $('#otp3').val();
			var otp4 = $('#otp4').val();
			var otp5 = $('#otp5').val();
			var otp6 = $('#otp6').val();
			if(otp1=="" || otp2=="" || otp3=="" || otp4=="" || otp5=="" || otp6==""){
				$('#cotp_Msg').text('Please Enter otp').css("display","block");
			}else{
				var otp = otp1+otp2+otp3+otp4+otp5+otp6;
				var pUrl = "${contextPath}/ihubauth/"+otp;
				$("#otpEntryForm")[0].reset();
				$.ajax({
					type: "POST",
					url: pUrl,
					async: false,
					cache: false,
					data: JSON.stringify(uploadResponse),
					dataType:"json",
					contentType: 'application/json',
					success : function(authResponse) {
						if(authResponse == null || authResponse == ''){
							$('#cotp_Msg').text('We noticed your Internet is slow,Please try again').css("display","block");
						}else{
						if(authResponse.status_cd == '1') {
							$('#otpModalClose').click();
						}else{
							$('#cotp_Msg').text('Please Enter Valid otp').css("display","block");
						}
						}
					},
					error : function(e, status, error) {
						$('#otpModalClose').click();
						if(e.responseText) {
							errorNotification(e.responseText);
						}
					}
				});
			}
		}
		function initialize() {
			var field2 = document.getElementById('editBusinessAddress');
			var autocomplete2 = new google.maps.places.Autocomplete(field2);
		}
		var table = $('#turnover_table').DataTable({
			 "dom": '<"toolbar">Blfrtip',
				"paging": true,
				"searching": true,
				//"pageLength": 3,
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
		$("#turnover_table_wrapper .toolbar").html('<h4>Turnover Configuration</h4>');
		function populateEditTurnover(clientid,year,turnover){
			$('#editfinancialyear').val(year);
			if(turnover.indexOf('E') > 0){
				$('#editturnover').val(Number(turnover));
			}else{
				$('#editturnover').val(turnover);
			}
		}
		function otpTryAgain(apiAcessStatus){
			$('#cotp_Msg').text('').css("display","none");
			$('#otp_Msg').text('').css("display","none");
			var state = $('#statename').val();
			var gstname = "";
			if(apiAcessStatus == 'apiEnabled'){
				gstname = $('#addfirmgstnno').val();
				$("#otpEntryForm")[0].reset();
			}else{
				gstname = $('#gstnUserId').val();
				$("#accessotpEntryForm")[0].reset();
				$('#gstnUserId').val(gstname);
			}
			$.ajax({
				url: "${contextPath}/verifyotp?state="+state+"&gstName="+gstname,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					uploadResponse = response;
				},
				error : function(e, status, error) {
					if(e.responseText) {
						$('#idClientError').html(e.responseText);
					}
				}
			});
		}
		$(function() {
			googlemapsinitialize();
			  $('input.turnover').on('input', function() {
			    this.value = this.value
			      .replace(/[^\d.]/g, '')             // numbers and decimals only
			      .replace(/(\..*)\./g, '$1')         // decimal can't exist more than once
			      .replace(/(\.[\d]{2})./g, '$1');    // not more than 4 digits after decimal
			  });
			});
		function submitTurnover(){$('#add_tur_form').submit();}
	$('#add_tur_form').submit(function(e){
	var err = 0;
	$('#add_tur_form').find('input').each(function(){
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
	$('#add_tur_form').find('select').each(function(){
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
</script>
</body>

</html>