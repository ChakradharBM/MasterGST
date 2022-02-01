var contextPath = $('#__contextPath').val();
var _mapperFields = [];
var navListItems = $('div.setup-panel div a'),
          allWells = $('.setup-content'),
          allNextBtn = $('.nextBtn'),
  		  allPrevBtn = $('.prevBtn');

  allWells.hide();

	 

    navListItems.click(function (e) {	
       e.preventDefault();
      var $target = $($(this).attr('href')),
              $item = $(this);
		 	//  $item.parent().removeClass('active');
	//	 $('div.setup-panel div a.btn-primary').closest('div.stepwizard-step').addClass('active');
	 // $('div.setup-panel div.active a').css('pointer-events', 'none');
      if (!$item.hasClass('disabled')) {
		  navListItems.removeClass('btn-primary').addClass('btn-default');
          $item.addClass('btn-primary');
          allWells.hide();
          $target.show();
          $target.find('input:eq(0)').focus();
      }
  });
  
  allPrevBtn.click(function(){
      var curStep = $(this).closest(".setup-content"),
          curStepBtn = curStep.attr("id"),
          prevStepWizard = $('div.setup-panel div a[href="#' + curStepBtn + '"]').parent().prev().children("a");

          prevStepWizard.removeAttr('disabled').trigger('click');
  });

  allNextBtn.click(function(){
	  	var butVal = $(this).data('val');
	  if(butVal == 'first'){
		  loadMapperFiles(this);
	  }else if(butVal == 'second'){
		  previewMapper(this);
	  }else{
		  loadNextTab(this);
	  }
  });

  $('div.setup-panel div a.btn-primary').trigger('click');
  
 
 var pageTable =  $('#pageTable').DataTable({
      "dom": '<"toolbar">frtip',
      "paging": false,
      "responsive": true,
	  "info": false,
	  "searching": false,
	  "ordering": false,
	  "fixedColumns":true,
	  "columnDefs": [
	            {
	             "render": function ( data, type, row ) {
	            	 		return row+'<input type="hidden" class="mapnme" data-val="'+row+'" name="'+row+'_nme"  id="'+row+'_nme" value="'+row+'"/>';
	             		   }, 
	             "targets": 0
	         	} ,
	            {
	         		
	            	"render": function ( data, type, row ) {
	            		return '<img src="'+contextPath+'/static/mastergst/images/master/arrow-right.png" alt="True" class="img-fluid arrow-wd"> '+
                    	'<span class="action-img"><img src="'+contextPath+'/static/mastergst/images/master/false.png" alt="True" id="'+row+'_img"></span>'
                    	+'<img src="'+contextPath+'/static/mastergst/images/master/arrow-left.png" alt="True" class="img-fluid arrow-wd">';
                    	
	            	}, 
	            	"targets": 1
	            }, 
	            {
	            	
	            	"render": function ( data, type, row ) {
	            		return '<a class="pull-right" style="margin-top:7px" href="javascript:void" onclick="clearMappings(\''+row+'_val\')">clear</a>'+
	            		'<div><select class="form-control form-control-sm mapval pull-left" style="width:220px;max-width:240px;margin-right:20px;" data-val="'+row+'" name="'+row+'_val"  id="'+row+'_val"><option class="opt_0" data-opt="opt_0" value=""></option></select></div>'+
	            		'<a style="display:none; margin-right:20px; color:red;margin-top:7px" id="'+row+'_config_lnk" href="javascript:void" onclick="configureMappings(\''+row+'\')">Configure Now</a>';
	            	}, 
	            	"targets": 2
	            } 
	  
	  	],
		
  });
 
 
 var mapTable =  $('#mapTable').DataTable({
	 "dom": '<"toolbar">frtip',
	 "paging": false,
	 "responsive": true,
	 "info": false,
	 "searching": false,
	 "ordering": false,
	 "fixedColumns":true,
	 "columnDefs": [
	                {
	                	"width": "245px",
	                	"render": function ( data, type, row ) {
	                		return row.name+'<input type="hidden" class="mapnme" data-val="'+row.code+'" name="'+row.code+'_nme"  id="'+row.code+'_nme" value="'+row.name+'"/>';
	                	}, 
	                	"targets": 0
	                } ,
	                {
	                	
	                	"render": function ( data, type, row ) {
	                		return '<img src="'+contextPath+'/static/mastergst/images/master/arrow-right.png" alt="True" class="img-fluid"> '+
	                		'<span class="action-img"><img src="'+contextPath+'/static/mastergst/images/master/false.png" alt="True" id="'+row.code+'_img"></span>'
	                		+'<img src="'+contextPath+'/static/mastergst/images/master/arrow-left.png" alt="True" class="img-fluid">';
	                		
	                	}, 
	                	"targets": 1
	                }, 
	                {
	                	
	                	"render": function ( data, type, row ) {
	                		return '<a class="pull-right" href="javascript:void" onclick="clearMappings(\''+row.code+'_val\')">clear</a><select class="form-control form-control-sm mapval" style="width:240px;max-width:240px" data-val="'+row.code+'" name="'+row.code+'_val"  id="'+row.code+'_val"><option class="opt_0" data-opt="opt_0" value=""></option></select>';
	                	}, 
	                	"targets": 2
	                } 
	                
	                ],
	                
 });
 
 var previewTable =  $('#previewTable').DataTable({
	 "dom": '<"toolbar">frtip',
	 "paging": false,
	 "responsive": true,
	 "info": false,
	 "searching": false,
	 "ordering": false,
	 "columnDefs": [
		            {
		             "render": function ( data, type, row ) {
		            	 		return row.name;
		             		   }, 
		             "targets": 0
		         	} ,
		            {
		            	"render": function ( data, type, row ) {
		            		return '<img src="'+contextPath+'/static/mastergst/images/master/arrow-right.png" alt="True" class="img-fluid"> '+
	                    	'<span class="action-img"><img src="'+contextPath+'/static/mastergst/images/master/false.png" alt="True" id="'+row.code+'_preimg"></span>'+ 
	                    	'<img src="'+contextPath+'/static/mastergst/images/master/arrow-left.png" alt="True" class="img-fluid">';
		            	}, 
		            	"targets": 1
		            }, 
		            {
		            	"render": function ( data, type, row ) {
		            		return '<span id="'+row.code+'_val_preview"></span>';
		            	}, 
		            	"targets": 2
		            } 
		  
		  	],
	 
 });
	
  var dtable = $('#dbTable').DataTable({
	  "dom": '<"toolbar">frtip',
	  "paging": false,
	  "responsive": true,
	  "info": false,
	  "searching": false,
	  "ordering": false 
	  
  });
  
  var previousVal;
  var previousOptCd;
  
