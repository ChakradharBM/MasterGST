/*
filedrag.js - HTML5 File Drag & Drop demonstration
Featured on SitePoint.com
Developed by Craig Buckler (@craigbuckler) of OptimalWorks.net
*/
(function() {

	var secondcb = $id("idSheet6");
	// getElementById
	function $id(id) {
		return document.getElementById(id);
	}


	// output information
	function Output(msg) {
		
		var m = $id("messages6");
		m.innerHTML = msg + m.innerHTML;
	}


	// file drag hover
	function FileDragHover(e) {
		e.stopPropagation();
		e.preventDefault();
		$id("customerFile").className = (e.type == "dragover" ? "hover" : ".xls");
		
	}


	// file selection
	function FileSelectHandler(e) {
	  
		// cancel event and hover styling
		FileDragHover(e);
		// fetch FileList object
		//var files = e.target.files || e.dataTransfer.files;
//alert(files);
		// process all File objects
		if(e.target.files){
		for (var i = 0, f; f = e.target.files[i]; i++) {
			ParseFile(f);
		}
		}
		if(e.dataTransfer.files){
			for (var i = 0, f; f = e.dataTransfer.files[i]; i++) {
			$("#customerFile").prop("files", e.dataTransfer.files);
			$('#idSheet6').show();
			
			}
			Output(e.dataTransfer.files[0].name);
		}
		var typexls = e.type.match('.xls');
		/*if(!typexls){ 
			alert("Please upload xls file");
		}else{
			alert("You upload xls file");
			}*/
		
	}

 
	// output file information
	function ParseFile(file) {
		secondcb.style.display = "block";
		var x = document.getElementById('customerFile').value;
		Output(
			 "<p><span><strong><a href='"+ x +"'> <span class='sm-img'></span>" + file.name +"</span></strong> </a>"+
			//"<span>type: <strong>" + file.type +"</span></strong>"+
			//" <span>size: <strong>" + file.size +" bytes</span></strong>"+
			"</p>"
		);
	}


	// initialize
	function Init() {

		var fileselect = $id("customerFile"),
			filedrag = $id("filedragi"),
			submitbutton = $id("submitbutton6");

		// file select
		fileselect.addEventListener("change", FileSelectHandler, false);

		// is XHR2 available?
		var xhr = new XMLHttpRequest();
		var parser = new DOMParser();
		//var doc = parser.parseFromString(xhr, "application/xls");
		if (xhr.upload) {
			//filedrag.setRequestHeader("Content-Type", "text/xls");
			// file drop
			filedrag.addEventListener("dragover", FileDragHover, false);
			filedrag.addEventListener("dragleave", FileDragHover, false);
			filedrag.addEventListener("drop", FileSelectHandler, false);
			filedrag.style.display = "block";
			
			// remove submit button
			//submitbutton.style.display = "none";
			
		}

	}

	// call initialization file
	if (window.File && window.FileList && window.FileReader) {
		Init();
	}


})();

