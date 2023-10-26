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

});

function initSource() {

}

function ajaxDevice() {

    if(! $('#theForm').validate().form()){
        return;
    }

    var sd = $('#theForm').serialize();
    $.ajaxSettings.timeout = '10000';
    $.post("ajaxDevice", sd, function (data, status) {
        var msg = eval(data);
        swal({
                title: msg.content,
                //text: msg.etraInfo,
                type: msg.title
            }, function () {
            //刷新页面
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

