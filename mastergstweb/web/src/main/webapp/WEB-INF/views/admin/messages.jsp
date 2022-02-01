<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Messages</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/dashboards.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/editor.css">
<script src="${contextPath}/static/mastergst/js/jquery/editor.js"></script>
<style type="text/css">
button.btn.btn-blue-dark.pull-right{padding: 7px;}
.modal-dialog{overflow-y: initial !important}
.modal-body{height: auto;overflow-y: auto;}
.modal-content{height:100%}
.fa-eye:before {content: "\f06e";font-family: FontAwesome;}
body.modal-open {overflow: hidden;}
#previewModal .modal-body{overflow-x:hidden}
a.btn.btn-default.dropdown-toggle{    padding: 2px;}
a.btn.btn-default{    padding: 5px;}
</style>
<script type="text/javascript">
var msgTab;var upTab;var newsTab;
var selRow;
function addMessage(){
	var content = msgData();
	if(content == 'error'){
		return;
	}
	$.ajax({
			url: '${contextPath}/message', 
			data: content,
			method: 'POST',
			contentType : 'application/json',
			cache : false,
			success: function(result, textSt, xhr){
				msgTab.row.add(result).draw(false);
				$('.canclbtn').click();
			},
			error: function(result){}
		});
}

function updateMessage(){
	var content = msgData();
	if(content == 'error'){
		return;
	}
	var msgId = $('#msgId').val();
	$.ajax({
			url: '${contextPath}/message/'+msgId, 
			data: content,
			method: 'POST',
			contentType : 'application/json',
			cache : false,
			success: function(result, textSt, xhr){
				msgTab.row(selRow).data(result).draw(false);
				$('.canclbtn').click();
			},
			error: function(result){}
		});
}

function msgData(){
	var userTypes= [];
	var isValid = true;
	$.each($("input[name='usertype']:checked"), function(){
		userTypes.push($(this).val());
	});
	var megObj = new Object;
	megObj.message = $('#message').val();
	if(megObj.message == ''){
		$('#message_Msg').html('Please enter Message');
		isValid = false;
	}
	megObj.subject = $('#subject').val();
	if(megObj.subject == ''){
		$('#subject_Msg').html('Please enter Subject');
		isValid = false;
	}
	if(userTypes.length == 0){
		$('#usertype_Msg').html('Please select User Type');
		isValid = false;
	}
	megObj.usertype = userTypes;
	return isValid ? JSON.stringify(megObj) : 'error';
}
function addUpdate(){
	var content = upData();
	if(content == 'error'){
		return;
	}
	$.ajax({
			//url: '${contextPath}/updates/'+${id}+'/'+${fullname}+'/'+${usertype},
			url: '${contextPath}/updates',
			data: content,
			method: 'POST',
			contentType : 'application/json',
			cache : false,
			success: function(response){
				//location.reload(true);
				//$('#upTable tbody').append('<tr><td>'+response.createdDate+'</td><td>'+response.title+'</td><td>'+response.description+'</td></tr>');
				$('.canclupbtn').click();
				$('#latestUpdates').addClass('active').click();
				window.location.href = '${contextPath}/obtainmessages?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&msgtype=updatesType';
				
			},
			error: function(result){}
		});
}
function updateLatest(){
	var content = upData();
	if(content == 'error'){
		return;
	}
	var updateId = $('#updateId').val();
	$.ajax({
			url: '${contextPath}/updates/'+updateId,
			data: content,
			method: 'POST',
			contentType : 'application/json',
			cache : false,
			success: function(response){
				
				//location.reload(true);
				$('.canclupbtn').click();
				$('#latestUpdates').addClass('active').click();
				window.location.href = '${contextPath}/obtainmessages?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&msgtype=updatesType';
			},
			error: function(result){}
		});
}

