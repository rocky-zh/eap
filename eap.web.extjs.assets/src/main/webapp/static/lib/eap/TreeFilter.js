// ref: http://remotetree.extjs.eu/source.php?file=js/Ext.ux.tree.TreeFilterX.js

Ext.define('eap.TreeFilter', {
	extend: 'Ext.tree.TreeFilter',

	expandOnFilter: true,

	filter: function(value, attr, startNode) {
		var _t = this;
		if (false !== _t.expandOnFilter) {
			startNode = startNode || _t.tree.root;
			var animate = _t.tree.animate;
			_t.tree.animate = false;
			startNode.expand(true, false, function() {
				_t.callParent(arguments);
			});
			_t.tree.animate = animate;
		}
		else {
			_t.callParent(arguments);
		}
	},

    filterBy: function(fn, scope, startNode) {
        var _t = this;
		startNode = startNode || _t.tree.root;
		if(_t.autoClear) {
			_t.clear();
		}
		var af = _t.filtered, rv = _t.reverse;
 
		var f = function(n) {
			if(n === startNode) {
				return true;
			}
			if(af[n.id]) {
				return false;
			}
			var m = fn.call(scope || n, n);
			if(!m || rv) {
				af[n.id] = n;
				n.ui.hide();
				return true;
			}
			else {
				n.ui.show();
				var p = n.parentNode;
				while(p && p !== _t.root) {
					p.ui.show();
					p = p.parentNode;
				}
				return true;
			}
			return true;
		};
		startNode.cascade(f);
 
		if(_t.remove){
		   for(var id in af) {
			   if(typeof id != "function") {
				   var n = af[id];
				   if(n && n.parentNode) {
					   n.parentNode.removeChild(n);
				   }
			   }
		   } 
		}
	}
});