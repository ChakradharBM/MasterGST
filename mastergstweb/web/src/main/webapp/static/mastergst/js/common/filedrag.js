/*
filedrag.js - HTML5 File Drag & Drop demonstration
Featured on SitePoint.com
Developed by Craig Buckler (@craigbuckler) of OptimalWorks.net
*/
(function() {

	var secondcb = $id("secondcb");
	// getElementById
	function $id(id) {
		return document.getElementById(id);
	}


	// output information
	function Output(msg) {
		var m = $id("messages");
		m.innerHTML = msg + m.innerHTML;
	}


	// file drag hover
	function FileDragHover(e) {
		e.stopPropagation();
		e.preventDefault();
		e.target.className = (e.type == "dragover" ? "hover" : ".xml");
		
	}


	// file selection
	function FileSelectHandler(e) {
	  
		// cancel event and hover styling
		FileDragHover(e);
		
		// fetch FileList object
		var files = e.target.files || e.dataTransfer.files;

		// process all File objects
		for (var i = 0, f; f = files[i]; i++) {
			ParseFile(f);
		}
		var typexml = e.type.match('.xml');
		if(!typexml){ }
		
	}

 
	// output file information
	function ParseFile(file) {
		secondcb.style.display = "block";
		var x = document.getElementById('fileselect').value;
		Output(
			 "<p><span><strong><a href='"+ x +"'> <span class='sm-img'></span>" + file.name +"</span></strong> </a>"+
			//"<span>type: <strong>" + file.type +"</span></strong>"+
			//" <span>size: <strong>" + file.size +" bytes</span></strong>"+
			"</p>"
		);

	}


	// initialize
	function Init() {

		var fileselect = $id("fileselect"),
			filedrag = $id("filedrag"),
			submitbutton = $id("submitbutton");

		// file select
		fileselect.addEventListener("change", FileSelectHandler, false);

		// is XHR2 available?
		var xhr = new XMLHttpRequest();
		var parser = new DOMParser();
		//var doc = parser.parseFromString(xhr, "application/xml");
		if (xhr.upload) {
			//filedrag.setRequestHeader("Content-Type", "text/xml");
			// file drop
			filedrag.addEventListener("dragover", FileDragHover, false);
			filedrag.addEventListener("dragleave", FileDragHover, false);
			filedrag.addEventListener("drop", FileSelectHandler, false);
			filedrag.style.display = "block";
			
			// remove submit button
			submitbutton.style.display = "none";
			
		}

	}

	// call initialization file
	if (window.File && window.FileList && window.FileReader) {
		Init();
	}


})();