function upData(){
	var isValid = true;
	var upObj = new Object;
	upObj.description = $('#textcontent').val();
	if(upObj.description == ''){
		$('#textArea_Msg').html('Please enter Notes');
		isValid = false;
	}
	upObj.title = $('#title').val();
	if(upObj.title == ''){
		$('#title_Msg').html('Please enter Title');
		isValid = false;
	}
	return isValid ? JSON.stringify(upObj) : 'error';
}
function addNews(){
	var content = newsData();
	if(content == 'error'){
		return;
	}
	$.ajax({
			url: '${contextPath}/news',
			data: content,
			method: 'POST',
			contentType : 'application/json',
			cache : false,
			success: function(response){
				//location.reload(true);
				//$('#newsTable tbody').append('<tr><td>'+response.createdDate+'</td><td>'+response.title+'</td><td>'+response.description+'</td></tr>');
				$('.canclnewsbtn').click();
				$('#latestNews').addClass('active').click();
				window.location.href = '${contextPath}/obtainmessages?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&msgtype=newsType';
			},
			error: function(result){}
		});
}
function updateNews(){
	var content = newsData();
	if(content == 'error'){
		return;
	}
	var newsId = $('#newsId').val();
	$.ajax({
			url: '${contextPath}/news/'+newsId, 
			data: content,
			method: 'POST',
			contentType : 'application/json',
			cache : false,
			success: function(response){
				//location.reload(true);
				$('.canclupbtn').click();
				$('#latestNews').addClass('active').click();
				window.location.href = '${contextPath}/obtainmessages?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>&msgtype=newsType';
			},
			error: function(result){}
		});
}

