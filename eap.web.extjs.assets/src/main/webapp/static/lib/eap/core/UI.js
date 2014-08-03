/*
* Author: chiknin@gmail.com
*/
Ext.define('eap.core.UI', {
	extend: 'Ext.panel.Panel',

	moduleId: null,
	module: null,
	// renderView: Ext.emptyFn,
	// showWindow: Ext.emptyFn,

	uiConfig: Ext.emptyFn,
	initData: Ext.emptyFn,
	
	initComponent: function() {
		var _t = this,
			// uiConfig = Ext.applyIf(_t.uiConfig() || {}, {
			// 	border: false,
			// 	layout: 'fit',
			// 	defaults: {
			// 		border: false
			// 	}
			// });
			uiConfig = {
				border: false,
				layout: 'fit',
				defaults: {
					border: false
				},
				items: _t.uiConfig() || {}
			};
			
		Ext.apply(_t, uiConfig);
		Ext.apply(_t.initialConfig, uiConfig);
		
		_t.callParent(arguments);

		var itemIdComps = _t.query('[itemId]');
		Ext.each(itemIdComps, function(itemIdComp, index) {
			_t[itemIdComp.itemId] = itemIdComp;
		});
		
		AC.updateLocale();
	}
	
	,afterRender: function() {
		var _t = this;
		_t.callParent(arguments);
		_t.initData();
	}
	
	,destroy: function() {
		var _t = this;
		_t.moduleId = null;
		_t.module = null;
		// _t.renderView = null;
		// _t.showWindow = null;
		_t.callParent(arguments);
	}

	,makeDirectFn: function(method) {
		return AC.makeDirectFn(this.moduleId, method);
	}

});