function initMapVal(){ 
	
	$('.mapval').on('focus', function(){
		previousVal = $(this).val();
		previousOptCd = $(this).find(":selected").data('opt');
	});
  $('.mapval').on('change', function(){
	  var code = $(this).data('val');
	  var value = $(this).val();
	  var optCd = $(this).find(":selected").data('opt');
	  var selObj =this;
	  if(previousVal != ''){
		  $('.mapval').each(function(){
			  if($(this).attr('id') != $(selObj).attr('id')){
				  $(this).append($(selObj).find('.'+previousOptCd).clone());
			  }
		  });
	  }
	  if(value == ''){
		  $('#'+code+'_img').attr('src', contextPath+'/static/mastergst/images/master/false.png');
		  $('#'+code+'_config_lnk').css('display','none');
	  }else{
		  $('.'+optCd).each(function(){
				 if($(this).parent().attr('id') != $(selObj).attr('id')){
					 $(this).remove()
				 }
			});
		  $(this).removeClass('focusbdr');
		  $('#'+code+'_img').attr('src', contextPath+'/static/mastergst/images/master/if_tick.png');
		 $('#'+code+'_config_lnk').css('display','block');
	  }
	  previousVal = value;
	  previousOptCd = optCd;
  });
 }

function initMapperFieldVals(){
    
    $('.mapfildval').on('focus', function(){
		previousVal = $(this).val();
		previousOptCd = $(this).find(":selected").data('opt');
	});
  $('.mapfildval').on('change', function(){
	  var code = $(this).data('val');
	  var value = $(this).val();
	  var optCd = $(this).find(":selected").data('opt');
	  var selObj =this;
	  if(previousVal != ''){
		  $('.mapfildval').each(function(){
			  if($(this).attr('id') != $(selObj).attr('id')){
				  $(this).append($(selObj).find('.'+previousOptCd).clone().removeAttr("selected"));
			  }else{
				  $(".mapfildval option[value='"+previousVal+"']").prop("selected","");
				  //$('option:selected', this).removeAttr('selected');
				 // $(this).append($(selObj).find('.'+previousOptCd).clone().removeAttr("selected"));
				 // $(".mapfildval option[value='"+previousVal+"']").find(":selected").removeAttr("selected");
			  }
		  });
	  }
	  if(value == ''){
		  $('#'+code+'_fildimg').attr('src', contextPath+'/static/mastergst/images/master/false.png');
	  }else{
		  $('.'+optCd).each(function(){
				 if($(this).parent().attr('id') != $(selObj).attr('id')){
					 $(this).remove()
				 }
			});
		  $(this).removeClass('focusbdr');
		  $('#'+code+'_fildimg').attr('src', contextPath+'/static/mastergst/images/master/if_tick.png');
	  }
	  previousVal = value;
	  previousOptCd = optCd;
  });
}
  


  function loadNextTab(obj){
	  $('div.setup-panel div a.btn-primary').closest('div.stepwizard-step').addClass('active');
      var curStep = $(obj).closest(".setup-content"),
          curStepBtn = curStep.attr("id"),
          nextStepWizard = $('div.setup-panel div a[href="#' + curStepBtn + '"]').parent().next().children("a"),
          curInputs = curStep.find("input[type='text'],input[type='url']"),
          isValid = true;

      $(".form-group").removeClass("has-error");
      for(var i=0; i<curInputs.length; i++){
          if (!curInputs[i].validity.valid){
              isValid = false;
              $(curInputs[i]).closest(".form-group").addClass("has-error");
          }
      }

      if (isValid)
          nextStepWizard.removeAttr('disabled').trigger('click');
  }
 
  function loadMapperFiles(obj){
	  $('.errormsg').html('');
	  var formdata = new FormData($('#MappingFileForm')[0]);
 	  formdata.delete('fileselect');
 	  var obj = $('#fileselect')[0];
 	  $.each(obj.files, function(j, ph){
 		 formdata.append('fileselect_'+j, ph);
 	  });
 	 var simplifiedOrDetail = $("input[name='simplifiedOrDetail']:checked").val();
 	 formdata.append('mapperName', $('#MapperName').val());
 	 formdata.append('mapperType', $("#MapperType").val());
 	 formdata.append('clientId', $('#clientId').val());
 	 formdata.append('userId', $('#userId').val());
 	 formdata.append('skipRows',$('#skipRows').val());
 	formdata.append('simplifiedOrDetail', simplifiedOrDetail);
 	  var uri = contextPath+'/addmapperfile';
 	  var filesize = $('#file_detail').text();
 	  if(filesize == ''){
	 	 $.ajax({
				url: uri, 
				data: formdata,
				method: 'POST',
				processData: false,
				contentType: false,
				enctype: 'multipart/form-data',
				success: function(result, textSt, xhr){
					if(result.mapperPages){
						pageTable.clear().draw();
						//previewTable.clear().draw();
						$.each(result.pages, function(i, page){
							pageTable.row.add(page).draw(false);
							//previewTable.row.add(field).draw(false);
						});
						var mapSel = $('.mapval');
						_mapperFields = result.mapperPages;
						$.each(result.mapperPages, function(i, page){
							mapSel.append('<option class="opt_'+(i+1)+'" data-opt="opt_'+(i+1)+'" value="'+page+'">'+page+'</option>');
						});
						initMapVal();
						loadNextTab(obj);
					}else{
						$.each(result, function(k, v){
							$('#'+k+'_Msg').html(v);
						});
					}
				},
				error: function(result){
					
				}
			});
 	  }
  }
  
  
  function configureMappings(pgCode){
	  var jObj = new Object;
	  var simplifiedOrDetail = $("input[name='simplifiedOrDetail']:checked").val();
	  jObj.pageCode = pgCode;
	  jObj.pageName=$('#'+pgCode+'_nme').val();
	  jObj.mappedPage= $('#'+pgCode+'_val').val();
	  jObj.mapperType =  $("#MapperType").val();
	  jObj.simplifiedOrDetail =  simplifiedOrDetail;
	  var mapperid = $('#MapperId').val();
	  var simplifiedOrDetail = simplifiedOrDetail;
	  var uri = contextPath+'/getpagefields';
	  if(mapperid != ''){
		  uri+='/'+mapperid;
	  }
	  $.ajax({
		  url: uri, 
		  data: JSON.stringify(jObj),
		  method: 'POST',
		  processData: false,
		  contentType: 'application/json',
		  success: function(result, textSt, xhr){
			  $('#model_cont').html(result);
			  initMapperFieldVals();
			  $('#viewModal').modal('show');
		  },
		  error: function(result){
			  
		  }
	  });
  }
  
  
  
  
  function loadMapperFiles1(obj){
	  $('.errormsg').html('');
	  formdata.append('mapperType', $("#MapperType").val());
	  formdata.append('clientId', $('#clientId').val());
	  formdata.append('userId', $('#userId').val());
	  var uri = contextPath+'/getpagefields'
	  $.ajax({
		  url: uri, 
		  data: formdata,
		  method: 'POST',
		  processData: false,
		  contentType: 'application/json',
		  success: function(result, textSt, xhr){
			  if(result.mapperFields){
				  mapTable.clear().draw();
				  previewTable.clear().draw();
				  $.each(result.fields, function(i, field){
					  mapTable.row.add(field).draw(false);
					  previewTable.row.add(field).draw(false);
				  });
				  var mapSel = $('.mapval');
				  _mapperFields = result.mapperFields;
				  $.each(result.mapperFields, function(i, field){
					  mapSel.append('<option class="opt_'+(i+1)+'" data-opt="opt_'+(i+1)+'" value="'+field+'">'+field+'</option>');
				  });
				  initMapVal();
				  loadNextTab(obj);
			  }else{
				  $.each(result, function(k, v){
					  $('#'+k+'_Msg').text(v);
				  });
			  }
		  },
		  error: function(result){
			  
		  }
	  });
  }
  
  function previewMapper(obj){
	 $('.mapval').each(function(){
		 var code = $(this).data('val');
		 var value = $(this).val();
		 if(value == ''){
			 $('#'+code+'_preimg').attr('src', contextPath+'/static/mastergst/images/master/false.png'); 
		 }else{
			 $('#'+code+'_val_preview').html(value); 
			 $('#'+code+'_preimg').attr('src', contextPath+'/static/mastergst/images/master/if_tick.png'); 
		 }
	 });
	 loadNextTab(obj);
  }
  
  function saveMapperFields(){
	  var data = {};
	  var isValid = true;
	  $('.mapfldnme').each(function(){
		  var code = $(this).data('val');
		  var value = $('#'+code+'_fildval').val();
		  data[$(this).val()]=value;
		  if(value == ''){
			  $('#'+code+'_fildval').addClass('focusbdr');
			  //isValid = false;
		  }
	  });
	  var discConfig = $("input[name='discConfig']:checked").val();
	  var pageCode = $('#pageCode').val();
	  var pageName = $('#'+pageCode+'_nme').val();
	  var mapperObj = {};
	  var discountObj = {};
	  discountObj[pageName] = discConfig;
	  mapperObj[pageName] = {'page': $('#mappedPage').val(), 'mappings': data};//, 'upMappedFields':remining};
	  var simplifiedOrDetail = $("input[name='simplifiedOrDetail']:checked").val();
	  var mapperData = {};
	  mapperData['mapperType'] = $("#MapperType").val();
	  mapperData['mapperName'] = $('#MapperName').val();
	  mapperData['clientId'] = $('#mapclientId').val();
	  mapperData['userId'] = $('#mapuserId').val();
	  mapperData['globaltemplate'] = $('#globaltemplate').val();
	  mapperData['skipRows'] = $('#skipRows').val();
	  mapperData['mapperConfig'] = mapperObj;
	  mapperData['discountConfig'] = discountObj;
	  mapperData['simplifiedOrDetail'] = simplifiedOrDetail;
	  var jsonData = JSON.stringify(mapperData);
	  var mapperid = $('#MapperId').val();
	  var uri = contextPath +'/submitmapper'
	  if(mapperid != ''){
		  uri+='/'+mapperid+'/'+isValid;
	  }
	  $.ajax({
			url: uri, 
			data: jsonData,
			method: 'POST',
			processData: false,
			contentType: 'application/json',
			success: function(result, textSt, xhr){
				$('#MapperId').val(result.id);
				$('#fild_modl_close').click();
				$('.saveallmappings').removeAttr("disabled");
				$('.saveallmappings').removeAttr("title");
				$('#'+pageCode+'_config_lnk').css("color","#008000").html("Configured");
			},
			error: function(result){
				
			}
		});
	  
  }
  
  function deleteMapper(mapperid, obj){
	 
	  var uri = contextPath +'/deletemapper/'+mapperid;
	  $.ajax({
		  url: uri, 
		  method: 'GET',
		  processData: false,
		  contentType: 'application/json',
		  success: function(result, textSt, xhr){
			  dtable.row($(obj).parents('tr')).remove().draw();
		  },
		  error: function(result){
			  
		  }
	  });
  }
  