function newsData(){
	var isValid = true;
	var newsObj = new Object;
	newsObj.description = $('#newstextcontent').val();
	if(newsObj.description == ''){
		$('#newstextArea_Msg').html('Please enter Notes');
		isValid = false;
	}
	newsObj.title = $('#newstitle').val();
	if(newsObj.title == ''){
		$('#newstitle_Msg').html('Please enter Title');
		isValid = false;
	}
	return isValid ? JSON.stringify(newsObj) : 'error';
}
</script>
</head>
<body>
	<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>


	<div class="bodywrap" style="min-height: 500px;">
	<div class="bodybreadcrumb">
            <div class="container">
				<div class="row">
					<div class="col-sm-12">
						<div class="bdcrumb-tabs">
							<ul class="nav nav-tabs" role="tablist">
							  <li class="nav-item permissionCREATE_MESSAGES"> <a class="nav-link active" data-toggle="tab" id="latestMessages" href="#messages">Messages </a> </li>
							  <li class="nav-item permissionLATESTUPDATES"> <a class="nav-link" data-toggle="tab" id="latestUpdates" href="#latestUp">Latest Updates </a> </li>
							  <li class="nav-item permissionLATESTNEWS"> <a class="nav-link" data-toggle="tab" id="latestNews" href="#news">News</a> </li>
							</ul>
						</div>
					</div>
				</div>
			</div>
        </div>
        <div class="tab-content" style="padding-top: 100px;">
   		 <div id="messages" class="tab-pane fade in active show">
		<div class="db-ca-wrap mt-4" style="padding-top:0px!important;margin-top:0px!important">
			<div class="container">
				<div class="row">
					<div class="col-md-12 col-sm-12">
						<div class="customtable db-ca-view tabtable1">
							<table id="msgTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
									<th>Date</th>
									<th>Subject</th>
									<th>Message</th>
									<th>User Type</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
					</div>

				</div>
			</div>
		</div>
		</div>
		<div id="latestUp" class="tab-pane fade">
		<div class="db-ca-wrap mt-4" style="padding-top:0px!important;margin-top:0px!important">
			<div class="container">
				<div class="row">
					<div class="col-md-12 col-sm-12">
						<div class="customtable db-ca-view latestUpTable">
							<table id="upTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
									<th>Date</th>
									<th>Update Title</th>
									<th>Update Notes</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
					</div>

				</div>
			</div>
		</div>
		</div>
		<div id="news" class="tab-pane fade">
		<div class="db-ca-wrap mt-4" style="padding-top:0px!important;margin-top:0px!important">
			<div class="container">
				<div class="row">
					<div class="col-md-12 col-sm-12">
						<div class="customtable db-ca-view latestnewsTable">
							<table id="newsTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
								<thead>
									<tr>
									<th>Date</th>
									<th>News Title</th>
									<th>News Notes</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
					</div>

				</div>
			</div>
		</div>
		</div>
		</div>
	</div>
     <div class="modal fade" id="editModal" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md modal-right" role="document" style="height:100%">
            <div class="modal-content">
			    <div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
			            <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
			    </button>
			    <div class="bluehdr" style="width:100%">
			          <h3>New Message</h3>
			    </div>
				</div>
      <div class="modal-body meterialform popupright bs-fancy-checks" style="height:100%">
        <!-- row begin -->
        <div class="row  p-5">
          <div class="form-group col-md-12 col-sm-12">
            <p class="lable-txt mb-4">User Group</p>
            <span class="errormsg" id="usertype_Msg"></span>
            <div class="form-check form-check-inline">
              <input class="form-check-input msgformele" type="checkbox" id="asp" name="usertype" value="aspdeveloper" />
              <label for="asp"><span class="ui"></span> </label>
              <span class="labletxt">ASP</span> </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input msgformele" type="checkbox" id="ca" name="usertype" value="cacmas" />
              <label for="ca"><span class="ui"></span> </label>
              <span class="labletxt">CA</span> </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input msgformele" type="checkbox" id="smallMedium" name="usertype" value="taxp" />
              <label for="smallMedium"><span class="ui"></span> </label>
              <span class="labletxt">Tax Professionals</span> </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input msgformele" type="checkbox" id="smallMedium" name="usertype" value="business" />
              <label for="smallMedium"><span class="ui"></span> </label>
              <span class="labletxt">Small & Medium</span> </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input msgformele" type="checkbox" id="Enterprise" name="usertype" value="enterprise" />
              <label for="Enterprise"><span class="ui"></span> </label>
              <span class="labletxt">Enterprise</span> </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input msgformele" type="checkbox" id="SuvidhaKendra" name="usertype" value="suvidha" />
              <label for="SuvidhaKendra"><span class="ui"></span> </label>
              <span class="labletxt">Suvidha Kendra</span> </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input msgformele" type="checkbox" id="Partners" name="usertype" value="partner" />
              <label for="Partners"><span class="ui"></span> </label>
              <span class="labletxt">Partners</span> </div>
          </div>
          <div class="bdr-b col-12 mb-3">&nbsp;</div>
          <div class="form-group col-md-12 col-sm-12">
            <p class="lable-txt">Subject</p>
            <span class="errormsg" id="subject_Msg"></span>
            <input type="text" class="msgformele" id="subject" name="subject" required="required" placeholder="message subject" value="" />
            <input type="hidden" id="msgId" name="msgId" value="" />
            <label for="input" class="control-label"></label>
            <i class="bar"></i> </div>
          <div class="form-group col-md-12 col-sm-12">
            <p class="lable-txt">Message Description </p>
            <span class="errormsg" id="message_Msg"></span>
            <textarea  id="message" class="msgformele" name="message" required="required" placeholder="message description"></textarea>
            <label for="input" class="control-label"></label>
            <i class="bar"></i> </div>
          <div class="bdr-b col-12">&nbsp;</div>
        </div>
        <!-- row end -->
      </div>
      <div class="modal-footer text-center" style="display:block">
      <div class="col-12 text-center mt-3"><a href="#" class="btn btn-blue-dark msgsbtn" aria-label="Close" onclick="submitMessage()">Create</a> <a href="#" class="btn btn-blue-dark ml-2 canclbtn" data-dismiss="modal" aria-label="Close">Cancel</a> </div>
      </div>
    </div>
  </div>
