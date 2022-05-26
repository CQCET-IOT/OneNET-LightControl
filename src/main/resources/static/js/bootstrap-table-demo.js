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

function rowStyle(row, index) {
  var classes = ['active', 'success', 'info', 'warning', 'danger'];

  if (index % 2 === 0 && index / 2 < classes.length) {
    return {
      classes: classes[index / 2]
    };
  }
  return {};
}

function scoreSorter(a, b) {
  if (a > b) return 1;
  if (a < b) return -1;
  return 0;
}

function nameFormatter(value) {
  return value + '<i class="icon wb-book" aria-hidden="true"></i> ';
}

function starsFormatter(value) {
  return '<i class="icon wb-star" aria-hidden="true"></i> ' + value;
}

function queryParams() {
  return {
    type: 'owner',
    sort: 'updated',
    direction: 'desc',
    per_page: 100,
    page: 1
  };
}

function buildTable($el, cells, rows) {
  var i, j, row,
    columns = [],
    data = [];

  for (i = 0; i < cells; i++) {
    columns.push({
      field: '字段' + i,
      title: '单元' + i
    });
  }
  for (i = 0; i < rows; i++) {
    row = {};
    for (j = 0; j < cells; j++) {
      row['字段' + j] = 'Row-' + i + '-' + j;
    }
    data.push(row);
  }
  $el.bootstrapTable('destroy').bootstrapTable({
    columns: columns,
    data: data,
    iconSize: 'outline',
    icons: {
      columns: 'glyphicon-list'
    }
  });
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
            $.post('dodel', {id: row.id}, function (data, status) {
                var msg= eval(data);
                swal({
                        title: msg.content,
                        text: "",
                        type: msg.title
                    }, function () {
                        $('#userTable1').bootstrapTable('refresh',opts);
                    }
                );
            });

        });

    },
    'click .RoleOfedit': function (e, value, row, index) {
        $("#myModalLabel").html("修改用户");
        $("#id").val(row.id);
        $("#stuid").val(row.stuid);
        $("#username").val(row.username);
        $("#password").val(row.password);
        $("#confirm_password").val(row.password);
        $("#classname").val(row.classname);
        $("#modal-form").modal('show');
    },
    'click .RoleOfadd': function (e, value, row, index) {
        $("#myModalLabel").html("新增用户");
        $("#id").val("");
        $("#stuid").val("");
        $("#username").val("");
        $("#password").val("");
        $("#confirm_password").val("");
        $("#classname").val("");
        $("#modal-form").modal('show');

    }
};
//数字
function doOnMsoNumberFormat(cell, row, col){
    var result = "";
    if (row > 0 && col == 0){
        result = "\\@";
    }
    return result;
}

