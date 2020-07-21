(function ($, window) {
    var app = {
        //初始化
        init: function () {
            this.cacheElements();//缓存dom节点
            this.bindEvents();//绑定事件
            this.menuChangeFunc();//监听滚动事件,切换菜单，改变菜单视图
        },
        //缓存dom节点
        cacheElements: function () {
            this.$body = $('body');
            this.$body.on('click','#my_navbar_info ul li',function () {
                $('#my_navbar_info ul li').removeClass('active');
                $(this).addClass('active');
            });
        },

        //绑定事件
        bindEvents: function () {
            this.$body.on('click', '#batchUpload', this.batchUpload);// 批量修改
            this.$body.on('click', '#exportTemplate', this.exportTemplate);// 下载模板
            this.$body.on('change', '#file', this.fileChange);// 监听文件改变
        },

        // 下载模板
        exportTemplate: function () {
            // 通过form表单形式下载表格
            // 设置参数
            var searchCondition ={
                fpxTrackingNo:fpxTrackingNo,
                parcelStatus:parcelStatus,
            };
            // 发送请求
            var form = $('<form method="POST" action="' + CTX + "/download" + '">');
            // 设置参数
            $.each(searchCondition, function(k, v) {
                form.append($('<input type="hidden" name="' + k +
                    '" value="' + v + '">'));
            });
            $('#templateExport').append(form);
            form.submit();
        },

        // 监听变更并赋值显示文件路径
        fileChange: function () {
            $("#uploadStatusDiv").hide();
            // 判断校验文件类型
            var fileName = $("#file").val();
            var ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (ext != 'xlsx' && ext != 'xls') {
                $("#file").val("");
                layer.alert("请上传xlsx、xls文档格式文件");
                return;
            }

            $("#showPath").val(fileName);
        },

        // 上传导入数据文件
        batchUpload: function () {
            if (!$("#file").val()) {
                layer.alert("请选择xlsx、xls文档格式文件");
                return;
            }
            $('#mask').show();
            $("#batchUpdateForm").ajaxSubmit({
                type: "POST",
                url: CTX + "/upload",
                contentType: false,
                processData: false,
                success: function (data) {
                    $('#mask').hide();
                    // 清空文件
                    $("#showPath").val('');
                    $("#file").val("");

                    if (data.flag == 'success') {
                        $('#uploadStatusDiv').show();
                        $('#uploadStatus').html("导入成功");

                    } else if (data.message == '导入失败') {
                        // 导入失败则下载错误数据
                        $('#uploadStatusDiv').show();
                        $('#uploadStatus').html("导入失败，详情见附件");
                        var errorResultForm = $('<form method="GET" action="' + CTX + "/page/packageRule/errorResult" + '">');
                        $('#errorResult').append(errorResultForm);
                        errorResultForm.submit();
                    } else {
                        $('#uploadStatusDiv').show();
                        $('#uploadStatus').html("导入失败，"+data.message);
                    }
                }
            });
        },

        //监听滚动事件,切换菜单，改变菜单视图
        menuChangeFunc: function () {
            this.$body.scrollspy({
                target: '#fpx-navbar-info',
                offset: 120
            });
            window.onscroll = function (e) {
                var scroll = document.body.scrollTop || document.documentElement.scrollTop;
                if (scroll < 50) {
                    $('.nav.nav-pills li:eq(0)').addClass('active');
                }
                ;
            };
        }
    };
    app.init();//初始化
})(jQuery, window);