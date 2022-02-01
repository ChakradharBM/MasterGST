<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <link rel="icon" href="static/images/master/favicon.ico" />
    <title>MasterGST | Stock Details Report</title>
    <%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
    <%@include file="/WEB-INF/views/includes/reports_script.jsp"%>
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/select2.min.css" media="all" />
    <script src="${contextPath}/static/mastergst/js/jquery/select2.min.js" type="text/javascript"></script>
</head>
<style>
.sret-period{margin-bottom: 0;padding: 0px;vertical-align: baseline;font-size: 1rem;text-transform: capitalize;margin-right: 10px;}
#reports_stockDetail tbody tr td:first-child{color:black;}
.sdetail::after,#sdetail_filing_option1::after,#ssdetail_filing_option1::after{display:none;}
.datetime-wrap{display: inline-flex};
.select2-container{box-sizing: unset;}
.select2-container--default .select2-selection--single{border: 1px solid lightgray!important;border-radius: 4px;box-sizing: unset;box-shadow: none;}
.select2-container--default .select2-search--dropdown .select2-search__field{box-sizing: inherit;box-shadow: none;}
.select2.select2-container.select2-container--default{width:180px!important;}
</style>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
    <div class="breadcrumbwrap">
	    <div class="container bread">
	     	<div class="row">
	        	<div class="col-md-12 col-sm-12">
	            	<ol class="breadcrumb">
	                	<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">Stock Details Report</li>
	                </ol>
	                <div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
		<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right mb-3" role="button" style="padding: 4px 25px;">Back</a>
					<h4>
				<span class="reports-monthly">Monthly Stock Detail Report of <c:choose><c:when test='${fn:length(client.businessname) > 45}'>${fn:substring(client.businessname, 0, 45)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span>
				<span class="reports-yearly" style="display: none;">Yearly Stock Detail Report of <c:choose><c:when test='${fn:length(client.businessname) > 45}'>${fn:substring(client.businessname, 0, 45)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span>
				<span class="reports-custom" style="display: none;">Custom Stock Detail Report of <c:choose><c:when test='${fn:length(client.businessname) > 45}'>${fn:substring(client.businessname, 0, 45)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></span>
			</h4>
			<p>
				<span class="reports-monthly">Monthly Stock Detail Report gives you a summary of your monthly Stock Detail.</span>
				<span class="reports-yearly" style="display: none;">Yearly Stock Detail Report gives you a summary of your yearly Stock Detail.</span>
				<span class="reports-custom" style="display: none;">Custom Stock Detail Report gives you a summary of your custom Stock Detail.</span>
			</p>
			<label>Item Name:</label>
			<select id="stockItemname" class="itemdrop_search" name="state" style="width:180px!important">
				<option value=""> - select Item - </option>
				<c:forEach items="${items}" var="item">
					<option value="<c:out value="${item.itemno}"/>"><c:out value="${item.itemno}"/></option>
				</c:forEach>
			</select>
			<span id="itemname_error" style="color:red;"></span>
			
							<div class="dropdown chooseteam mr-0" style="z-index:2;">
					<span class="dropdown-toggle yearly sdetail" data-toggle="dropdown" id="sdetail_fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
						<div class="typ-ret" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span id="filing_option" class="filing_option"	style="vertical-align: top;">Monthly</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
						</div>
					</span>
					<div class="dropdown-menu ret-type"	style="width: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a><a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
					</div>
					<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="sret-period" class="sret-period">Report Period:</label></span>
						<div class="datetimetxt datetime-wrap pull-right">
							<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
								<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
									<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div><button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
						</div>
					</span> 
					<span style="display:none" class="yearly-sp"> 
						<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="ssdetail_filing_option1"	style="margin-right: 10px; display: inline-flex;">
							<label id="sret-period" class="sret-period" style="margin-bottom: 3px;">Report Period:</label>
							<div class="typ-ret type_ret_yearly" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
								<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
								<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
									<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
								</span>
							</div>
						</span>
						<div class="dropdown-menu ret-type1" id="sledger_financialYear1" style="width: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
						</div>
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
					</span>
					<span class="datetimetxt custom-sp" style="display:none" id="custom-sp">
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
						<div class="datetimetxt datetime-wrap to-picker">
						<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
							<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 8px; height: 28px; margin-right: 10px;">
								<input type="text" class="form-control totime" value="02-2012"	readonly="">
								<span class="input-group-addon add-on pull-right"><i	class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>
						</div>
						<div class="datetimetxt datetime-wrap dpfromtime">
							<label	style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
							<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
								<input type="text" class="form-control fromtime" value="02-2012"	readonly="">
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>	
						</div>
					</span>
				</div>
			<div class="customtable db-ca-view salestable reportsdbTableStockDetail">
					<table id='reports_stockDetail' class="row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th class="text-center">Date</th><th class="text-center">Item Name</th><th class="text-center">Transaction Type</th><th class="text-center">Quantity</th><th class="text-center">Closing Stock</th>
							</tr>
						</thead>
						<tbody id='stockDetail_body'></tbody>
					</table>
				</div>
			
		</div>
	</div>
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<script src="${contextPath}/static/mastergst/js/inventory/stock_summary_reports.js" type="text/javascript"></script>
	<script type="text/javascript">
	$(function(){
		var date = new Date();
		var month = date.getMonth()+1;
		var	year = date.getFullYear();
		var day = date.getDate();
		var mnt = date.getMonth()+1;
		var yr = date.getFullYear();
		salesFileName = 'MGST_Monthly_'+gstnnumber+'_'+month+year;
		var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
		var customValue = day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr;
		var date = $('.dpMonths').datepicker({
			autoclose: true,
			viewMode: 1,
			minViewMode: 1,
			format: 'mm-yyyy'
		}).on('changeDate', function(ev) {
			month = ev.date.getMonth()+1;
			year = ev.date.getFullYear();
		});
		$('.dpCustom').datepicker({
			format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		}).on('changeDate', function(ev) {
			day = ev.date.getDate();
			mnt = ev.date.getMonth()+1;
			yr = ev.date.getFullYear();
			$('.fromtime').val(((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		});
		$('.dpCustom1').datepicker({
			format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		}).on('changeDate', function(ev) {
			day = ev.date.getDate();
			mnt = ev.date.getMonth()+1;
			yr = ev.date.getFullYear();
			$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		});
		$('.dpMonths').datepicker('update', dateValue);
		$('.dpCustom').datepicker('update', customValue);
		$('.dpCustom1').datepicker('update', customValue);
		//loadStockDetailTable('${id}', '${client.id}', '${month}', '${year}', '${usertype}', '${fullname}','Monthly');
		stockDetailTable = $('#reports_stockDetail').DataTable({
			
		});
	});
	function generateData() {
		var abc = $('#sdetail_fillingoption span').html();
		var itemName = $('#stockItemname').val();
		if(itemName == ""){
			$('#itemname_error').text("Please Select Item Name");
		}else{
			$('#itemname_error').text("");
			if(abc == 'Monthly'){
				$('.reports-monthly').css("display", "block");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "none");
				var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
				var type='';
				loadStockDetailTable('${id}', '${client.id}', mn, yr, '${usertype}', '${fullname}', 'Monthly', itemName);
				stockSummaryExcelDownloads('${id}', '${client.id}', '${month}', '${year}', '${usertype}', '${fullname}', abc, 'Detail', itemName);
			}else if (abc == 'Yearly') {
				$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "block");$('.reports-custom').css("display", "none");
				var year=$('#yearlyoption').html().split("-");
				var type='';
				loadStockDetailTable('${id}', '${client.id}', 0, year[1], '${usertype}', '${fullname}', 'Yearly', itemName);
				stockSummaryExcelDownloads('${id}', '${client.id}', 0 ,year[1], '${usertype}', '${fullname}', abc, 'Detail', itemName);
			}else{
				$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "block");
				var fromtime = $('.fromtime').val();var totime = $('.totime').val();$('.fromtime').val(fromtime);$('.totime').val(totime);
				var type ='custom';
				loadStockDetailTable('${id}', '${client.id}', fromtime, totime, '${usertype}', '${fullname}', type, itemName);
				stockSummaryExcelDownloads('${id}', '${client.id}', fromtime, totime, '${usertype}', '${fullname}', abc, 'Detail', itemName);
			}
		}
	}
	</script>

</body>
</html>