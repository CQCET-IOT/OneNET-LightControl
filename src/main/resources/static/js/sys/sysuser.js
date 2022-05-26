//以下为修改jQuery Validation插件兼容Bootstrap的方法，没有直接写在插件中是为了便于插件升级
        $.validator.setDefaults({
            highlight: function (element) {
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
            },
            success: function (element) {
                element.closest('.form-group').removeClass('has-error').addClass('has-success');
            },
            errorElement: "span",
            errorPlacement: function (error, element) {
                if (element.is(":radio") || element.is(":checkbox")) {
                    error.appendTo(element.parent().parent().parent());
                } else {
                    error.appendTo(element.parent());
                }
            },
            errorClass: "help-block m-b-none",
            validClass: "help-block m-b-none"


        });

        //以下为官方示例
        $().ready(function () {
            // validate signup form on keyup and submit
            var icon = "<i class='fa fa-times-circle'></i> ";
            var vali = $("#theForm").validate({
                rules: {
                    username: {
                        required: true,
                        minlength: 2
                    },
                    password: {
                        required: true,
                        minlength: 5
                    },
                    confirm_password: {
                        required: true,
                        minlength: 5,
                        equalTo: "#password"
                    },
                    classname: {
                        required: true,
                    },
                },
                messages: {
                    username: {
                        required: icon + "请输入操作员账号",
                        minlength: icon + "姓名必须两个字符以上"
                    },
                    password: {
                        required: icon + "请输入您的密码",
                        minlength: icon + "密码必须5个字符以上"
                    },
                    confirm_password: {
                        required: icon + "请再次输入密码",
                        minlength: icon + "密码必须5个字符以上",
                        equalTo: icon + "两次输入的密码不一致"
                    },
                    classname: icon + "请输入操作员显示名"

                }
            });

            $(function() {
                $('#modal-form').modal('hide');
            });
            $(function() {
                $('#modal-form').on('hide.bs.modal',
                    function() {
                        $("#myModalLabel").html("新增操作员");
                        $("#id").val("");
                        $("#stuid").val("");
                        $("#username").val("");
                        $("#password").val("");
                        $("#confirm_password").val("");
                        $("#classname").val("");
                        $("input[name='check']").each(function(){
                            $(this).prop('checked',false);
                        });
                        vali.resetForm();
                        $(".form-group").removeClass("has-error");
                    });
            });
        });

/*!
 * Remark (http://getbootstrapadmin.com/remark)
 * Copyright 2015 amazingsurge
 * Licensed under the Themeforest Standard Licenses
 */
function cellStyle(value, row, index) {
    var classes = ['active', 'success', 'info', 'warning', 'danger'];

    if (index % 2 === 0 && index / 2 < classes.length) {
        return {
            classes: classes[index / 2]
        };
    }
    return {};
}

window.operateEvents = {
    'click .RoleOfdelete': function (e, value, row, index) {
        swal({
            title: "您确定要删除该用户吗",
            text: "删除后将无法恢复，请谨慎操作！",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "删除",
            closeOnConfirm: false
        }, function () {
            $.post('ajaxdel', {id: row.id}, function (data, status) {
                var msg= eval(data);
                swal({
                        title: msg.content,
                        text: "",
                        type: msg.title
                    }, function () {
                        $('#theTable').bootstrapTable('refresh',opts);
                    }
                );
            });

        });

    },
    'click .RoleOfedit': function (e, value, row, index) {
        $("#myModalLabel").html("修改用户");
        $("#id").val(row.id);
        $("#username").val(row.username);
        $("#password").val(row.password);
        $("#confirm_password").val(row.password);
        $("#classname").val(row.classname);
        $("input[name='check']").each(function(){
            var value = $(this).val();
            if(row.roleids.indexOf(value)>-1){
                $(this).prop('checked',true);
            }else {
                $(this).prop('checked',false);
            }
        });

        $("#modal-form").modal('show');
    },
    'click .RoleOfadd': function (e, value, row, index) {
        $("#myModalLabel").html("新增用户");
        $("#id").val("");
        $("#username").val("");
        $("#password").val("");
        $("#confirm_password").val("");
        $("#classname").val("");
        $("#modal-form").modal('show');

    }
};
var opts = {
    url: "sysuserlist",
    cache:false,
    pagination: true,
    sidePagination:"server",
    queryParams: function(params) {
        return {
            pageNumber: params.offset /params.limit +1,
            pageSize: params.limit,
            search: params.search
        };
    },
    iconSize: 'outline',
    toolbar: '#tableToolbar',
    showRefresh: true,
    showToggle: false,
    showColumns: true,
    buttonsAlign:"right",  //按钮位置
    icons: {
        refresh: 'glyphicon-repeat',
        columns: 'glyphicon-list'
    },

    columns: [{ //编辑datagrid的列
        title : '序号',
        field : 'id',
        //align : 'center',
        width : 20
        //,checkbox : true
    }, {
        field : 'username',
        title : '账号',
        width : 80
    }, {
        field : 'classname',
        title : '姓名',
        width : 80
    }, {
        field : 'roleids',
        title : 'role',
        visible:false
    }, {
        field : 'roledescrips',
        title : '角色',
        width : 80
    },  {
        field: 'operate',
        title: '操作',
        align: 'center',
        width : 100,
        events: operateEvents,
        formatter: operateFormatter
    }]
};

function operateFormatter(value, row, index) {
    return [
        '<button type="button" class="RoleOfdelete btn btn-primary  btn-xs" style="margin-right:15px;">删除</button>',

        '<button type="button" class="RoleOfedit btn btn-primary  btn-xs" style="margin-right:15px;">修改</button>'
    ].join('');
}
function recordRoles() {
    //方案一
    //$('#rolesinfo').val($("input[name='check']").map(function(){return this.value}).get().join(','));
    //方案二
    var idsstr = " ";
    var zhi = " ";
    $("input[name='check']").each(function(){ //遍历table里的全部checkbox
        idsstr += $(this).val() + ","; //获取所有checkbox的值
        if($(this).is(':checked')) //如果被选中
            zhi += $(this).val() + ","; //获取被选中的值
    });
    if(idsstr.length > 0) //如果获取到
        idsstr = idsstr.substring(0, idsstr.length - 1); //把最后一个逗号去掉
    if(zhi.length > 0) //如果获取到
        zhi = zhi.substring(0, zhi.length - 1); //把最后一个逗号去掉
    $('#roleids').val(zhi);
    return true;
}
function addUser() {
    return recordRoles();
}

function ajaxAdd() {
    recordRoles();
    if($('#theForm').valid()){
        var sd = $('#theForm').serialize();
        $("#modal-form").modal('hide');
        $.post("ajaxadd",sd,function (data, status) {
        var msg= eval(data);
        swal({
                title: msg.content,
                text: msg.etraInfo,
                type: msg.title
            }, function () {
                $('#theTable').bootstrapTable('refresh',opts);

            }
        );
        });
    };
}
(function(document, window, $) {
    'use strict';

    (function () {
        $('#theTable').bootstrapTable(opts);

    })();

})(document, window, jQuery);

