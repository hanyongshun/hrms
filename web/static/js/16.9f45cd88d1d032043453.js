webpackJsonp([16],{Gt4T:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var s=a("Xxa5"),r=a.n(s),n=a("exGp"),i=a.n(n),l=a("Dd8w"),o=a.n(l),c=a("gyMJ"),d=a("NYxO"),u=a("PJh5"),f=a.n(u),p=a("ecGJ"),h={name:"app-show-staff-out",props:["recordId"],data:function(){return{form:{name:"",date:"",type_text:"",to_store_name:"",status_text:"",desc:""}}},computed:o()({},Object(d.d)("stateChange",["btnLoading"])),methods:{getFormInfo:function(t,e){var a=this;return i()(r.a.mark(function s(){var n;return r.a.wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return s.next=2,t();case 2:1==(n=s.sent).code?a[e]=n.data||[]:a.$message.error(n.message);case 4:case"end":return s.stop()}},s,a)}))()},saveRecord:function(){var t=this;return i()(r.a.mark(function e(){var a;return r.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,Object(c.n)({id:t.recordId});case 2:1==(a=e.sent).code?t.closePanle():t.$message.error(a.message);case 4:case"end":return e.stop()}},e,t)}))()},submitForm:function(){var t=this;this.$confirm("确认撤销调出员工吗?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(i()(r.a.mark(function e(){return r.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:t.saveRecord();case 1:case"end":return e.stop()}},e,t)}))).catch(function(){})},closePanle:function(){this.$emit("reloadEvent","reload")}},created:function(){var t=this;this.getFormInfo(i()(r.a.mark(function e(){return r.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",Object(c._15)({id:t.recordId}));case 1:case"end":return e.stop()}},e,t)})),"form")}},m={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticStyle:{width:"70%"}},[a("el-form",{ref:"form",attrs:{model:t.form,"label-width":"100px",size:"small"}},[a("el-form-item",{attrs:{label:"调出员工："}},[a("span",{staticClass:"form-record-show"},[t._v(t._s(t.form.name))])]),t._v(" "),a("el-form-item",{attrs:{label:"调出日期："}},[a("span",{staticClass:"form-record-show"},[t._v(t._s(t.form.date))])]),t._v(" "),a("el-form-item",{attrs:{label:"类别："}},[a("span",{staticClass:"form-record-show"},[t._v(t._s(t.form.type_text))])]),t._v(" "),a("el-form-item",{attrs:{label:"调入门店："}},[a("span",{staticClass:"form-record-show"},[t._v(t._s(t.form.to_store_name))])]),t._v(" "),a("el-form-item",{attrs:{label:"状态："}},[a("span",{staticClass:"form-record-show"},[t._v(t._s(t.form.status_text))])]),t._v(" "),a("el-form-item",{attrs:{label:"说明："}},[a("el-input",{attrs:{type:"textarea",rows:4,disabled:"",clearable:"",placeholder:"请输入说明..."},model:{value:t.form.desc,callback:function(e){t.$set(t.form,"desc",e)},expression:"form.desc"}})],1),t._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary",loading:t.btnLoading},on:{click:t.submitForm}},[t._v("撤销")])],1)],1)],1)},staticRenderFns:[]};var _=a("VU/8")(h,m,!1,function(t){a("KwdY")},null,null).exports,v={name:"app-staff-out-list",data:function(){var t=this;return{search:{out_store_id:"",in_store_id:"",start_date:"",end_date:"",type:"",status:"",keyword:""},list:[],loading:!1,curPageIndex:1,dialog:{showVisible:!1},recordId:"",pickerOptions:{disabledDate:function(e){if(""!==t.search.start_date)return e.getTime()<f()(t.search.start_date)}}}},computed:o()({},Object(d.c)("app",["deptId"]),Object(d.d)("dict",["deptList","fromDeptList","transferOutList","staffOutStatusList"])),methods:o()({},Object(d.b)("dict",["createDeptList","createFromDeptList","createTransferOutList","createOutStatusList"]),{initial:function(){""!=this.deptId&&(this.search.out_store_id=this.deptId,this.searchHandle())},recordHandler:function(t,e){this.dialog[e]=!0,this.recordId=t},cancelHandler:function(t){var e=this;this.$confirm("确认撤销调出员工吗?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(i()(r.a.mark(function a(){var s;return r.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.next=2,Object(c.n)({id:t});case 2:1==(s=a.sent).code?e.$message.success(s.message):e.$message.error(s.message);case 4:case"end":return a.stop()}},a,e)}))).catch(function(){})},getStuffList:function(t,e){var a=this;return i()(r.a.mark(function s(){var n;return r.a.wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return t=t>0?Number(t):a.curPageIndex,a.loading=!0,s.next=4,Object(c._14)(o()({pageNum:t,pageSize:10},a.search));case 4:1==(n=s.sent).code?(a.list=n.data.list,a.list.total=n.data.totalRow||1,e&&e()):a.$message.error(n.message),a.loading=!1;case 7:case"end":return s.stop()}},s,a)}))()},searchHandle:function(t){if("all"===t)for(var e in this.search)this.search[e]="out_store_id"!=e?"":this.deptId;if(f()(this.search.end_date)<f()(this.search.start_date))return this.$message.warning("结束日期不能小于开始日期"),this.search.end_date="";this.getStuffList(1)},handleCurrentChange:function(t){this.curPageIndex=t,this.getStuffList(t)},reloadGetData:function(t){if("reload"==t){for(var e in this.dialog)this.dialog[e]=!1;this.getStuffList(this.curPageIndex)}}}),created:function(){this.initial(),this.getStuffList(),this.createDeptList(),this.createFromDeptList(),this.createTransferOutList(),this.createOutStatusList()},components:{AppDialog:p.a,AppShowStaffOut:_}},g={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",[a("nav",{staticClass:"app-location-wrapper"},[a("el-breadcrumb",{staticClass:"fl",attrs:{separator:"/"}},[a("el-breadcrumb-item",{attrs:{to:{path:"/sys_setting"}}},[t._v("系统管理")]),t._v(" "),a("el-breadcrumb-item",[t._v("员工管理")]),t._v(" "),a("el-breadcrumb-item",[t._v("调出员工列表")])],1)],1),t._v(" "),a("div",{staticClass:"component-top"},[a("div",{staticClass:"search-title fl"},[t._v("调出门店：")]),t._v(" "),a("el-select",{staticClass:"fl",staticStyle:{width:"120px","margin-right":"10px"},attrs:{size:"small",placeholder:"调出门店"},on:{change:t.searchHandle},model:{value:t.search.out_store_id,callback:function(e){t.$set(t.search,"out_store_id",e)},expression:"search.out_store_id"}},t._l(t.deptList,function(t,e){return a("el-option",{key:e,attrs:{label:t.name,value:t.value}})})),t._v(" "),a("div",{staticClass:"search-title fl"},[t._v("调入门店：")]),t._v(" "),a("el-select",{staticClass:"fl",staticStyle:{width:"120px","margin-right":"10px"},attrs:{size:"small",placeholder:"调入门店"},on:{change:t.searchHandle},model:{value:t.search.in_store_id,callback:function(e){t.$set(t.search,"in_store_id",e)},expression:"search.in_store_id"}},t._l(t.fromDeptList,function(t,e){return a("el-option",{key:e,attrs:{label:t.name,value:t.value}})})),t._v(" "),a("div",{staticClass:"search-title fl"},[t._v("开始日期：")]),t._v(" "),a("el-date-picker",{staticClass:"fl",staticStyle:{width:"160px","margin-right":"10px"},attrs:{size:"small",type:"date",placeholder:"开始日期",format:"yyyy 年 MM 月 dd 日","value-format":"yyyy-MM-dd"},on:{change:t.searchHandle},model:{value:t.search.start_date,callback:function(e){t.$set(t.search,"start_date",e)},expression:"search.start_date"}}),t._v(" "),a("div",{staticClass:"search-title fl"},[t._v("结束日期：")]),t._v(" "),a("el-date-picker",{staticClass:"fl",staticStyle:{width:"160px","margin-right":"10px"},attrs:{size:"small",type:"date",placeholder:"结束日期",format:"yyyy 年 MM 月 dd 日","value-format":"yyyy-MM-dd","picker-options":t.pickerOptions},on:{change:t.searchHandle},model:{value:t.search.end_date,callback:function(e){t.$set(t.search,"end_date",e)},expression:"search.end_date"}}),t._v(" "),a("div",{staticClass:"search-title fl"},[t._v("类型：")]),t._v(" "),a("el-select",{staticClass:"fl",staticStyle:{width:"100px","margin-right":"10px"},attrs:{size:"small",clearable:"",placeholder:"类型"},on:{change:t.searchHandle},model:{value:t.search.type,callback:function(e){t.$set(t.search,"type",e)},expression:"search.type"}},t._l(t.transferOutList,function(t,e){return a("el-option",{key:e,attrs:{label:t.name,value:t.value}})})),t._v(" "),a("div",{staticClass:"search-title fl"},[t._v("状态：")]),t._v(" "),a("el-select",{staticClass:"fl",staticStyle:{width:"80px","margin-right":"10px"},attrs:{size:"small",clearable:"",placeholder:"状态"},on:{change:t.searchHandle},model:{value:t.search.status,callback:function(e){t.$set(t.search,"status",e)},expression:"search.status"}},t._l(t.staffOutStatusList,function(t,e){return a("el-option",{key:e,attrs:{label:t.name,value:t.value}})})),t._v(" "),a("el-input",{staticClass:"fl",staticStyle:{width:"150px"},attrs:{size:"small",placeholder:"姓名/电话/拼音","prefix-icon":"el-icon-search",clearable:""},nativeOn:{keyup:function(e){return"button"in e||!t._k(e.keyCode,"enter",13,e.key,"Enter")?t.searchHandle(e):null}},model:{value:t.search.keyword,callback:function(e){t.$set(t.search,"keyword",e)},expression:"search.keyword"}}),t._v(" "),a("el-button",{staticClass:"fl",staticStyle:{"margin-left":"10px"},attrs:{size:"small"},on:{click:function(e){e.stopPropagation(),t.searchHandle("all")}}},[t._v("\r\n            全部\r\n        ")])],1),t._v(" "),a("div",{staticClass:"component-main"},[a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],attrs:{size:"small",data:t.list,stripe:"",border:""}},[a("el-table-column",{attrs:{label:"调出门店","min-width":"150"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",{style:{color:e.row.out_store_color}},[t._v(t._s(e.row.out_store_name))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"调入门店","min-width":"150"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",{style:{color:e.row.in_store_color}},[t._v(t._s(e.row.in_store_name))])]}}])}),t._v(" "),a("el-table-column",{attrs:{prop:"name",label:"姓名"}}),t._v(" "),a("el-table-column",{attrs:{prop:"date",label:"日期"}}),t._v(" "),a("el-table-column",{attrs:{prop:"type",label:"类型"}}),t._v(" "),a("el-table-column",{attrs:{label:"状态",width:"120"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-tag",{attrs:{type:e.row.status_color,size:"medium"}},[t._v(t._s(e.row.status_text))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"操作",width:"160"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-button",{attrs:{size:"mini"},on:{click:function(a){a.stopPropagation(),t.recordHandler(e.row.id,"showVisible")}}},[t._v("\r\n                        查看\r\n                    ")]),a("el-button",{attrs:{type:"danger",size:"mini"},on:{click:function(a){a.stopPropagation(),t.cancelHandler(e.row.id)}}},[t._v("\r\n                        撤销\r\n                    ")])]}}])})],1),t._v(" "),a("el-pagination",{staticClass:"pagination",attrs:{background:"",layout:"prev, pager, next, jumper",total:t.list.total},on:{"current-change":t.handleCurrentChange}})],1),t._v(" "),a("app-dialog",{attrs:{title:"查看调入信息",visible:t.dialog.showVisible},on:{"update:visible":function(e){t.$set(t.dialog,"showVisible",e)}}},[a("app-show-staff-out",{attrs:{"record-id":t.recordId},on:{reloadEvent:t.reloadGetData}})],1)],1)},staticRenderFns:[]};var b=a("VU/8")(v,g,!1,function(t){a("klmv")},null,null);e.default=b.exports},KwdY:function(t,e){},klmv:function(t,e){}});