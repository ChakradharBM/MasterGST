<!-- Add Customer Modal Start -->
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<style>
#gstnnumber{text-transform: uppercase;}
.bnkdetails .meterialform .checkbox input:checked~.helper::before{height: 11px;width: 2px;left: 5px;top: 11px;}
.bnkdetails .meterialform .checkbox input:checked~.helper::after{height: 6px;top: 7px;left: 0px;}
.bnkdetails .meterialform .checkbox .helper{border-radius:4px;color: #337ab7;}
.dr_cr_drop{width: 49px!important;border: 1px solid lightgrey!important;margin-top: 6px;}
</style>
    <div class="modal fade" id="addLedgerModal" role="dialog" aria-labelledby="addLedgerModal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
        <div class="modal-dialog modal-md  modal-right" role="document">
            <div class="modal-content">
                <div class="modal-body meterialform popupright bs-fancy-checks">
                    <button type="button" class="close" aria-label="Close" onclick="closeAddCustomer('addLedgerModal')">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="invoice-hdr bluehdr">
                        <h3 id="addcust">Add Ledger</h3>
                    </div>
					<form method="POST" data-toggle="validator" id="ledgerForm" class="meterialform" name="ledgerForm" action="${contextPath}/cp_addsaveledgerdetails/${id}/${client.id}/${fullname}/${usertype}/${month}/${year}" modelAttribute="ledger">
                   <div class="row content p-5">
                	<p class="lable-txt astrich col-md-6" id="idledName" style="margin-top: 16px;">Ledger Name</p>
                	<div class="form-group mt-1 mb-1 col-md-6" id="bmsg">
                            <span class="errormsg" id="ledgerName_Msg"></span><span class="errormsg" id="addledgerName_Msg" style="margin-top:-16px;"></span>
                            <input type="text" id="addledgername" class="ledgername" name="ledgerName" required="required" onchange="updateLedgerDetails1()" data-error="Enter the ledger Name" placeholder="ledger Name" value="" />
                            <label for="name" class="control-label"></label>
							<div class="help-block with-errors"></div>
                            <i class="bar"></i>
                     </div>
                     <p class="lable-txt col-md-6 astrich" style="margin-top: 19px;">Under the Group/Sub-group of</p>
                     <div class="form-group mt-1 col-md-6 mb-6">
                            <span class="errormsg" id="headname_Msg"></span><span class="errormsg" id="addheadName_Msg" style="margin-top:-10px;"></span>
                            <input type="text" id="addgoslname" name="grpsubgrpName" data-error="Enter the group or subgroup name"  onchange="updateLedgerDetails1()" placeholder="Group or SubGroup"  value="" />
                            <label for="input" class="control-label"></label>
							<div class="help-block with-errors"></div>
							<div id="addGroupNameempty" style="display:none">
								<div class="ddbox">
								  <p>Search didn't return any results.</p>
								</div>
							</div>
                            <i class="bar"></i> 
                     </div>
                     <p class="lable-txt col-md-6" id="idledName" style="margin-top: 15px;">Opening Balance as on ( <span id="addledgerOpeningBalanceMonth"></span> )</p>
	                	 <div class="col-md-6 row">
	                	 <div class="form-group mb-1 col-md-10 pr-0">
	                           <input type="text" id="addledgerOpeningBalance" class="ledgerOpeningBalance update_Ledger" name="ledgerOpeningBalance" data-error="Enter the Opening Balance" placeholder="Opening Balance" value=""/>
	                            <label for="name" class="control-label"></label>
								<div class="help-block with-errors"></div>
	                            <i class="bar"></i>
	                     </div>
	                     <div class="form-group mb-1 col-md-1">
	                      <select class="dr_cr_drop" id="addledgerOpeningBalanceType"><option>Dr</option><option>Cr</option></select>
	                      </div>
	                     </div>
	                     <div>
		                     <a href="${contextPath}/about/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="booksStarting pl-3 mr-1" style="color:#344371;font-size:14px;">Click here </a><p style="font-size:14px;display: contents;">to Change the Opening Balance Date (or Go to Settings -> Profile -> Edit Client <span class="pl-3">and update the date)</span></p>                     
	                     </div>
	                     <h6 class="mt-3 addheadlpath ml-3" style="display:none;">Path :  <span class="text-left" id="addlpath"></span></h6>
                  </div>
                  
                  <div class="modal-footer" style="margin-top:300px;">
                  <input type="hidden" id="addledger_name"/> 
                   <input type="hidden" id="addsledger_name"/> 
                   <input type="hidden" id="addlpath_name"/> 
                   <input type="hidden" class="ledger_rowno" id="ledger_rowno" value="">
                    <input type="hidden" name="ledgerpath" id="addledgerpath_name"/> 
                   <input type="hidden" name="clientid" value="${client.id}">	
				   <a type="button" id="submitButton_ledger" class="btn btn-blue-dark" onclick="saveLedger('ledger')">Save Ledger</a>   
                   <button type="button" class="btn btn-blue-dark " onclick="closeAddCustomer('addLedgerModal')" aria-label="Close">cancel</button>       
                </div>
					</form>
                </div>
            </div>
        </div>
    </div>
	<script type="text/javascript">
	var options = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/groupslist/${client.id}?query="+ phrase + "&format=json";
			},
			getValue: "groupname",
			
			list: {
				match: {
					enabled: true
				},
				onChooseEvent: function() {
					var groupdetails = $("#addgoslname").getSelectedItemData();$('#addledger_name').val(groupdetails.name);$('#addsledger_name').val(groupdetails.userid);$('#addlpath_name').val(groupdetails.path);
					$('#addlpath').text(groupdetails.path+"/"+$('#addledgername').val());$('#addledgerpath_name').val(groupdetails.path+"/"+$('#addledgername').val());$('.addheadlpath').css('display','block');
				}, 
				onLoadEvent: function() {
					if($("#eac-container-addgoslname ul").children().length == 0) {
						$('#addGroupNameempty').show();
					} else {
						$('#addGroupNameempty').hide();
					}
				},
				maxNumberOfElements: 10
			},
		};
	$('#addgoslname').easyAutocomplete(options);
	
	$('#addledgername').keyup(function() {
		var spath = $("#addledgername").val();
		var path = $("#addlpath_name").val();
		var path = $('#addlpath').text(path+"/"+spath);
		$('#addledgerpath_name').val(path+"/"+spath);
		$('.addheadlpath').css('display','block');
	});
	function addledgerValidation(type){
		var ledgerName = $('#addledgername').val();
		var ledgerHeadName = $('#addgoslname').val();
		var c=0;
		if(type == 'ledger') {	
			if(ledgerName == "" || ledgerName == null){
				$('#addledgerName_Msg').text("Please Enter ledger Name").css("display","block"); 
				c++;
			}else{
				$('#addledgerName_Msg').text(""); 
			}
			if(ledgerHeadName =="" || ledgerHeadName == null){
				$('#addheadName_Msg').text("Please Enter group or subgroup name").css("display","block"); 
				c++;
			}else{
				$('#addheadName_Msg').text(""); 
			}
	}
		return c==0; 
	}
	function updateLedgerDetails1(){
		var ledgerName = $('#addledgername').val();
		var ldgrHeadName = $('#addgoslname').val();
		if(ledgerName == ''){
			$('#ledgerName_Msg').show();
		} else{
			$('#addledgerName_Msg').text(""); 
			$('#ledgerName_Msg').hide();
		}
		if(ldgrHeadName == '' || ldgrHeadName == null){
		$('#addheadName_Msg').show();
		} else{
			$('#headname_Msg').text(""); 
			$('#addheadName_Msg').hide();
		}
	}
	</script>
    <!-- Edit Modal End -->