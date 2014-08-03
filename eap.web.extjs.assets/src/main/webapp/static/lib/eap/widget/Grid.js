/*
* Author: chiknin@gmail.com
*/
Ext.define('eap.widget.Grid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.eapgrid',
	
	uses: [
		'Ext.data.DirectStore',
		'Ext.grid.RowNumberer',
		'Ext.selection.CheckboxModel',
		'Ext.toolbar.Paging',
		'Ext.ux.RowExpander'
	],

	frame: false,
	border: false,
	columnLines: false,
	stripeRows: true,
	sortableColumns: false,
	
	formConfig: null,
	storeConfig: null,

	rowNumberer: true,
	selMode: null, // single / simple / multi / null
	editingMode: null, // cellediting / rowediting / null
	editingConfig: null,
	expandRowBodyTpl: null,
	paging: false,

	// filterFeature:  

	idProperty: 'id',
	totalProperty: 'totalCount',
	root: 'records',// 'items',
	
	print: false,

	directFn: null,

	initComponent: function() {
		var _t = this, 
			config = _t._defaultGridConfig();
		if (_t.store == undefined) {
			config.store = _t._buildStore();
		}
		if (_t.selModel == undefined && _t.selMode != undefined) {
			config.selModel = _t._buildSelModel();
		}
		if (_t.plugins == undefined) {
			config.plugins = _t._buildPlugins();
		}
		if (_t.rowNumberer) {
			config.columns = [Ext.create('Ext.grid.RowNumberer')].concat(_t.columns || []);	
		}
		
		Ext.apply(_t, config);
		_t.callParent(arguments);
	},

	bridgeToolbars: function() {
		var _t = this;
		_t.callParent(arguments);
		if (_t.paging == true) {
			_t.dockedItems = _t.dockedItems.concat(
				Ext.create('Ext.toolbar.Paging', {
					dock: 'bottom',
					store: _t.store,
					displayInfo: true
				}
			));
		}
	},

	getAllColumn: function() {
		var _t = this, col_i, col, subCol_i, allCol = [];
		for (col_i in _t.columns) {
			col = _t.columns[col_i];
			
			if (Ext.isArray(col.columns)) {
				for (subCol_i in col.columns) {
					allCol.push(col.columns[subCol_i]);
				}
			} else {
				allCol.push(col);
			}
		}
		
		return allCol;
	},

	_buildStore: function() {
		var _t = this,
			cols = _t.getAllColumn(),//_t.columns,
			fields = [];
		
		Ext.each(cols, function(col) {
			if (col.dataIndex) {
				var field = {name: col.dataIndex};
				Ext.each(_t._modelFieldAttrs, function(attr) { // TODO 
					var attrVal = col[attr];
					if (attrVal != undefined) {
						field[attr] = attrVal;
					}
				}, _t);
				if (field.dateFormat == undefined && col.format != undefined) {
					field['dateFormat'] = col.format;
				}
				
				fields.push(field);
			}
		}, _t);
		
		var config = Ext.applyIf(_t.storeConfig || {}, {
			directFn: _t.directFn,
			idProperty: _t.idProperty,
			totalProperty: _t.totalProperty,
			root: _t.root,
			simpleSortMode: true,
//			sortParam: 'sortField',
//			directionParam: 'sortDir',
			
			autoDestroy: true,
			remoteSort: true,
//			sorters: [{
//		         property: 'name',
//		        direction: 'ASC'
//		    }],
			fields: fields,
			listeners: {
				load: {
					fn: function(store, records, successful, opts) {
						if (successful && (_t.selMode == 'simple' || _t.selMode == 'multi')) {
							var selRecords = Ext.Array.filter(records, function(record) {
								return eval(record.raw.checked) == true; // TODO checked / selected
							});

							this.getSelectionModel().select(selRecords);
						}
					}
					,scope: _t
				}
			}
		});

		return Ext.create(config.directFn == undefined 
				? 'Ext.data.ArrayStore' : 'Ext.data.DirectStore', 
			config);
	},
	_buildSelModel: function() {
		var _t = this;
		return {
			selType: 'checkboxmodel',
			mode: _t.selMode
		};
	},
	_buildPlugins: function() {
		var _t = this,
			plugins = [];
		
		if (_t.formConfig) {
			plugins.push(
				Ext.create('eap.wiget.GridForm')
			);
			
			_t.tbar = _t.tbar || ['->'];
			if (_t.tbar.indexOf('->') == -1) {
				_t.tbar.push('->');
			}
			_t.tbar.push({
				iconCls: 'icon-chevron-up',
				itemId: 'gridFormClrBtn',
				tooltip: '隐藏查询面板',
				handler: function() {
					if (this.iconCls == 'icon-chevron-up') {
						_t.hideForm();
					} else {
						_t.showForm();
					}
				}
			});
		}
		if (_t.print) {
			plugins.push(
				// Ext.create('app.core.widget.plugin.XGridPrinter')
			);
		}
		
		if (_t.editingMode != undefined) {
			plugins.push(
				Ext.apply({
					ptype: _t.editingMode
				}, _t.editingConfig)
			);
		}
		if (_t.expandRowBodyTpl != undefined) {
			plugins.push(
				Ext.create('Ext.ux.RowExpander', {
					rowBodyTpl : _t.expandRowBodyTpl
				})
			);
		}
		
		return plugins;
	},

	_defaultGridConfig: function() {
		return {
		};
	},

	_modelFieldAttrs: [
		'type',
		'convert',
		'dateFormat',
		'useNull',
		'defaultValue',
		'mapping',
		'sortType',
		'allowBlank',
		'persist',
		'sortDir'
	],

	getFormPanel: function() {
		return this.down('#eapGridForm');
	},

	hideForm: function() {
		var _t = this;
		if (_t.formConfig) {
			_t.getFormPanel().hide();

			var _b = _t.down('#gridFormClrBtn');
			_b.setIconCls('icon-chevron-down');
			_b.setTooltip('显示查询面板');
		}
	},
	showForm: function() {
		var _t = this;
		if (_t.formConfig) {
			_t.getFormPanel().show();

			var _b = _t.down('#gridFormClrBtn');
			_b.setIconCls('icon-chevron-up');
			_b.setTooltip('隐藏查询面板');
		}
	}
});

