<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<%@include file="/WEB-INF/views/includes/script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/font-awesome.min.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datepicker.css" media="all" />

<script src="${contextPath}/static/mastergst/js/common/bootstrap-datepicker.js" type="text/javascript"></script>
<style>
.aspdateinfo{border-bottom: 1px solid lightgray; margin-bottom: 27px;padding: 0px;}
.ulist li{list-style:none;border-right: 1px solid lightgray; display: table-cell;text-align: center;width:5%!important;font-size:12px;padding:10px;}
   .customtable .customtable-hdr h6{font-size:15px;}
    h4.hdrtitle1{display: inline-block;color: #374583;font-size: 16px;float:right;margin-top: 2px;margin-right: 5px;}
    ul.nav.nav-tabs.nav-justified .nav-link{color: #707172;}
</style>
<script type="text/javascript">
	var table1;
	var currDate = new Date(), tableObj = new Object();
	var currMonth=currDate.getMonth()+1;
	var dashboardtype='<c:out value="${dashboardType}"/>';
	var dashboardapitype='<c:out value="${dashboardApiType}"/>';
	$(document).ready(function() {
		$('.mndash').removeClass('active');
		$('#mtab'+currMonth).addClass('active');
		$('#Production'+currMonth).css('font-weight','bold');
		$('#EinvProduction'+currMonth).css('font-weight','bold');
		$('#EwayBillProduction'+currMonth).css('font-weight','bold');
		if(dashboardapitype == 'gst'){
			$('#einv_api').css("display","none");
			$('#eway_api').css("display","none");
			$('#gst_api').css("display","block");
		}else if(dashboardapitype == 'eway'){
			$('#einv_api').css("display","none");
			$('#gst_api').css("display","none");
			$('#eway_api').css("display","block");
		}else if(dashboardapitype == 'einv'){
			$('#einv_api').css("display","block");
			$('#gst_api').css("display","none");
			$('#eway_api').css("display","none");
		}else if(dashboardapitype == 'all'){
			$('#gst_api').css("display","inline-block");
			$('#eway_api').css("display","inline-block");
			$('#einv_api').css("display","inline-block");
		}else if(dashboardapitype == 'gsteway'){
			$('#gst_api').css("display","inline-block");
			$('#eway_api').css("display","inline-block");
			$('#einv_api').css("display","none");
		}else if(dashboardapitype == 'gsteinv'){
			$('#gst_api').css("display","inline-block");
			$('#eway_api').css("display","none");
			$('#einv_api').css("display","inline-block");
		}else if(dashboardapitype == 'ewayeinv'){
			$('#gst_api').css("display","none");
			$('#eway_api').css("display","inline-block");
			$('#einv_api').css("display","inline-block");
		}else{
			$('#breadcrumb').css("display","none");
		}
		if(dashboardtype == 'gst'){
			if(dashboardapitype == 'gst' || dashboardapitype == 'all' || dashboardapitype == 'gsteway' || dashboardapitype == 'gsteinv'){
				$('#gstapi').addClass("active");
				$('#ewaybillapi').removeClass("active");
				$('#einvapi').removeClass("active");
			}else if(dashboardapitype == 'eway'){
				$('#ewaybillapi').addClass("active");
				$('#gstapi').removeClass("active");
				$('#einvapi').removeClass("active");
			}else if(dashboardapitype == 'einv'){
				$('#ewaybillapi').removeClass("active");
				$('#gstapi').removeClass("active");
				$('#einvapi').addClass("active");
			}
		}else if(dashboardtype == "einv"){
			$('#ewaybillapi').removeClass("active");
			$('#gstapi').removeClass("active");
			$('#einvapi').addClass("active");
		}else{
			$('#ewaybillapi').addClass("active");
			$('#gstapi').removeClass("active");
			$('#einvapi').removeClass("active");
		}
		 
		var month = currDate.getMonth()+1;
		var year = currDate.getFullYear();
		var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
		var date = $('#datetimepicker').datepicker({
			viewMode: 1,
			minViewMode: 1,
			format: 'mm-yyyy'
		}).on('changeDate', function(ev) {
			$('.datepicker').toggle();
			updateTabs(ev.date.getFullYear(), ev.date.getMonth()+1);
			
		});
		$('#datetimepicker').datepicker('setValue', dateValue);
		if($('#datetimepicker').val() == ""){
			$('#datetimepicker').val(dateValue);
		}
		$('#financialYear').val(year);
		updateTabs(year, month);
		//$('a[href="#tab'+currDate.getDate()+'"]').click();
		$('.gstmeteringcount').html(0);
		$.ajax({
		url: "${contextPath}/mdfymonthlyinvoiceusage?year="+year+"&userId=<c:out value="${id}"/>",
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(summary) {
			$.each(summary, function(key, value){
				$('#'+key).html(value);								
			});
		}
	});
		
	});
	
	function fetchFilingStatus(mnth){
		var year = $('#financialYear').val();
		$('.gstmeteringcount').css('font-weight','100');
		$('#Production'+mnth).css('font-weight','bold');
		$('#EwayBillProduction'+mnth).css('font-weight','bold');
		updateTabs(year,mnth);
	}
	function updateTabs(year, month) {
		var days = new Date(year, month, 0).getDate();
		var gstewayUsername = "GST User Name";
		if(dashboardtype == 'gst'){
			gstewayUsername = "GST User Name";
		}else if(dashboardtype == "einv"){
			gstewayUsername = "e-Invoice User Name";
		}else{
			gstewayUsername = "e-Way Bill User Name";
		}
		var content = '<li class="nav-item"> <a class="nav-link" data-toggle="tab" href="#tab0" role="tab" onclick="updateAspRecords('+0+','+month+','+year+')">All</a> </li>';
		for (i = 1; i <= days; i++) { 
    		 content += '<li class="nav-item"> <a class="nav-link" data-toggle="tab" href="#tab'+[i]+'" role="tab" onclick="updateAspRecords('+ [i]+','+month+','+year+')">'+ [i]+'</a> </li>';
		}
		$('#idTabList').html(content);
		content = '<div class="tab-pane" id="tab0" role="tabpanel"><div class="customtable"><div class="customtable-hdr"><h6>My API Usage List - <strong>All Days</strong></h6></div><a href="#" class="status_excel_btns"><img src="${contextPath}/static/mastergst/images/dashboard-ca/excel-icon.png" alt="Excel" style="width:35px;margin-left: 75%;position: absolute;z-index: 1;"></a><table id="idTable0" class="display row-border dataTable meterialform aspDataTable" cellspacing="0" width="100%"><thead><tr><th>'+gstewayUsername+'</th><th>IP Address</th><th>Type</th><th>Service Name</th><th>Status</th><th>Size</th><th>Cost</th><th>Start Time</th></tr></thead><tbody id="idBody0"></tbody></table></div></div>';
		for (j = 1; j <= days; j++) {                
			 content += '<div class="tab-pane" id="tab'+[j]+'" role="tabpanel"><div class="customtable"><div class="customtable-hdr"><h6>My API Usage List - <strong>'+ [j] +' Day</strong></h6></div><a href="#" class="status_excel_btns"><img src="${contextPath}/static/mastergst/images/dashboard-ca/excel-icon.png" alt="Excel" style="width:35px;margin-left: 75%;position: absolute;z-index: 1;"></a><table id="idTable'+[j]+'" class="display row-border dataTable meterialform aspDataTable" cellspacing="0" width="100%"><thead><tr><th>'+gstewayUsername+'</th><th>IP Address</th><th>Type</th><th>Service Name</th><th>Status</th><th>Size</th><th>Cost</th><th>Start Time</th></tr></thead><tbody id="idBody'+[j]+'"></tbody></table></div></div>';
		}
		$('#tab-content').html(content);
		//for (day = 1; day <= days; day++) {  
		//	$('#idTable'+day).DataTable();
		//}
		$('a[href="#tab'+currDate.getDate()+'"]').click();
	}
	
	function updatetime(dat){
		var dt = "";
		if(dat.starttime != null && dat.starttime != ""){
			var createdDt1 = new Date(dat.starttime) ;
			var month = createdDt1.getMonth() + 1; 
			var day = createdDt1.getDate();
			var year = createdDt1.getFullYear();
			//var createdDt = new Date(dat.starttime).toLocaleTimeString();
			
			var hours = createdDt1.getHours();
			var minutes = createdDt1.getMinutes();
			var ampm = hours >= 12 ? 'PM' : 'AM';
			hours = hours % 12;
			  //hours = hours ? hours : 12; // the hour '0' should be '12'
			minutes = minutes < 10 ? '0'+minutes : minutes;
			var seconds = createdDt1.getSeconds();
			seconds = seconds < 10 ? '0'+seconds : seconds;
			var strTime = hours + ':' + minutes + ':' + seconds + ' ' + ampm;
			dt = day+'-'+month+'-'+year+' '+strTime;
		}
		return dt;
	}
	function updateAspRecords(day,month,year){
		var cost = '<c:out value="${rate}" />';
		if(tableObj['idTable'+day]) {
			$('#idTable'+day).DataTable().destroy();
		}
		
		//$('.aspDataTable thead').empty();
		//$('.aspDataTable tbody').empty();
		$('#idTable'+day+' tbody').empty();
		table1 = $('#idTable'+day).DataTable({
			"serverSide": true,
			 "processing": true,
			"ajax": $.fn.dataTable.pipeline( {
				url: '${contextPath}/getAspMeterdata?id=${id}&dashboardType=${dashboardType}&day='+day+'&month='+(month-1)+'&year='+year+'&cost='+cost,
		        type: 'GET',
				pages:5
				 } ),
				
		     "paging": true,
		     "order": [[0,'desc']],
		     'pageLength':10,
			 "lengthMenu": [ [10, 25, 50], [10, 25, 50] ],
			 responsive:true,
	         columns: [
                  {data:'gstnusername', name:"gstnusername"},
                  {data:'ipusr', name:"ipusr"},
                  {data:'type', name:"type"},
                  {data:'servicename', name:'servicename'},
				  {data:'status',name:'status'},
				  {data:'size',name:'size'},
				  {data:'cost',name:'cost'},
				  {data:updatetime,name:'starttime'}
                  ]
		 });
		 tableObj['idTable'+day] = true;
		 
		 $.ajax({
			url : '${contextPath}/getAspMeterdataTraffic?id=${id}&dashboardType=${dashboardType}&day='+day+'&month='+(month-1)+'&year='+year,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(json) {
				if(json.failCounter == null || json.failCounter == "0"){
					$('#failCounter').html(0);
				}else{
					$('#failCounter').html(json.failCounter);
				}
				if(json.requestPerSecond == null || json.requestPerSecond == "0/0"){
					$('#requestPerSecond').html("0/0");
				}else{
					$('#requestPerSecond').html(json.requestPerSecond);
				}
			}
		});
		$('.status_excel_btns').attr('href','${contextPath}/newdwnldAspUsage?id=${id}&dashboardType=${dashboardType}&day='+day+'&month='+(month-1)+'&year='+year+'&cost='+cost);
		//$('.status_excel_btns').attr('href','${contextPath}/dwnldAspUsage?id=${id}&dashboardType=${dashboardType}&day='+day+'&month='+(month-1)+'&year='+year+'&cost='+cost);
	}
	
	 $.fn.dataTable.pipeline = function ( opts ) {
    // Configuration options
    var conf = $.extend( {
      pages: 5,     // number of pages to cache
      url: '',      // script url
      data: null,   // function or object with parameters to send to the server
                    // matching how `ajax.data` works in DataTables
      method: 'GET' // Ajax HTTP method      
    }, opts );
 
    // Private variables for storing the cache
    var cacheLower = -1;
    var cacheUpper = null;
    var cacheLastRequest = null;
    var cacheLastJson = null;
 
    return function ( request, drawCallback, settings ) {
      var ajax          = false;
      var requestStart  = request.start;
      var drawStart     = request.start;
      var requestLength = request.length;
      var requestEnd    = requestStart + requestLength;
       
      if ( settings.clearCache ) {
        // API requested that the cache be cleared
        ajax = true;
        settings.clearCache = false;
      }
      else if ( cacheLower < 0 || requestStart < cacheLower || requestEnd > cacheUpper ) {
        // outside cached data - need to make a request
        ajax = true;
      }
      else if ( JSON.stringify( request.order )   !== JSON.stringify( cacheLastRequest.order ) ||
        JSON.stringify( request.columns ) !== JSON.stringify( cacheLastRequest.columns ) ||
        JSON.stringify( request.search )  !== JSON.stringify( cacheLastRequest.search )
      ){
        // properties changed (ordering, columns, searching)
        ajax = true;
      }
       
      // Store the request for checking next time around
      cacheLastRequest = $.extend( true, {}, request );
 
      if ( ajax ) {
        // Need data from the server
        if ( requestStart < cacheLower ) {
			if(requestLength == 25){
				requestStart = requestStart - (requestLength*(1));
			}else if(requestLength == 50){
				requestStart = requestStart - (requestLength*(0));
			}else{
				requestStart = requestStart - (requestLength*(conf.pages-1));
			}
          if ( requestStart < 0 ) {
            requestStart = 0;
          }
        }
        cacheLower = requestStart;
		if(requestLength == 25){
			cacheUpper = requestStart + (requestLength * 2);
		}else if(requestLength == 50){
			cacheUpper = requestStart + (requestLength * 1);
		}else{
			cacheUpper = requestStart + (requestLength * conf.pages);
		}
        request.start = requestStart;
		if(requestLength == 25){
			request.length = requestLength*2;
		}else if(requestLength == 50){
			request.length = requestLength*1;
		}else{
			request.length = requestLength*conf.pages;
		}
        // Provide the same `data` options as DataTables.
        if ( $.isFunction ( conf.data ) ) {
          // As a function it is executed with the data object as an arg
          // for manipulation. If an object is returned, it is used as the
          // data object to submit
          var d = conf.data( request );
          if ( d ) {
            $.extend( request, d );
          }
        } else if ( $.isPlainObject( conf.data ) ) {
          // As an object, the data given extends the default
          $.extend( request, conf.data );
        }
 
        settings.jqXHR = $.ajax( {
          type: conf.method,
          "url": conf.url,
          "data": request,          
          dataType: "json",
          "cache":    false,
                   
          success: function ( json ) {
            cacheLastJson = $.extend(true, {}, json);

            if ( cacheLower != drawStart ) {
              json.data.splice( 0, drawStart-cacheLower );
            }
            json.data.splice( requestLength, json.data.length );
            drawCallback( json );
          }
        } );
      } else {
        json = $.extend( true, {}, cacheLastJson );
        json.draw = request.draw; // Update the echo for each response
        json.data.splice( 0, requestStart-cacheLower );
        json.data.splice( requestLength, json.data.length );
        drawCallback( json );
      }
    }
  };  
 
// Register an API method that will empty the pipelined data, forcing an Ajax
// fetch on the next draw (i.e. `table.clearPipeline().draw()`)
$.fn.dataTable.Api.register( 'clearPipeline()', function () {
    return this.iterator( 'table', function ( settings ) {
        settings.clearCache = true;
    } );
} );


</script>
</head>

<body>
<%@include file="/WEB-INF/views/includes/app_header.jsp" %>
<div class="bodywrap">
	<div class="bodybreadcrumb" id="breadcrumb" style="display:block">
		<div class="container">
			<div class="row">
				<div class="col-sm-12">
					<div class="navbar-left">
						<ul>
							<li class="nav-item" id="gst_api">
								<a class="nav-link urllink active" id="gstapi" href="${contextPath}/dashboard?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&hour=Hour&hdvalue=1&usertype=<c:out value="${usertype}"/>&dashboardType=gst">GST APIs Dashboard</a>
							</li>
							<li class="nav-item" id="eway_api">
								<a class="nav-link urllink"  href="${contextPath}/dashboard?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&hour=Hour&hdvalue=1&usertype=<c:out value="${usertype}"/>&dashboardType=eway" id="ewaybillapi">e-Way Bill APIs Dashboard</a>
							</li>
							<li class="nav-item" id="einv_api">
								<a class="nav-link urllink"  href="${contextPath}/dashboard?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&hour=Hour&hdvalue=1&usertype=<c:out value="${usertype}"/>&dashboardType=einv" id="einvapi">e-Invoice APIs Dashboard</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
  <div class="db-inner" style="padding-top:50px">
    <div class="dashboard-inner">
		<div class="db-inner-txt-wrap row">
			<div class="col-sm-12">
				<div class="db-right-side-text">
					<div class="clearfix">
						<div class="pull-left">
							<c:choose>
							<c:when test="${dashboardType eq 'gst'}">
								<h3>GST APIs Usage Dashboard</h3>
							</c:when>
							<c:when test="${dashboardType eq 'eway'}">
								<h3>EWAY BILL APIs Usage Dashboard</h3>
							</c:when>
							<c:when test="${dashboardType eq 'einv'}">
								<h3>E-INVOICE APIs Usage Dashboard</h3>
							</c:when>
							<c:otherwise>
								<h3>Usage of GST APIs</h3>
							</c:otherwise>
						</c:choose>	
							
						</div>
						<select class="pull-right" name="financialYear" id="financialYear">
							<option value="2022">2022</option>
							<option value="2021">2021</option>
							<option value="2020">2020</option>
							<option value="2019">2019</option>
							<option value="2018">2018</option>
							<option value="2017">2017</option>
						</select>
						<h4 class="hdrtitle1">Year : </h4>
						
					</div>
					
					<!--- breadcrumb end -->
					<div class="row mb-3">
						<div class="col-md-3 col-sm-12">
						  <div class="db-box"> <span class="db-box-title"> Today Traffic</span> <span class="pull-right"><span class="db-box-sm-txt">Request / sec</span> <span class="db-box-count" id="requestPerSecond"><c:out value="${requestPerSecond}" /></span></span> </div>
						</div>
						<div class="col-md-3 col-sm-12">
						  <div class="db-box red"> <span class="db-box-title"> Today Errors</span> <span class="pull-right"><span class="db-box-sm-txt">Request / sec</span> <span class="db-box-count" id="failCounter"><c:out value="${failCounter}" /></span></span> </div>
						</div>
						<div class="col-md-3 col-sm-12">
						  <div class="db-box green"> <span class="db-box-title"> Yearly API Usage</span> <span class="pull-right"><span class="db-box-sm-txt">Available  / Used <span class="db-box-count" id="apiData"><c:if test="${not empty avlInvoices}"><c:out value="${avlInvoices}" /></c:if><c:if test="${empty avlInvoices}">-</c:if>/<c:if test="${not empty usedInvoices}"><c:out value="${usedInvoices}" /></c:if><c:if test="${empty usedInvoices}">-</c:if></span></span> </div>
						</div>
						<div class="col-md-3 col-sm-12">
						  <div class="db-box darkblue"> <span class="db-box-title"> Yearly Billing</span> <span class="pull-right"><span class="db-box-sm-txt">Available ( <i class="fa fa-rupee"></i> ) / Used  ( <i class="fa fa-rupee"></i> )<span class="db-box-count" id="billingData"><c:if test="${not empty avlBilling}"><c:out value="${avlBilling}" /></c:if><c:if test="${empty avlBilling}">-</c:if>/<c:if test="${not empty usedBilling}"><c:out value="${usedBilling}" /></c:if><c:if test="${empty usedBilling}">-</c:if></span> </span></div>
						</div>
					</div>
					<div class="dashboard-inner-txt">
					
					
  <div class="db-asp-wrap">
  
	<div style="height:35px;background-color: #f6f9fb;">
	<div>					

<ul class="nav nav-tabs nav-justified" role="tablist" id="asp-months" style="width:100%;color:#707172; font-size:13px;border-bottom: 1px solid #ddd;">
 <li class="nav-item" style="margin-top: 7px;font-size: 14px;color: green;">Months</li>
 <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab1" onClick="fetchFilingStatus('1')" href="#" role="tab">January </a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab2" onClick="fetchFilingStatus('2')" href="#" role="tab">February</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab3" onClick="fetchFilingStatus('3')" href="#" role="tab">March</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab4" onClick="fetchFilingStatus('4')" href="#" role="tab">April</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab5" onClick="fetchFilingStatus('5')" href="#" role="tab">May</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab6" onClick="fetchFilingStatus('6')" href="#" role="tab" aria-expanded="false">June</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab7" onClick="fetchFilingStatus('7')" href="#" role="tab" aria-expanded="true">July</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab8" onClick="fetchFilingStatus('8')" href="#" role="tab">August</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab9" onClick="fetchFilingStatus('9')" href="#" role="tab">September</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab10" onClick="fetchFilingStatus('10')" href="#" role="tab">October</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab11" onClick="fetchFilingStatus('11')" href="#" role="tab">November</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="mtab12" onClick="fetchFilingStatus('12')" href="#" role="tab">December</a></li>
</ul>
  <div class="tab-content">
  
	<div class="tab-pane active" id="dashtab1" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab2" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab3" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab4" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab5" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab6" role="tabpanel"><p></p></div>
	<div class="tab-pane" id="dashtab7" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab8" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab9" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab10" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab11" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab12" role="tabpanel"><p></p></div>
  
</div>
</div>
	</div>		
						<!-- db-box begin -->
		   <div class="row">
		   	<!-- GST info start -->
				<div class="col-sm-12"><div class="aspdateinfo">
				<ul class="ulist" style="padding-left:0;height:24px;">
				<li style="text-align: center;font-size: 14px;color:blue;"> Used APIs</li>
				<c:choose>
				<c:when test="${dashboardType eq 'gst'}">
					<li class="gstmeteringcount" id="Production1"></li>
					<li class="gstmeteringcount" id="Production2"></li>
					<li class="gstmeteringcount" id="Production3"></li>
					<li class="gstmeteringcount" id="Production4"></li>
					<li class="gstmeteringcount" id="Production5"></li>
					<li class="gstmeteringcount" id="Production6"></li>
					<li class="gstmeteringcount" id="Production7"></li>
					<li class="gstmeteringcount" id="Production8"></li>
					<li class="gstmeteringcount" id="Production9"></li>
					<li class="gstmeteringcount" id="Production10"></li>
					<li class="gstmeteringcount" id="Production11"></li>
					<li class="gstmeteringcount" id="Production12"></li>
				</c:when>
				<c:when test="${dashboardType eq 'eway'}">
					<li class="gstmeteringcount" id="EwayBillProduction1"></li>
					<li class="gstmeteringcount" id="EwayBillProduction2"></li>
					<li class="gstmeteringcount" id="EwayBillProduction3"></li>
					<li class="gstmeteringcount" id="EwayBillProduction4"></li>
					<li class="gstmeteringcount" id="EwayBillProduction5"></li>
					<li class="gstmeteringcount" id="EwayBillProduction6"></li>
					<li class="gstmeteringcount" id="EwayBillProduction7"></li>
					<li class="gstmeteringcount" id="EwayBillProduction8"></li>
					<li class="gstmeteringcount" id="EwayBillProduction9"></li>
					<li class="gstmeteringcount" id="EwayBillProduction10"></li>
					<li class="gstmeteringcount" id="EwayBillProduction11"></li>
					<li class="gstmeteringcount" id="EwayBillProduction12"></li>
				</c:when>
				<c:otherwise>
					<li class="gstmeteringcount" id="EInvoiceProduction1"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction2"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction3"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction4"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction5"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction6"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction7"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction8"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction9"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction10"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction11"></li>
					<li class="gstmeteringcount" id="EInvoiceProduction12"></li>
				</c:otherwise>
				</c:choose>
				</ul>
				
				
				</div></div>
				<!-- GST info end -->
				  </div>
					
					
						<!-- Nav tabs begin -->
						<div class="clearfix db-nav-tabs-wrap" style="margin-top: -21px;">
							<div class="selhr-txt">Date</div>
							<!--<div class="selhr-txt" id="alldates">All</div>-->
							<ul class="nav nav-tabs nav-justified" role="tablist" id="idTabList">
							</ul>
						</div>
						
						<!-- Nav tabs end -->
						<div class="clearfix"></div>
						<!-- Tab panes  begin -->
						<div class="tab-content" id="tab-content">
						</div>
						<!-- Tab panes  end -->
					
			</div>
		</div>
	</div>
</div>
</div>
<!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->

</body>
<script type="text/javascript">
 
$('#financialYear').change(function() {
	$('.gstmeteringcount').html(0);
	var finYear = $(this).val();
	$('#mtab'+currMonth).addClass('active');
	fetchFilingStatus(currMonth);
	$.ajax({
		url: "${contextPath}/mdfymonthlyinvoiceusage?year="+finYear+"&userId=<c:out value="${id}"/>",
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(summary) {
			$.each(summary, function(key, value){
				$('#'+key).html(value);								
			});
		}
	});
});
</script>
</html>
