/*
* Author: chiknin@gmail.com
*/
Ext.define('eap.core.DicModel', {
    extend: 'Ext.data.Model',
    fields: [ 'type', 'key', 'value', 'name', 'desc', 'group' ]
});
Ext.define('eap.core.DicStore', {
	extend: 'Ext.data.DirectStore',
	requires: [
		'eap.core.DicModel'
	],
	model: 'eap.core.DicModel'
});

Ext.define('eap.core.Dic', {
	singleton: true,
	alternateClassName: 'Dic',
	
	requires: [
		'Ext.data.StoreManager',
		'eap.core.DicStore'
	],
	
	uses: [
		'Ext.form.CheckboxGroup',
		'Ext.form.RadioGroup',
		'Ext.form.field.Display'
	],
	
	getDicData: function(param) {
		var dicData, dicCfg = AC.config.dic;
		AC.doAction(dicCfg.moduleId, {
			m: dicCfg.remoteMethod,
			p: param,
			cb: function(data, response) {
				dicData = data.result;
			},
			sync: true
		});

		return dicData;

		// return [{value: '0', name: 'No'}, {value: '1', name: 'Yes'}];
		// return (Ems.syncDirectRequest('ems.system.System', 'getDicData', [param || {}]).result) || [];
	},
	getDicDataAsync: function(param, callback, scope) {
		var dicCfg = AC.config.dic;
		AC.doAction(dicCfg.moduleId, {
			m: dicCfg.remoteMethod,
			p: param,
			cb: function(data) {
				Ext.callback(callback, scope, [data]);
			}
		});
	},
	
	getStore: function(dicType) {
		var _t = this,
			storeId = _t._getStoreId(dicType),
			dicStore = Ext.data.StoreManager.lookup(storeId);
		if (!dicStore) {
			var data = _t.getDicData({type: dicType});
			dicStore = Ext.create('eap.core.DicStore', {
				storeId: storeId,
				simpleSortMode: true,
				remoteSort: false
			});
			dicStore.loadData(data);
		}
		
		return dicStore;
	},
	_getStoreId: function(dicType) {
		var dicCfg = AC.config.dic,
			storeId = (dicCfg.storeIdPrefix + dicType);
		return storeId;
	},
	distroyStore: function(dicType) {
		var _t = this,
			storeId = _t._getStoreId(dicType),
			dicStore = Ext.data.StoreManager.lookup(storeId);
		if (dicStore) {
//			dicStore.destroy();
			dicStore.destroyStore();
		}
	},
	
	getEmptyStore: function() {
		var _t = this,
			dicStore = Ext.create('eap.core.DicStore', {
				autoDestroy: true
			});
		return dicStore;
	},
	
	localComboBox: function(config, store) {
		var _t = this;
		return Ext.create('Ext.form.field.ComboBox', Ext.applyIf(config || {}, {
			queryMode: 'local',
			forceSelection: false,
			editable: false,
			valueField: 'value',
			displayField: 'name',
			store: store || _t.getEmptyStore()
		}));
	},
	
	comboBox: function(dicType, config) {
		var _t = this, 
			dicCfg = AC.config.dic,
			dicStore = _t.getStore(dicType),
			hdrOpt = config && config.headerOption;
		
		if (hdrOpt != undefined && hdrOpt !== false) {
			var hdrOptData = Ext.isObject(hdrOpt) ? hdrOpt : dicCfg.defaultComboHeaderOption, 
				records = dicStore.getRange(), 
				data = [hdrOptData];
				
			Ext.each(records, function(record) {
				data.push(record.data);
			});
			
			dicStore = _t.getEmptyStore();
			dicStore.loadData(data);
			
			delete config.headerOption;
		}
		
		var comboBox = _t.localComboBox(config, dicStore);
		
		if (dicStore.getCount() > 0) {
			if (comboBox.value == undefined) {
				var record, value;
				if (comboBox.valueKey) {
					record = comboBox.findRecord('key', comboBox.valueKey);
				} else {
					record = dicStore.getAt(0);
				}
				if (record) {
					value = record.data[comboBox.valueField];
				}
				
				if (value != undefined) {
					comboBox.originalValue = value;
					comboBox.setValue(value);
				}
			}
		}
		
		return comboBox;
	},
	
	checkboxGroup: function(dicType, config) {
		return this._createFieldContainer(dicType, 'checkboxGroup', config);
	},
	
	radioGroup: function(dicType, config) {
		return this._createFieldContainer(dicType, 'radioGroup', config);
	},
	
	_fieldContainerConfig: {
		checkboxGroup: {
			className: 'Ext.form.CheckboxGroup',
			getNameFn: function(fieldContainer, data) {
				return fieldContainer.groupName + '_' + data['key'];
			}
		},
		radioGroup: {
			className: 'Ext.form.RadioGroup',
			getNameFn: function(fieldContainer, data) {
				return fieldContainer.groupName;
			}
		}
	},
	_createFieldContainer: function(dicType, ctType, config) {
		var _t = this,
			dicStore = _t.getStore(dicType),
			records = dicStore.getRange(),
			fcCfg = _t._fieldContainerConfig[ctType],
			items = [];
		
		Ext.each(records, function(record) {
			var data = record.data,
				itemName = fcCfg.getNameFn(config, data),
				checked = false;
			
			 if (config.value != undefined) {
			 	checked = Ext.isArray(config.value) ? Ext.Array.contains(config.value, data.value) : config.value == data.value;
			 } else {
			 	if (config.valueKey != undefined) {
					checked = Ext.isArray(config.valueKey) ? Ext.Array.contains(config.valueKey, data.key) : config.valueKey == data.key;
				}	
			 }
			
			items.push({
				boxLabel: data.name,
				name: itemName,
				inputValue: data.value,
				checked: checked
			});
		});
		delete config.value;
		
		return Ext.create(fcCfg.className, Ext.apply({items: items}, config));
	},
	
	renderer: function(dicType) {
		var _t = this,
			dicStore = _t.getStore(dicType);
		
		return function(value) { //, metaData, record, rowIndex, colIndex, store, view) {
			var dicRecord = dicStore.findRecord('value', value);
			if (dicRecord) {
				return dicRecord.data.name;
			}
			
			return value || '';
		};
	},
	
	displayfield: function(dicType, config) { // TODO refItemId
		var _t = this, 
			displayField = Ext.create('Ext.form.field.Display', Ext.apply(config, {
				listeners: {
					change: function(comp, newValue, oldValue, eOpts) {
						if (newValue) {
							var dicStore = _t.getStore(dicType),
								record = dicStore.findRecord('value', newValue);
							if (record) {
								_t.setRawValue(_t.valueToRaw(record.data['name'] || newValue));
							}
						}
					}
				}
			}));
		if (config.value) {
			var dicStore = _t.getStore(dicType),
				record = dicStore.findRecord('value', config.value);
			if (record) {
				displayField.setRawValue(displayField.valueToRaw(record.data['name'] || config.value));
			}
		}
		
		return displayField;
	},
	
	column: function(dicType, config) {
		if (Ext.isString(dicType)) {
			dicType = {dicType: dicType};
		}
		
		return Ext.applyIf(config || {}, {
			renderer: Dic.renderer(dicType.dicType),
			field: Dic.comboBox(dicType.dicType, dicType)
		});
	}
});