//处理导出内容,这个方法可以自定义某一行、某一列、甚至某个单元格的内容,也就是将其值设置为自己想要的内容
function DoOnCellHtmlData(cell, row, col, data){
    if(row == 0){
        return data;
    }

    //由于备注列超过6个字的话,通过span标签处理只显示前面6个字,如果直接导出的话会导致内容不完整,因此要将携带完整内容的span标签中title属性的值替换
    if(col == 4 || col ==11 || col == 7){
        var spanObj = $(data);//将带 <span title="val"></span> 标签的字符串转换为jQuery对象
        var title = spanObj.attr("title");//读取<span title="val"></span>中title属性的值
        //var span = cell[0].firstElementChild;//读取cell数组中的第一个值下的第一个元素
        if(typeof(title) != 'undefined'){
            return title;
        }
    }

    return data;
}
var opts = {
    url: "userlist",
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
    showExport: true,  //是否显示导出按钮
    buttonsAlign:"right",  //按钮位置
    exportTypes:['json', 'xml', 'csv', 'txt', 'sql', 'excel','pdf'],  //导出文件类型
    icons: {
        refresh: 'glyphicon-repeat',
        columns: 'glyphicon-list',
        export:'glyphicon-export'
    },
    exportDataType:'all',
    exportOptions:{
        ignoreColumn: [4],  //忽略某一列的索引
        fileName: '总台帐报表',  //文件名称设置
        worksheetName: 'sheet1',  //表格工作区名称
        tableName: '总台帐报表',
        excelstyles: ['background-color', 'color', 'font-size', 'font-weight'],
        onMsoNumberFormat: doOnMsoNumberFormat,
        onCellHtmlData: DoOnCellHtmlData
    },
    columns: [{ //编辑datagrid的列
        title : '序号',
        field : 'id',
        //align : 'center',
        width : 20
        //,checkbox : true
    }, {
        field : 'stuid',
        title : '学号',
        width : 80
    }, {
        field : 'username',
        title : '姓名',
        width : 80
    }, {
        field : 'classname',
        title : '班级',
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

(function(document, window, $) {
  'use strict';

  // Example Bootstrap Table From Data
  // ---------------------------------
  (function() {
    var bt_data = [{
      "Tid": "1",
      "First": "奔波儿灞",
      "sex": "男",
      "Score": "50"
    }, {
      "Tid": "2",
      "First": "灞波儿奔",
      "sex": "男",
      "Score": "94"
    }, {
      "Tid": "3",
      "First": "作家崔成浩",
      "sex": "男",
      "Score": "80"
    }, {
      "Tid": "4",
      "First": "韩寒",
      "sex": "男",
      "Score": "67"
    }, {
      "Tid": "5",
      "First": "郭敬明",
      "sex": "男",
      "Score": "100"
    }, {
      "Tid": "6",
      "First": "马云",
      "sex": "男",
      "Score": "77"
    }, {
      "Tid": "7",
      "First": "范爷",
      "sex": "女",
      "Score": "87"
    }];


    $('#exampleTableFromData').bootstrapTable({
      data: bt_data,
      // mobileResponsive: true,
      height: "250"
    });
  })();

  // Example Bootstrap Table Columns
  // -------------------------------
  (function() {
    $('#exampleTableColumns').bootstrapTable({
      url: "js/demo/bootstrap_table_test.json",
      height: "400",
      iconSize: 'outline',
      showColumns: true,
      icons: {
        refresh: 'glyphicon-repeat',
        toggle: 'glyphicon-list-alt',
        columns: 'glyphicon-list'
      }
    });
  })();


  // Example Bootstrap Table Large Columns
  // -------------------------------------
  buildTable($('#exampleTableLargeColumns'), 50, 50);

  // Example Bootstrap Table Toolbar
  // -------------------------------
  (function() {
    $('#exampleTableToolbar').bootstrapTable({
      url: "js/demo/bootstrap_table_test2.json",
      search: true,
      showRefresh: true,
      showToggle: true,
      showColumns: true,
      toolbar: '#exampleToolbar',
      iconSize: 'outline',
      icons: {
        refresh: 'glyphicon-repeat',
        toggle: 'glyphicon-list-alt',
        columns: 'glyphicon-list'
      }
    });
  })();

  (function() {
        $('#exampleTablePagination').bootstrapTable({
            url: "user/listtest",
            cache:false,
            showRefresh: true,
            showToggle: true,
            showColumns: true,
            iconSize: 'outline',
            icons: {
                refresh: 'glyphicon-repeat',
                toggle: 'glyphicon-list-alt',
                columns: 'glyphicon-list'
            }
        });
    })();

  // Example Bootstrap Table Events
  // ------------------------------
  (function() {
    $('#exampleTableEvents').bootstrapTable({
      //url: "/static/js/bootstrap_table_test.json",
        url: "user/userlist",
      //search: true,
        cache:false,
      pagination: true,
        sidePagination:"server",
        queryParams: function(params) {
          //var subcompany = $('#subcompany option:selected').val();
          //var name = $('#name').val();
          return {
              pageNumber: params.offset,
              pageSize: params.limit,
              companyId:985,
              name:'中股文'
          };
      },
      showRefresh: true,
      showToggle: true,
      showColumns: true,
      iconSize: 'outline',
      toolbar: '#exampleTableEventsToolbar',
      icons: {
        refresh: 'glyphicon-repeat',
        toggle: 'glyphicon-list-alt',
        columns: 'glyphicon-list'
      }
    });

    var $result = $('#examplebtTableEventsResult');

    $('#exampleTableEvents').on('all.bs.table', function(e, name, args) {
        console.log('Event:', name, ', data:', args);
      })
      .on('click-row.bs.table', function(e, row, $element) {
        $result.text('Event: click-row.bs.table');
      })
      .on('dbl-click-row.bs.table', function(e, row, $element) {
        $result.text('Event: dbl-click-row.bs.table');
      })
      .on('sort.bs.table', function(e, name, order) {
        $result.text('Event: sort.bs.table');
      })
      .on('check.bs.table', function(e, row) {
        $result.text('Event: check.bs.table');
      })
      .on('uncheck.bs.table', function(e, row) {
        $result.text('Event: uncheck.bs.table');
      })
      .on('check-all.bs.table', function(e) {
        $result.text('Event: check-all.bs.table');
      })
      .on('uncheck-all.bs.table', function(e) {
        $result.text('Event: uncheck-all.bs.table');
      })
      .on('load-success.bs.table', function(e, data) {
        $result.text('Event: load-success.bs.table');
      })
      .on('load-error.bs.table', function(e, status) {
        $result.text('Event: load-error.bs.table');
      })
      .on('column-switch.bs.table', function(e, field, checked) {
        $result.text('Event: column-switch.bs.table');
      })
      .on('page-change.bs.table', function(e, size, number) {
        $result.text('Event: page-change.bs.table');
      })
      .on('search.bs.table', function(e, text) {
        $result.text('Event: search.bs.table');
      });
  })();

    (function () {
        $('#userTable1').bootstrapTable(opts);

    })();

})(document, window, jQuery);
