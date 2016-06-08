BCFactory={};

function BBjSession(baseurl){
	this.baseurl = baseurl;
	this.ex = new Array();
	this.vars = new Array();
	this.ret = new Array();
	this.errorcallback = function(e){
		alert(e);
	};
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
			case 'boolean':
				o.t = 'bool';
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



BBjSession.prototype.exec = function(success, funcName, errorcallback) {
	if (this.error){
		//maybe add something more clever to direct the user to in case of an error
		return;
	}

	var ud = '_' + +new Date,
		script = document.createElement('script'),
		head = document.getElementsByTagName('head')[0] || document.documentElement;

	var json='{';
	json=json+'"tid":"'+ud+'"';
	
	if (this.ses) {	
		json = json+',"ses":"'+this.ses+'"';
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
	
	var ax="ex="+encodeURIComponent(json);

	var request = document.createElement("iron-request");
	request.completes.then(
		function(req){
			var data = JSON.parse(req.response.replace(/\\'/g, "'"));
			this.ses.ses = data.ses;
			this.ses.result = data;
			
			e = data.err;
			if (e) {
				if (success && errorcallback)
					success[errorcallback](data);
				else if(errorcallback)
					success(data);
				else
					ses.errorcallback(data);
			}
			else {
				

				if (funcName)
					success && success[funcName](this.ses);
				else
					success && success(this.ses);
			}
		}
	);
	
	request.send({"url":this.baseurl,"method":"POST","body":ax,"handleAs":"text"});
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



BBjSession.prototype.getFileUrl = function(fileName) {
	url=this.baseurl;
	var json='{';
	if (this.ses) {	
		json = json+'"ses":"'+this.ses+'"';
	};
	json = json+',"file":'+JSON.stringify(fileName);
	var json=json+'}';
	return url+"?ex="+json;
}
