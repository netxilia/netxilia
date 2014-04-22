var nx = this.nx || {};
nx.admin={
datasource: null,
init: function(desc) {
	var that = this;
	this.desc = desc;
	$("#datasources").tabs({
		select: function(event, ui) {
			if (ui.index < that.desc.datasources.length)
				that.viewDatasource(that.desc.datasources[ui.index]);
			else 
				that.newDatasource();
    	}
	});
	$("#datasources").tabs( "select" , 0);
	that.viewDatasource(that.desc.datasources[0]);
	$("#createAdminForm").validate();
	},
	
	
	viewDatasource: function(ds){
		for(var att in ds) {
			$("#ds-" + att).val(ds[att]);
		}
	},
	
	testDatasource: function() {
		if (this.datasource) {
			nx.resources.ds.test(this.datasource, function(msg){
				alert(msg);
			});
		}
	},
	
	newDatasource: function() {
		this.viewDatasource({id:-1, name:'', driver:'', url:'', username:'', password:''});
	}
}