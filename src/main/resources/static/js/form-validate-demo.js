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
            var vali = $("#newUserForm").validate({
                rules: {
                    username: {
                        required: true,
                        minlength: 2
                    },
                    stuid: {
                        required: true,
                        minlength: 10,
                        digits:true
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
                        required: icon + "请输入用户姓名",
                        minlength: icon + "姓名必须两个字符以上"
                    },
                    stuid: {
                        required: icon + "请输入学号",
                        minlength: icon + "学号必须10位数字"
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
                    classname: icon + "请输入用户班级"

                }
            });

            $(function() {
                $('#modal-form').modal('hide')
            });

            $(function() {
                $('#modal-form').on('hide.bs.modal',
                    function() {
                        $("#myModalLabel").html("新增用户");
                        $("#id").val("");
                        $("#stuid").val("");
                        $("#username").val("");
                        $("#password").val("");
                        $("#confirm_password").val("");
                        $("#classname").val("");
                        vali.resetForm();
                        $(".form-group").removeClass("has-error");
                    })
            });
        });


