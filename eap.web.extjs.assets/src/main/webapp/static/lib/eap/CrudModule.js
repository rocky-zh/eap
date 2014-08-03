/*
* Author: chiknin@gmail.com
*/
Ext.define('eap.CrudModule', {
	extend: 'eap.core.Module',
	
	submitButton: null, // create | update
	cancelButton: null,
	
	actionText: {
		c: '添加',
		u: '修改',
		d: '删除',
		r: '查看'
	},
	
	addViewId: null, // require
	editViewId: null, // require
	readViewId: null, // require
	
	init: function(){
		var _t = this;
		_t.callParent(arguments);
		
		if (!_t.submitButton) {
			_t.submitButton = {
				text: '保存',
				handler: function(){
					var win = this.up('window'), 
					viewPanel = win.down('panel'), 
					form = viewPanel.down('form').getForm();
					if (!form.isValid()) {
						return;
					}
					
					viewPanel.submitFormData({
						success: function(form, action){
							_t._onSuccess(viewPanel.action);
							win.close();
							_t.down('eapgrid').store.load();
						}
					});
				}
			};
		}
		if (!_t.cancelButton) {
			_t.cancelButton = {
				text: '关闭',
				handler: function(){
					this.up('window').close();
				}
			};
		}
	},
	
	activate: function(params){
		// this.down('eapgrid').store.load();
	},
	
	getReqParams: function(action, eventSource){
		var _t = this,
			grid = _t.down('eapgrid'),
			sm = grid.getSelectionModel(), 
			slts = sm.getSelection();
		
		if (action != 'c') {
			if (slts.length == 0) {
				AU.showInfoDialog({
					msg: Ext.String.format('请您选中记录后再进行{0}操作。', _t.actionText[action]),
					animateTarget: eventSource && eventSource.el
				});
				// AU.showMsg(Ext.String.format('请您选中记录后再进行{0}操作。', _t.actionText[action]));

				return false;
			}
			
			if (action == 'u' || action == 'r') {
				if (slts.length > 1) {
					AU.showInfoDialog({
						msg: Ext.String.format('请您只选中要进行{0}操作的一条记录。', _t.actionText[action]),
						animateTarget: eventSource && eventSource.el
					});
					return false;
				} else {
					return slts[0].raw;
				}
			}
			if (action == 'd') {
				var ids = [];
				Ext.each(slts, function(slt) {
					ids.push(slt.raw[grid.idProperty]);
				});
				
				return ids;
			}
		}
		
		var sltRaws = Ext.Array.map(slts, function(slt) {
			return slt.raw; // slt.data 可以获取更新后的数据
		});
		return sltRaws;
	},
	
	onCreate: function(params, request){
		var _t = this, 
			eo = request.eventSource,
			action = 'c', 
			reqParams = _t.getReqParams(action, eo);
		if (reqParams === false) {
			return;
		}
		
		_t.showWindow(_t.addViewId || _t.editViewId, {
			action: action,
			reqParams: reqParams,
			apis: _t.ui.apis,
			submitFormData: _t.ui.submitFormData
		}, {
			title: _t.actionText[action],
			buttons: [_t.submitButton, _t.cancelButton],
			autoScroll: true,
			animateTarget: eo.el
		});
	},
	
	onRead: function(params, request){
		var _t = this, 
			eo = request.eventSource,
			action = 'r', 
			reqParams = (params && params.reqParams) || _t.getReqParams(action, eo);
		if (reqParams === false) {
			return;
		}
		
		_t.showWindow(_t.readViewId, {
			action: action,
			reqParams: reqParams,
			apis: _t.ui.apis,
			loadFormData: _t.ui.loadFormData,
			initData: function() {
				var _t = this;
				if (_t.reqParams) {
					_t.loadFormData({
						params: _t.reqParams
					});
				}
			}
		}, {
			title: _t.actionText[action],
			buttons: [_t.cancelButton],
			autoScroll: true,
			animateTarget: eo && eo.el
		});
	},
	
	onUpdate: function(params, request){
		var _t = this, 
			eo = request.eventSource,
			action = 'u', 
			reqParams = _t.getReqParams(action, eo);
		if (reqParams === false) {
			return;
		}
		
		_t.showWindow(_t.editViewId, {
			action: action,
			reqParams: reqParams,
			apis: _t.ui.apis,
			loadFormData: _t.ui.loadFormData,
			submitFormData: _t.ui.submitFormData,
			initData: function() {
				var _t = this;
				if (_t.reqParams) {
					_t.loadFormData({
						params: _t.reqParams
					});
				}
			}
		}, {
			title: _t.actionText[action],
			buttons: [_t.submitButton, _t.cancelButton],
			autoScroll: true,
			animateTarget: eo.el
		});
	},
	
	onDelete: function(params, request){
		var _t = this, 
			eo = request.eventSource,
			action = 'd',
			reqParams = _t.getReqParams(action, eo);
		if (reqParams === false) {
			return;
		}
			
		AU.showConfirmDialog({
			animateTarget: eo.el,
			msg: Ext.String.format('您确认删除选中的记录({0}条)？', reqParams.length),
			callback: function(btnId, value) {
				if (btnId == 'yes') {
					_t.doAction({
						m: 'delete',
						p: {ids: reqParams},
						cb: function(result, e) {
							if (result.success) {
								_t._onSuccess(action);
								_t.down('eapgrid').store.load();
							} else {
								alert(result.data);
							}
						}
					})
				}
			}
		});
	},
	
	onPrint: function(params, request){
		this.down('eapgrid').print();
		return false;
	},
	
	_onSuccess: function(action) {
		AU.showMsg(Ext.String.format('{0}成功', this.actionText[action]));
//		AU.showSuccessDialog({
//			msg: Ext.String.format('{0}成功', this.actionText[action])
//		});
	},
	
	_onFailure: function() {
	}
});