</div>
<div class="modal fade modal-right" id="editupModal" tabindex="-1" role="dialog" aria-labelledby="editupModal" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document" style="    height: 100%;">
    <div class="modal-content">
     <div class="modal-header p-0">
       <button type="button" class="close mr-2" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
        <div class="bluehdr" style="width:100%"><h3>New Updates</h3></div>
        </div>
          <div class="modal-body meterialform popupright bs-fancy-checks" style="overflow-x: hidden;">
        <!-- row begin -->
        <div class="row p-4">
         <ul class="nav nav-tabs col-md-12 pl-3" role="tablist" id="tabsactive">
					<li class="nav-item"><a class="nav-link active" id="draft" data-toggle="tab" href="#drafts" role="tab">Draft</a></li>
					<li class="nav-item"><a class="nav-link " id="preview" data-toggle="tab" href="#preview_mode" role="tab">Preview</a></li>
			  </ul>
				<div class="tab-content col-md-12 mb-3 mt-1">
					<div class="tab-pane active col-md-12" id="drafts" role="tabpane1" >
        	<div class=" col-md-12">
        	<!-- <button type="button" class="btn btn-blue-dark pull-right" onclick="previewPopup()" style="color: white;border: none;margin-top: -8px;"><i class="fas fa-eye pr-2" ></i>preview</button> -->
        	</div>
          <div class="form-group col-md-12 col-sm-12">
            <p class="lable-txt">Update Summary</p>
            <span class="errormsg" id="title_Msg"></span>
            <input type="text" class="upformele" id="title" name="title" required="required" placeholder="Update Title" value="" onkeyup="mypreviewfun('update')" />
            <input type="hidden" id="updateId" name="updateId" value="" />
            <label for="input" class="control-label"></label>
            <i class="bar"></i> </div>
          <div class="form-group col-md-12 col-sm-12">
            <p class="lable-txt" style="display: inline;">Update Notes</p>
            <span class="errormsg" id="textArea_Msg"></span>
            <textarea  id="textcontent" class="hidden" name="description" placeholder="message description" style="display:none"></textarea>
            <div id="editor_code" data-target="content" class="form-control"></div>
            <label for="input" class="control-label"></label>
          </div>
          </div>
          <div class="tab-pane col-md-12 mt-0" id="preview_mode" role="tabpane2" style="width:763px">
          <div class="row  p-2">
          <div class="form-group col-md-12 col-sm-12">
          <h5 class="m-0"><span id="update_summary_text"></span></h5>
          </div>
          <div class="form-group col-md-12 col-sm-12" class="mb-2 mr-3" style="font-size: 13px;color: slategray;">
           <span id="update_notes_text"></span>
           </div>
       	 </div>
          </div>
          </div>
        </div>
        <!-- row end -->
     </div>
      <div class="modal-footer text-center"><button type="submit" class="btn btn-blue-dark upbtn" aria-label="Close" onclick="submitUpdate()">Create</button> <a href="#" class="btn btn-blue-dark ml-2 canclupbtn" data-dismiss="modal" aria-label="Close">Cancel</a> </div>
    </div>
  </div>
</div>
<div class="modal fade modal-right" id="editnewsModal" tabindex="-1" role="dialog" aria-labelledby="editnewsModal" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document" style="    height: 100%;">
    <div class="modal-content">
     <div class="modal-header p-0">
       <button type="button" class="close mr-2" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
        <div class="bluehdr" style="width:100%"><h3>Create News</h3></div>
        </div>
          <div class="modal-body meterialform popupright bs-fancy-checks" style="overflow-x: hidden;">
        <!-- row begin -->
        <div class="row p-4">
         <ul class="nav nav-tabs col-md-12 pl-3" role="tablist" id="tabsactive">
					<li class="nav-item"><a class="nav-link active" id="newsdraft" data-toggle="tab" href="#newsdrafts" role="tab">Draft</a></li>
					<li class="nav-item"><a class="nav-link " id="newspreview" data-toggle="tab" href="#newspreview_mode" role="tab">Preview</a></li>
			  </ul>
				<div class="tab-content col-md-12 mb-3 mt-1">
					<div class="tab-pane active col-md-12" id="newsdrafts" role="tabpane1" >
        	<div class=" col-md-12">
        	<!-- <button type="button" class="btn btn-blue-dark pull-right" onclick="previewPopup()" style="color: white;border: none;margin-top: -8px;"><i class="fas fa-eye pr-2" ></i>preview</button> -->
        	</div>
          <div class="form-group col-md-12 col-sm-12">
            <p class="lable-txt">News Title</p>
            <span class="errormsg" id="newstitle_Msg"></span>
            <input type="text" class="newsformele" id="newstitle" name="title" required="required" placeholder="News Title" value="" onkeyup="mypreviewfun('news')" />
            <input type="hidden" id="newsId" name="newsId" value="" />
            <label for="input" class="control-label"></label>
            <i class="bar"></i> </div>
          <div class="form-group col-md-12 col-sm-12">
            <p class="lable-txt" style="display: inline;">News Notes</p>
            <span class="errormsg" id="newstextArea_Msg"></span>
            <textarea  id="newstextcontent" class="hidden" name="description" placeholder="News description" style="display:none"></textarea>
            <div id="newseditor_code" data-target="content" class="form-control"></div>
            <label for="input" class="control-label"></label>
          </div>
          </div>
          <div class="tab-pane col-md-12 mt-0" id="newspreview_mode" role="tabpane2" style="width:763px">
          <div class="row  p-2">
          <div class="form-group col-md-12 col-sm-12">
          <h5 class="m-0"><span id="news_summary_text"></span></h5>
          </div>
          <div class="form-group col-md-12 col-sm-12" class="mb-2 mr-3" style="font-size: 13px;color: slategray;">
           <span id="news_notes_text"></span>
           </div>
       	 </div>
          </div>
          </div>
        </div>
        <!-- row end -->
     </div>
      <div class="modal-footer text-center"><button type="submit" class="btn btn-blue-dark newsbtn" aria-label="Close" onclick="submitnews()">Create</button> <a href="#" class="btn btn-blue-dark ml-2 canclnewsbtn" data-dismiss="modal" aria-label="Close">Cancel</a> </div>
    </div>
  </div>
