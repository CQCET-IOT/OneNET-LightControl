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
$(document).ready(function () {
    // validate signup form on keyup and submit
    var icon = "<i class='fa fa-times-circle'></i> ";

    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green',
    });
    initSource();
    $('input:radio[name="sourcetype"]').click(function () {
        initSource();
    });
});

function initSource() {
    $('#token').val('');
    var cs = $('input:radio[name="sourcetype"]:checked').val();
    if (cs == 'user') {
        $('#div_userid').show();
        $('#div_productid').hide();
        $('#div_projectid').hide();
        $('#label_key').text('用户密钥');
        $('#version').val('2020-05-29');
    } else if (cs == 'project') {
        $('#div_userid').hide();
        $('#div_productid').hide();
        $('#div_projectid').show();
        $('#label_key').text('项目密钥');
        $('#version').val('2020-05-29');
    } else if (cs == 'product') {
        $('#div_userid').hide();
        $('#div_productid').show();
        $('#div_projectid').hide();
        $('#label_key').text('产品或设备密钥');
        $('#version').val('2018-10-31');
    }
}

function copyText() {
    var ele = document.getElementById("token");
    ele.select();
    document.execCommand('copy', false, null);
    layer.tips('已复制到剪贴板', $('#token'), {
        tips: [1, '#3595CC'],
        time: 4000
    });
}

function ajaxToken() {

    if(! $('#theForm').validate().form()){
        return;
    }

    var sd = $('#theForm').serialize();
    $.ajaxSettings.timeout = '10000';
    $.post("ajaxToken", sd, function (data, status) {
        var msg = eval(data);
        swal({
                title: msg.content,
                //text: msg.etraInfo,
                type: msg.title
            }, function () {
                var str = msg.etraInfo;
                $('#token').val(str);
            }
        );
    }).error(function (xhr, status, info) {
        swal({
                title: '请求超时',
                type: status
            }, function () {
                //刷新页面
            }
        );
    });
}

