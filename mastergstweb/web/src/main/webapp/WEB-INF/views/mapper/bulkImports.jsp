<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Import Bulk Invoices</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/mapimports/mapimports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/echarts/echarts.js"></script>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/echarts/echartsTheme.js"></script>
<style>
.astrich::after{ margin-left:-5px}
.last-seen-data{border-right: 1px solid lightgray;}
.select-temp-error{ text-align: right;color: red;margin-bottom: 3px;height: 24px;}
input#bulkfileselect{opacity:0}
</style>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>  
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active">Bulk Imports</li>
					</ol>
				</div>
			</div>
		</div>
	</div>
  <div class="container ">
    <div class="mapimport-wrap row mt-0">
      <h3 class="hdr-txt mb-2 pl-0 col-md-12">Bulk Imports Sales/Purchases</h3>
      <p class="mb-2">Here you can import more than 500 Invoices and will notify you once import is completed.</p>
      <div class="d-none" id="dashboard" style="width:100%;background-color: white;">
      <p style="text-align:center; margin-top: 29px;">We are importing you invoices, and it will take few minutes based on number of invoices. <br/>Once your import is completed we will send you details to your registered mail.</p>
	      <div class="row mb-4" style="">
		        <div class="col-md-4"></div>
		        <div class="col-md-2"><img src="${contextPath}/static/mastergst/images/index/excel.png" style="width: 120px;"></div>
		        <div class="col-md-2" style="margin-left: -53px;margin-top: -19px;"><img src="${contextPath}/static/mastergst/images/index/add1_.gif" style="width: 107px;margin-top: 72px;"></div>
		        <div class="col-md-2"><img src="${contextPath}/static/mastergst/images/index/Database.png" style="width: 120px;margin-left: -50px;"></div>
	       </div>
      		<p style="text-align:center;margin-bottom:50px"><a class="nav-link urllink btn btn-blue-dark mr-4" href="#" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change" style="display: inline-block;">Continue to Dashboard</a> <a href="#" class="urllink btn btn-blue-dark" link="${contextPath}/bulkimports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=tax" >Import more Invoices</a>  </p> 
      </div>
      <div class="row col-md-12 mb-3" style="background-color: white;">
      <div class="col-md-6 last-seen-data">
      <h6 style="margin-top: 20px;text-align: center;font-size: 21px;">Bulk Import History</h6>
      	<table cellpadding="5" style="width: 100%;border: 1px solid lightgray;margin-bottom: 20px;">
      	    <thead style="background-color:#5769bb; color:#fff;font-family:Arial, Helvetica, sans-serif; font-size:14px;">
      			<tr><th>Type</th><th class="text-center">Date</th><th class="text-center">Imported</th><th class="text-center">Failure</th><th class="text-center">Task</th></tr>
      		</thead>
      		<tbody>
      		<c:forEach items="${bulkimport}" var="bulk">
	      		<tr>
	      			<td style="word-break: break-all;"><c:choose><c:when test="${not empty bulk.fileName}"><c:out value="${bulk.fileName}"/></c:when><c:otherwise><c:out value="${bulk.type}"/></c:otherwise></c:choose></td>
	      			<td class="text-center"><fmt:formatDate value="${bulk.taskStartTime}" pattern="dd/MM/yyyy"/></td>
	      			<td style="color:green;" class="text-center"><c:out value="${bulk.imported}"/></td>
	      			<td style="color:red;" class="text-center"><c:out value="${bulk.failure}"/></td>
	      			<td class="text-center"><c:choose><c:when test="${bulk.task eq 'COMPLETE'}">COMPLETED</c:when><c:otherwise><c:out value="${bulk.task}"/></c:otherwise></c:choose></td>
	      		</tr>
	      	</c:forEach>
	      	</tbody>
      	</table>
      </div>
          <div class="stepwizard-iner col-md-6 pr-0">
          <!-- stepy form 1 -->
          <h6 style="font-size: 20px;text-align: center;" >Import Invoices Here</h6>
          <div class="select-temp-error col-md-12" style="float: right;"><span id="importBulkSummaryError"></span></div>
          <form id="bulkupload" class="form-inline col-md-12" id="BulkForm" method="post" enctype="multipart/form-data" action="${contextPath}/bulkInvoices/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}">
	          <div class="setup-content" id="step-1" style="width:100%">
			  <div class="form-group col-md-12 col-sm-12 m-auto">
			  	<label class="col-md-6 astrich">Select Invoice Template Type</label>
				<select class="form-control col-md-6" id="MapperType" name="returntype">
					<option value="">- Select invoice template -</option>
	                <option value="GSTR1">Sales Invoice</option>
	                <option value="Purchase Register">Purchase Invoice</option>
	                <option value="TallyGSTR1">Tally Sales Invoice</option>
	                <option value="TallyV17GSTR1">Tally Sales Invoice-V1.7</option>
	                <option value="TallyPrimeGSTR1">Tally Prime Sales Invoice</option>
	                <option value="TallyPurchaseRegister">Tally Purchase Invoice</option>
	                <option value="SageGSTR1">Sage Sales Invoice</option>
	                <option value="SagePurchaseRegister">Sage Purchase Invoice</option>
	                <option value="WalterPurchaseRegister">Single Sheet Template of Purchase Invoice</option>
	                <option value="EINVOICE">E-Invoice</option>
	                <c:if test = "${user.accessEntertainmentEinvoiceTemplate eq true}">
						<option value="EntertainmentEINVOICE">Allu E-Invoice</option>
					</c:if>
	                <option value="SageEINVOICE">Sage E-Invoice</option>
	                <option value="GSTR1OFFLINE">GSTR1 Offline Utility</option>
	                <option value="mastergst_all_filedsGSTR1">MasterGST All Fields Template</option>
	                <c:if test = "${user.accessCustomFieldTemplate eq true}">
						<option value="FHPLTemplateGSTR1">MasterGST Sales Template with Additional Fields</option>
					</c:if>
	                <c:forEach items="${mappers}" var="mapper">
						<option value="${mapper.id}">${mapper.mapperName}</option>
					</c:forEach>
	            </select>
	            <div class="col-md-6 pr-4">
								<p class="text-right mt-3">
									<b>Branches</b>
								</p>
							</div>
							<select class="form-control col-md-6 mb-2" name="branch">
								<option value="">- Select Branches -</option>
								<c:forEach items="${client.branches}" var="branches">
									<option value="${branches.name}"><c:out
											value="${branches.name}" /></option>
								</c:forEach>
							</select>
							<div class="col-md-6 pr-4">
								<p class="text-right mt-3">
									<b>Verticals</b>
								</p>
							</div>
							<select class="form-control col-md-6" name="vertical">
								<option value="">- Select Verticals -</option>
								<c:forEach items="${client.verticals}" var="verticals">
									<option value="${verticals.name}"><c:out
											value="${verticals.name}" /></option>
								</c:forEach>
							</select>
						<div class="col-md-6 p-0 mt-2 text-right bimpFinYear" style="display:none;"><h6>Do You have Any B2C Invoices / HSN Summary ? : </h6></div>
						<div class="meterialform form-group-inline bb2cradio m-2" style="display:none;">
								<div class="form-radio">
									<div class="radio">
										<label><input name="bsubmiton" id="bsubmiton" type="radio" value="Yes" /><i class="helper"></i>Yes</label>
									</div>
								</div>
								<div class="form-radio">
									<div class="radio">
										<label><input name="bsubmiton" id="bsubon" type="radio" value="No"/><i class="helper"></i>No</label>
									</div>
								</div>
							</div>
						<p id="btallyb2c" style="display:none;color:green;">(For B2C invoices / HSN Summary template doesn't provide invoice date, So please select Financial Year & Month.)</p>
						<div class="col-md-6 pr-4 btimpFinYear" style="display:none;"><p class="text-right mt-3"><b>Financial Year</b></p></div><select name="btallyfinancialYear" class="form-control col-md-6 btimpFinYear bimpTallyFinYear" onchange="updatebImpFinacialYear(this.value)" style="display:none;"><option value="2021">2021 - 2022</option><option value="2020">2020 - 2021</option><option value="2019">2019 - 2020</option><option value="2018">2018 - 2019</option><option value="2017">2017 - 2018</option></select>
						<div class="col-md-6 pr-4 btimpFinYear" style="display:none;"><p class="text-right mt-3"><b>Month</b></p></div><select name="btallymonth" class="form-control col-md-6 btimpFinYear bimpTallyMonth" onchange="updatebImpMonth(this.value)" style="display:none;"><option value="4">April</option><option value="5">May</option><option value="6">June</option><option value="7">July</option><option value="8">August</option><option value="9">September</option><option value="10">October</option><option value="11">November</option><option value="12">December</option><option value="1">January</option><option value="2">February</option><option value="3">March</option></select>
						<div id="bb2cstallytemplate" class="small text-right" style="color:red;"></div>	
	         </div>
			  <div class="col-12" style="display:block">
	            <fieldset style="margin-top:  15px;">
	            <div class="small text-right">* Please upload Excel format only.</div>
	            <div class="filedragwrap" onclick="choosebulkfileImport()">
	              <div  id="filedrag">
	                <!-- <input type="hidden" id="MAX_FILE_SIZE" name="MAX_FILE_SIZE" value="300000" /> -->
	                <div class="filedraginput"> <span class="choosefile">Choose File</span>
	                 <span class="bulk_errormsg" id="bulkfileselect_Msg"></span>
	                  <input type="file" id="bulkfileselect" name="file" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" style="display:none;"/>
	                </div>
	                <div class="ortxt"> --( OR )--</div>
	                <div>Drop file here</div>
	                <div id="submitbutton1" style="display: none;">
	                  <button type="submit">Upload Files</button>
	                </div>
	              </div>
	            </div>
	           </fieldset>
	          <div class="form-group col-md-12 col-sm-12">
	            <div class="shadowbox" id="bulkviewfiles">
	              <h5>Uploaded/Selected Document Name</h5>
	              <div id="file_name"></div>
	              <span id="useremailid" class="d-none">
	              	<input type="checkbox" name="useremail_cnckbox" class="form-group" style="display: inline-flex;vertical-align: sub;" checked> once it is done bulk import ,MasterGST send statement your email id <span><c:out value="${user.email}"/></span>
	              	(or)
	              	<input type="text" name="ccmailid" class="form-control col-md-6" placeholder= " Email (optional)"/>
	              </span>
	            </div>
	          </div>
	        </div>
	        <div class="form-group  col-sm-12 col-xs-12 mt-3" style="margin-left: 29%;">
	        	<input type="button" class="btn btn-blue-dark" onClick="performBulkImport(this)" value="Import Invoices"/>
	        </div>
	      </div>
    </form>
    </div>
    </div>
 </div>
</div>
<!-- footer begin here -->
<%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script>

function choosebulkfileImport(){
	$('#bulkfileselect')[0].click();
}
var formurl='';
$('#MapperType').change(function(){
	$('#bb2cstallytemplate,#btallyb2c').css("display","none");$('.btimpFinYear,.bimpFinYear,.bb2cradio').css("display","none");
	$('.bimpTallyFinYear').val('');
	$('.bimpTallyMonth').val('');
	var mappertype = $('#MapperType').val();
	if(mappertype == 'TallyPurchaseRegister' || mappertype == 'Purchase Register' || mappertype == 'SageGSTR1' || mappertype == 'SagePurchaseRegister' || mappertype == 'EINVOICE' || mappertype == 'SageEINVOICE' || mappertype == 'EntertainmentEINVOICE' || mappertype == 'FHPLTemplateGSTR1' || mappertype == 'WalterPurchaseRegister'){
	}else{
		var d = new Date();
		var n = d.getMonth();
		$('.bimpTallyMonth').val(n+1);
		$('.bimpTallyFinYear').val('${year}');
		var mnth = $(".bimpTallyMonth option:selected").text();
		$('#bsubon').prop('checked',true);$('#bsubmiton').prop('checked',false);
		$('.bimpFinYear').css("display","block");$('.bb2cradio').css("display","inline-block");
		$('#bb2cstallytemplate').html("");
	}
	$('#importBulkSummaryError').text('');
	if($('#MapperType').val() == 'Purchase Register'){
		formurl='${contextPath}/bulkInvoices/${id}/${fullname}/${usertype}/${client.id}/Purchase Register/${month}/${year}';
	}else if($('#MapperType').val() == 'GSTR1'){
		formurl='${contextPath}/bulkInvoices/${id}/${fullname}/${usertype}/${client.id}/GSTR1/${month}/${year}';
	}else if($('#MapperType').val() != ""){
		formurl='${contextPath}/bulkInvoices/${id}/${fullname}/${usertype}/${client.id}/GSTR1/${month}/${year}';
	}else{
		formurl = "";
		$('#importBulkSummaryError').text('please select invoice template type');
	}
});
$('#bulkfileselect').change(function(e){
	var fileName = e.target.files[0].name;
	if(fileName !=""){
		$('#useremailid').removeClass('d-none');
	}
});

$("input[name='bsubmiton']").click(function(){
	var status = $("input[name='bsubmiton']:checked").val();
	if(status == 'Yes'){
		$('.btimpFinYear').css("display","block");
		$('#bb2cstallytemplate,#btallyb2c').css("display","block");
		$('.bimpTallyFinYear').val('${year}');
		var d = new Date();
		var n = d.getMonth();
		$('.bimpTallyMonth').val(n+1);
		var mnth = $(".bimpTallyMonth option:selected").text();
		var fintext = $( ".bimpTallyFinYear option:selected" ).text();$('#bb2cstallytemplate').html("Note: All B2C Invoices will come under "+mnth+" of "+fintext+" ");
	}else{
		$('#bb2cstallytemplate,#btallyb2c').css("display","none");
		$('.btimpFinYear').css("display","none");
		$('.bimpTallyFinYear').val('');
		$('.bimpTallyMonth').val('');
	}
});
$('#filedraginput').change(function(e){
	var fileName = e.target.files[0].name;
	if(fileName !=""){
		$('#useremailid').removeClass('d-none');
	}
});

function updatebImpFinacialYear(finVal){
	var mnth = $(".bimpTallyMonth option:selected").text();
	var fintext = $( ".bimpTallyFinYear option:selected" ).text();$('#bb2cstallytemplate').html("Note: All B2C Invoices will come under "+mnth+" of "+fintext+" ");
}
function updatebImpMonth(month){
	var mnth = $(".bimpTallyMonth option:selected").text();
	var fintext = $(".bimpTallyFinYear option:selected").text();$('#bb2cstallytemplate').html("Note: All B2C Invoices will come under "+mnth+" of "+fintext+" ");
}

function performBulkImport(btn) {
	if(formurl ==''){
    	$('#importBulkSummaryError').text('please select invoice template type');
	}else{
		if($('#file_name').text() == ''){
			$('#importBulkSummaryError').text('please select a file');
		}else{
			$('#bulkupload').ajaxSubmit( {
				url: $("#bulkupload").attr("action"),
				dataType: 'json',
				type: 'POST',
				cache: false,
				beforeSend: function() {
			        $('#dashboard').removeClass('d-none');
			        $('.stepwizard-iner').css('display','none');
			        $('.last-seen-data').css('display','none');
			    },
				success: function(response) {},
				error: function(e) {}
			});
		}
	}
}
</script>
<script src="${contextPath}/static/mastergst/js/common/filedrag-map8.js" type="text/javascript"></script>
</body>
</html>