</div>
	<!-- footer begin here -->
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->

</body>
<script type="text/javascript">
function formatUpdatedDate(dat, type, row){
	var createdDt = new Date(dat.updatedDate) ;
    var month = createdDt.getUTCMonth() + 1; 
	var day = createdDt.getUTCDate();
	var year = createdDt.getUTCFullYear();
	return day+'-'+month+'-'+year;
}
$(document).ready(function(){
	var msgtype = '${msgtype}';
	if(msgtype == 'updatesType'){
		$('#latestUpdates').click();
	}else if(msgtype == 'newsType'){
		$('#latestNews').click();
	}else{
		$('#latestMessages').click();
	}
	$('#messages').addClass('show').prop('aria-expanded','true');
	$('#msgs_lnk').addClass('active');
	msgTab = $('#msgTable').DataTable({
	     "ajax": {
	         url: "${contextPath}/messages",
	         type: 'GET'
			 
	     },
	     "serverSide": true,
	     "paging": true,
	     "order": [[0,'desc']],
	     'pageLength':10,
        dom: '<"toolbar">frtip',
        columns: [
                  {data:formatUpdatedDate, name:"createdDate"},
                  {data:'subject', name:"subject"},
                  {data:'message', name:"message"},
                  {data:'usertype', 'orderable':false}
                  ]
	 });
	$('.tabtable1 div.toolbar').html('<h4 class="hdrtitle">Messages</h4><button class="btn btn-blue permissionCREATE_MESSAGES" type="button" id="createmsg_btn" onclick="createMsgPopup()">Create Message</button>');
	
	$('.tabtable1 tbody').on('click', 'tr', function(){
		var rowData = msgTab.row(this).data();
		selRow = this;
		createMsgPopup();
		$('#msgId').val(rowData.msgId);
		$('#subject').val(rowData.subject);
		$('#message').val(rowData.message);
		$.each(rowData.usertype, function(i, val){
			$('input:checkbox[value="'+val+'"]').prop('checked', true);
		});
		$('.msgsbtn').text('Update');
	});
		
	//$('#lupdates_lnk').addClass('active');
	
	
	
	upTab = $('#upTable').DataTable({
	     "ajax": {
	         url: "${contextPath}/getupdates",
	         type: 'GET'
			 
	     },
	     "serverSide": true,
	     "paging": true,
	     "order": [[0,'desc']],
	     'pageLength':10,
        dom: '<"toolbar">frtip',
        columns: [
                  {data:formatUpdatedDate, name:"createdDate"},
                  {data:'title', name:"title"},
                  {data:'description', name:"description"},
                  ]
	 });
	$('.latestUpTable div.toolbar').html('<h4 class="hdrtitle">Latest Updates</h4><button class="btn btn-blue" type="button" onclick="createUpdatePopup()">Create Update</button>');
	
	newsTab = $('#newsTable').DataTable({
	     "ajax": {
	         url: "${contextPath}/getnews",
	         type: 'GET'
			 
	     },
	     "serverSide": true,
	     "paging": true,
	     "order": [[0,'desc']],
	     'pageLength':10,
       dom: '<"toolbar">frtip',
       columns: [
                 {data:formatUpdatedDate, name:"createdDate"},
                 {data:'title', name:"title"},
                 {data:'description', name:"description"},
                 ]
	 });
	$('#newsTable_wrapper div.toolbar').html('<h4 class="hdrtitle">Latest News</h4><button class="btn btn-blue" type="button" onclick="createNewsPopup()">Create News</button>');
	
	$('.latestUpTable tbody').on('click', 'tr', function(){
		var rowData = upTab.row(this).data();
		selRow = this;
		createUpdatePopup();
		$('#updateId').val(rowData.updateId);
		$('#title').val(rowData.title);
		$('#textcontent').val(rowData.description);
		$('.Editor-editor').html(rowData.description);
		$('.upbtn').text('Update');
	});
	
	$('.latestnewsTable tbody').on('click', 'tr', function(){
		var rowData = newsTab.row(this).data();
		selRow = this;
		createNewsPopup();
		$('#newsId').val(rowData.newsId);
		$('#newstitle').val(rowData.title);
		$('#newstextcontent').val(rowData.description);
		$('.news-editor').html(rowData.description);
		$('.newsbtn').text('Update');
	});
	
	function formatUpdatedDate(dat, type, row){
		var createdDt = new Date(dat.updatedDate) ;
	    var month = createdDt.getUTCMonth() + 1; 
		var day = createdDt.getUTCDate();
		var year = createdDt.getUTCFullYear();
		return day+'-'+month+'-'+year;
	}
	$('.Editor-editor').attr('tabindex','0');
	
	});
	
