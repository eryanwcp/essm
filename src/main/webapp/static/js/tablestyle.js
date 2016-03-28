function del()
	{
		if(confirm("你真的想删除该记录么？"))
		{
			return true;
		}
		return false;
	}

function add_event(tr){
	tr.onmouseover = function(){
		tr.className += ' hover';
	};
	tr.onmouseout = function(){
		tr.className = tr.className.replace(' hover', '');
	};
}

function stripe(table) {
	var trs = table.getElementsByTagName("tr");
	for(var i=1; i<trs.length; i++){
		var tr = trs[i];
		tr.className = i%2 != 0? 'odd' : 'even';
		add_event(tr);
	}
}

window.onload = function(){
	var tables = document.getElementsByTagName('table');
	for(var i=0; i<tables.length; i++){
		var table = tables[i];
		if(table.className == 'list'){
			stripe(tables[i]);
		}
	}
}