var nx = self.nx || {};

nx.resources = {
	restContext: "/netxilia/rest",	
	getHeaders: null,
	ajax: function(type, url, params, callback, errorCallback, ajaxParams) {
		var paramsWithMethod = params || {};
		
		//cleanup params
		for(var p in paramsWithMethod)
			if (paramsWithMethod[p] == null)
				delete paramsWithMethod[p];
		
		//this is the JBOSS RESTEasy way. is this standard !?
		/*
		if (type=="PUT") {
			paramsWithMethod["_method"] = "put";
			type="POST";
		} else if (type=="DELETE") {
			paramsWithMethod["_method"] = "delete";
			type="POST";
		}*/
		var ajaxCall = {
				url:this.restContext + url,
				type: type,
				data: paramsWithMethod,
				dataType: "text",
				beforeSend: function(xhr) {
					 if (nx.resources.getHeaders) {
						 var headers = nx.resources.getHeaders();
						 if (headers) {
							 for(var h in headers)
								 xhr.setRequestHeader(h, headers[h]);
						 }	
						 xhr.setRequestHeader("ajax", "true");
						 xhr.setRequestHeader("Accept", "application/json");						 
					 }
				},
				success: function(data) {
					if (callback) {	
						if (data == null || data == "") {
							callback(null);
						} else if (data.charAt("0") != '{' && data.charAt("0") != '['){
							callback(data);
						} else {
							callback(eval("(" + data + ")"));
						}
					}
				},
				error: function(request, textStatus) {
					if (errorCallback)
						errorCallback(textStatus, request, request.getResponseHeader("nx-error"));
					if (request.status == 401 && nx.resources.disconnected) {
						nx.resources.disconnected();
					}
				}
			};
		if (ajaxParams) {
			for(var p in ajaxParams)
				ajaxCall[p] = ajaxParams[p];
		}

		jQuery.ajax(ajaxCall);
	},
	
	get: function(url, params, callback, errorCallback) {
		this.ajax("GET", url, params, callback, errorCallback);
	},
	post: function(url, params, callback, errorCallback) {
		this.ajax("POST", url, params, callback, errorCallback);
	},
	put: function(url, params, callback, errorCallback) {
		this.ajax("PUT", url, params, callback, errorCallback);
	},
	del: function(url, params, callback, errorCallback) {
		this.ajax("DELETE", url, params, callback, errorCallback);
	}
	
	
};

//home
nx.resources.home = {
		treeview: function(callback, errorCallback) {
			nx.resources.get("/home/treeview", {}, callback, errorCallback);
		}
};

//events
nx.resources.events = {
		poll: function(windowId, callback, errorCallback) {
			nx.resources.post("/events/" + windowId, {}, callback, errorCallback);
		}
};

//windows
nx.resources.windows = {
	register: function(workbook, sheet, callback, errorCallback) {	
		nx.resources.put("/windows/" + workbook + "/" + sheet, {}, callback, errorCallback);
	},
	terminate: function(windowId, callback, errorCallback) {	
		nx.resources.del("/windows/" + windowId, {}, callback, errorCallback, {async:false});
	},
	notifySelection: function(windowId, areaRef, callback, errorCallback) {
		nx.resources.post("/windows/" + windowId + "/notifySelection/" + areaRef, null, callback, errorCallback);
	},
	getWindowsForSheet: function(workbook, sheet, callback, errorCallback) {	
		nx.resources.get("/windows/" + workbook + "/" + sheet, {}, callback, errorCallback);
	},
	undo: function(windowId, callback, errorCallback) {	
		nx.resources.put("/windows/" + windowId + "/undo", {}, callback, errorCallback);
	},
	redo: function(windowId, callback, errorCallback) {	
		nx.resources.put("/windows/" + windowId + "/redo", {}, callback, errorCallback);
	}
};

//cells
nx.resources.cells = {
	setValue: function(workbook, areaRef, value, callback) {
		nx.resources.post("/cells/" + workbook + "/" + areaRef + "/value", {value: value}, callback);
	},
	
	replicate: function(workbook, fromCell, toArea, callback) {
		nx.resources.post("/cells/" + workbook + "/replicate/" + fromCell + "/" + toArea, null, callback);
	},
	
	paste: function(workbook, fromCell, toCell, value, callback) {
		nx.resources.post("/cells/" + workbook + "/paste/" + fromCell + "/" + toCell, {value:value}, callback);
	},

	move: function(workbook, fromArea, toCell, callback) {
		nx.resources.put("/cells/" + workbook + "/move/" + fromArea + "/" + toCell, null, callback);
	},

	setStyle: function(workbook, areaRef, style, callback) {
		nx.resources.post("/cells/" + workbook + "/" + areaRef + "/style", {style: style}, callback);
	},
	
	applyStyle: function(workbook, areaRef, style, mode, callback) {
		nx.resources.post("/cells/" + workbook + "/" + areaRef + "/style/apply", {style: style, mode: mode}, callback);
	},
		
	setFormat: function(workbook, areaRef, format, callback) {
		nx.resources.post("/cells/" + workbook + "/" + areaRef + "/format", {format: format}, callback);
	},	
	
	merge: function(workbook, areaRef, callback, errorCallback) {
		nx.resources.put("/cells/" + workbook + "/" + areaRef + "/merge", null, callback, errorCallback);
	},
	
	append: function(workbook, sheetName, value, callback, errorCallback) {
		nx.resources.put("/cells/" + workbook + "/" + sheetName + "/append", {value:value}, callback, errorCallback);
	},
	
	
	
	find: function(workbook, sheetName, startRef, searchText, callback, errorCallback) {
		nx.resources.get("/cells/" + workbook + "/" + sheetName + "/find", {startRef: startRef, searchText:searchText}, callback, errorCallback);
	}
	
	
};

