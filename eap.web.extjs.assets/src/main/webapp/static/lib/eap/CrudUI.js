/*
* Author: chiknin@gmail.com
*/
Ext.define('eap.CrudUI', {
	extend: 'eap.core.UI',
	
	isCurdUI: true,
	
	action: null, // c[create] | r[read] | u[update] | d[delete]
	
	reqParams: null,
	
	apis: null,
	getApis: Ext.emptyFn,
	
	isListUI: true,
	enableCreateAction: true,
	enableUpdateAction: true,
	enableDeleteAction: true,
	enableReadAction: true,
	enablePrint: true,
	
	constructor: function() {
		var _t = this;
		_t.callParent(arguments);

		_t.apis = _t.getApis();
		if (!_t.apis) {
			_t.apis = {};
			if (_t.enableCreateAction || _t.enableUpdateAction || _t.enableDeleteAction || _t.enableReadAction) {
				_t.apis.load = AC.makeDirectFn(_t.moduleId, 'read');
			}
			if (_t.enableCreateAction) {
				_t.apis.c = {
					submit: AC.makeDirectFn(_t.moduleId, 'create')
				}
			}
			if (_t.enableUpdateAction) {
				_t.apis.u = {
					submit: AC.makeDirectFn(_t.moduleId, 'update')
				}
			}
			if (_t.enableDeleteAction) {
				_t.apis.d = {
					submit: AC.makeDirectFn(_t.moduleId, 'delete')
				}
			}
		}
	},
	
	initComponent: function() {
		var _t = this;
		_t.callParent(arguments);
		
		if (_t.isListUI) {
			var grid = _t.down('eapgrid'),
				tbar = [].concat(grid.tbarButtons || []),
				createActionBtn,
				updateActionBtn,
				readActionBtn,
				deleteActionBtn;
			if (_t.enableCreateAction) {
				createActionBtn = Ext.create('Ext.button.Button', {
					xtype: 'button',
					itemId: 'createActionBtn',
					iconCls: 'icon-plus',
					tooltip: '添加',
					listeners: AC.makeRequestEvent('click', _t.moduleId, {m: 'onCreate'})
				});
				tbar.push(createActionBtn);
			}
			if (_t.enableUpdateAction) {
				updateActionBtn = Ext.create('Ext.button.Button', {
					itemId: 'updateActionBtn',
					iconCls: 'icon-pencil',
					tooltip: '修改',
					// hidden: true,
					listeners: AC.makeRequestEvent('click', _t.moduleId, {m: 'onUpdate'})
				});
				tbar.push(updateActionBtn);
			}
			if (_t.enableReadAction) {
				readActionBtn = Ext.create('Ext.button.Button', {
					itemId: 'readActionBtn',
					iconCls: 'icon-eye-open',
					tooltip: '查看',
					// hidden: true,
					listeners: AC.makeRequestEvent('click', _t.moduleId, {m: 'onRead'})
				});
				tbar.push(readActionBtn);
			}
			if (_t.enableDeleteAction) {
				deleteActionBtn = Ext.create('Ext.button.Button', {
					itemId: 'deleteActionBtn',
					iconCls: 'icon-remove',
					tooltip: '删除',
					// hidden: true,
					listeners: AC.makeRequestEvent('click', _t.moduleId, {m: 'onDelete'})
				});
				tbar.push(deleteActionBtn);
			}
			if (_t.enablePrint) {
				tbar.push({
					iconCls: 'icon-print',
					tooltip: '打印',
					listeners: AC.makeRequestEvent('click', _t.moduleId, {m: 'onPrint'})
				});
			}
			
			if (tbar.length > 0) {
				grid.getDockedItems()[1].add(0, tbar);
				// grid.addDocked({
				// 	xtype: 'toolbar',
				// 	dock: 'top',
				// 	items: tbar
				// });
			}

			// grid.on('selectionchange', function(comp, selected, eOpts) {
			// 	Ext.defer(function() {
			// 		if (selected.length > 0) {
			// 			_t.enableUpdateAction && updateActionBtn && (updateActionBtn.show());
			// 			_t.enableReadAction && readActionBtn && (readActionBtn.show());
			// 			_t.enableDeleteAction && deleteActionBtn && (deleteActionBtn.show());
			// 		} else {
			// 			_t.enableUpdateAction && updateActionBtn && (updateActionBtn.hide());
			// 			_t.enableReadAction && readActionBtn && (readActionBtn.hide());
			// 			_t.enableDeleteAction && deleteActionBtn && (deleteActionBtn.hide());
			// 		}
			// 	}, 10);
			// }, _t);
		}
	},
	
	loadFormData: function(options) {
		var _t = this,
			formPanel = _t.down('form');
		if (!formPanel) {
			return;
		}

		var form = formPanel.getForm(),
			apis = _t.apis,
			loadApi = (apis[_t.action] && apis[_t.action].load) || apis.load;
		
		formPanel.api = {
			load: loadApi
		};
		form.api = {
			load: loadApi
		};
		form.load(options);
	},
	
	submitFormData: function(options) {
		var _t = this,
			formPanel = _t.down('form');
		if (!formPanel) {
			return;
		}

		var form = formPanel.getForm(),
			apis = _t.apis,
			submitApi = (apis[_t.action] && apis[_t.action].submit) || apis.submit;
		
//		if (_t.action == 'd') {
//			
//		} else {
			formPanel.api = {
				submit: submitApi
			};
			form.api = {
				submit: submitApi
			};
			form.submit(options);
//		}
	}
});