function createMsgPopup(){
	resetEles();
	$('#msgId').val('');
	$('.msgsbtn').text('Create');
	$('#editModal').modal('show');
}

function resetEles(){
	$('.msgformele').each(function(){
		if($(this).attr('type') == 'checkbox'){
			$(this).prop('checked', false);
		}else{
			$(this).val('');
		}
	});
}

function submitMessage(){
	$('.errormsg').text('');
	if($('#msgId').val() ==''){
		addMessage();
	}else{
		updateMessage();
	}
}
	
	function createUpdatePopup(){
		$("#editor_code").Editor();
		$('.Editor-editor').attr('onkeyup','mypreviewfun("update")');
		$('.Editor-editor').addClass('update-editor');
		resetupEles();
		$('#updateId').val('');
		$('.upbtn').text('Create');
		$('#editupModal').modal('show');
	}
	function createNewsPopup(){
		$("#newseditor_code").Editor();
		$('.Editor-editor').attr('onkeyup','mypreviewfun("news")');
		$('.Editor-editor').addClass('news-editor');
		resetupEles();
		$('#newsId').val('');
		$('.newsbtn').text('Create');
		$('#editnewsModal').modal('show');
	}
	function resetupEles(){
		$('.upformele').each(function(){
			if($(this).attr('type') == 'checkbox'){
				$(this).prop('checked', false);
			}else{
				$(this).val('');
			}
		});
	}
	
	function submitUpdate(){
		$('.errormsg').text('');
		$('#textcontent').val($('.update-editor').html());
		if($('#updateId').val() ==''){
			addUpdate();
		}else{
			updateLatest();
		}
	}
	
		
	function mypreviewfun(type){
		if(type == 'update'){
			$('#update_summary_text').text($('#title').val());
			$('#textcontent').val($('.update-editor').html());
			 // ref textarea
		    textarea1 = document.getElementById('textcontent');
		    // where to view HTML code
		    viewHtml = document.getElementById('update_notes_text');
		    // Finally get HTML output in div
		    viewHtml.innerHTML = textarea1.value ;
			//$('#update_notes_text').text($('#textcontent').val());
		}else if(type == 'news'){
			$('#news_summary_text').text($('#newstitle').val());
			$('#newstextcontent').val($('.news-editor').html());	
			 textarea1 = document.getElementById('newstextcontent');
			    // where to view HTML code
			    viewHtml = document.getElementById('news_notes_text');
			    // Finally get HTML output in div
			    viewHtml.innerHTML = textarea1.value ;
				//$('#update_notes_text').text($('#textcontent').val());
		}
		
		
	}
	
	function submitnews(){
		$('.errormsg').text('');
		$('#newstextcontent').val($('.news-editor').html());
		if($('#newsId').val() ==''){
			addNews();
		}else{
			updateNews();
		}
	}
	
</script>

</html>