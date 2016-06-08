
BCFactory={};

function BBjSession(baseurl){
	this.baseurl = baseurl;
	this.ex = new Array();
	this.vars = new Array();
	this.ret = new Array();
}


BBjSession.prototype.getSessionID = function() {
	return this.sessionid;
}

BBjSession.prototype.pushVar = function(name,value) {
	o = new Object();
	o.n = name;
	switch (typeof(value)){
			case 'string':
				o.t = 'str';
				break;
			case 'number':
				o.t = 'num';
				break;	
			case 'object':
				o.t = 'dr';
				break;
	}
	o.v = value;
    this.vars.push(o);
	
}

BBjSession.prototype.pushRet = function(name) {
    this.ret.push(name);
	
}

BBjSession.prototype.create = function(v,cl) {
	o = new Object();
	o.op = 'create';
	o.var = v;
	o.class = cl;
	this.pushEx(o);
}

BBjSession.prototype.invoke = function(v,rv,m,args) {
	o = new Object();
	o.op = 'invoke';
	o.var = v;
	o.retvar = rv;
	o.method = m;
	o.args = args;
	this.pushEx(o);
}

BBjSession.prototype.pushEx = function(command) {
    this.ex.push(command);
}

BBjSession.prototype.reset =  function() {
	this.ex = new Array();
	this.vars = new Array();
	this.ret = new Array();
}

BBjSession.prototype.getTypeof = function(vn) {

	for (var v in this.vars){
		if ( this.vars[v].n == vn){
			return this.vars[v].t;	
		}	
	}
	return 'undefined';
}

BBjSession.prototype.exec = function(success) {
	if (this.error){
		//maybe add something more clever to direct the user to in case of an error
		return;
		}
		
 var ud = '_' + +new Date,
        script = document.createElement('script'),
        head = document.getElementsByTagName('head')[0] 
               || document.documentElement;
    window[ud] = function(data) {
	
        head.removeChild(script);
		this.ses.ses = data.ses;
		this.ses.result = data;
		var x=this;
		e = data.err;
		if (e){
			alert(e);
			this.ses.error=e;
		}
		else 
		{
			for (var k in data) {
				if (data[k].constructor == Array) {
					for (var rec in data[k]) {
						if (data[k][rec].datarow) {
							for (var field in data[k][rec].datarow) {
							var name = data[k][rec].datarow[field].Name;
							var type = data[k][rec].datarow[field].Type;
							switch (type){
								case "C":
									value = data[k][rec].datarow[field].StringValue;
									break;
								//todo: move to separate "prep datarow method"; add other data types
							}
							data[k][rec][name]=value;
							}
						}
					}
				
			};
			};
			success && success(this.ses);
		};
    };
	
	url=this.baseurl;

	var json='{';
	if (this.ses) {	
		json = json+'"ses":"'+this.ses+'"';
	};
	
	
	if (this.vars.length >0){
		if (json.length >1)
			json = json + ',';
		json = json+'"vars":'+JSON.stringify(this.vars);
		this.vars = new Array();		
	}

	if (this.ex.length >0){
		if (json.length >1)
			json = json + ',';
		json = json+'"ex":'+JSON.stringify(this.ex);
		this.ex = new Array();		
	}
	
	if (this.ret.length >0){
		if (json.length >1)
			json = json + ',';
		json = json+'"ret":'+JSON.stringify(this.ret);
		this.ret = new Array();
	}
	
	json = json+'}';
	url=url+"?ex="+json;
	
	if (url.indexOf ("?") >-1){
		url=url+ '&callback=' + ud;
	}
	else{
		url=url+ '?callback=' + ud;
	}
	console.log(url);
    script.src = url;
    head.appendChild(script);
	
}


function guid() {
//hier vielleicht einfach hochzaehlen??
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return 'x_'+s4() + s4() + s4();
}