//formatters
nx.resources.formatters = {
		getFormatValues: function(workbook, fmt, callback) {
			nx.resources.get($.url("/formatters/{}/{}/formatValues",workbook,fmt) , null, callback);
		},	

		getValues: function(workbook, formatterName, callback) {
			nx.resources.get($.url("/formatters/{}/{}/values",workbook,formatterName), null, callback);
		},	
		
		setFormatter: function(workbook, formatterName, sourceWorkbookName, nameRef, valueRef, callback) {
			nx.resources.post($.url("/formatters/{}/{}/{}/{}/{}", workbook,formatterName,sourceWorkbookName,nameRef,valueRef ), null, callback);
		}
};

//workbooks 
nx.resources.workbooks = {
	newWorkbook: function(workbook, config, callback, errorCallback) {
		nx.resources.put("/workbooks/" + workbook, {config:config}, callback, errorCallback);
	},

	deleteWorkbook: function(workbook, callback, errorCallback) {
		nx.resources.del("/workbooks/" + workbook , null, callback, errorCallback);
	}
}
//sheets
nx.resources.sheets = {
	newSheet: function(workbook, name, callback, errorCallback) {
		nx.resources.put("/sheets/" + workbook + "/" + name, null, callback, errorCallback);
	},
	
	del: function(workbook, name, callback, errorCallback) {
		nx.resources.del("/sheets/" + workbook + "/" + name, null, callback, errorCallback);
	},
	
	sort: function(workbook, name, sortSpec, callback, errorCallback) {
		nx.resources.post("/sheets/" + workbook + "/" + name + "/sort/" + sortSpec, null, callback, errorCallback);
	},
	
	setAlias: function(workbook, name, aliasName, ref, callback, errorCallback) {
		nx.resources.post("/sheets/" + workbook + "/" + name + "/alias/" + aliasName + "/" + ref, null, callback, errorCallback);
	},
	
	deleteAlias: function(workbook, name, aliasName, ref, callback, errorCallback) {
		nx.resources.del("/sheets/" + workbook + "/" + name + "/alias/" + aliasName, null, callback, errorCallback);
	}

};

//rows
nx.resources.rows = {
		insert: function(workbook, sheetName, pos, callback) {
			nx.resources.put("/rows/" + workbook + "/" + sheetName + "/" + pos, null, callback);
		},
		
		del: function(workbook, sheetName, pos, callback) {
			nx.resources.del("/rows/" + workbook + "/" + sheetName + "/" + pos, null, callback);
		}
	};

//columns
nx.resources.columns = {
		insert: function(workbook, sheetName, pos, callback) {
			nx.resources.put("/columns/" + workbook + "/" + sheetName + "/" + pos, null, callback);
		},
		
		del: function(workbook, sheetName, pos, callback) {
			nx.resources.del("/columns/" + workbook + "/" + sheetName + "/" + pos, null, callback);
		},
		
		modify: function(workbook, sheetName, pos, width, callback) {
			nx.resources.post("/columns/" + workbook + "/" + sheetName + "/" + pos, {width:width}, callback);
		}
		
	};



//charts
nx.resources.charts = {
		add: function(workbook, sheetName, areaRef, title, type, callback, errorCallback) {
			nx.resources.put("/charts/" + workbook + "/" + sheetName, {areaRef: areaRef, title:title, type:type}, callback, errorCallback);
		},
		set: function(workbook, sheetName, index, areaRef, title, type, callback, errorCallback) {
			nx.resources.post("/charts/" + workbook + "/" + sheetName + "/" + index, {areaRef: areaRef, title:title, type:type}, callback, errorCallback);
		},
		del: function(workbook, sheetName, index, callback, errorCallback) {
			nx.resources.del("/charts/" + workbook + "/" + sheetName + "/" + index, null, callback, errorCallback);
		},
		move: function(workbook, sheetName, index, left, top, width, height, callback, errorCallback) {
			nx.resources.post("/charts/" + workbook + "/" + sheetName + "/" + index + "/move", {left:left, top:top, width:width, height:height}, callback, errorCallback);
		}
	};

//data sources
nx.resources.ds = {
	list: function(callback) {
		nx.resources.get("/ds", null, callback);
	},
	add: function(name, description, driver, url, username, password, callback) {
		nx.resources.put("/ds", {name: name, description: description, driver: driver, url: url, username: username, password: password}, callback);
	},
	save: function(id, name, description, driver, url, username, password,  callback) {
		nx.resources.post("/ds/" + id, {name: name, description: description, driver: driver, url: url, username: username, password: password}, callback);
	},
	
	remove: function(id, callback) {
		nx.resources.del("/ds/" + id, null, callback);
	},
	
	test: function(id, callback, errorCallback) {
		nx.resources.get("/ds/" + id + "/test", null, callback, errorCallback);
	},

	setConfigurationForWorkbook:function(key, config, callback, errorCallback) {
		nx.resources.post("/ds/workbooks/" + key, {config:config}, callback, errorCallback);	
	},

	deleteConfigurationForWorkbook:function(key, callback, errorCallback) {
		nx.resources.del("/ds/workbooks/" + key, null, callback, errorCallback);	
	},
	
	edit: function(id, callback, errorCallback) {
		nx.resources.get("/ds/" + id + "/edit", null, callback, errorCallback);
	}
}