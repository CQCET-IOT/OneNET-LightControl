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

//日期范围限制
var start = {
    elem: '#start',
    format: 'YYYY-MM-DD hh:mm:ss',
    min: '2019-06-01 23:59:59', //设定最小日期为当前日期
    max: '2099-06-16 23:59:59', //最大日期
    istime: true,
    istoday: false,
    choose: function (datas) {
        end.min = datas; //开始日选好后，重置结束日的最小日期
        end.start = datas //将结束日的初始值设定为开始日
    }
};
var end = {
    elem: '#end',
    format: 'YYYY-MM-DD hh:mm:ss',
    min: '2019-06-01 23:59:59',
    max: '2099-06-16 23:59:59',
    istime: true,
    istoday: false,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(start);
laydate(end);
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
    }
};
var opts = {
    url: "sysloglist",
    cache:false,
    pagination: true,
    sidePagination:"server",
    queryParams: function(params) {
        return {
            pageNumber: params.offset /params.limit +1,
            pageSize: params.limit,
            name: $("#name").val(),
            search: $("#search").val(),
            beginDate:$("#start").val(),
            endDate:$("#end").val()
        };
    },
    iconSize: 'outline',
    toolbar: '#tableToolbar',
    showRefresh: false,
    showToggle: false,
    showColumns: false,
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
        width : 50
    }, {
        field : 'ip',
        title : 'IP',
        width : 50
    }, {
        field : 'logDate',
        title : '时间',
        width : 80
    }, {
        field : 'module',
        title : '模块',
        width : 50
    }, {
        field : 'despription',
        title : '细节',
        width : 200
        /*    },  {
                field: 'operate',
                title: '操作',
                align: 'center',
                width : 100,
                events: operateEvents,
                formatter: operateFormatter
            */
    }]
};

function operateFormatter(value, row, index) {
    return [
        '<button type="button" class="RoleOfdelete btn btn-primary  btn-xs" style="margin-right:15px;">删除</button>',

        '<button type="button" class="RoleOfedit btn btn-primary  btn-xs" style="margin-right:15px;">修改</button>'
    ].join('');
}
//查询
$(document).on('click', ".queryButton",function(){
    $('#theTable').bootstrapTable('refresh',opts);
});
//清空
$(document).on('click', ".clearButton",function(){
    $("#name").val("");
    $("#search").val("");
    $("#start").val("");
    $("#end").val("");

    $('#theTable').bootstrapTable('refresh',opts);
});

(function(document, window, $) {
    'use strict';

    (function () {
        $('#theTable').bootstrapTable(opts);

    })();

})(document, window, jQuery);

