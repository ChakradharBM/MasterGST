<!-- header page begin -->
<input type="hidden" name="__contextPath" id="__contextPath" value="${contextPath}"/>
<nav class="navbar navbar-toggleable-md navbar-inverse fixed-top bg-inverse navbar-db nav-ca">
       <div class="container"> <!-- toggle button for device begin -->
        <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation"> <span class="navbar-toggler-icon"></span> </button>
        <!-- toggle button for device end -->
        <a class="navbar-brand" href="#"> <img src="${contextPath}/static/mastergst/images/master/logo-mastergst.png" alt="Master Gst" /> </a>
        <div class="collapse navbar-collapse" id="navbarCollapse">
            <div class="mainnav-wrap">
                <!-- main menu begin -->
				<c:set var="userCA" value="<%=MasterGSTConstants.CAS%>"/>
				<c:set var="userTaxP" value="<%=MasterGSTConstants.TAXPRACTITIONERS%>"/>
                <c:set var="userCenter" value="<%=MasterGSTConstants.SUVIDHA_CENTERS%>"/>
                <c:set var="userEnterprise" value="<%=MasterGSTConstants.ENTERPRISE%>"/>
                 <c:set var="userBusiness" value="<%=MasterGSTConstants.BUSINESS%>"/>
                <ul class="navbar-nav mainnav-left db-ca">
                    <li class="nav-item"> <a id="nav-dashboard" class="nav-link urllink" href="#" link="${contextPath}/cadb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> Dashboard </a> </li>
                    <li class="nav-item"> <a id="nav-client" class="nav-link urllink" href="#" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose> </a> </li>
                    <!-- <li class="nav-item"> <a id="nav-report" class="nav-link" href="#"> Reports </a> </li>
                    <li class="nav-item"> <a id="nav-connector" class="nav-link urllink" href="#" link="${contextPath}/connectors/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> Connectors </a> </li> -->
                   <c:choose> 
                  	<c:when test="${usertype eq userCA || usertype eq userTaxP}"> 
                  		<li class="nav-item"> <a id="nav-dashb" class="nav-link urllink firm_link" href="#" link="${contextPath}/configurefirm/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> My Firm </a> </li>
                  	</c:when>
                 </c:choose>
		   
		    <li class="nav-item" id="idPermissionTeam"> <c:choose><c:when test="${usertype eq userCenter}"><a id="nav-team" class="nav-link urllink disable" href="#" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> Admin</c:when><c:otherwise><a id="nav-team" class="nav-link urllink" href="#" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> Admin</c:otherwise></c:choose> </a> </li>
					<li class="nav-item"> <a id="nav-billing" class="nav-link urllink deactivelink" href="#" link="${contextPath}/dbllng/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"> Billing </a> </li>
                </ul>
				<c:set var = "varname" value = "${fn:split(fullname, ' ')}"/>
                <ul class="navbar-nav mainnav-right move-not">
                      <li class="nav-item dropdown">
			             <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false"> <span class="dbspriticons notifybell"></span><span class="badge blue badge-circle">${fn:length(messages)}</span> </a>
			              <c:if test="${not empty messages}"> 
				             <div class="dropdown-menu  dropdown-menu-right">
				             <c:forEach items="${messages}" var="message">
				             	<a class="dropdown-item" href="#">${message.subject}</a> 
				             </c:forEach>
				            </div>
			            </c:if>
			         </li>
                    <li class="nav-item dropdown" style="min-width: 220px;"> <a class="nav-link dropdown-toggle p-0 pl-1 pt-1" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false" style="height:100%;"> <span class="rounded-circle img-sm-rounded">${fn:substring(varname[0], 0, 1)}<c:if test="${fn:length(varname) gt 1}">${fn:substring(varname[1], 0, 1)}</c:if>
					</span> <span class="user-name"><c:choose><c:when test='${fn:length(fullname) > 25}'><c:out value="${fn:substring(fullname, 0, 25)}.." /></c:when><c:otherwise><c:out value="${fullname}" /></c:otherwise></c:choose><span class="dbspriticons ddarw"></span></span>
					<span class="lastloggedin">Last Logged In : <b><span id="usrloggedin"></span></b></span>
					</a>
					<div class="dropdown-menu dropdown-menu-right">
						<ul class="profilelist">
							<li class="rowbdr">
							<div class="row">
							<div class="col-md-3">
								<div class="icon-l"><span class="userimg"><img src="${contextPath}/static/mastergst/images/credentials/user1.jpg" alt="User" class="circle" /></span> </div></div>
								<div class="col-md-9">
								<div class="icon-r"><span class="usertxt" style="white-space: pre-line;"><c:choose><c:when test='${fn:length(fullname) > 25}'><c:out value="${fn:substring(fullname, 0, 25)}.." /></c:when><c:otherwise><c:out value="${fullname}" /></c:otherwise></c:choose></span> </div>
								</div>
								</div>
							</li>
							<li>
								<div class="icon-l"><span class="cred-sprite credicon1"></span> </div>
								<div class="icon-r"><span class="icontxt" id="acctUserMail"></span> </div>
							</li>
							<li>
								<div class="icon-l"><span class="cred-sprite credicon2"></span> </div>
								<div class="icon-r"><span class="icontxt" id="acctUserCell"></span> </div>
							</li>
							<li>
								<div class="icon-l"><span class="cred-sprite credicon5"></span> </div>
								<div class="icon-r"><span class="icontxt" id="acctUserAddr"></span> </div>
							</li>
							<li class="divider"></li>
							<li><a class="btn btn-blue-dark btn-sm pull-left" data-toggle="modal" data-target="#profileViewModal" onclick="googlemapsinitialize()">View Profile</a> <a class="btn btn-blue-dark btn-sm pull-right" href="${contextPath}/logout">Logout</a>   </li>
						</ul>
					</div>
					</li>
					
	            </ul>
                <!-- right side menu end -->
            </div>
        </div></div>
    </nav>
	