Ext.define('eap.wiget.GridForm', {
	alias: 'plugin.eapgridform',

	grid: null,

	init: function(grid) {
		var _t = this,
			formConfig = grid.formConfig;
		_t.grid = grid;

		if (formConfig) {
			var config = _t._defaultFormConfig();
			Ext.apply(config, formConfig);
			grid.addDocked(config, 0);
			
			grid.mon(grid.store, 'beforeload', function(store, operation) { // TODO 
				operation.params = Ext.apply(operation.params || {}, _t._getFormValues());
				return true;
			}, _t);
		}
	},
	destroy: function() {
		var _t = this;
		delete _t.grid._formValues;
		_t.grid = null;
	},

	_defaultFormConfig: function() {
		var _t = this,
			formConfig = _t.grid.formConfig;
		return {
			dock: 'top',
			itemId: 'eapGridForm',
			xtype: 'form',
//			title: '查询筛选',
//			titleCollapse: true,
//			collapsible: true,
			frame: _t.grid.frame,
			border: false,
			bodyPadding: '10 10 0 10',
			layout: {
				type: 'table',
				columns: formConfig.fieldColumns || 3
			},
			defaults: {
				xtype: 'textfield'
			},
			fieldDefaults: {
				labelAlign: 'right',
				labelWidth: 70
			},
			buttonAlign: 'center',
			buttons:  [{
				text: '查询并隐藏',
				handler: function() {
					_t._submitForm.call(this, _t, formConfig);
					_t.grid.hideForm();
				}
			}, {
				itemId: 'queryBtn',
				text: '查询',
				handler: function() {
					_t._submitForm.call(this, _t, formConfig);
				}
			}, {
				text: '重置',
				handler: _t._resetForm
			}]
		};
	},
	_submitForm: function(gridForm, formConfig) {
		var form = this.up('form').getForm();
		if (!form.isValid()) {
			return;
		}
		
		if (formConfig.submitValidate && formConfig.submitValidate(form, this) === false) {
			return;
		}
		
		gridForm._setFormValues(form.getValues());
		gridForm.grid.store.load();
	},
	_resetForm: function() {
		var form = this.up('form').getForm();
		form.reset();
	},

	_setFormValues: function(values) {
		this.grid._formValues = values;
	},
	_getFormValues: function() {
		return this.grid._formValues;
	}
});