function editMapper(mapperid){
	$('.saveallmappings').removeAttr("disabled");
	$('.saveallmappings').removeAttr("title");
	$('.cancelmapping').removeAttr("disabled");
	 var uri = contextPath+'/mapper'+'/'+mapperid
	 $.ajax({
			url: uri, 
			method: 'GET',
			processData: false,
			contentType: false,
			success: function(result, textSt, xhr){
				var mapperObj = result.mapper;
				var mapperConfig = mapperObj.mapperConfig;
				var mapperPageFields = mapperObj.mapperPageFields;
				$('#MapperName').val(mapperObj.mapperName);
				$('#MapperId').val(mapperid);
				$('#MapperType').val(mapperObj.mapperType);
				if(mapperObj.skipRows == null){
					mapperObj.skipRows = 1;
				}
				$('#skipRows').val(mapperObj.skipRows);
				if(mapperObj.globaltemplate == true || mapperObj.globaltemplate == 'true'){
					$('#globaltemplate').val('true');
					$('#globaltemplate').prop("checked",true);
				}else{
					$('#globaltemplate').val('false');
				}
				if(mapperObj.simplifiedOrDetail == null){
					mapperObj.simplifiedOrDetail = "Detailed";
				}
				if(mapperObj.simplifiedOrDetail == 'Detailed'){
					$('input[name=simplifiedOrDetail][value="Detailed"]').attr('checked', 'checked');
					$('input[name=simplifiedOrDetail][value="Simplified"]').removeAttr('checked');
				}else{
					$('input[name=simplifiedOrDetail][value="Detailed"]').removeAttr('checked');
					$('input[name=simplifiedOrDetail][value="Simplified"]').attr('checked', 'checked');
				}
				pageTable.clear().draw();
				$.each(result.pages, function(i, page){
					pageTable.row.add(page).draw(false);
				});
				
				var i=0;
				var mapperPageOptions ='';
				$.each(mapperPageFields, function(ky, val){
					mapperPageOptions += '<option class="opt_'+(i+1)+'" data-opt="opt_'+(i+1)+'" value="'+ky+'">'+ky+'</option>';
					i++;
				});
				$('.mapnme').each(function(){
					var name = $(this).val();
					var code = $(this).data('val');
					$('#'+code+"_val").append(mapperPageOptions);	
					var value = mapperConfig[name];
					if(value){
						
						//$('#'+code+"_val").append('<option class="opt_'+(i+1)+'" data-opt="opt_'+(i+1)+'" value="'+value.page+'">'+value.page+'</option>');
						$('#'+code+"_val").val(value.page);
						$('#'+code+'_img').attr('src', contextPath+'/static/mastergst/images/master/if_tick.png');
						$('#'+code+'_config_lnk').css('display','block').html("Configured");
						$('#'+code+'_config_lnk').css('color','#008000');
						i++;
					}else{
						$('#'+code+'_img').attr('src', contextPath+'/static/mastergst/images/master/false.png');
						$('#'+code+'_config_lnk').css('display','none');
					}
				});
				initMapVal();
				loadNextTab($('.first_nxt'));
				$('.edit_pre').attr('disabled', true);
				$('.mapviewModal-wrap').css('display', 'none');
				$('.subscriptwrap').css('display', 'block');
			},
			error: function(result){
				
			}
		});
}
/*function editMapper(mapperid){
	var uri = contextPath+'/mapper'+'/'+mapperid
	$.ajax({
		url: uri, 
		method: 'GET',
		processData: false,
		contentType: false,
		success: function(result, textSt, xhr){
			var mapperObj = result.mapper;
			var mapperConfig = mapperObj.mapperConfig;
			$('#MapperName').val(mapperObj.mapperName);
			$('#MapperId').val(mapperid);
			
			if(mapperObj.mapperType ==  'Sales'){
				document.getElementById('MapperType').checked=true;
			}else if(mapperObj.mapperType ==  'Purchages'){
				document.getElementById('MapperType1').checked=true;
			}
			mapTable.clear().draw();
			previewTable.clear().draw();
			$.each(result.fields, function(i, field){
				mapTable.row.add(field).draw(false);
				previewTable.row.add(field).draw(false);
			});
			//var mapSel = $('.mapval');
			//_mapperFields = result.mapperFields;
			var i=0;
				$.each(mapperConfig, function(key, value){
					mapSel.append('<option class="opt_'+(i+1)+'" data-opt="opt_'+(i+1)+'" value="'+value+'">'+value+'</option>');
					i++;
				});
			var i=0;
			$('.mapnme').each(function(){
				var name = $(this).val();
				var code = $(this).data('val');
				var value = mapperConfig[name];
				$('#'+code+"_val").append('<option class="opt_'+(i+1)+'" data-opt="opt_'+(i+1)+'" value="'+value+'">'+value+'</option>');
				$('#'+code+"_val").val(value);
				$('#'+code+'_img').attr('src', contextPath+'/static/mastergst/images/master/if_tick.png');
				i++;
			});
			initMapVal();
			loadNextTab($('.first_nxt'));
			$('.edit_pre').attr('disabled', true);
			$('.mapviewModal-wrap').css('display', 'none');
			$('.subscriptwrap').css('display', 'block');
		},
		error: function(result){
			
		}
	});
}
*/
function clearAllMappings(cls){
	$('.'+cls).each(function(){
		previousVal = $(this).val();
		previousOptCd = $(this).find(":selected").data('opt');
		$(this).val('').change();
	});
}

function clearMappings(selId){
	var selObj = $('#'+selId);
	previousVal = selObj.val();
	previousOptCd = selObj.find(":selected").data('opt');
	selObj.val('').change();
}

function globaltemplatecheckval(){
	if($('#globaltemplate').is(":checked")){
		$('#globaltemplate').val('true');
	}else{
		$('#globaltemplate').val('false');
	}
}
  