<!-- profileEdit Modal Start -->
<%@include file="/WEB-INF/views/includes/profileeditmodal.jsp" %>
<%@include file="/WEB-INF/views/includes/subscriptionnowmodal.jsp" %>
<!-- profileEdit Modal End -->
<!-- header page end -->
<script type="text/javascript">
	var month, year, userPermissions;var scriptElement = "";var userMail = "";
	var rPArray = ['Settings','Reports','Invoices','GSTN Actions','General','GSTR1','GSTR2','GSTR3B','GSTR4','GSTR5','GSTR6','Excel Download In Books And Returns','Excel Download In Reports','Multi GSTIN','All Configurations','New Returns'];
	var pArray=['Add Invoice','Edit Invoice','Delete Invoice','Print Invoice','Download Invoice','Edit As Amendment','Upload Invoice','Submit Invoice','File Invoice','Sales Invoices','Purchase Invoices','Reports','Users','Roles','Bank Information','Customers','Suppliers','Items','Services','Branches','Verticals','Configurations','Accounting','E Commerce Operator','Download Sales','Download Purchases','Download GSTR2A','Upload Sales','Upload Purchases','Submit GST Returns','File GST Returns','Sales','Purchase','MultiMonthGSTR2A','Filing Status','Bank Details','Invoice Print','Profile','GSTR1','Amendments','Upload Documents','Filing GSTR1','GSTR2','GSTR2A','MisMatched','Filing GSTR2','Unclaimed ITC','Offset Liability','TAX Payment','GSTR3B','GSTR3B Invoices','Filing GSTR3B','Filing GSTR4','GSTR4','GSTR6','GSTR5','Filing GSTR5','Import Sales','Import Purchases','Import Templates','Bulk Imports','Purchase Order','Purchase Invoices'
						,'Sales Vs GSTR3B Vs GSTR1 Yearly','Sales Vs GSTR3B Vs GSTR1 Monthly','Purchases Vs GSTR3B Vs GSTR2A Monthly','Purchases Vs GSTR3B Vs GSTR2A Yearly','HSN Wise Sales','HSN Wise Purchase','Tax Slab Wise Sales','Tax Slab Wise Purchase','Invoice Wise GSTR2 Vs GSTR2A','Supplier Wise GSTR2 Vs GSTR2A','ITC Claimed','ITC Unclaimed','MultiMonthGSTR3B',
					'Delivery Challans','Estimates','Proforma Invoices','Purchase Order','Accept GSTR2A','Suppliers GST Filing summary','Monthly Tax and ITC summary','Sales','GSTR1A','Purchases','GSTR2A','Reconcile','Unclaimed ITC','Sales','Purchases','Multi month GSTR1','Multi month GSTR2A','Multi month GSTR2B','Invoice Wise GSTR2 Vs GSTR2A','Supplier Wise GSTR2 Vs GSTR2A','ITC Claimed','ITC Unclaimed','HSN Wise Purchase','HSN Wise Sales','Tax Slab Wise Purchase','Tax Slab Wise Sales','Multi GSTIN SEARCH',
					'Stock Summary','Stock Detail','Stock Ledger','Rate List','Item Sales Summary','Low Stock Level','Stock Aging',
					'Notes And Terms','Reconcile Config','Invoice Config','Print Config','Other Config','Eway Bill Config','Eway Bill','Multi Month GSTR1','Yearly Reconcile','Trial Balance','P And L','Balance Sheet','Cash Ledger','Credit Ledger','Liability Ledger','Einvoice','Einvoice Config','E-invoice','Ewaybill','Generate IRN','Cancel IRN','Generate Ewaybill','Download Ewaybill','Cancel Ewaybill','Update Vehicle'];
	$(function() {
		$.ajax({
			url: "${contextPath}/getuser?userId=${id}",
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(user) {
				if(user) {
					userMail = user.accessCustomFieldTemplate;
					$('#acctUserMail').html(user.email);
					$('#acctUserCell').html(user.mobilenumber);
					$('#acctUserAddr').html(user.address);
					$('#EmailId').html(user.email);
					$('#MobileNumber').html(user.mobilenumber);
					$('#Address').html(user.address);
					$('#usrloggedin').html(user.usrLastLoggedIn);
					if(user.accessCustomFieldTemplate) {
						$('.fhpl_import_template').show();	
					}else{
						$('.fhpl_import_template').hide();	
					}
					if(user.accessEntertainmentEinvoiceTemplate) {
						$('.entertaiment_einvoice_import_template').show();	
					}else{
						$('.entertaiment_einvoice_import_template').hide();	
					}
					if(user.accessDrive) {
						$('#idDocs').show();
					} else {
						$('#idDocs').hide();
					}
					if(user.accessTravel) {
						$('.idTravel').show();
					} else {
						$('.idTravel').hide();
					}
					if(user.accessANX1) {
						$('#idANX1').show();
					} else {
						$('#idANX1').hide();
					}
					if(user.accessANX2) {
						$('#idANX2').show();
					} else {
						$('#idANX2').hide();
					}
					if(user.accessAcknowledgement) {
						$('.idAcknowledgement').show();
					} else {
						$('.idAcknowledgement').hide();
					}
					if(user.accessGstr8) {
						$('.idaccessGstr8').show();
					} else {
						$('.idaccessGstr8').hide();
					}
					
					if(user.parentid == null || user.parentid == '') {
						$('#nav-billing').removeClass('disable');
						$('#nav-team').removeClass('disable');
						$('.multiselectuserlist').css("display","inline-block");
						if(user.type == '<%=MasterGSTConstants.SUVIDHA_CENTERS%>') {
							$('#idCDInit, #cpCenterNav').show();
						}
					}else{
						$('.child_user').hide();
						$('.multiselectuserlist').css("display","inline-block");
						//$('.role_nav').css("display","none");
						if(user.type == '<%=MasterGSTConstants.SUVIDHA_CENTERS%>') {
							$('#cp_User').css("display","none");
							$('#cpUser').css("display","none");
						}
					}
				}
			}
		});
		var pUrl = "${contextPath}/usrprms?userid=${id}";
		<c:if test="${not empty client && not empty client.id}">
			pUrl = "${contextPath}/usrclntprms?userid=${id}&clientid=${client.id}";
		</c:if>
		$.ajax({
			url: pUrl,
			async: true,
			cache: false,
			//dataType:"json",
			contentType: 'application/json',
			success : function(permissions) {
				userPermissions = permissions;
				rolesPermissions();
			}
		});
		$('.urllink').on("click", function() {
			var url = $(this).attr('link');
			var filingOption = '<c:out value="${client.filingOption}"/>';
			if(url) {
				if(url.indexOf('alliview') > 0 && url.indexOf('?') > 0){
					if(filingOption == 'Quarterly' && url.indexOf('GSTR3B') < 0){
						if(month == '1' || month == '2' || month == '3'){
							url=url.replace('?','/'+3+'/'+year+'?');
						}else if(month == '4' || month == '5' || month == '6'){
							url=url.replace('?','/'+6+'/'+year+'?');
						}else if(month == '7' || month == '8' || month == '9'){
							url=url.replace('?','/'+9+'/'+year+'?');
						}else if(month == '10' || month == '11' || month == '12'){
							url=url.replace('?','/'+12+'/'+year+'?');
						}
					}else{
						url=url.replace('?','/'+month+'/'+year+'?');
					}
				}else if(url.indexOf('addAnnualinvoice') > 0){
					if(url.indexOf('?') > 0) {
						url=url.replace('?','/03/'+year+'?');
					} else {
						url+='/03/'+year;
					}
				}else{
					if(url.indexOf('?') > 0) {
						url=url.replace('?','/'+month+'/'+year+'?');
					} else {
						url+='/'+month+'/'+year;
					}
				}
				location.href=url;
			}
		});
		$(".otp_form_input .form-control").keyup(function () {
			if (this.value.length == this.maxLength) {
				$(this).next().next('.form-control').focus();
			}
		});
		$('.otp_form_input .form-control').keydown(function(e) {
		    if ((e.which == 8 || e.which == 46) && $(this).val() =='') {
		        $(this).prev('input').focus();
		        $(this).prev().prev('.form-control').focus();
		    }
		});
	});
	function ebillAndEinvoiceAccess(category,name){
		if(category == 'Ewaybill Actions'){
			if(name == "Generate Ewaybill"){
				genEbill = false;
			}else if(name == "Download Ewaybill"){
				dwndEbill = false;
			}else if(name == "Cancel Ewaybill"){
				canEbill = false;
			}else if(name == "Update Vehicle"){
				uptVehicle = false;
			}
		}else if(category == 'Einvoice Actions'){
			if(name == "Generate IRN"){
				gnIRN = false;
			}else if(name == "Cancel IRN"){
				canIRN = false;
			}
		}
	}
	function rolesPermissions() {
		if(userPermissions != null && userPermissions != '') {
			pArray.forEach(function(item) {
				Object.keys(userPermissions).forEach(function(category) {
					if($.inArray(category, rPArray) != -1) {
						rPArray.splice($.inArray(category, rPArray),1);
					}
					var present = false;
					var allconfig = false;
					userPermissions[category].forEach(function(perm) {
						if(!present && (perm.name == item) && (perm.status.includes('Yes'))) {
							if(category == 'All Configurations'){
								var permAccessArrayss = ['gtab1','gtab2','gtab3','gtab4','gtab5','gtab6','gtab7'];
								var permAccessArrays = ['gtab2','gtab3','gtab4','gtab5','gtab6','gtab7'];
								var ipermAccessArrayss = ['Notes And Terms','Reconcile Config','Invoice Config','Print Config','Other Config','Eway Bill Config','Einvoice Config'];
								var ppermAccessArrayss = ['Notes And Terms','Reconcile Config','Print Config','Other Config','Eway Bill Config','Einvoice Config'];
								if(perm.name == "Invoice Config"){
									var abcd = $('.permission'+category.replace(/\s/g, '_')+'-'+item.replace(/\s/g, '_')).attr("href");
									if(abcd == undefined){
									}else{
										$('.permission'+category.replace(/\s/g, '_')+'-'+item.replace(/\s/g, '_')).addClass("active");
										$('#'+abcd.substring(1)).addClass("active");
										ppermAccessArrayss.forEach(function(permAccess) {
											$('.permission'+category.replace(/\s/g, '_')+'-'+permAccess.replace(/\s/g, '_')).removeClass("active");
										});
										permAccessArrays.forEach(function(permAccess) {
											$('#'+permAccess).removeClass("active");
										});
										allconfig = true;
									}
								}else{
									if(!allconfig){
										var abcd = $('.permission'+category.replace(/\s/g, '_')+'-'+item.replace(/\s/g, '_')).attr("href");
										if(abcd == undefined){
										}else{
										$('.permission'+category.replace(/\s/g, '_')+'-'+item.replace(/\s/g, '_')).addClass("active");
										$('#'+abcd.substring(1)).addClass("active");
										var tabid = abcd.substring(1);
										ipermAccessArrayss.splice($.inArray(item, ipermAccessArrayss),1);
										permAccessArrayss.splice($.inArray(tabid, permAccessArrayss),1);
										ipermAccessArrayss.forEach(function(permAccess) {
											$('.permission'+category.replace(/\s/g, '_')+'-'+permAccess.replace(/\s/g, '_')).removeClass("active");
										});
										permAccessArrayss.forEach(function(permAccess) {
											$('#'+permAccess).removeClass("active");
										});
										allconfig = true;
										}
									}
								}
							}
							present = true;
							$('.permission'+category.replace(/\s/g, '_')+'-'+item.replace(/\s/g, '_')).show();
						}
						if(!present && (perm.name == item) && (perm.status.includes('No'))) {
							ebillAndEinvoiceAccess(category,perm.name);
						}
					});
					if(!present) {
						
						$('.permission'+category.replace(/\s/g, '_')+'-'+item.replace(/\s/g, '_')).hide();
						$('.permission'+category.replace(/\s/g, '_')+'-'+item.replace(/\s/g, '_')+'-'+item.replace(/\s/g, '_')).show();
					}
				});
			});
			rPArray.forEach(function(permAccess) {
				pArray.forEach(function(items){
					$('.permission'+permAccess.replace(/\s/g, '_')+'-'+items.replace(/\s/g, '_')).hide();
					$('.permission'+permAccess.replace(/\s/g, '_')+'-'+items.replace(/\s/g, '_')+'-'+items.replace(/\s/g, '_')).show();
				});
			});
			var avlArray = new Object();
			Object.keys(userPermissions).forEach(function(category) {
				userPermissions[category].forEach(function(perm) {
					if(!perm.status.includes('Yes')) {
						if(perm.name == 'Invoice / Print'){
							perm.name = 'Invoice Print';
						}
						if(avlArray[category+'-'+perm.name] == undefined) {
							avlArray[category+'-'+perm.name] = new Array();
						}
						if(perm.status.includes('-')){
							var sts = (perm.status).split("-");
							avlArray[category+'-'+perm.name].push(sts[0]);
						}else{
						avlArray[category+'-'+perm.name].push(perm.status);
						}
					}
				});
			});
			var permAccessArray = ['Add','View','Edit','Delete'];
			Object.keys(avlArray).forEach(function(category) {
				permAccessArray.forEach(function(permAccess) {
					if($.inArray(permAccess, avlArray[category]) != -1) {
						$('.permission'+category.replace(/\s/g, '_')+'-'+permAccess).hide();
						$('.permission'+category.replace(/\s/g, '_')+'-'+permAccess+'-'+category.replace(/\s/g, '_')).show();
						$('.permission'+category.replace(/\s/g, '_')+'-'+permAccess+'s').removeAttr("onClick");
						$('.permission'+category.replace(/\s/g, '_')+'-'+permAccess).removeAttr("href");
					}
				});
			});
		}
	}
	function profileSaveDetails(){
	   if(profileEditValidate()){
		var userDetails = new Object;
		//var fullname = $('#userfullname').val();
		var address = $('#useraddress').val();
		var mobilenumber = $('#usermobilenumber').val();
		$('.user_details_field').each(function() {
			var field = $(this).attr('data');
			if(field != 'createdate') {
				userDetails[field]=$('#user'+field).val();
			}
			$(this).html(userDetails[field]);
		});
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : '${contextPath}/updteprofileuserdtls?id=${id}',
				data : JSON.stringify(userDetails),
				dataType : 'json',
				success : function(dat) {
					$('#acctUserCell').text(mobilenumber);
					$('#acctUserAddr').text(address);
					
					$('#profile_save_btn').css('display','none');
					$('#profile_edit_btn').css('display','block');
				}
			});
	   }
	}
	function formatNumber(nStr) {
		var negativenumber = false;
		if(nStr && nStr.includes("-")){
			negativenumber = true;
			nStr = nStr.replace("-","");
		}
		nStr=nStr.toString();var afterPoint = '';
		if(nStr.indexOf('.') > 0)
		   afterPoint = nStr.substring(nStr.indexOf('.'),nStr.length);
		nStr = Math.floor(nStr);
		nStr=nStr.toString();
		var lastThree = nStr.substring(nStr.length-3);
		var otherNumbers = nStr.substring(0,nStr.length-3);
		if(otherNumbers != '')
		    lastThree = ',' + lastThree;
		var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + afterPoint;
		if(negativenumber){
			res = "-"+res;
		}
		return res;
	}
</script>